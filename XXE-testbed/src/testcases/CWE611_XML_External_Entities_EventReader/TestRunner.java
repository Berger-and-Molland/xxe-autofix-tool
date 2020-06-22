package testcases.CWE611_XML_External_Entities_EventReader;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(
    	Test_XML_EventReader_case1.class,
    	Test_XML_EventReader_case2.class,
    	Test_XML_EventReader_case3.class,
    	Test_XML_EventReader_case4.class,
    	Test_XML_EventReader_case5.class,
    	Test_XML_EventReader_case6.class,
    	Test_XML_EventReader_case11.class
    );
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
  }
}
