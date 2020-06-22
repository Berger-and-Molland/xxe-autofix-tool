package testcases.CWE611_XML_External_Entities_DocumentBuilder;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;


public class CWE611_XML_External_Entities__DocumentBuilder_case8{
	public DocumentBuilderFactory factory;
    public DocumentBuilder dBuilder;

    public String bad(String filePath) throws Throwable
    {
	    File inputFile = new File(filePath);
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = factory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile); // Insecure
	    
	    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
	    DocumentBuilder dBuilder2 = factory.newDocumentBuilder();
	    Document doc2 = dBuilder2.parse(inputFile); // Secure
	    
	    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
	    DocumentBuilder dBuilder3 = factory.newDocumentBuilder();
	    Document doc3 = dBuilder3.parse(inputFile); // Insecure
	    
	    return doc3.getDocumentElement().getTextContent();
    }

    public String good(String filePath) throws Throwable
    {
	    File inputFile = new File(filePath);
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
	    DocumentBuilder dBuilder = factory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile); // Insecure
	    
	    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
	    DocumentBuilder dBuilder2 = factory.newDocumentBuilder();
	    Document doc2 = dBuilder2.parse(inputFile); // Secure
	    
	    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
	    DocumentBuilder dBuilder3 = factory.newDocumentBuilder();
	    Document doc3 = dBuilder3.parse(inputFile); // Insecure
	    
	    return doc3.getDocumentElement().getTextContent();
    }
}
