package testcases.CWE611_XML_External_Entities_SAXParser;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import testcasesupport.IO;

public class CWE611_XML_External_Entities__SAXParser_12{
	
    public String bad(String filePath) throws Throwable
    {
    	if (IO.staticReturnsTrueOrFalse()){
    		InputStream inputStream = new FileInputStream(filePath);
    		
    		SAXParserFactory factory = SAXParserFactory.newInstance();
    		SAXParser parser = factory.newSAXParser();
    		PrintHandler handler = new PrintHandler();
    		
    		parser.parse(inputStream, handler);
    		return handler.getRes();
    		
    	}
    	return null;
    }
    
    public String good(String filePath) throws Throwable
    {
    	if (IO.staticReturnsTrueOrFalse()){
    		InputStream inputStream = new FileInputStream(filePath);
    		
    		SAXParserFactory factory = SAXParserFactory.newInstance();
    		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    		SAXParser parser = factory.newSAXParser();
    		
    		PrintHandler handler = new PrintHandler();
    		
    		parser.parse(inputStream, handler);
    		return handler.getRes();
    		
    	}
    	return null;
    }
}
