package com.papao.books.view.util.importers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadXMLFile extends DefaultHandler {

    private final List<String[]> fileContents;
    private List<String> lineValues = new ArrayList<String>();
    private String valoare;
    private final String XML_SOURCE;
    private final String NEXT_LINE_TOKEN;

    public ReadXMLFile(final String XML_SOURCE, final String NEXT_LINE_TOKEN) throws SAXException, ParserConfigurationException, IOException {
        super();
        this.XML_SOURCE = XML_SOURCE;
        this.NEXT_LINE_TOKEN = NEXT_LINE_TOKEN;
        this.fileContents = new ArrayList<String[]>();
        parseDocument();
    }

    private void parseDocument() throws SAXException, ParserConfigurationException, IOException {

        // get a factory
        SAXParserFactory spf = SAXParserFactory.newInstance();

        // get a new instance of parser
        SAXParser sp = spf.newSAXParser();

        // parse the file and also register this class for call backs
        sp.parse(new File(this.XML_SOURCE).toURI().toString(), this);
    }

    // Event Handlers
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
        // reset
        this.valoare = "";
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.valoare = new String(ch, start, length);
        if (!this.valoare.equalsIgnoreCase(this.NEXT_LINE_TOKEN) && !this.valoare.startsWith(" \n ") && !this.valoare.trim().equals("")) {
            this.lineValues.add(this.valoare);
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (qName.equals(this.NEXT_LINE_TOKEN)) {
            this.fileContents.add(this.lineValues.toArray(new String[this.lineValues.size()]));
            this.lineValues.clear();
        }
    }

    public List<String[]> getFileContents() {
        return this.fileContents;
    }

}
