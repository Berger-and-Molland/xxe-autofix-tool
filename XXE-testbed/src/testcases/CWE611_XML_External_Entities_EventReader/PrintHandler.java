package testcases.CWE611_XML_External_Entities_EventReader;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PrintHandler extends DefaultHandler {
	private String res;
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        //System.out.println("Node = " + qName);
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {

        //System.out.println("New content received");
        //System.out.println(new String(ch).substring(start, start + length));
        this.res = new String(ch).substring(start, start + length);
    }
    
    public String getRes() {
    	return this.res;
    }
}