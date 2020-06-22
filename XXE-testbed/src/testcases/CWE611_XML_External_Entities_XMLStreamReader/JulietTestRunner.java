package testcases.CWE611_XML_External_Entities_XMLStreamReader;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class JulietTestRunner {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(
    	Test_CWE611_XML_External_Entities__XMLStreamReader_01.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_02.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_03.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_04.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_05.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_06.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_07.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_08.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_09.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_10.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_11.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_13.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_14.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_15.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_16.class,
    	Test_CWE611_XML_External_Entities__XMLStreamReader_17.class
    	
    );
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
  }
}
