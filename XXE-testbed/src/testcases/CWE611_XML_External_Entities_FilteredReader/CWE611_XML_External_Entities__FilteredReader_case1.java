package testcases.CWE611_XML_External_Entities_FilteredReader;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;


public class CWE611_XML_External_Entities__FilteredReader_case1{
	XMLInputFactory factory;
    XMLStreamReader reader;

    public String bad(String filePath) throws Throwable
    {
    	InputStream inputStream = new FileInputStream(filePath);
    	
    	XMLInputFactory factory = XMLInputFactory.newInstance();
        reader = factory.createFilteredReader(factory.createXMLStreamReader(inputStream), new StreamFilter() {
			@Override
			public boolean accept(XMLStreamReader reader) {
				return true;
			}
		});

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
    	
    	XMLInputFactory factory = XMLInputFactory.newInstance();
    	factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    	factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        reader = factory.createFilteredReader(factory.createXMLStreamReader(inputStream), new StreamFilter() {
			@Override
			public boolean accept(XMLStreamReader reader) {
				return true;
			}
		});

    	while(reader.hasNext()) {
    		reader.next();
	        if(reader.getEventType() == XMLStreamReader.START_ELEMENT) {
	        	return reader.getElementText();
	        }
        }
		return null;
    }
}
