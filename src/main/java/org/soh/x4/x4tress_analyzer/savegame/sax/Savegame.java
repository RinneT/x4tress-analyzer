package org.soh.x4.x4tress_analyzer.savegame.sax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.soh.x4.x4tress_analyzer.model.Component;
import org.xml.sax.Attributes;

/**
 * Savegame class representing the parts of the X4 savegame structure we require.<br>
 * This class is intended only to be used for savegame parsing.<br>
 * To perform operations, transform it to anything in <i>org.soh.x4.x4tress_analyzer.model<i> first.<b>
 * This package more closely resembles the data structures available in the X4 scripts as well.
 * 
 * @author Son of Hubert
 *
 */
public class Savegame {
	
	private List<Component> objectList = new ArrayList<>();

	private int globalEventsListId;
	
	public List<Component> getObjectList() {
		return objectList;
	}
	
	/**
	 * The reference map containing String values.<br>
	 * In the X4 Savegame structure, all "String" values in a list are actually references to the stringMap.<br>
	 * Only a few String values are directly saved within the list. E.g. <i>"xmlkeyword"</i>.<br>
	 * The key is an Integer ID.
	 */
	public HashMap<Integer, String> stringMap = new HashMap<>();
	
	/**
	 * The listMap is a Map of all List values saved in the X4 savegame.<br>
	 * List values contain a Type and a value. The value can be the value itself,<br>
	 * or as in most cases, a reference to a different value.<br>
	 * E.g. to the stringMap in case of the type <i>"string"</i>, or to a different List in case of the type <i>"list"</i>.
	 */
	public HashMap<Integer, List<ListValue>> listMap = new HashMap<>();
	
	/**
	 * Set the savegame reference ID for the GlobalEvents list,<br>
	 * allowing us to link the contained lists to it.
	 * @param refId the reference ID from the SaveGame
	 */
	public void initializeGlobalEventsList(int refId) {
		if (refId > 0) {
			globalEventsListId = refId;
		}
	}
	
	public int getGlobalEventsListId() {
		return globalEventsListId;
	}
	
	/**
	 * Add a component element from the savegame if it is valid<br>
	 * A component is valid if it is of any type of Ship or a station station<br>
	 * @param attr The Attributes from the xml <i>"component"</i>
	 */
	public void addComponentIfValid(Attributes attr) {
		String code = attr.getValue("code");
		String objectClass = attr.getValue("class");
		
		if (code != null && objectClass != null && (objectClass.contains("station") || objectClass.contains("ship"))) {
			objectList.add(new Component(attr));
		}
	}
}
