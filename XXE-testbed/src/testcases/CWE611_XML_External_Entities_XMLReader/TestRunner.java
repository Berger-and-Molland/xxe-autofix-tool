package testcases.CWE611_XML_External_Entities_XMLReader;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(
		Test_XML_External_Entities__XMLReader_case4.class,
		Test_XML_External_Entities__XMLReader_case6.class,
		Test_XML_External_Entities__XMLReader_case11.class
    );
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
  }
}
