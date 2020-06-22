package testcases.CWE611_XML_External_Entities_SAXParser;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CWE611_XML_External_Entities__SAXParser_case9
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
        parser1.parse(inputStream, handler1); // Insecure

        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        SAXParser parser2 = factory.newSAXParser();
        PrintHandler handler2 = new PrintHandler();
        parser2.parse(inputStream, handler2); // Secure
        parser2.parse(inputStream, handler2); // Secure
		
		return handler1.getRes();
    }
    
    public String good(String filePath) throws Throwable
    {
		InputStream inputStream = new FileInputStream(filePath);
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        SAXParser parser1 = factory.newSAXParser();
        PrintHandler handler1 = new PrintHandler();
        parser1.parse(inputStream, handler1); // Insecure
        parser1.parse(inputStream, handler1); // Insecure

        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        SAXParser parser2 = factory.newSAXParser();
        PrintHandler handler2 = new PrintHandler();
        parser2.parse(inputStream, handler2); // Secure
        parser2.parse(inputStream, handler2); // Secure
		
		return handler1.getRes();
    }
}
