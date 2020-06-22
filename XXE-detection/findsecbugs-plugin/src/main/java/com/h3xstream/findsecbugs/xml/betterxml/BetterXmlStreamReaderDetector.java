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
import edu.umd.cs.findbugs.ba.*;
import edu.umd.cs.findbugs.ba.constant.ConstantDataflow;
import edu.umd.cs.findbugs.ba.constant.ConstantFrame;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;
import edu.umd.cs.findbugs.classfile.*;
import edu.umd.cs.findbugs.visitclass.DismantleBytecode;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.*;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BetterXmlStreamReaderDetector extends InstanceTrackDetector {
    private static final String XXE_XMLSTREAMREADER_TYPE = "XXE_XMLSTREAMREADER";

    public BetterXmlStreamReaderDetector(BugReporter bugReporter) {
        super(bugReporter);
        // Handle createXMLStreamReader
        addTrackedObject(new TrackedObject("javax/xml/stream/XMLInputFactory.newFactory")
                .addInitInstruction("javax/xml/stream/XMLInputFactory.newInstance")
                .addTrackedReturnValue(new TrackedReturnValue("javax/xml/stream/XMLInputFactory",
                        "Ljavax/xml/stream/XMLInputFactory;", "javax/xml/stream/XMLInputFactory.createXMLStreamReader", true))
                .addTrackedReturnValue(new TrackedReturnValue("javax/xml/stream/XMLInputFactory",
                        "Ljavax/xml/stream/XMLInputFactory;", "javax/xml/stream/XMLInputFactory.createXMLEventReader", true))
                // createFilteredReader
                .addTrackedReturnValue(new TrackedReturnValue("javax/xml/stream/XMLInputFactory",
                        "Ljavax/xml/stream/XMLInputFactory;", "javax/xml/stream/XMLInputFactory.createFilteredReader", true))
                .addTrackedCallForObject(new TrackedCall("javax/xml/stream/XMLInputFactory.setProperty",
                        Arrays.asList(0, XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES),
                        Arrays.asList(0, 1),
                        XXE_XMLSTREAMREADER_TYPE)
                        .reportBugWhenNotCalled(true)
                )
                .addTrackedCallForObject(new TrackedCall("javax/xml/stream/XMLInputFactory.setProperty",
                        Arrays.asList(0, XMLInputFactory.SUPPORT_DTD),
                        Arrays.asList(0, 1),
                        XXE_XMLSTREAMREADER_TYPE)
                        .reportBugWhenNotCalled(true)
                )
                // Add bug if these insecure methods are called
                .addTrackedCallForObject(new TrackedCall("javax/xml/stream/XMLInputFactory.setProperty",
                        Arrays.asList(1, XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES),
                        Arrays.asList(0, 1),
                        XXE_XMLSTREAMREADER_TYPE)
                        .reportBugWhenCalled(true)
                )
                .addTrackedCallForObject(new TrackedCall("javax/xml/stream/XMLInputFactory.setProperty",
                        Arrays.asList(1, XMLInputFactory.SUPPORT_DTD),
                        Arrays.asList(0, 1),
                        XXE_XMLSTREAMREADER_TYPE)
                        .reportBugWhenCalled(true)
                )
        );
    }
}