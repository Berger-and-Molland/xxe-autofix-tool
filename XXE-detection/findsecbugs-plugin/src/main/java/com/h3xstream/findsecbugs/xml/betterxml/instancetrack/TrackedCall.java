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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This object is used in the AbstractInstanceTrackingDetector to track specific method calls.
 *
 * It stores the textual representation of the Invoke instruction for the call, its expected value
 * and the reporting behavior of the detector when this call is encountered.
 */
public class TrackedCall {
    private List<Object> expectedValues;
    private List<Integer> parameterIndexes;
    private String invokeInstruction;
    private String bugType;
    private boolean reportBugWhenNotLastCall;
    private boolean reportBugWhenCalled;
    private boolean reportBugWhenNotCalled;

    public TrackedCall(String invokeInstruction, List<Object> expectedValues, List<Integer> parameterIndexes, String bugType) {
        this.invokeInstruction = invokeInstruction;
        this.expectedValues = expectedValues;
        this.parameterIndexes = parameterIndexes;
        this.bugType = bugType;
    }

    public String getInvokeInstruction() { return invokeInstruction; }

    public List<Object> getExpectedValues() { return expectedValues; }

    public List<Integer> getParameterIndexes() {
        return parameterIndexes;
    }

    public String getBugType() { return bugType; }

    public boolean getReportBugWhenNotLastCall() { return reportBugWhenNotLastCall; }

    public TrackedCall reportBugWhenNotLastCall(boolean shouldReport) {
        reportBugWhenNotLastCall = shouldReport;
        return this;
    }

    public boolean getReportBugWhenNotCalled() {
        return this.reportBugWhenNotCalled;
    }

    public TrackedCall reportBugWhenNotCalled(boolean shouldReport) {
        reportBugWhenNotCalled = shouldReport;
        return this;
    }

    public boolean getReportBugWhenCalled() { return reportBugWhenCalled; }

    public TrackedCall reportBugWhenCalled(boolean shouldReport) {
        reportBugWhenCalled = shouldReport;
        return this;
    }
}
