package testcases.CWE611_XML_External_Entities_DocumentBuilder;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;


public class CWE611_XML_External_Entities__DocumentBuilder_case10{
	public DocumentBuilderFactory factory;
    public DocumentBuilder dBuilder;

    public String bad(String filePath) throws Throwable
    {
	    File inputFile = new File(filePath);
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = factory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile); // Insecure
	    doc = dBuilder.parse(inputFile); // Insecure
	    
	    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
	    DocumentBuilder dBuilder2 = factory.newDocumentBuilder();
	    Document doc2 = dBuilder2.parse(inputFile); // Secure
	    doc = dBuilder.parse(inputFile); // Insecure
	    
	    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
	    DocumentBuilder dBuilder3 = factory.newDocumentBuilder();
	    Document doc3 = dBuilder3.parse(inputFile); // Insecure
	    doc2 = dBuilder2.parse(inputFile); // Secure
	    
	    return doc2.getDocumentElement().getTextContent();
    }

    public String good(String filePath) throws Throwable
    {
	    File inputFile = new File(filePath);
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
	    DocumentBuilder dBuilder = factory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile); // Secure
	    doc = dBuilder.parse(inputFile); // Secure
	    
	    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
	    DocumentBuilder dBuilder2 = factory.newDocumentBuilder();
	    Document doc2 = dBuilder2.parse(inputFile); // Secure
	    doc = dBuilder.parse(inputFile); // Secure
	    
	    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
	    DocumentBuilder dBuilder3 = factory.newDocumentBuilder();
	    Document doc3 = dBuilder3.parse(inputFile); // Secure
	    doc2 = dBuilder2.parse(inputFile); // Secure
	    
	    return doc2.getDocumentElement().getTextContent();
    }
}
