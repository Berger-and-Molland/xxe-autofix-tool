package testcases.CWE611_XML_External_Entities_SAXParser;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CWE611_XML_External_Entities__SAXParser_case8
{
	public SAXParserFactory factory;
    public SAXParser parser;

    public String bad(String filePath) throws Throwable
    {
		InputStream inputStream = new FileInputStream(filePath);
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser1 = factory.newSAXParser();
        PrintHandler handler1 = new PrintHandler();
        parser1.parse(inputStream, handler1); // Insecure

        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        SAXParser parser2 = factory.newSAXParser();
        PrintHandler handler2 = new PrintHandler();
        parser2.parse(inputStream, handler2); // Secure
        
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        SAXParser parser3 = factory.newSAXParser();
        PrintHandler handler3 = new PrintHandler();
        parser3.parse(inputStream, handler3); // Insecure
		
		return handler3.getRes();
    }
    
    public String good(String filePath) throws Throwable
    {
		InputStream inputStream = new FileInputStream(filePath);
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser1 = factory.newSAXParser();
        PrintHandler handler1 = new PrintHandler();
        parser1.parse(inputStream, handler1); // Insecure

        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        SAXParser parser2 = factory.newSAXParser();
        PrintHandler handler2 = new PrintHandler();
        parser2.parse(inputStream, handler2); // Secure
        
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        SAXParser parser3 = factory.newSAXParser();
        PrintHandler handler3 = new PrintHandler();
        parser3.parse(inputStream, handler3); // Insecure
		
		return handler3.getRes();
    }
}
