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

import com.h3xstream.findsecbugs.xml.betterxml.instancetrack.MultipleTrackedCalls;
import com.h3xstream.findsecbugs.xml.betterxml.instancetrack.TrackedCall;
import com.h3xstream.findsecbugs.xml.betterxml.instancetrack.TrackedObject;
import com.h3xstream.findsecbugs.xml.betterxml.instancetrack.TrackedReturnValue;
import edu.umd.cs.findbugs.BugReporter;

import javax.xml.XMLConstants;
import java.util.Arrays;

public class BetterSAXParserDetector extends InstanceTrackDetector {
    private static final String XXE_SAX_PARSER_TYPE = "XXE_SAXPARSER";

    public BetterSAXParserDetector(BugReporter bugReporter) {
        super(bugReporter);
        addTrackedObject(new TrackedObject("javax/xml/parsers/SAXParserFactory.newInstance")
                .addTrackedReturnValue(new TrackedReturnValue("javax/xml/parsers/SAXParserFactory",
                        "Ljavax/xml/parsers/SAXParserFactory;", "javax/xml/parsers/SAXParserFactory.newSAXParser", false))
                //.addTrackedReturnValue(new TrackedReturnValue("")) // Using custom content handler
                .addTrackedReturnValue(new TrackedReturnValue("javax/xml/parsers/SAXParserFactory",
                        "Ljavax/xml/parsers/SAXParser;", "javax/xml/parsers/SAXParser.parse", true))
                // Below two return values are for tracking the use of custom handlers since these use XMLReader
                .addTrackedReturnValue(new TrackedReturnValue("javax/xml/parsers/SAXParserFactory",
                        "Ljavax/xml/parsers/SAXParser;", "javax/xml/parsers/SAXParser.getXMLReader", false))
                .addTrackedReturnValue(new TrackedReturnValue("javax/xml/parsers/SAXParser",
                        "Lorg/xml/sax/XMLReader;", "org/xml/sax/XMLReader.parse", true))
                .addTrackedCallForObject(new TrackedCall("javax/xml/parsers/SAXParserFactory.setFeature",
                        Arrays.asList(1, "http://apache.org/xml/features/disallow-doctype-decl"),
                        Arrays.asList(0, 1),
                        XXE_SAX_PARSER_TYPE)
                        .reportBugWhenNotCalled(true)
                )
                .addTrackedCallForObject(new TrackedCall("javax/xml/parsers/SAXParserFactory.setFeature",
                        Arrays.asList(1, XMLConstants.FEATURE_SECURE_PROCESSING),
                        Arrays.asList(0, 1),
                        XXE_SAX_PARSER_TYPE)
                        .reportBugWhenNotCalled(true)
                )
                // For custom handlers
                .addTrackedCallForObject(new TrackedCall("org/xml/sax/XMLReader.setEntityResolver",
                        Arrays.asList("org.xml.sax.EntityResolver"),
                        Arrays.asList(0),
                        XXE_SAX_PARSER_TYPE)
                        .reportBugWhenNotCalled(true)
                )
                .addMultipleTrackedCallsForObject(
                        new MultipleTrackedCalls(XXE_SAX_PARSER_TYPE)
                        .addTrackedCall(new TrackedCall("javax/xml/parsers/SAXParserFactory.setFeature",
                                Arrays.asList(0, "http://xml.org/sax/features/external-general-entities"),
                                Arrays.asList(0, 1),
                                XXE_SAX_PARSER_TYPE)
                                .reportBugWhenNotCalled(true),
                                Arrays.asList(new TrackedCall("javax/xml/parsers/SAXParserFactory.setFeature",
                                        Arrays.asList(1, "http://xml.org/sax/features/external-general-entities"),
                                        Arrays.asList(0, 1),
                                        XXE_SAX_PARSER_TYPE)
                                        .reportBugWhenCalled(true)))
                        .addTrackedCall(new TrackedCall(
                                "javax/xml/parsers/SAXParserFactory.setFeature",
                                Arrays.asList(0, "http://xml.org/sax/features/external-parameter-entities"),
                                Arrays.asList(0, 1),
                                XXE_SAX_PARSER_TYPE)
                                        .reportBugWhenNotCalled(true),
                                Arrays.asList(new TrackedCall("javax/xml/parsers/SAXParserFactory.setFeature",
                                        Arrays.asList(1, "http://xml.org/sax/features/external-parameter-entities"),
                                        Arrays.asList(0, 1),
                                        XXE_SAX_PARSER_TYPE)
                                        .reportBugWhenCalled(true)))
                        .addTrackedCall(new TrackedCall(
                                        "javax/xml/parsers/SAXParserFactory.setFeature",
                                        Arrays.asList(0, "http://apache.org/xml/features/nonvalidating/load-external-dtd"),
                                        Arrays.asList(0, 1),
                                        XXE_SAX_PARSER_TYPE)
                                        .reportBugWhenNotCalled(true),
                                Arrays.asList(new TrackedCall("javax/xml/parsers/SAXParserFactory.setFeature",
                                        Arrays.asList(1, "http://apache.org/xml/features/nonvalidating/load-external-dtd"),
                                        Arrays.asList(0, 1),
                                        XXE_SAX_PARSER_TYPE)
                                        .reportBugWhenCalled(true)
                                ))
                        .addTrackedCall(new TrackedCall(
                                "javax/xml/parsers/SAXParserFactory.setXIncludeAware",
                                Arrays.asList(0),
                                Arrays.asList(0),
                                XXE_SAX_PARSER_TYPE)
                                .reportBugWhenNotCalled(true),
                                Arrays.asList(new TrackedCall(
                                        "javax/xml/parsers/SAXParserFactory.setXIncludeAware",
                                        Arrays.asList(1),
                                        Arrays.asList(0),
                                        XXE_SAX_PARSER_TYPE)
                                        .reportBugWhenCalled(true)))
                        )
                // Add bug if these insecure methods are called
                .addTrackedCallForObject(new TrackedCall("javax/xml/parsers/SAXParserFactory.setFeature",
                        Arrays.asList(0, XMLConstants.FEATURE_SECURE_PROCESSING),
                        Arrays.asList(0, 1),
                        XXE_SAX_PARSER_TYPE)
                        .reportBugWhenCalled(true)
                )
                .addTrackedCallForObject(new TrackedCall("javax/xml/parsers/SAXParserFactory.setFeature",
                        Arrays.asList(0, "http://apache.org/xml/features/disallow-doctype-decl"),
                        Arrays.asList(0, 1),
                        XXE_SAX_PARSER_TYPE)
                        .reportBugWhenCalled(true)
                )
        );
    }
}
