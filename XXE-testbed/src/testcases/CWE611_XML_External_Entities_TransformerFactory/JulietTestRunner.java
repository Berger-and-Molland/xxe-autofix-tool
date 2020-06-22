package testcases.CWE611_XML_External_Entities_TransformerFactory;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class JulietTestRunner {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(
    	Test_CWE611_XML_External_Entities__Transformerfactory_01.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_02.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_03.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_04.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_05.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_06.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_07.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_08.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_09.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_10.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_11.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_13.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_14.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_15.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_16.class,
    	Test_CWE611_XML_External_Entities__Transformerfactory_17.class
    );
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
  }
}
