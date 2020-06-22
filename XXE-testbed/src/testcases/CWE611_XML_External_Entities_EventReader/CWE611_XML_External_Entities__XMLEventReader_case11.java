package testcases.CWE611_XML_External_Entities_EventReader;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class CWE611_XML_External_Entities__XMLEventReader_case11 {
	public XMLInputFactory factory;
	public XMLEventReader reader;
	
	private class Bar {
        public void setProperty(String s, Object b) {
        }
    }

	public String bad(String filePath) throws Throwable {
		String elementOne = null;
		InputStream inputStream = new FileInputStream(filePath);
		
		XMLInputFactory factory = XMLInputFactory.newFactory();
		
		Bar b = new Bar();
		b.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		b.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		
		
		XMLEventReader reader = factory.createXMLEventReader(inputStream);

		while (reader.hasNext()) {
			XMLEvent event = (XMLEvent) reader.next();

			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				if (element.getName().getLocalPart().equals("foo")) {
					event = (XMLEvent) reader.next();

					if (event.isCharacters()) {
						elementOne = event.asCharacters().getData();
					}
				}
			}
		}
		
		return elementOne;
	}

	public String good(String filePath) throws Throwable {
		String elementOne = null;
		InputStream inputStream = new FileInputStream(filePath);
		
		XMLInputFactory factory = XMLInputFactory.newFactory();
		
		Bar b = new Bar();
		b.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		b.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		
		
		XMLEventReader reader = factory.createXMLEventReader(inputStream);

		while (reader.hasNext()) {
			XMLEvent event = (XMLEvent) reader.next();

			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				if (element.getName().getLocalPart().equals("foo")) {
					event = (XMLEvent) reader.next();

					if (event.isCharacters()) {
						elementOne = event.asCharacters().getData();
					}
				}
			}
		}
		
		return elementOne;
	}
}
