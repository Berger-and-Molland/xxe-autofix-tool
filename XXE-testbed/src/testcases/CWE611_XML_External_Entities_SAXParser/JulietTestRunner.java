package testcases.CWE611_XML_External_Entities_SAXParser;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class JulietTestRunner {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(
    	Test_CWE611_XML_External_Entities__SAXParser_01.class,
    	Test_CWE611_XML_External_Entities__SAXParser_02.class,
    	Test_CWE611_XML_External_Entities__SAXParser_03.class,
    	Test_CWE611_XML_External_Entities__SAXParser_04.class,
    	Test_CWE611_XML_External_Entities__SAXParser_05.class,
    	Test_CWE611_XML_External_Entities__SAXParser_06.class,
    	Test_CWE611_XML_External_Entities__SAXParser_07.class,
    	Test_CWE611_XML_External_Entities__SAXParser_08.class,
    	Test_CWE611_XML_External_Entities__SAXParser_09.class,
    	Test_CWE611_XML_External_Entities__SAXParser_10.class,
    	Test_CWE611_XML_External_Entities__SAXParser_11.class,
    	Test_CWE611_XML_External_Entities__SAXParser_13.class,
    	Test_CWE611_XML_External_Entities__SAXParser_14.class,
    	Test_CWE611_XML_External_Entities__SAXParser_15.class,
    	Test_CWE611_XML_External_Entities__SAXParser_16.class,
    	Test_CWE611_XML_External_Entities__SAXParser_17.class
    );
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
  }
}
