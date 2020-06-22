package testcases.CWE611_XML_External_Entities_TransformerFactory;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

public class CWE611_XML_External_Entities__TransformerFactory_case10{
	public TransformerFactory factory;
	//public Transformer transformer;

	public String bad(String filePath) throws Throwable
	{	
		File inputFile = new File(filePath);
		StreamSource input = new StreamSource(inputFile);
		TransformerFactory factory = TransformerFactory.newInstance();

		Transformer transformer1 = factory.newTransformer();
		DOMResult result1 = new DOMResult();
		transformer1.transform(input, result1);
		transformer1.transform(input, result1);
		
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		Transformer transformer2 = factory.newTransformer();
		DOMResult result2 = new DOMResult();
		transformer1.transform(input, result1);
		transformer2.transform(input, result2);
		
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
		Transformer transformer3 = factory.newTransformer();
		DOMResult result3 = new DOMResult();
		transformer2.transform(input, result2);
		transformer3.transform(input, result3);
		
		
		Document document = (Document)result3.getNode();
		return document.getDocumentElement().getTextContent(); 
	}

    public String good(String filePath) throws Throwable
    {	
		File inputFile = new File(filePath);
		StreamSource input = new StreamSource(inputFile);
		TransformerFactory factory = TransformerFactory.newInstance();
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

		Transformer transformer1 = factory.newTransformer();
		DOMResult result1 = new DOMResult();
		transformer1.transform(input, result1);
		transformer1.transform(input, result1);
		
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		Transformer transformer2 = factory.newTransformer();
		DOMResult result2 = new DOMResult();
		transformer1.transform(input, result1);
		transformer2.transform(input, result2);
		
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		Transformer transformer3 = factory.newTransformer();
		DOMResult result3 = new DOMResult();
		transformer2.transform(input, result2);
		transformer3.transform(input, result3);
		
		
		Document document = (Document)result3.getNode();
		return document.getDocumentElement().getTextContent(); 
	}
}
