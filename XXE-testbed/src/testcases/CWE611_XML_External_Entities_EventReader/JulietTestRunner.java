package testcases.CWE611_XML_External_Entities_EventReader;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class JulietTestRunner {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(
    	Test_CWE611_XML_External_Entities__XMLEventReader_01.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_02.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_03.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_04.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_05.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_06.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_07.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_08.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_09.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_10.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_11.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_13.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_14.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_15.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_16.class,
    	Test_CWE611_XML_External_Entities__XMLEventReader_17.class
    	
    );
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
  }
}
