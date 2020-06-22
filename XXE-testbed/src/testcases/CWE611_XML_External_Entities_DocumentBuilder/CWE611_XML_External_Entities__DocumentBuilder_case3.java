package testcases.CWE611_XML_External_Entities_DocumentBuilder;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;


public class CWE611_XML_External_Entities__DocumentBuilder_case3{
	public DocumentBuilderFactory factory;
    public DocumentBuilder dBuilder;

    public String bad(String filePath) throws Throwable
    {
	    File inputFile = new File(filePath);
	    factory = DocumentBuilderFactory.newInstance();
	    dBuilder = factory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile);
	    doc.getDocumentElement().normalize();
	    return doc.getDocumentElement().getTextContent();
    }

    public String good(String filePath) throws Throwable
    {
    	File inputFile = new File(filePath);
	    factory = DocumentBuilderFactory.newInstance();
	    
	    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
	    
	    dBuilder = factory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile);
	    doc.getDocumentElement().normalize();
	    return doc.getDocumentElement().getTextContent();
    }
}
