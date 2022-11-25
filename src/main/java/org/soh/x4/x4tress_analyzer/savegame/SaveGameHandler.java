package org.soh.x4.x4tress_analyzer.savegame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soh.x4.x4tress_analyzer.model.DataStorage;
import org.soh.x4.x4tress_analyzer.model.GlobalEvent;
import org.soh.x4.x4tress_analyzer.savegame.sax.ListValue;
import org.soh.x4.x4tress_analyzer.savegame.sax.Savegame;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler to load data from a X4 savegame
 * 
 * @author Son of Hubert
 *
 */
public class SaveGameHandler extends DefaultHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(SaveGameHandler.class);

	private static final String TAG_COMPONENT = "component";

	private static final String TAG_VALUE = "value";

	// refs tag encloses the references used in scripts.
	// has a "type" attribute. E.g. type="string"
	private static final String TAG_REFS = "refs";

	private static final String TAG_REF = "ref";

	private static final String GLOBAL_EVENTS = "$SoHGlobalEvents";

	private StringBuilder elementValue = null;
	private Savegame savegame = null;

	private int componentsChecked = 0;

	private String currentRefsType = null;

	/**
	 * Lists can contain further sublists or immediate values. We save the current
	 * ListId to reference its values correctly.
	 */
	private Integer currentListId = null;

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
		case TAG_COMPONENT:
			componentsChecked++;
			savegame.addComponentIfValid(attr);
			break;
		case TAG_REFS:
			currentRefsType = attr.getValue("type");
		case TAG_REF:
			handleTagRef(attr);
			break;
		case TAG_VALUE:
			if ("list".equals(currentRefsType)) {
				handleTagValue(attr);
			} else {
				if (GLOBAL_EVENTS.equals(attr.getValue("name")) && "list".equals(attr.getValue("type"))) {
					Integer globlEventsReferenceId = Integer.parseInt(attr.getValue("value"));
					savegame.initializeGlobalEventsList(globlEventsReferenceId);
				}
			}
			break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (qName) {
		case TAG_COMPONENT:
			break;
		case TAG_REFS:
			currentRefsType = null;
			break;
		}
	}
	
	/**
	 * Convert the {@link org.soh.x4.x4tress_analyzer.savegame.sax.Savegame Savegame} object used during savegame parsing<br>
	 * into a {@link org.soh.x4.x4tress_analyzer.model.DataStorage DataStorage} object used for processing.
	 * @return the DataStorage object.
	 * @throws NullPointerException if the savegame was not successfully loaded
	 */
	public DataStorage getData() throws NullPointerException {
		DataStorage ds = null;
		if (savegame != null) {
			LOGGER.info("Checked " + componentsChecked + " components.");
			LOGGER.info("Loaded " + savegame.getObjectList().size() + " components of type Station or Ship.");
			
			ArrayList<GlobalEvent> globalEvents = new ArrayList<>();
			
			/**
			 * Steps:
			 * 1. Get the Global Events list. This only contains references to other list entries
			 * 2. For each entry, get the referenced ID and convert that to a Global Event
			 */
			HashMap<Integer, List<ListValue>> listMap = savegame.getListMap();
			List<ListValue> eventReference = listMap.get(savegame.getGlobalEventsListId());
			
			for (ListValue event : eventReference) {
				if ("list".equals(event.getType()) && event.getValue() != null) {
					try {
						List<ListValue> eventAsList = listMap.get(event.getValueAsInteger());
						GlobalEvent globalEventFromListEntry = savegame.globalEventFromListEntry(eventAsList);
						globalEvents.add(globalEventFromListEntry);																
					} catch (IndexOutOfBoundsException e) {
						LOGGER.error("Failed to create Global Event entry for Event reference Id '" + event.getValue() + "'!", e.getMessage());
					}
				} else {
					LOGGER.warn("Found a non-reference entry in the GlobalEvents reference table!");
				}
			}
			
			LOGGER.info("Loaded " + globalEvents.size() + " global Events.");
			
			ds = new DataStorage(savegame.getObjectList(), globalEvents);
			
		} else {
			LOGGER.warn("Tried loading the components list before a file was parsed!");
			throw new NullPointerException("Tried loading the components list before a file was parsed!");
		}
		return ds;
	}
	
	
	/**
	 * Read the required data from an xml tag
	 * @param attr The tag attributes
	 */
	private void handleTagRef(Attributes attr) {
		if (currentRefsType != null) {
			String strValue = null;
			Integer id = null;
			switch (currentRefsType) {
			case "string":
				strValue = attr.getValue("id");
				if (strValue != null) {
					id = Integer.parseInt(strValue);
					String value = attr.getValue("string");
					if (value != null) {
						savegame.getStringMap().put(id, value);
					}
				}
				break;
			case "list":
				strValue = attr.getValue("id");
				if (strValue != null) {
					id = Integer.parseInt(strValue);
						currentListId = id;
						savegame.getListMap().put(id, new ArrayList<>());
				}
			}
		}
	}

	/**
	 * Reads a value tag and saves it to the global list
	 * 
	 * @param attr
	 */
	private void handleTagValue(Attributes attr) {
		String valueType = attr.getValue("type");
		if (valueType != null) {
			Integer value = null;
			Double timeValue = null;
			Double lengthValue = null;
			String strValue = null;
			switch (valueType) {
			case "list":
				strValue = attr.getValue("value");
				if (strValue != null) {
					value = Integer.parseInt(strValue);
					if (currentListId != null && value != null) {
						List<ListValue> entry = savegame.getListMap().get(currentListId);
						if (entry != null) {
							entry.add(new ListValue(valueType, value));
						}
					}					
				}
				break;
			case "string":
				// In the case of a list, string is always a reference to savegame.stringMap!
				strValue = attr.getValue("value");
				if (strValue != null) {
					value = Integer.parseInt(strValue);
					if (currentListId != null && value != null) {
						List<ListValue> entry = savegame.getListMap().get(currentListId);
						if (entry != null) {
							entry.add(new ListValue(valueType, value));
						}
					}
				}
				break;
			case "time":
				strValue = attr.getValue("value");
				if (strValue != null) {
				timeValue = Double.valueOf(strValue);
					if (currentListId != null && timeValue != null) {
						List<ListValue> entry = savegame.getListMap().get(currentListId);
						if (entry != null) {
							entry.add(new ListValue(valueType, timeValue));
						}
					}
				}
				break;
			case "xmlkeyword":
				// xmlkeyword is an actual string, not referencing any value
				strValue = attr.getValue("value");
				if (currentListId != null && strValue != null) {
					List<ListValue> entry = savegame.getListMap().get(currentListId);
					if (entry != null) {
						entry.add(new ListValue(valueType, strValue));
					}
				}
				break;
			case "length":
				// xmlkeyword is an actual string, not referencing any value
				strValue = attr.getValue("value");
				if (strValue != null) {
					lengthValue = Double.valueOf(strValue);
						if (currentListId != null && lengthValue != null) {
							List<ListValue> entry = savegame.getListMap().get(currentListId);
							if (entry != null) {
								entry.add(new ListValue(valueType, lengthValue));
							}
						}
					}
				break;
			}
		}
	}

}
