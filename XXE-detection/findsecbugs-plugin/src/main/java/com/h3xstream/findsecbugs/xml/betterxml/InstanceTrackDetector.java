/**
 * Find Security Bugs
 * Copyright (c) Philippe Arteau, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */
package com.h3xstream.findsecbugs.xml.betterxml;

import com.h3xstream.findsecbugs.common.InterfaceUtils;
import com.h3xstream.findsecbugs.xml.betterxml.instancetrack.*;
import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.ba.XField;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.JavaClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class InstanceTrackDetector extends OpcodeStackDetector {
    private final BugReporter bugReporter;
    private List<TrackedObject> trackedObjects = new ArrayList<>();
    private Map<XField, SourceLineAnnotation> fieldUse = new HashMap<>();

    public InstanceTrackDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;

    }

    @Override
    public void sawOpcode(int seen) {
        if (seen == Const.PUTFIELD || seen == Const.PUTSTATIC) {
            XField xFieldOperand = getXFieldOperand();
            // Get uses of class fields and map to source line
            // FIXME: Handle Illegalargumentexception with io, below line should fix this?
            if(getStack().getStackDepth() < 1) {
                return;
            }
            OpcodeStack.Item stackItem = stack.getStackItem(1);
            if (stackItem.getRegisterNumber() != 0 ) {
                return;
            }
            fieldUse.put(xFieldOperand, SourceLineAnnotation.fromVisitedInstruction(this));
        }

        if (seen != Const.INVOKEVIRTUAL && seen != Const.INVOKEINTERFACE && seen != Const.INVOKESTATIC) {
            return;
        }

        JavaClass javaClass = getThisClass();
        //(1rst solution for secure parsing proposed by the CERT) Sandbox in an action with limited privileges
        if (InterfaceUtils.isSubtype(javaClass, "java.security.PrivilegedExceptionAction")) {
            return; //Assuming the proper right are apply to the sandbox
        }

        String fullOperand = getFullOperand();
        TrackedObject trackedObject = getTrackedObjectForInitCall(fullOperand);
        if (trackedObject != null) {
            // Found an object initialization call.
            // We save the location of the call and call foundObjectInitCall
            SourceLineAnnotation objectCreationLocation = SourceLineAnnotation.fromVisitedInstruction(this, getPC());
            TrackedObjectInstance instance = trackedObject.addTrackedObjectInstance(getClassContext().getJavaClass(), getMethodDescriptor());
            foundObjectInitCall(trackedObject, instance);
            // Use the init location as the first tracked return value, and add the bugs to it
            instance.addFoundTrackedReturnValue(objectCreationLocation);
        } else {
            if (seen != Const.INVOKESTATIC) {
                OpcodeStack.Item currentItem = getStack().getItemMethodInvokedOn(this);
                SourceLineAnnotation objectCreationLocation = SourceLineAnnotation.fromVisitedInstruction(this, currentItem.getPC());
                XMethod returnValueOf = currentItem.getReturnValueOf();
                XField fieldValue = currentItem.getXField();
                String signature = currentItem.getSignature();
                SourceLineAnnotation initLocation = SourceLineAnnotation.fromVisitedInstruction(this, getPC());

                if (returnValueOf == null && fieldUse.containsKey(fieldValue)) {
                    // If return value of the method doesn't exist; try to get the values from a class field
                    // Get sourceline from last field use, do this here since we need this location to track calls
                    objectCreationLocation = fieldUse.get(fieldValue);
                }

                for (TrackedObject currentObject : trackedObjects) {
                    for (TrackedObjectInstance instance : currentObject.getTrackedObjectInstances()) {
                        for (TrackedReturnValue trackedReturnValue : currentObject.getTrackedReturnValues()) {
                            // Check the return value of a method first
                            if (returnValueOf != null && trackedReturnValue.getSlashedClassName().equals(returnValueOf.getMethodDescriptor().getSlashedClassName())
                                    && trackedReturnValue.getSignature().equals(signature) && trackedReturnValue.getFullOperand().equals(fullOperand)) {
                                if (instance.getFoundTrackedReturnValueLine(objectCreationLocation)) {
                                    instance.addFoundTrackedReturnValue(initLocation);
                                    if (trackedReturnValue.isReportBugOnThisLine()){
                                        instance.addVulnerableLine(objectCreationLocation, initLocation);
                                    }
                                }
                            }
                            // If return value of the method doesn't exist; try to get the values from a class field
                            else if (fieldValue != null && trackedReturnValue.getSignature().equals(signature) && trackedReturnValue.getFullOperand().equals(fullOperand)) {
                                if (instance.getFoundTrackedReturnValueLine(objectCreationLocation)) {
                                    instance.addFoundTrackedReturnValue(initLocation);
                                    if (trackedReturnValue.isReportBugOnThisLine()){
                                        instance.addVulnerableLine(objectCreationLocation, initLocation);
                                    }
                                }
                            }
                        }
                    }
                }
                for (TrackedObject currentObject : trackedObjects) {
                    for (TrackedObjectInstance instance : currentObject.getTrackedObjectInstances()) {
                        if (instance.getTrackedReturnValueSourceLines().contains(objectCreationLocation)) {
                            // Check single calls that make instance secure
                            for (TrackedCall currentCall : currentObject.getTrackedCalls()) {
                                if (currentCall.getInvokeInstruction().equals(fullOperand)) {
                                    foundTrackedObjectCall(instance, currentCall, getStack(), objectCreationLocation);
                                }
                            }
                            // Check multiple calls necessary to make instance secure
                            for (MultipleTrackedCalls currentMultipleTrackedCall : currentObject.getMultipleTrackedCalls()) {
                                for (TrackedCall callFound : currentMultipleTrackedCall.getTrackedCalls()) {
                                    // TODO: This assumes that the "undo" tracked calls have the same invoke instruction
                                    if (callFound.getInvokeInstruction().equals(fullOperand)) {
                                        foundTrackedMultipleCall(instance, callFound, currentMultipleTrackedCall, getStack(), objectCreationLocation);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private TrackedObject getTrackedObjectForInitCall(String invokeInstruction) {
        // Finds the tracked object that listens to the invokeInstruction
        for (TrackedObject currentObject : trackedObjects) {
            for (String initInstruction : currentObject.getInitInstructions()) {
                if (initInstruction.equals(invokeInstruction)) {
                    return currentObject;
                }
            }
        }
        return null;
    }

    protected void foundObjectInitCall(TrackedObject trackedObject, TrackedObjectInstance instance) {
        for (TrackedCall trackedCall : trackedObject.getTrackedCalls()) {
            // If we report on missing calls or when the tracked call is not the last one,
            //  we flag this object until we find the call.
            if (trackedCall.getReportBugWhenNotLastCall() || trackedCall.getReportBugWhenNotCalled()) {
                instance.addBug(trackedCall.getBugType());
            }
        }
        // TODO: MultipleTrackedCalls report separate bugs depending on call reported?
        for (MultipleTrackedCalls multipleTrackedCalls : trackedObject.getMultipleTrackedCalls()) {
            for (TrackedCall trackedCall : multipleTrackedCalls.getTrackedCalls()) {
                if (trackedCall.getReportBugWhenNotLastCall() || trackedCall.getReportBugWhenNotCalled()) {
                    instance.addBug(trackedCall.getBugType());
                }
            }
        }
    }

    protected void foundTrackedObjectCall(TrackedObjectInstance instance, TrackedCall callFound, OpcodeStack stack, SourceLineAnnotation objectCreationLocation) {
        // Only one call necessary to make instance secure, so we immediately remove the bug if we find this call
        if (isStackParamsEqualToExpectedParams(callFound, stack)) {
//            instance.addTrackedCallFound(callFound);
            if (callFound.getReportBugWhenNotCalled()) {
                instance.removeBug(callFound.getBugType(), objectCreationLocation);
            } else if (callFound.getReportBugWhenCalled()) {
                instance.addBug(callFound.getBugType(), objectCreationLocation);
            }
        }
    }

    protected void foundTrackedMultipleCall(TrackedObjectInstance instance,
                                            TrackedCall callFound, MultipleTrackedCalls currentMultipleTrackedCall,
                                            OpcodeStack stack, SourceLineAnnotation objectCreationLocation) {


        if (isStackParamsEqualToExpectedParams(callFound, stack) && callFound.getReportBugWhenNotCalled()) {
            instance.addTrackedCallFound(callFound);
        } else {
            // Check if this is a call that is undoing the call found
            for (TrackedCall vulnerableCall : currentMultipleTrackedCall.getVulnerableTrackedCalls(callFound)) {
                if (isStackParamsEqualToExpectedParams(vulnerableCall, stack) && vulnerableCall.getReportBugWhenCalled()) {
                    instance.removeTrackedCallFound(callFound);
                    instance.addBug(callFound.getBugType());
                }
            }
        }

        // Check if all the tracked calls of multipleTrackedCall has been found, and remove bug
        if (instance.getTrackedCallsFound().containsAll(currentMultipleTrackedCall.getTrackedCalls())) {
            instance.removeBug(currentMultipleTrackedCall.getBugType(), objectCreationLocation);
            // TODO: Check if this breaks if the reportbugwhencalled calls interfere?
        }

        // Single calls are probably enough to make something insecure, so don't need to handle this for
        // MultipleTrackedCalls
    }

    protected List<TrackedObject> getTrackedObjects() {
        return trackedObjects;
    }

    protected void addTrackedObject(TrackedObject trackedObject) {
        trackedObjects.add(trackedObject);
    }

    private boolean isStackParamsEqualToExpectedParams(TrackedCall callFound, OpcodeStack stack) {
        boolean isAllEqual = true;
        for (int i = 0; i < callFound.getExpectedValues().size(); i++) {
            Object expectedValue = callFound.getExpectedValues().get(i);
            int parameterIndex = callFound.getParameterIndexes().get(i);
            // First assume stack parameter is a constant value
            OpcodeStack.Item item = stack.getStackItem(parameterIndex);
            Object value = getSpecialValue(item);
            if (value != null && value.equals(expectedValue)) {
                continue;
            } else if (value != null && !value.equals(expectedValue)) {
                isAllEqual = false;
                continue;
            }
            // Check if the stack value is an implementation of an interface we're looking for
            try {
                isAllEqual = InterfaceUtils.isSubtype(item.getJavaClass(), (String) expectedValue);

            } catch (Exception e) {
                // Stack item is neither constant value, nor a subtype of an interface, so we assume all params are not equal
                isAllEqual = false;
            }
        }
        return isAllEqual;
    }

    private Object getSpecialValue(OpcodeStack.Item item) {
        Object constantValue = item.getConstant();
        XField field = item.getXField();

        // Convert Boolean.somevalue to an integer
        if (constantValue instanceof Boolean) {
            Boolean booleanValue = (Boolean) constantValue;
            return booleanValue ? 1 : 0;
        }

        // Convert Boolean.FALSE AND Boolean.TRUE to integer value
        if (field != null && field.getSignature().equals("Ljava/lang/Boolean;")) {
            if (field.getName().equals("FALSE")) {
                return 0;
            }
            if (field.getName().equals("TRUE")) {
                return 1;
            }
        }
        return constantValue;
    }

    private String getFullOperand() {
        return getClassConstantOperand() + "." + getNameConstantOperand();
    }

    @Override
    public void report() {
        // We report bugs for missing calls, want this to be where the parser was used.
        for (TrackedObject trackedObject : getTrackedObjects()) {
            for (TrackedObjectInstance trackedInstance : trackedObject.getTrackedObjectInstances()) {
                for(SourceLineAnnotation line : trackedInstance.getVulnerableLines()) {
                    for (String reportedBugs : trackedInstance.getVulnerabilitiesForLine(line)) {
                        bugReporter.reportBug(new BugInstance(this, reportedBugs, Priorities.NORMAL_PRIORITY)
                            .addClass(trackedInstance.getInitJavaClass()).addMethod(trackedInstance.getInitMethodDescriptor())
                                .addSourceLine(line)
                                .addString(trackedInstance.getInitJavaClass().getClassName() + "." + trackedInstance.getInitMethodDescriptor() + "(...)"));
                        }
                }
                // TODO: Fix the method reported. Can maybe change messages.xml as well
            }
        }
        super.report();
    }
}
