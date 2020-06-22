package testcases.CWE611_XML_External_Entities_FilteredReader;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;


public class CWE611_XML_External_Entities__FilteredReader_case8{
	XMLInputFactory factory;
    XMLStreamReader reader;

    public String bad(String filePath) throws Throwable
    {
    	String res1 = null;
    	String res2 = null;
    	String res3 = null;
    	InputStream inputStream = new FileInputStream(filePath);

    	XMLInputFactory factory = XMLInputFactory.newInstance();
    	XMLStreamReader reader1 = factory.createFilteredReader(factory.createXMLStreamReader(inputStream), new StreamFilter() {
			@Override
			public boolean accept(XMLStreamReader reader) {
				return true;
			}
		});

    	while(reader1.hasNext()) {
    		reader1.next();
	        if(reader1.getEventType() == XMLStreamReader.START_ELEMENT){
	        	res1 = reader1.getElementText();
	        }
        }
    	inputStream.reset();
    	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    	XMLStreamReader reader2 = factory.createFilteredReader(factory.createXMLStreamReader(inputStream), new StreamFilter() {
			@Override
			public boolean accept(XMLStreamReader reader) {
				return true;
			}
		});

    	while(reader2.hasNext()) {
    		reader2.next();
	        if(reader2.getEventType() == XMLStreamReader.START_ELEMENT){
	        	res2 = reader2.getElementText();
	        }
        }
    	
    	
    	inputStream.reset();
    	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, true);
    	factory.setProperty(XMLInputFactory.SUPPORT_DTD, true);
    	XMLStreamReader reader3 = factory.createFilteredReader(factory.createXMLStreamReader(inputStream), new StreamFilter() {
			@Override
			public boolean accept(XMLStreamReader reader) {
				return true;
			}
		});

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

    	XMLInputFactory factory = XMLInputFactory.newInstance();
    	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    	XMLStreamReader reader1 = factory.createFilteredReader(factory.createXMLStreamReader(inputStream), new StreamFilter() {
			@Override
			public boolean accept(XMLStreamReader reader) {
				return true;
			}
		});

    	while(reader1.hasNext()) {
    		reader1.next();
	        if(reader1.getEventType() == XMLStreamReader.START_ELEMENT){
	        	res1 = reader1.getElementText();
	        }
        }
    	inputStream.reset();
    	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    	XMLStreamReader reader2 = factory.createFilteredReader(factory.createXMLStreamReader(inputStream), new StreamFilter() {
			@Override
			public boolean accept(XMLStreamReader reader) {
				return true;
			}
		});

    	while(reader2.hasNext()) {
    		reader2.next();
	        if(reader2.getEventType() == XMLStreamReader.START_ELEMENT){
	        	res2 = reader2.getElementText();
	        }
        }
    	
    	
    	inputStream.reset();
    	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    	XMLStreamReader reader3 = factory.createFilteredReader(factory.createXMLStreamReader(inputStream), new StreamFilter() {
			@Override
			public boolean accept(XMLStreamReader reader) {
				return true;
			}
		});

    	while(reader3.hasNext()) {
    		reader3.next();
	        if(reader3.getEventType() == XMLStreamReader.START_ELEMENT){
	        	res3 = reader3.getElementText();
	        }
        }
		return res3;
    }
}
