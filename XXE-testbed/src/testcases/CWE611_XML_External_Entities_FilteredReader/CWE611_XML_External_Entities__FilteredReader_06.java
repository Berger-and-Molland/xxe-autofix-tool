package testcases.CWE611_XML_External_Entities_FilteredReader;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.StreamFilter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;


public class CWE611_XML_External_Entities__FilteredReader_06{
	private static final int PRIVATE_STATIC_FINAL_FIVE = 5;

    public String bad(String filePath) throws Throwable
    {
    	if (PRIVATE_STATIC_FINAL_FIVE == 5){
	    	InputStream inputStream = new FileInputStream(filePath);
	    	
	    	XMLInputFactory factory = XMLInputFactory.newInstance();
	        XMLStreamReader reader = factory.createFilteredReader(factory.createXMLStreamReader(inputStream), new StreamFilter() {
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
    	}
		return null;
    }

    public String good(String filePath) throws Throwable
    {
    	if (PRIVATE_STATIC_FINAL_FIVE == 5){
	    	InputStream inputStream = new FileInputStream(filePath);
	    	
	    	XMLInputFactory factory = XMLInputFactory.newInstance();
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
		        if(reader.getEventType() == XMLStreamReader.START_ELEMENT) {
		        	return reader.getElementText();
		        }
	        }
    	}
		return null;
    }
}
