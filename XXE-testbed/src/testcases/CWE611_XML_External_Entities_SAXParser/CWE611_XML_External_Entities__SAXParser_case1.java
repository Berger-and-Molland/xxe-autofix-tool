package testcases.CWE611_XML_External_Entities_SAXParser;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class CWE611_XML_External_Entities__SAXParser_case1
{
	public SAXParserFactory factory;
    public SAXParser parser;

    public String bad(String filePath) throws Throwable
    {
		InputStream inputStream = new FileInputStream(filePath);
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		parser = factory.newSAXParser();
		
		PrintHandler handler = new PrintHandler();
		
		parser.parse(inputStream, handler);
		return handler.getRes();
    }
    
    public String good(String filePath) throws Throwable
    {
		InputStream inputStream = new FileInputStream(filePath);
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		parser = factory.newSAXParser();
		
		PrintHandler handler = new PrintHandler();
		
		parser.parse(inputStream, handler);
		return handler.getRes();
    }
}
