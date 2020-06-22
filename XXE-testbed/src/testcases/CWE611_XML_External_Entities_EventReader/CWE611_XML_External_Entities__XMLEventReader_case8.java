package testcases.CWE611_XML_External_Entities_EventReader;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class CWE611_XML_External_Entities__XMLEventReader_case8 {
	public XMLInputFactory factory;
	public XMLEventReader reader;

	public String bad(String filePath) throws Throwable {
		String elementOne1 = null;
		String elementOne2 = null;
		String elementOne3 = null;
		InputStream inputStream = new FileInputStream(filePath);
		
		XMLInputFactory factory = XMLInputFactory.newFactory();
		XMLEventReader reader1 = factory.createXMLEventReader(inputStream);

		while (reader1.hasNext()) {
			XMLEvent event = (XMLEvent) reader1.next();

			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				if (element.getName().getLocalPart().equals("foo")) {
					event = (XMLEvent) reader1.next();

					if (event.isCharacters()) {
						elementOne1 = event.asCharacters().getData();
					}
				}
			}
		}
		
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		XMLEventReader reader2 = factory.createXMLEventReader(inputStream);
		while (reader2.hasNext()) {
			XMLEvent event = (XMLEvent) reader2.next();

			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				if (element.getName().getLocalPart().equals("foo")) {
					event = (XMLEvent) reader2.next();

					if (event.isCharacters()) {
						elementOne2 = event.asCharacters().getData();
					}
				}
			}
		}
		
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, true);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, true);
		XMLEventReader reader3 = factory.createXMLEventReader(inputStream);
		while (reader3.hasNext()) {
			XMLEvent event = (XMLEvent) reader3.next();

			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				if (element.getName().getLocalPart().equals("foo")) {
					event = (XMLEvent) reader3.next();

					if (event.isCharacters()) {
						elementOne3 = event.asCharacters().getData();
					}
				}
			}
		}
		return elementOne3;
	}

	public String good(String filePath) throws Throwable {
		String elementOne1 = null;
		String elementOne2 = null;
		String elementOne3 = null;
		InputStream inputStream = new FileInputStream(filePath);
		
		XMLInputFactory factory = XMLInputFactory.newFactory();
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		XMLEventReader reader1 = factory.createXMLEventReader(inputStream);

		while (reader1.hasNext()) {
			XMLEvent event = (XMLEvent) reader1.next();

			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				if (element.getName().getLocalPart().equals("foo")) {
					event = (XMLEvent) reader1.next();

					if (event.isCharacters()) {
						elementOne1 = event.asCharacters().getData();
					}
				}
			}
		}
		
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		XMLEventReader reader2 = factory.createXMLEventReader(inputStream);
		while (reader2.hasNext()) {
			XMLEvent event = (XMLEvent) reader2.next();

			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				if (element.getName().getLocalPart().equals("foo")) {
					event = (XMLEvent) reader2.next();

					if (event.isCharacters()) {
						elementOne2 = event.asCharacters().getData();
					}
				}
			}
		}
		
		factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		XMLEventReader reader3 = factory.createXMLEventReader(inputStream);
		while (reader3.hasNext()) {
			XMLEvent event = (XMLEvent) reader3.next();

			if (event.isStartElement()) {
				StartElement element = event.asStartElement();
				if (element.getName().getLocalPart().equals("foo")) {
					event = (XMLEvent) reader3.next();

					if (event.isCharacters()) {
						elementOne3 = event.asCharacters().getData();
					}
				}
			}
		}
		return elementOne3;
	}
}
