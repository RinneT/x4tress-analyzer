package org.soh.x4.x4tress_analyzer.savegame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soh.x4.x4tress_analyzer.savegame.sax.Savegame;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaveGameHandler extends DefaultHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveGameHandler.class);
	
	private static final String COMPONENT = "component";

	private StringBuilder elementValue = null;
	private Savegame savegame = null;
	
	private int componentsChecked = 0;

	@Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        } else {
            elementValue.append(ch, start, length);
        }
    }

    @Override
    public void startDocument() throws SAXException {
    	componentsChecked = 0;
    	savegame = new Savegame();
    }

    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException {
        switch (qName) {
            case COMPONENT:
            	componentsChecked++;
                savegame.addComponentIfValid(attr);
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case COMPONENT:
                break;
        }
    }
    
    public Savegame getComponents() {
    	if (savegame != null) {
    		LOGGER.info("Checked " + componentsChecked + " components.");
    		LOGGER.info("Loaded " + savegame.getObjectList().size() + " components of type Station or Ship.");    		
    	} else {
    		LOGGER.warn("Tried loading the components list before a file was parsed!");
    	}
    	return savegame;
    }

}
