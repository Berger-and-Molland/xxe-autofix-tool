package testcases.CWE611_XML_External_Entities_XMLStreamReader;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;


public class CWE611_XML_External_Entities__XMLStreamReader_case11{
	public XMLInputFactory factory;
	public XMLStreamReader reader;
	
	private class Bar {
        public void setProperty(String s, Object b) {
        }
    }
	
    public String bad(String filePath) throws Throwable
    {
    	InputStream inputStream = new FileInputStream(filePath);
    	
    	XMLInputFactory factory = XMLInputFactory.newFactory();
    	XMLStreamReader reader = factory.createXMLStreamReader(inputStream);
    	
    	Bar b = new Bar();
    	b.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	b.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    	
    	while(reader.hasNext()) {
    		reader.next();
	        if(reader.getEventType() == XMLStreamReader.START_ELEMENT){
	        	return reader.getElementText();
	        }
        }
		return null;
    }

    public String good(String filePath) throws Throwable
    {
    	InputStream inputStream = new FileInputStream(filePath);
    	
    	XMLInputFactory factory = XMLInputFactory.newFactory();
    	Bar b = new Bar();
    	b.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	b.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    	
    	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    	XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

    	while(reader.hasNext()) {
    		reader.next();
	        if(reader.getEventType() == XMLStreamReader.START_ELEMENT){
	        	return reader.getElementText();
	        }
        }
		return null;
    }
}
