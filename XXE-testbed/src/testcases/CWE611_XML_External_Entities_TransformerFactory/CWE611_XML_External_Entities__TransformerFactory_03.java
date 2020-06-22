package testcases.CWE611_XML_External_Entities_TransformerFactory;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

public class CWE611_XML_External_Entities__TransformerFactory_03{
	public String bad(String filePath) throws Throwable
	{	
		if(5==5) {
			File inputFile = new File(filePath);
			StreamSource input = new StreamSource(inputFile);
			
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			
			DOMResult result = new DOMResult();
			transformer.transform(input, result);
			
			Document document = (Document)result.getNode();
			return document.getDocumentElement().getTextContent();
		}
		return null;
	}

    public String good(String filePath) throws Throwable
    {
    	if(5==5) {
			File inputFile = new File(filePath);
		    StreamSource input = new StreamSource(inputFile);
		
		    TransformerFactory factory = TransformerFactory.newInstance();
		    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		    Transformer transformer = factory.newTransformer();
	
			DOMResult result = new DOMResult();
			transformer.transform(input, result);
		
			Document document = (Document)result.getNode();
			return document.getDocumentElement().getTextContent(); 
    	}
    	return null;
    }
}
