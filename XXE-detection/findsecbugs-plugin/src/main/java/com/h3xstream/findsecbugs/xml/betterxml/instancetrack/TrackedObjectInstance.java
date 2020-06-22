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

import java.util.*;

/**
 * This object is used in the AbstractInstanceTrackingDetector to store the location of object instances and
 * the bugs found for this particular object.
 */
public class TrackedObjectInstance {
    private JavaClass initJavaClass;
    private MethodDescriptor initMethodDescriptor;
    private List<String> bugsFound = new ArrayList<String>();
    private Set<TrackedCall> trackedCallsFound = new HashSet<>();
    private Map<SourceLineAnnotation, List<String>> foundTrackedReturnValues = new HashMap<>();
    private Map<SourceLineAnnotation, List<String>> vulnerableLines = new HashMap<>();

    public TrackedObjectInstance(JavaClass initJavaClass, MethodDescriptor initMethodDescriptor) {
        this.initJavaClass = initJavaClass;
        this.initMethodDescriptor = initMethodDescriptor;
    }

    public JavaClass getInitJavaClass() {
        return initJavaClass;
    }

    public MethodDescriptor getInitMethodDescriptor() {
        return initMethodDescriptor;
    }

    public void removeBug(String bugType) {
        bugsFound.remove(bugType);
    }

    public void removeBug(String bugType, SourceLineAnnotation objectCreationLocation) {
        bugsFound.remove(bugType);
        // Update that previous instances which were marked as vulnerable are now safe
        this.foundTrackedReturnValues.put(objectCreationLocation, getCopyOfBugs());
    }
    public void addBug(String bugType) {
        // Check if the bug is already in the list to avoid duplicated bugs.
        if (!bugsFound.contains(bugType)) {
            bugsFound.add(bugType);
        }
    }

    public void addBug(String bugType, SourceLineAnnotation objectCreationLocation) {
        // Check if the bug is already in the list to avoid duplicated bugs.
        if (!bugsFound.contains(bugType)) {
            bugsFound.add(bugType);
        }
        // Update that previous instances now have gotten more bugs
        this.foundTrackedReturnValues.put(objectCreationLocation, getCopyOfBugs());
    }

    public Set<TrackedCall> getTrackedCallsFound() {
        return trackedCallsFound;
    }

    public void addTrackedCallFound(TrackedCall trackedCall) {
        // TODO: Check if already exists
        this.trackedCallsFound.add(trackedCall);
    }

    public void removeTrackedCallFound(TrackedCall trackedCall) {
        if (this.trackedCallsFound.contains(trackedCall)) {
            this.trackedCallsFound.remove(trackedCall);
        }
    }

    public void addFoundTrackedReturnValue(SourceLineAnnotation initLocation) {
        this.foundTrackedReturnValues.put(initLocation, getCopyOfBugs());
    }

    public boolean getFoundTrackedReturnValueLine(SourceLineAnnotation initLocation) {
        return this.foundTrackedReturnValues.containsKey(initLocation);
    }

    public Set<SourceLineAnnotation> getTrackedReturnValueSourceLines() {
        return this.foundTrackedReturnValues.keySet();
    }

    public void addVulnerableLine(SourceLineAnnotation objectCreationLocation, SourceLineAnnotation vulnerableLine) {
        if(this.foundTrackedReturnValues.get(objectCreationLocation).size() > 0) {
            List<String> currentBugs = new ArrayList<>();
            currentBugs.addAll(this.foundTrackedReturnValues.get(objectCreationLocation));
            vulnerableLines.put(vulnerableLine, currentBugs);
        }
    }

    public Set<SourceLineAnnotation> getVulnerableLines() {
        return this.vulnerableLines.keySet();
    }

    public List<String> getVulnerabilitiesForLine(SourceLineAnnotation line) {
        return this.vulnerableLines.get(line);
    }

    private List<String> getCopyOfBugs() {
        return  new ArrayList<>(bugsFound);
    }

}
