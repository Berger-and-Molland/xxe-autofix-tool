package testcases.CWE611_XML_External_Entities_DocumentBuilder;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;


public class CWE611_XML_External_Entities__DocumentBuilder_case11{
	
	private class Bar {
        public void setFeature(String s, boolean b) {
        }
    }
	
	public DocumentBuilderFactory factory;
    public DocumentBuilder dBuilder;

    public String bad(String filePath) throws Throwable
    {
    	Bar b = new Bar();
        b.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        
	    File inputFile = new File(filePath);
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = factory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile); // Insecure
	    
	    return doc.getDocumentElement().getTextContent();
    }

    public String good(String filePath) throws Throwable
    {
    	Bar b = new Bar();
        b.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        
	    File inputFile = new File(filePath);
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
	    DocumentBuilder dBuilder = factory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile); // Secure
	    
	    return doc.getDocumentElement().getTextContent();
    }
}
