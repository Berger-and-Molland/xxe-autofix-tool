package testcases.CWE611_XML_External_Entities_XMLReader;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class CWE611_XML_External_Entities__XMLReader_case11{
	private class Bar {
        public void setFeature(String s, boolean b) {
        }
    }

    public String bad(String filePath) throws Throwable
    {
    	InputSource inputSource = new InputSource(filePath);

    	XMLReader reader = XMLReaderFactory.createXMLReader();
    	
    	Bar b = new Bar();
    	// Calls the setFeature method with correct parameters but on the wrong object
    	b.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    	b.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); 
    	b.setFeature("http://xml.org/sax/features/external-general-entities", false);
    	b.setFeature("http://xml.org/sax/features/external-parameter-entities", false); 
    	
    	
    	PrintHandler handler = new PrintHandler();
    	reader.setContentHandler(handler);

    	reader.parse(inputSource);
    	
		return handler.getRes();
    }

    public String good(String filePath) throws Throwable
    {
    	InputSource inputSource = new InputSource(filePath);

    	XMLReader reader = XMLReaderFactory.createXMLReader();
    	
    	Bar b = new Bar();
    	// Calls the setFeature method with correct parameters but on the wrong object
    	b.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    	b.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); 
    	b.setFeature("http://xml.org/sax/features/external-general-entities", false);
    	b.setFeature("http://xml.org/sax/features/external-parameter-entities", false); 
    	
    	reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    	reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); 
    	reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
    	reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
    	PrintHandler handler = new PrintHandler();
    	reader.setContentHandler(handler);

    	reader.parse(inputSource);
    	
		return handler.getRes();
    }
}
