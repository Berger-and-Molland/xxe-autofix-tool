package testcases.CWE611_XML_External_Entities_SAXParser;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CWE611_XML_External_Entities__SAXParser_case11
{
	private class Bar {
        public void setFeature(String s, boolean b) {
        }
    }
	
	public SAXParserFactory factory;
    public SAXParser parser;

    public String bad(String filePath) throws Throwable
    {
    	Bar b = new Bar();
    	// Calls the setFeature method with correct parameters but on the wrong object
    	b.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true); 
    	
		InputStream inputStream = new FileInputStream(filePath);
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        PrintHandler handler = new PrintHandler();
        parser.parse(inputStream, handler); // Insecure


		return handler.getRes();
    }
    
    public String good(String filePath) throws Throwable
    {
    	Bar b = new Bar();
    	// Calls the setFeature method with correct parameters but on the wrong object
    	b.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true); 
    	
		InputStream inputStream = new FileInputStream(filePath);
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        SAXParser parser = factory.newSAXParser();
        PrintHandler handler = new PrintHandler();
        parser.parse(inputStream, handler); // Secure


		return handler.getRes();
    }
}
