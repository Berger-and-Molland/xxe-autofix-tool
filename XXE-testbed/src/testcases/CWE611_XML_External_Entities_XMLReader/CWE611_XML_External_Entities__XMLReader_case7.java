package testcases.CWE611_XML_External_Entities_XMLReader;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class CWE611_XML_External_Entities__XMLReader_case7{

    public String bad(String filePath) throws Throwable
    {
    	InputSource inputSource = new InputSource(filePath);

    	XMLReader reader1 = XMLReaderFactory.createXMLReader();
    	PrintHandler handler1 = new PrintHandler();
    	reader1.setContentHandler(handler1);

    	reader1.parse(inputSource);


    	
    	XMLReader reader2 = XMLReaderFactory.createXMLReader();
    	reader2.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    	reader2.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); 
    	reader2.setFeature("http://xml.org/sax/features/external-general-entities", false);
    	reader2.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    	PrintHandler handler2 = new PrintHandler();
    	reader2.setContentHandler(handler2);

    	reader2.parse(inputSource);
    	
    	
		return handler1.getRes();
    }

    public String good(String filePath) throws Throwable
    {
    	InputSource inputSource = new InputSource(filePath);

    	XMLReader reader1 = XMLReaderFactory.createXMLReader();
    	PrintHandler handler1 = new PrintHandler();
    	reader1.setContentHandler(handler1);
    	reader1.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    	reader1.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); 
    	reader1.setFeature("http://xml.org/sax/features/external-general-entities", false);
    	reader1.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

    	reader1.parse(inputSource);


    	
    	XMLReader reader2 = XMLReaderFactory.createXMLReader();
    	reader2.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    	reader2.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); 
    	reader2.setFeature("http://xml.org/sax/features/external-general-entities", false);
    	reader2.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    	PrintHandler handler2 = new PrintHandler();
    	reader2.setContentHandler(handler2);

    	reader2.parse(inputSource);
    	
    	
		return handler1.getRes();
    }
}
