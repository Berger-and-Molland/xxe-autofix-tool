package testcases.CWE611_XML_External_Entities_XMLStreamReader;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;


public class CWE611_XML_External_Entities__XMLStreamReader_05{
	private boolean privateTrue = true;

    public String bad(String filePath) throws Throwable
    {
    	if(privateTrue) {
	    	InputStream inputStream = new FileInputStream(filePath);
	    	
	    	XMLInputFactory factory = XMLInputFactory.newFactory();
	    	XMLStreamReader reader = factory.createXMLStreamReader(inputStream);
	
	    	while(reader.hasNext()) {
	    		reader.next();
		        if(reader.getEventType() == XMLStreamReader.START_ELEMENT){
		        	return reader.getElementText();
		        }
	    	}
    	}
		return null;
    }

    public String good(String filePath) throws Throwable
    {
    	if(privateTrue) {
	    	InputStream inputStream = new FileInputStream(filePath);
	    	
	    	XMLInputFactory factory = XMLInputFactory.newFactory();
	    	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
	    	factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
	    	XMLStreamReader reader = factory.createXMLStreamReader(inputStream);
	
	    	while(reader.hasNext()) {
	    		reader.next();
		        if(reader.getEventType() == XMLStreamReader.START_ELEMENT){
		        	return reader.getElementText();
		        }
	        }
    	}
		return null;
    }
}
