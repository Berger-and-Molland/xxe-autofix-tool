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

import java.util.*;

public class MultipleTrackedCalls {
    private Map<TrackedCall, List<TrackedCall>> trackedCalls = new HashMap<>();

    private String bugType;

    public MultipleTrackedCalls(String bugType) {
        this.bugType = bugType;
    }

    public String getBugType() { return bugType; }

    public Set<TrackedCall> getTrackedCalls() {
        return this.trackedCalls.keySet();
    }

    public List<TrackedCall> getVulnerableTrackedCalls(TrackedCall trackedCall) {
        return this.trackedCalls.get(trackedCall);
    }

    public MultipleTrackedCalls addTrackedCall(TrackedCall safe, List<TrackedCall> vulnerable) {
        this.trackedCalls.put(safe, vulnerable);
        return this;
    }
}
