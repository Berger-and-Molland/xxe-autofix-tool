package testcases.CWE611_XML_External_Entities_XMLStreamReader;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;


public class CWE611_XML_External_Entities__XMLStreamReader_case8{
	public XMLInputFactory factory;
	public XMLStreamReader reader;
	
    public String bad(String filePath) throws Throwable
    {
    	String res1 = null;
    	String res2 = null;
    	String res3 = null;
    	
    	InputStream inputStream = new FileInputStream(filePath);
    	
    	XMLInputFactory factory = XMLInputFactory.newFactory();
    	XMLStreamReader reader1 = factory.createXMLStreamReader(inputStream);

    	while(reader1.hasNext()) {
    		reader1.next();
	        if(reader1.getEventType() == XMLStreamReader.START_ELEMENT){
	        	res1 = reader1.getElementText();
	        }
        }

    	inputStream.reset();
    	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    	XMLStreamReader reader2 = factory.createXMLStreamReader(inputStream);

    	while(reader2.hasNext()) {
    		reader2.next();
	        if(reader2.getEventType() == XMLStreamReader.START_ELEMENT){
	        	res2 = reader2.getElementText();
	        }
        }

    	inputStream.reset();
    	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, true);
    	factory.setProperty(XMLInputFactory.SUPPORT_DTD, true);
    	XMLStreamReader reader3 = factory.createXMLStreamReader(inputStream);

    	while(reader3.hasNext()) {
    		reader3.next();
	        if(reader3.getEventType() == XMLStreamReader.START_ELEMENT){
	        	res3 = reader3.getElementText();
	        }
        }
    	
		return res3;
    }

    public String good(String filePath) throws Throwable
    {
    	String res1 = null;
    	String res2 = null;
    	String res3 = null;
    	
    	InputStream inputStream = new FileInputStream(filePath);
    	
    	XMLInputFactory factory = XMLInputFactory.newFactory();
    	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    	XMLStreamReader reader1 = factory.createXMLStreamReader(inputStream);

    	while(reader1.hasNext()) {
    		reader1.next();
	        if(reader1.getEventType() == XMLStreamReader.START_ELEMENT){
	        	res1 = reader1.getElementText();
	        }
        }

    	inputStream.reset();
    	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    	XMLStreamReader reader2 = factory.createXMLStreamReader(inputStream);

    	while(reader2.hasNext()) {
    		reader2.next();
	        if(reader2.getEventType() == XMLStreamReader.START_ELEMENT){
	        	res2 = reader2.getElementText();
	        }
        }

    	inputStream.reset();
    	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    	XMLStreamReader reader3 = factory.createXMLStreamReader(inputStream);

    	while(reader3.hasNext()) {
    		reader3.next();
	        if(reader3.getEventType() == XMLStreamReader.START_ELEMENT){
	        	res3 = reader3.getElementText();
	        }
        }
    	
		return res3;
    }
}
