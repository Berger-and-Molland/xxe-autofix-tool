package testcode.xxe;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import testcode.xxe.util.PrintHandler;
import testcode.xxe.util.PrintHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import java.io.File;
import java.io.IOException;


public class SaxParserSafeProperty1 {
    public SAXParserFactory factory;
    public SAXParser parser;

    public void case1() throws ParserConfigurationException, SAXException, IOException {
        String xml = "xxe.xml";
        SAXParserFactory factory = SAXParserFactory.newInstance();
        parser = factory.newSAXParser();
        parser.parse(xml, new PrintHandler());
    }

    public void case1_safe() throws ParserConfigurationException, SAXException, IOException {
        String xml = "xxe.xml";
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        parser = factory.newSAXParser();
        parser.parse(xml, new PrintHandler());
    }

    public void case2() throws ParserConfigurationException, SAXException, IOException {
        String xml = "xxe.xml";
        factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(xml, new PrintHandler());
    }

    public void case2_safe() throws ParserConfigurationException, SAXException, IOException {
        String xml = "xxe.xml";
        factory = SAXParserFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        SAXParser parser = factory.newSAXParser();
        parser.parse(xml, new PrintHandler());
    }

    public void case3() throws ParserConfigurationException, SAXException, IOException {
        String xml = "xxe.xml";
        factory = SAXParserFactory.newInstance();
        parser = factory.newSAXParser();
        parser.parse(xml, new PrintHandler());
    }

    public void case3_safe() throws ParserConfigurationException, SAXException, IOException {
        String xml = "xxe.xml";
        factory = SAXParserFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        parser = factory.newSAXParser();
        parser.parse(xml, new PrintHandler());
    }

    public void case4() throws ParserConfigurationException, SAXException, IOException {
        String xml = "xxe.xml";
        parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(xml, new PrintHandler());
    }


    public void case5() throws ParserConfigurationException, SAXException, IOException {
        String xml = "xxe.xml";
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse(xml, new PrintHandler());
    }

    public void case5_safe() throws ParserConfigurationException, SAXException, IOException {
        String xml = "xxe.xml";
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        SAXParser parser = factory.newSAXParser();
        parser.parse(xml, new PrintHandler());
    }

    public void case6() throws ParserConfigurationException, SAXException, IOException {
        String xml = "xxe.xml";
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(xml, new PrintHandler());
    }
    // NOTE: Case 6 cannot be safe
//    public void case6_safe() throws ParserConfigurationException, SAXException, IOException {
//        String xml = "xxe.xml";
//        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
//        parser.parse(xml, new PrintHandler());
//    }

    public void case7() throws ParserConfigurationException, SAXException, IOException {
        String xml = "xxe.xml";
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser1 = factory.newSAXParser();
        parser1.parse(xml, new PrintHandler()); // Insecure

        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        SAXParser parser2 = factory.newSAXParser();
        parser2.parse(xml, new PrintHandler()); // Secure

    }

    public void case8() throws ParserConfigurationException, SAXException, IOException {
        String xml = "xxe.xml";
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser1 = factory.newSAXParser();
        parser1.parse(xml, new PrintHandler()); // Insecure

        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        SAXParser parser2 = factory.newSAXParser();
        parser2.parse(xml, new PrintHandler()); // Secure

        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        SAXParser parser3 = factory.newSAXParser();
        parser3.parse(xml, new PrintHandler()); // Insecure
    }

    public void case9() throws ParserConfigurationException, SAXException, IOException {
        String xml = "xxe.xml";
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser1 = factory.newSAXParser();
        parser1.parse(xml, new PrintHandler()); // Insecure
        parser1.parse(xml, new PrintHandler()); // Insecure

        SAXParserFactory factory1 = SAXParserFactory.newInstance();
        factory1.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        SAXParser parser2 = factory1.newSAXParser();
        parser2.parse(xml, new PrintHandler()); // secure
        parser2.parse(xml, new PrintHandler()); // secure
    }

    public void case10() throws ParserConfigurationException, SAXException, IOException {
        String xml = "xxe.xml";
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser1 = factory.newSAXParser();
        parser1.parse(xml, new PrintHandler()); // Insecure
        parser1.parse(xml, new PrintHandler()); // Insecure

        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        SAXParser parser2 = factory.newSAXParser();
        parser1.parse(xml, new PrintHandler()); // Is still insecure; need to fix
        parser2.parse(xml, new PrintHandler()); // Secure

        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
        SAXParser parser3 = factory.newSAXParser();
        parser3.parse(xml, new PrintHandler()); // Insecure
        parser2.parse(xml, new PrintHandler()); // Is still secure; need to fix
    }

    private class Bar {
        public void setFeature(String s, boolean b) {

        }
    }

    public void case11() throws ParserConfigurationException, SAXException, IOException {
        Bar b = new Bar();
        b.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        String xml = "xxe.xml";
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser1 = factory.newSAXParser();
        parser1.parse(xml, new PrintHandler()); // Insecure
    }

}
