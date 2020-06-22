package testcases.CWE611_XML_External_Entities_TransformerFactory;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(
    	Test_XML_External_Entities__Transformerfactory_case1.class,
    	Test_XML_External_Entities__Transformerfactory_case2.class,
    	Test_XML_External_Entities__Transformerfactory_case3.class,
    	Test_XML_External_Entities__Transformerfactory_case4.class,
    	Test_XML_External_Entities__Transformerfactory_case5.class,
    	Test_XML_External_Entities__Transformerfactory_case6.class,
    	Test_XML_External_Entities__Transformerfactory_case11.class
    );
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
  }
}
