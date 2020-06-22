package testcases.CWE611_XML_External_Entities_DocumentBuilder;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class JulietTestRunner {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(
    	Test_CWE611_XML_External_Entities__DocumentBuilder_01.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_02.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_03.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_04.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_05.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_06.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_07.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_08.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_09.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_10.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_11.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_13.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_14.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_15.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_16.class,
    	Test_CWE611_XML_External_Entities__DocumentBuilder_17.class
    );
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
  }
}
