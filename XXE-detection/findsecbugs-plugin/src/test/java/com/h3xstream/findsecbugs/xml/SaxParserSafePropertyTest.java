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
package com.h3xstream.findsecbugs.xml;

import com.h3xstream.findbugs.test.BaseDetectorTest;
import com.h3xstream.findbugs.test.EasyBugReporter;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

public class SaxParserSafePropertyTest extends BaseDetectorTest {

    @Test
    public void detectUnsafeNoSpecialSettings() throws Exception {
        //Locate test code
        String[] files = {
                getClassFilePath("testcode/xxe/SaxParserSafeProperty")
        };

        //Run the analysis
        EasyBugReporter reporter = spy(new SecurityReporter());
        analyze(files, reporter);

        //Assertions

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty").inMethod("unsafeNoSpecialSettings").atLine(26)
                        .build()
        );


        //Should not trigger the other XXE patterns
        Mockito.verify(reporter, Mockito.never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_XMLREADER")
                        .inClass("SaxParserSafeProperty")
                        .build()
        );
        Mockito.verify(reporter, Mockito.never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_DOCUMENT")
                        .inClass("SaxParserSafeProperty")
                        .build()
        );
    }

    @Test
    public void avoidFalsePositiveOnSafeCases() throws Exception {
        //Locate test code
        String[] files = {
                getClassFilePath("testcode/xxe/SaxParserSafeProperty")
        };

        //Run the analysis
        EasyBugReporter reporter = spy(new SecurityReporter());
        analyze(files, reporter);

        //Assertions

        verify(reporter, never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty").inMethod("safeIgnoredDtdDisable")
                        .build()
        );

        verify(reporter, never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty").inMethod("safeSecureProcessing")
                        .build()
        );

        //Assertions
        verify(reporter,never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty").inMethod("safeManualConfiguration")
                        .build()
        );
    }

    @Test
    public void detectUnsafePartialConfiguration() throws Exception {
        //Locate test code
        String[] files = {
                getClassFilePath("testcode/xxe/SaxParserSafeProperty")
        };

        //Run the analysis
        EasyBugReporter reporter = spy(new SecurityReporter());
        analyze(files, reporter);

        //Assertions

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty").inMethod("unsafeManualConfig1").atLine(65)
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty").inMethod("unsafeManualConfig2").atLine(76)
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty").inMethod("unsafeManualConfig3").atLine(87)
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty").inMethod("unsafeManualConfig4").atLine(98)
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty").inMethod("unsafeManualConfig5").atLine(109)
                        .build()
        );


    }


    @Test
    public void testCustomMadeCases() throws Exception {
        String[] files = {
                getClassFilePath("testcode/xxe/SaxParserSafeProperty1")
        };

        //Run the analysis
        EasyBugReporter reporter = spy(new SecurityReporter());
        analyze(files, reporter);

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case1")
                        .build()
        );

        verify(reporter).doReportBug(
                        bugDefinition()
                                .bugType("XXE_SAXPARSER")
                                .inClass("SaxParserSafeProperty1").inMethod("case2")
                                .build()
                );

        verify(reporter).doReportBug(
                        bugDefinition()
                                .bugType("XXE_SAXPARSER")
                                .inClass("SaxParserSafeProperty1").inMethod("case3")
                                .build()
                );

        verify(reporter).doReportBug(
                        bugDefinition()
                                .bugType("XXE_SAXPARSER")
                                .inClass("SaxParserSafeProperty1").inMethod("case4")
                                .build()
                );

        verify(reporter).doReportBug(
                        bugDefinition()
                                .bugType("XXE_SAXPARSER")
                                .inClass("SaxParserSafeProperty1").inMethod("case5")
                                .build()
                );

        verify(reporter).doReportBug(
                        bugDefinition()
                                .bugType("XXE_SAXPARSER")
                                .inClass("SaxParserSafeProperty1").inMethod("case6")
                                .build()
                );

        verify(reporter, never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case1_safe")
                        .build()
        );

        verify(reporter, never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case2_safe")
                        .build()
        );

        verify(reporter, never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case3_safe")
                        .build()
        );

        verify(reporter, never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case5_safe")
                        .build()
        );

        // Case 7
        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case7").atLine(101)
                        .build()
        );

        verify(reporter, never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case7").atLine(105)
                        .build()
        );

        // Case 8
        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case8").atLine(113)
                        .build()
        );

        verify(reporter, never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case8").atLine(117)
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case8").atLine(121)
                        .build()
        );

        // Case 9
        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case9").atLine(128)
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case9").atLine(129)
                        .build()
        );

        verify(reporter, never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case9").atLine(134)
                        .build()
        );
        verify(reporter, never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case9").atLine(135)
                        .build()
        );

        // case 10
        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case10").atLine(142)
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case10").atLine(143)
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case10").atLine(147)
                        .build()
        );

        verify(reporter, never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case10").atLine(148)
                        .build()
        );

        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case10").atLine(152)
                        .build()
        );


        verify(reporter, never()).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case10").atLine(153)
                        .build()
        );

//        // case 11
        verify(reporter).doReportBug(
                bugDefinition()
                        .bugType("XXE_SAXPARSER")
                        .inClass("SaxParserSafeProperty1").inMethod("case11").atLine(168)
                        .build()
        );

    }

}
