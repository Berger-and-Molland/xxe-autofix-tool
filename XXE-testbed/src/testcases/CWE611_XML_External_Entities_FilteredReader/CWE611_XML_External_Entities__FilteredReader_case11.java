package testcases.CWE611_XML_External_Entities_FilteredReader;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;


public class CWE611_XML_External_Entities__FilteredReader_case11{
	XMLInputFactory factory;
    XMLStreamReader reader;
    private class Bar {
        public void setProperty(String s, Object b) {
        }
    }

    public String bad(String filePath) throws Throwable
    {
    	String res = null;
    	InputStream inputStream = new FileInputStream(filePath);

    	XMLInputFactory factory = XMLInputFactory.newInstance();
    	
    	Bar b = new Bar();
    	b.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	b.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    	 
    	
    	XMLStreamReader reader = factory.createFilteredReader(factory.createXMLStreamReader(inputStream), new StreamFilter() {
			@Override
			public boolean accept(XMLStreamReader reader) {
				return true;
			}
		});

    	while(reader.hasNext()) {
    		reader.next();
	        if(reader.getEventType() == XMLStreamReader.START_ELEMENT){
	        	res = reader.getElementText();
	        }
        }

		return res;
    }

    public String good(String filePath) throws Throwable
    {
    	String res = null;
    	InputStream inputStream = new FileInputStream(filePath);

    	XMLInputFactory factory = XMLInputFactory.newInstance();
    	
    	Bar b = new Bar();
    	b.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	b.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    	
    	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    	 
    	
    	XMLStreamReader reader = factory.createFilteredReader(factory.createXMLStreamReader(inputStream), new StreamFilter() {
			@Override
			public boolean accept(XMLStreamReader reader) {
				return true;
			}
		});

    	while(reader.hasNext()) {
    		reader.next();
	        if(reader.getEventType() == XMLStreamReader.START_ELEMENT){
	        	res = reader.getElementText();
	        }
        }

		return res;
    }
}
