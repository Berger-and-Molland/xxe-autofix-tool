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
package com.h3xstream.findsecbugs.xml.betterxml.instancetrack;

import edu.umd.cs.findbugs.SourceLineAnnotation;
import edu.umd.cs.findbugs.classfile.MethodDescriptor;
import org.apache.bcel.classfile.JavaClass;

import java.util.ArrayList;
import java.util.List;

/**
 * This object is used to provide the AbstractInstanceTrackingDetector class with the information required
 * to track method calls on object instances.
 *
 * It stores the Invoke instructions used to initialize this object and the calls we want to track.
 * The detector will also store the instances found during code analysis in this object.
 */
public class TrackedObject {
    private List<TrackedReturnValue> trackedReturnValues = new ArrayList<>();

    public List<TrackedReturnValue> getTrackedReturnValues() {
        return trackedReturnValues;
    }

    public TrackedObject(String initInstruction) {
        initInstructions.add(initInstruction);
    }

    private List<String> initInstructions = new ArrayList<String>();
    public List<String> getInitInstructions() { return initInstructions; }
    public TrackedObject addInitInstruction(String initInstruction) {
        initInstructions.add(initInstruction);

        // We return this object to make daisy chaining calls possible.
        return this;
    }

    private List<TrackedCall> trackedCalls = new ArrayList<TrackedCall>();
    public List<TrackedCall> getTrackedCalls() { return trackedCalls; }
    public TrackedObject addTrackedCallForObject(TrackedCall newCall) {
        trackedCalls.add(newCall);

        // We return this object to make daisy chaining calls possible.
        return this;
    }

    private List<TrackedObjectInstance> trackedObjectInstances = new ArrayList<TrackedObjectInstance>();
    public List<TrackedObjectInstance> getTrackedObjectInstances() { return trackedObjectInstances; }
    public TrackedObjectInstance addTrackedObjectInstance(JavaClass initJavaClass, MethodDescriptor initMethodDescriptor) {
        TrackedObjectInstance instance = new TrackedObjectInstance(initJavaClass, initMethodDescriptor);
        trackedObjectInstances.add(instance);

        // We return the added object instance to make daisy chaining calls possible.
        return instance;
    }


    private List<MultipleTrackedCalls> multipleTrackedCalls = new ArrayList<>();
    public List<MultipleTrackedCalls> getMultipleTrackedCalls() {
        return multipleTrackedCalls;
    }
    public TrackedObject addMultipleTrackedCallsForObject(MultipleTrackedCalls multipleTrackedCalls) {
        this.multipleTrackedCalls.add(multipleTrackedCalls);
        return this;
    }

    public TrackedObject addTrackedReturnValue(TrackedReturnValue trackedReturnValue) {
        if (!trackedReturnValues.contains(trackedReturnValue)) {
            trackedReturnValues.add(trackedReturnValue);
        }
        return this;
    }
}
