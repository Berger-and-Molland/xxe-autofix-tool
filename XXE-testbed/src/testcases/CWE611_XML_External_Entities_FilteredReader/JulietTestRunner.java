package testcases.CWE611_XML_External_Entities_FilteredReader;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class JulietTestRunner {
  public static void main(String[] args) {
    Result result = JUnitCore.runClasses(
    	Test_CWE611_XML_External_Entities__FilteredReader_01.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_02.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_03.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_04.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_05.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_06.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_07.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_08.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_09.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_10.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_11.class,
    	//Test_CWE611_XML_External_Entities__FilteredReader_10.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_13.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_14.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_15.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_16.class,
    	Test_CWE611_XML_External_Entities__FilteredReader_17.class
    	
    );
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
  }
}
