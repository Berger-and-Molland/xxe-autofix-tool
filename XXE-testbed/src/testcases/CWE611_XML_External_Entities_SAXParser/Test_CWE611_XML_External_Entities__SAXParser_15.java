package testcases.CWE611_XML_External_Entities_SAXParser;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.xml.sax.SAXParseException;



public class Test_CWE611_XML_External_Entities__SAXParser_15 {

	@Test
    public void vulnerable() {        
        
        Boolean vulnerable = true;
        
        // Test security of XMLParser
    	try {
    		CWE611_XML_External_Entities__SAXParser_15 parser = new CWE611_XML_External_Entities__SAXParser_15();
			String res = parser.bad("src/testcases/CWE611_XML_External_Entities/input.xml");

			if(res.equals("vulnerable")) {
				vulnerable = true;				
			} else {
				vulnerable = false;
			}
			
		} catch (SAXParseException e) {
			vulnerable = false;
		}catch (Throwable e) {
			e.printStackTrace();			
		}
    	assertFalse(vulnerable, "Bad parser should not be vulnerable to XXE");
	}
    	
    @Test
    public void functional() { 
    	Boolean function = true;
    	// Test proper function of XMLParser
    	try {
    		CWE611_XML_External_Entities__SAXParser_15 parser = new CWE611_XML_External_Entities__SAXParser_15();
			String res = parser.bad("src/testcases/CWE611_XML_External_Entities/good.xml");

			if(res.equals("test")) {
				function = true;				
			} else {
				function = false;
			}
			
		} catch (SAXParseException e) {
			function = false;
		}catch (Throwable e) {
			e.printStackTrace();			
		}
    	
    	assertTrue(function, "Bad parser should sucessfully parse XML");
    }
    
    @Test
    public void vulnerableGood() {        
        
        Boolean vulnerable = true;
        
        // Test security of XMLParser
    	try {
    		CWE611_XML_External_Entities__SAXParser_15 parser = new CWE611_XML_External_Entities__SAXParser_15();
			String res = parser.good("src/testcases/CWE611_XML_External_Entities/input.xml");

			if(res.equals("vulnerable")) {
				vulnerable = true;				
			} else {
				vulnerable = false;
			}
			
		} catch (SAXParseException e) {
			vulnerable = false;
		}catch (Throwable e) {
			e.printStackTrace();			
		}
    	assertFalse(vulnerable, "Good parser should not be vulnerable to XXE");
	}
    	
    @Test
    public void functionalGood() { 
    	Boolean function = true;
    	// Test proper function of XMLParser
    	try {
    		CWE611_XML_External_Entities__SAXParser_15 parser = new CWE611_XML_External_Entities__SAXParser_15();
			String res = parser.good("src/testcases/CWE611_XML_External_Entities/good.xml");
			
			if(res.equals("test")) {
				function = true;				
			} else {
				function = false;
			}
			
		} catch (SAXParseException e) {
			function = false;
		}catch (Throwable e) {
			e.printStackTrace();			
		}
    	
    	assertTrue(function, "Good parser should sucessfully parse XML");
    }

}
