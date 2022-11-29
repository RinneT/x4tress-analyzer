package org.soh.x4.x4tress_analyzer.savegame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soh.x4.x4tress_analyzer.model.DataStorage;
import org.soh.x4.x4tress_analyzer.model.GlobalEvent;
import org.soh.x4.x4tress_analyzer.model.Position;
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
	
	// represents a table key
	private static final String TAG_KEY = "key";

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

	/**
	 * Tables can contain further sublists, tables, or immediate values. We save the
	 * current TableId to reference its values correctly.
	 */
	private Integer currentTableId = null;

	/**
	 * Tables keys are always one line before the respective value in the savefile
	 * We use only Strings as keys in x4tress, which are of course represented as Integer
	 * references here.
	 */
	private Integer currentTableKey = null;

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
			if ("list".equals(currentRefsType) || "table".equals(currentRefsType)) {
				handleTagValue(attr);
			} else {
				if (GLOBAL_EVENTS.equals(attr.getValue("name")) && "list".equals(attr.getValue("type"))) {
					Integer globlEventsReferenceId = Integer.parseInt(attr.getValue("value"));
					savegame.initializeGlobalEventsList(globlEventsReferenceId);
				}
			}
			break;
		case TAG_KEY:
			// Table keys
			handleTagKey(attr);
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
		case TAG_REF:
			currentListId = null;
			currentTableId = null;
		}
	}

	/**
	 * Convert the {@link org.soh.x4.x4tress_analyzer.savegame.sax.Savegame
	 * Savegame} object used during savegame parsing<br>
	 * into a {@link org.soh.x4.x4tress_analyzer.model.DataStorage DataStorage}
	 * object used for processing.
	 * 
	 * @return the DataStorage object.
	 * @throws NullPointerException if the savegame was not successfully loaded
	 */
	public DataStorage getData() throws NullPointerException {
		DataStorage ds = null;
		if (savegame != null) {
			LOGGER.info("Checked " + componentsChecked + " components.");
			LOGGER.info("Loaded " + savegame.getObjectList().size() + " components of type Station or Ship.");

			ArrayList<GlobalEvent> globalEvents = new ArrayList<>();
			
			savegame.resolveTableKeys();

			/**
			 * Steps: 1. Get the Global Events list. This only contains references to other
			 * list entries 2. For each entry, get the referenced ID and convert that to a
			 * Global Event
			 */
			Map<Integer, List<ListValue>> listMap = savegame.getListMap();
			Map<Integer, Map<Integer, ListValue>> tableMap = savegame.getTableMap();			
			List<ListValue> eventReference = listMap.get(savegame.getGlobalEventsListId());

			for (ListValue event : eventReference) {
				if ("table".equals(event.getType()) && event.getValue() != null) {
					try {
						Map<Integer, ListValue> eventAsList = tableMap.get(event.getValueAsInteger());
						GlobalEvent globalEventFromListEntry = savegame.globalEventFromListEntry(eventAsList);
						globalEvents.add(globalEventFromListEntry);
					} catch (IndexOutOfBoundsException e) {
						LOGGER.error("Failed to create Global Event entry for Event reference Id '" + event.getValue()
								+ "'!", e.getMessage());
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
	 * 
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
				break;
			case "table":
				strValue = attr.getValue("id");
				if (strValue != null) {
					id = Integer.parseInt(strValue);
					currentTableId = id;
					savegame.getTableMap().put(id, new HashMap<Integer, ListValue>());
				}
				break;
			case "vector":
				strValue = attr.getValue("id");
				if (strValue != null) {
					id = Integer.parseInt(strValue);
					String valueX = attr.getValue("x");
					String valueY = attr.getValue("y");
					String valueZ = attr.getValue("z");
					if (valueX != null && valueY != null && valueZ != null) {
						savegame.getPositionMap().put(id, new Position(Double.parseDouble(valueX),
								Double.parseDouble(valueY), Double.parseDouble(valueZ)));
					}
				}
				break;
			}
		}
	}

	/**
	 * Add a value to a list or table<br>
	 * Determines what to add it to depending on<br>
	 * currentListId, currentTableId and currentTableKey
	 * @param valueType the value type
	 * @param value the value
	 */
	private void addToListOrTable(String valueType, Object value) {
		if (value != null) {
			if (currentListId != null) {
				List<ListValue> entry = savegame.getListMap().get(currentListId);
				if (entry != null) {
					entry.add(new ListValue(valueType, value));
				}
			} else if (currentTableId != null && currentTableKey != null) {
				Map<Integer, ListValue> entry = savegame.getTableMap().get(currentTableId);
				if (entry != null) {
					entry.put(currentTableKey, new ListValue(valueType, value));
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
					addToListOrTable(valueType, value);
				}
				break;
			case "table":
				strValue = attr.getValue("value");
				if (strValue != null) {
					value = Integer.parseInt(strValue);
					addToListOrTable(valueType, value);
				}
				break;
			case "string":
				// In the case of a list, string is always a reference to savegame.stringMap!
				strValue = attr.getValue("value");
				if (strValue != null) {
					value = Integer.parseInt(strValue);
					addToListOrTable(valueType, value);
				}
				break;
			case "time":
				strValue = attr.getValue("value");
				if (strValue != null) {
					timeValue = Double.valueOf(strValue);
					addToListOrTable(valueType, timeValue);
				}
				break;
			case "xmlkeyword":
				// xmlkeyword is an actual string, not referencing any value
				strValue = attr.getValue("value");
				addToListOrTable(valueType, strValue);
				break;
			case "shiptype":
				// xmlkeyword is an actual string, not referencing any value
				strValue = attr.getValue("value");
				addToListOrTable(valueType, strValue);
				break;
			case "class":
				// xmlkeyword is an actual string, not referencing any value
				strValue = attr.getValue("value");
				addToListOrTable(valueType, strValue);
				break;
			case "length":
				// length is an double value representing a distance
				strValue = attr.getValue("value");
				if (strValue != null) {
					lengthValue = Double.valueOf(strValue);
					addToListOrTable(valueType, lengthValue);
				}
				break;
			case "position":
				// In the case of a position, string is always a reference to
				// savegame.positionMap!
				strValue = attr.getValue("value");
				if (strValue != null) {
					value = Integer.parseInt(strValue);
					addToListOrTable(valueType, value);
				}
				break;
			}
		}
	}
	
	/**
	 * Reads a key tag and saves it to the current table key
	 * 
	 * @param attr
	 */
	private void handleTagKey(Attributes attr) {
		String valueType = attr.getValue("type");
		if (valueType != null) {
			String strValue = null;
			/*
			 * Theoretically, the key should be a ListValue, as X4 supports different Key types,
			 * but for x4tress we use only String
			 */
			switch (valueType) {
			case "string":
				// In the case of a list, string is always a reference to savegame.stringMap!
				strValue = attr.getValue("value");
				if (strValue != null) {
					currentTableKey = Integer.parseInt(strValue);
					// Add the key to the table key map for later String reference mapping
					savegame.getTableKeyMap().put(currentTableKey, null);
				}
				break;
			}
		}
	}

}
