package testcases.CWE611_XML_External_Entities_XMLReader;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class CWE611_XML_External_Entities__XMLReader_06{
	private static final int PRIVATE_STATIC_FINAL_FIVE = 5;
	
    public String bad(String filePath) throws Throwable
    {
    	if(PRIVATE_STATIC_FINAL_FIVE == 5) {
    		InputSource inputSource = new InputSource(filePath);

        	XMLReader reader = XMLReaderFactory.createXMLReader();
        	PrintHandler handler = new PrintHandler();
        	reader.setContentHandler(handler);

        	reader.parse(inputSource);
    		return handler.getRes();
    	}
    	return null;
    }

    public String good(String filePath) throws Throwable
    {
    	if(PRIVATE_STATIC_FINAL_FIVE == 5) {
    		InputSource inputSource = new InputSource(filePath);

        	XMLReader reader = XMLReaderFactory.createXMLReader();
        	PrintHandler handler = new PrintHandler();
        	reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        	reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); 
        	reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        	reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        	reader.setContentHandler(handler);

        	reader.parse(inputSource);
    		return handler.getRes();
    	}
    	return null;
    }
}