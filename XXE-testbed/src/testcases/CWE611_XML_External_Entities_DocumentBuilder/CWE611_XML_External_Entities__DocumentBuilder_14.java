package testcases.CWE611_XML_External_Entities_DocumentBuilder;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import testcasesupport.IO;


public class CWE611_XML_External_Entities__DocumentBuilder_14{

    public String bad(String filePath) throws Throwable
    {
    	if (IO.staticFive == 5){
		    File inputFile = new File(filePath);
		    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    Document doc = dBuilder.parse(inputFile);
		    doc.getDocumentElement().normalize();
		    return doc.getDocumentElement().getTextContent();
    	}
    	return null;
    }

    public String good(String filePath) throws Throwable
    {
    	if (IO.staticFive == 5){
	    	File inputFile = new File(filePath);
		    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		    
		    dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		    
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    Document doc = dBuilder.parse(inputFile);
		    doc.getDocumentElement().normalize();
		    return doc.getDocumentElement().getTextContent();
    	}
    	return null;
    }
}
