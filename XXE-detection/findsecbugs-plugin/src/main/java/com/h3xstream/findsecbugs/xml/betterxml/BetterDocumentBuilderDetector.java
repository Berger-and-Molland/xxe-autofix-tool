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
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.ClassContext;
import edu.umd.cs.findbugs.ba.XField;
import edu.umd.cs.findbugs.ba.XMethod;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import edu.umd.cs.findbugs.classfile.*;
import edu.umd.cs.findbugs.visitclass.DismantleBytecode;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.*;

import javax.xml.XMLConstants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BetterDocumentBuilderDetector extends InstanceTrackDetector {
    private static final String XXE_DOCUMENT_TYPE = "XXE_DOCUMENT";

    public BetterDocumentBuilderDetector(BugReporter bugReporter) {
        super(bugReporter);
        addTrackedObject(new TrackedObject("javax/xml/parsers/DocumentBuilderFactory.newInstance")
                .addTrackedReturnValue(new TrackedReturnValue("javax/xml/parsers/DocumentBuilderFactory",
                        "Ljavax/xml/parsers/DocumentBuilderFactory;", "javax/xml/parsers/DocumentBuilderFactory.newDocumentBuilder", false))
                .addTrackedReturnValue(new TrackedReturnValue("javax/xml/parsers/DocumentBuilderFactory",
                        "Ljavax/xml/parsers/DocumentBuilder;", "javax/xml/parsers/DocumentBuilder.parse", true))
                .addTrackedCallForObject(new TrackedCall("javax/xml/parsers/DocumentBuilderFactory.setFeature",
                        Arrays.asList(1, XMLConstants.FEATURE_SECURE_PROCESSING),
                        Arrays.asList(0, 1),
                        XXE_DOCUMENT_TYPE)
                        .reportBugWhenNotCalled(true)
                )
                .addTrackedCallForObject(new TrackedCall("javax/xml/parsers/DocumentBuilderFactory.setFeature",
                        Arrays.asList(1, "http://apache.org/xml/features/disallow-doctype-decl"),
                        Arrays.asList(0, 1),
                        XXE_DOCUMENT_TYPE)
                        .reportBugWhenNotCalled(true)
                )
                .addTrackedCallForObject(new TrackedCall("javax/xml/parsers/DocumentBuilder.setEntityResolver",
                        Arrays.asList("org.xml.sax.EntityResolver"),
                        Arrays.asList(0),
                        XXE_DOCUMENT_TYPE)
                        .reportBugWhenNotCalled(true))
                .addMultipleTrackedCallsForObject(
                        new MultipleTrackedCalls(XXE_DOCUMENT_TYPE)
                        .addTrackedCall(
                                new TrackedCall("javax/xml/parsers/DocumentBuilderFactory.setFeature",
                                        Arrays.asList(0, "http://xml.org/sax/features/external-general-entities"),
                                        Arrays.asList(0, 1),
                                        XXE_DOCUMENT_TYPE)
                                        .reportBugWhenNotCalled(true),
                                Arrays.asList(new TrackedCall("javax/xml/parsers/DocumentBuilderFactory.setFeature",
                                        Arrays.asList(1, "http://xml.org/sax/features/external-general-entities"),
                                        Arrays.asList(0, 1),
                                        XXE_DOCUMENT_TYPE)
                                        .reportBugWhenCalled(true))
                        )
                        .addTrackedCall(new TrackedCall(
                                "javax/xml/parsers/DocumentBuilderFactory.setFeature",
                                Arrays.asList(0, "http://xml.org/sax/features/external-parameter-entities"),
                                Arrays.asList(0, 1),
                                XXE_DOCUMENT_TYPE)
                                .reportBugWhenNotCalled(true),
                                Arrays.asList(new TrackedCall(
                                        "javax/xml/parsers/DocumentBuilderFactory.setFeature",
                                        Arrays.asList(1, "http://xml.org/sax/features/external-parameter-entities"),
                                        Arrays.asList(0, 1),
                                        XXE_DOCUMENT_TYPE)
                                        .reportBugWhenCalled(true)))
                        .addTrackedCall(new TrackedCall(
                                "javax/xml/parsers/DocumentBuilderFactory.setFeature",
                                Arrays.asList(0, "http://apache.org/xml/features/nonvalidating/load-external-dtd"),
                                Arrays.asList(0, 1),
                                XXE_DOCUMENT_TYPE)
                                .reportBugWhenNotCalled(true),
                                Arrays.asList(new TrackedCall("javax/xml/parsers/DocumentBuilderFactory.setFeature",
                                        Arrays.asList(1, "http://apache.org/xml/features/nonvalidating/load-external-dtd"),
                                        Arrays.asList(0, 1),
                                        XXE_DOCUMENT_TYPE)
                                        .reportBugWhenCalled(true)
                        ))
                        .addTrackedCall(new TrackedCall(
                                "javax/xml/parsers/DocumentBuilderFactory.setXIncludeAware",
                                Arrays.asList(0),
                                Arrays.asList(0),
                                XXE_DOCUMENT_TYPE)
                                .reportBugWhenNotCalled(true),
                                Arrays.asList(new TrackedCall(
                                        "javax/xml/parsers/DocumentBuilderFactory.setXIncludeAware",
                                        Arrays.asList(1),
                                        Arrays.asList(0),
                                        XXE_DOCUMENT_TYPE)
                                        .reportBugWhenCalled(true)))
                        .addTrackedCall(new TrackedCall(
                                "javax/xml/parsers/DocumentBuilderFactory.setExpandEntityReferences",
                                Arrays.asList(0),
                                Arrays.asList(0),
                                XXE_DOCUMENT_TYPE)
                                .reportBugWhenNotCalled(true),
                                Arrays.asList(new TrackedCall(
                                        "javax/xml/parsers/DocumentBuilderFactory.setExpandEntityReferences",
                                        Arrays.asList(1),
                                        Arrays.asList(0),
                                        XXE_DOCUMENT_TYPE)
                                        .reportBugWhenCalled(true)))
                )
                // Add bug if these insecure methods are called
                .addTrackedCallForObject(new TrackedCall("javax/xml/parsers/DocumentBuilderFactory.setFeature",
                        Arrays.asList(0, XMLConstants.FEATURE_SECURE_PROCESSING),
                        Arrays.asList(0, 1),
                        XXE_DOCUMENT_TYPE)
                        .reportBugWhenCalled(true)
                )
                .addTrackedCallForObject(new TrackedCall("javax/xml/parsers/DocumentBuilderFactory.setFeature",
                        Arrays.asList(0, "http://apache.org/xml/features/disallow-doctype-decl"),
                        Arrays.asList(0, 1),
                        XXE_DOCUMENT_TYPE)
                        .reportBugWhenCalled(true)
                )
        );
    }

}