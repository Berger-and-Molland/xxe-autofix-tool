package testcases.CWE611_XML_External_Entities_XMLReader;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class JulietTestRunner {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(
    	Test_CWE611_XML_External_Entities__XMLReader_01.class,
    	Test_CWE611_XML_External_Entities__XMLReader_02.class,
    	Test_CWE611_XML_External_Entities__XMLReader_03.class,
    	Test_CWE611_XML_External_Entities__XMLReader_04.class,
    	Test_CWE611_XML_External_Entities__XMLReader_05.class,
    	Test_CWE611_XML_External_Entities__XMLReader_06.class,
    	Test_CWE611_XML_External_Entities__XMLReader_07.class,
    	Test_CWE611_XML_External_Entities__XMLReader_08.class,
    	Test_CWE611_XML_External_Entities__XMLReader_09.class,
    	Test_CWE611_XML_External_Entities__XMLReader_10.class,
    	Test_CWE611_XML_External_Entities__XMLReader_11.class,
    	Test_CWE611_XML_External_Entities__XMLReader_13.class,
    	Test_CWE611_XML_External_Entities__XMLReader_14.class,
    	Test_CWE611_XML_External_Entities__XMLReader_15.class,
    	Test_CWE611_XML_External_Entities__XMLReader_16.class,
    	Test_CWE611_XML_External_Entities__XMLReader_17.class
    );
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
  }
}
