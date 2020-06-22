package testcases.CWE611_XML_External_Entities_EventReader;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class CWE611_XML_External_Entities__XMLEventReader_case3 {
	public XMLInputFactory factory;
	public XMLEventReader reader;

	public String bad(String filePath) throws Throwable {
		InputStream inputStream = new FileInputStream(filePath);

		factory = XMLInputFactory.newFactory();
		reader = factory.createXMLEventReader(inputStream);

		while (reader.hasNext()) {
			XMLEvent event = (XMLEvent) reader.next();

			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				if (element.getName().getLocalPart().equals("foo")) {
					event = (XMLEvent) reader.next();

					if (event.isCharacters()) {
						String elementOne = event.asCharacters().getData();
						return elementOne;
					}
				}
			}
			// handle more event types here...
		}
		return null;
	}

	public String good(String filePath) throws Throwable {
		InputStream inputStream = new FileInputStream(filePath);

		factory = XMLInputFactory.newFactory();
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		reader = factory.createXMLEventReader(inputStream);

		while (reader.hasNext()) {
			XMLEvent event = (XMLEvent) reader.next();

			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				if (element.getName().getLocalPart().equals("foo")) {
					event = (XMLEvent) reader.next();

					if (event.isCharacters()) {
						String elementOne = event.asCharacters().getData();
						return elementOne;
					}
				}
			}
			// handle more event types here...
		}
		return null;
	}
}
