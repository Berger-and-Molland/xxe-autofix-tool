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

// TODO: Make abstract class for this
public class BetterTransformerFactoryDetector extends InstanceTrackDetector {
    private static final String XXE_DTD_TRANSFORM_FACTORY_TYPE = "XXE_DTD_TRANSFORM_FACTORY";
    private static final String XXE_XSLT_TRANSFORM_FACTORY_TYPE = "XXE_XSLT_TRANSFORM_FACTORY";

    public BetterTransformerFactoryDetector(BugReporter bugReporter) {
        super(bugReporter);
        addTrackedObject(new TrackedObject("javax/xml/transform/TransformerFactory.newInstance")
                .addInitInstruction("javax/xml/transform/sax/SAXTransformerFactory.newInstance") // Handles SAXTransformer factory
                .addTrackedReturnValue(new TrackedReturnValue("javax/xml/transform/TransformerFactory",
                        "Ljavax/xml/transform/TransformerFactory;", "javax/xml/transform/TransformerFactory.newTransformer", false))
                .addTrackedReturnValue(new TrackedReturnValue("javax/xml/transform/TransformerFactory",
                        "Ljavax/xml/transform/Transformer;", "javax/xml/transform/Transformer.transform", true))
                .addTrackedCallForObject(new TrackedCall("javax/xml/transform/TransformerFactory.setFeature",
                        Arrays.asList(1, XMLConstants.FEATURE_SECURE_PROCESSING),
                        Arrays.asList(0, 1),
                        XXE_DTD_TRANSFORM_FACTORY_TYPE)
                        .reportBugWhenNotCalled(true)
                )
                .addTrackedCallForObject(new TrackedCall("javax/xml/transform/TransformerFactory.setFeature",
                        Arrays.asList(1, XMLConstants.FEATURE_SECURE_PROCESSING),
                        Arrays.asList(0, 1),
                        XXE_XSLT_TRANSFORM_FACTORY_TYPE)
                        .reportBugWhenNotCalled(true)
                )
                .addTrackedCallForObject(new TrackedCall("javax/xml/transform/TransformerFactory.setAttribute",
                        Arrays.asList("", XMLConstants.ACCESS_EXTERNAL_DTD), // Empty string on stack?
                        Arrays.asList(0, 1),
                        XXE_DTD_TRANSFORM_FACTORY_TYPE)
                        .reportBugWhenNotCalled(true)
                )
                .addTrackedCallForObject(new TrackedCall("javax/xml/transform/TransformerFactory.setAttribute",
                        Arrays.asList("", XMLConstants.ACCESS_EXTERNAL_STYLESHEET), // Empty string on stack?
                        Arrays.asList(0, 1),
                        XXE_XSLT_TRANSFORM_FACTORY_TYPE)
                        .reportBugWhenNotCalled(true)
                )
                // Add bug if these insecure methods are called
                .addTrackedCallForObject(new TrackedCall("javax/xml/transform/TransformerFactory.setFeature",
                        Arrays.asList(0, XMLConstants.FEATURE_SECURE_PROCESSING),
                        Arrays.asList(0, 1),
                        XXE_XSLT_TRANSFORM_FACTORY_TYPE)
                        .reportBugWhenCalled(true)
                )
                .addTrackedCallForObject(new TrackedCall("javax/xml/transform/TransformerFactory.setFeature",
                        Arrays.asList(0, XMLConstants.FEATURE_SECURE_PROCESSING),
                        Arrays.asList(0, 1),
                        XXE_DTD_TRANSFORM_FACTORY_TYPE)
                        .reportBugWhenCalled(true)
                )
                .addTrackedCallForObject(new TrackedCall("javax/xml/transform/TransformerFactory.setAttribute",
                        Arrays.asList("all", XMLConstants.ACCESS_EXTERNAL_DTD), // Empty string on stack?
                        Arrays.asList(0, 1),
                        XXE_DTD_TRANSFORM_FACTORY_TYPE)
                        .reportBugWhenCalled(true)
                )
                .addTrackedCallForObject(new TrackedCall("javax/xml/transform/TransformerFactory.setAttribute",
                        Arrays.asList("all", XMLConstants.ACCESS_EXTERNAL_STYLESHEET), // Empty string on stack?
                        Arrays.asList(0, 1),
                        XXE_XSLT_TRANSFORM_FACTORY_TYPE)
                        .reportBugWhenCalled(true)
                )
        );
    }
}