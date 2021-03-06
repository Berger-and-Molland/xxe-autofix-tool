package testcases.CWE611_XML_External_Entities_EventReader;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import testcasesupport.IO;

public class CWE611_XML_External_Entities__XMLEventReader_09 {
	public String bad(String filePath) throws Throwable {
		if(IO.STATIC_FINAL_TRUE) {
			InputStream inputStream = new FileInputStream(filePath);
	
			XMLInputFactory factory = XMLInputFactory.newFactory();
			XMLEventReader reader = factory.createXMLEventReader(inputStream);
	
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
			}
		}
		return null;
	}

	public String good(String filePath) throws Throwable {
		if(IO.STATIC_FINAL_TRUE) {
			InputStream inputStream = new FileInputStream(filePath);
	
			XMLInputFactory factory = XMLInputFactory.newFactory();
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
							String elementOne = event.asCharacters().getData();
							return elementOne;
						}
					}
				} 
			}
		}
		return null;
	}
}
