package org.soh.x4.x4tress_analyzer.savegame.sax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soh.x4.x4tress_analyzer.model.Component;
import org.soh.x4.x4tress_analyzer.model.GlobalEvent;
import org.soh.x4.x4tress_analyzer.model.Position;
import org.xml.sax.Attributes;

/**
 * Savegame class representing the parts of the X4 savegame structure we
 * require.<br>
 * This class is intended only to be used for savegame parsing.<br>
 * To perform operations, transform it to anything in
 * <i>org.soh.x4.x4tress_analyzer.model<i> first.<b> This package more closely
 * resembles the data structures available in the X4 scripts as well.
 * 
 * @author Son of Hubert
 *
 */
public class Savegame {

	private static final Logger LOGGER = LoggerFactory.getLogger(Savegame.class);

	/**
	 * How to use: The idx values refer to table keys in the
	 * global.$SoHGlobalEvents List table entries in soh_x_fourtress, as defined in
	 * soh_observer.xml If the list definition in soh_x_fourtress changes, change
	 * this list accordingly, as well as the
	 * <i>globalEventFromListEntry(Map<Integer, ListValue> entry)</i> function and the GlobalEvent class,
	 * as well as the <i>resolveTableKeys()</i> function;
	 * 
	 * Unfortunately X4 can sometimes save multiple references for the same key.
	 * As such, each keyword can have multiple Integers referenced as keys. Hence why we save them as arrays.
	 * 
	 * A more elegant way would be to convert the table map into a string key map in a second step, just like in X4.
	 * 
	 */

	private List<Integer> idxTimestamp = new ArrayList<>();

	private List<Integer> idxEventType = new ArrayList<>();

	private List<Integer> idxAttackerId = new ArrayList<>();

	private List<Integer> idxAttacker = new ArrayList<>();
	
	private List<Integer> idxAttackerType = new ArrayList<>();

	private List<Integer> idxAttackerFaction = new ArrayList<>();

	private List<Integer> idxAttackedId = new ArrayList<>();

	private List<Integer> idxAttacked = new ArrayList<>();
	
	private List<Integer> idxAttackedType = new ArrayList<>();

	private List<Integer> idxTargetComponent = new ArrayList<>();

	private List<Integer> idxAttackedFaction = new ArrayList<>();

	private List<Integer> idxSector = new ArrayList<>();

	private List<Integer> idxAttackedPos = new ArrayList<>();

	private List<Component> objectList = new ArrayList<>();

	/**
	 * The table key map contains the mapping of table keys as Integer with their
	 * String references
	 */
	private Map<Integer, String> tableKeyMap = new HashMap<>();

	public Map<Integer, String> getTableKeyMap() {
		return tableKeyMap;
	}

	/**
	 * Resolves all table keys as Strings from the String map
	 * Saves the referenced Integer keys in idx values.
	 */
	public void resolveTableKeys() {
		for (Entry<Integer, String> entry : tableKeyMap.entrySet()) {
			Integer key = entry.getKey();
			String strValue = stringMap.get(key);
			entry.setValue(strValue);
			if (strValue != null && key > 0) {
				switch (strValue) {
				case "$timestamp":
					idxTimestamp.add(key);
					break;
				case "$eventType":
					idxEventType.add(key);
					break;
				case "$attackerId":
					idxAttackerId.add(key);
					break;
				case "$attacker":
					idxAttacker.add(key);
					break;
				case "$attackerType":
					idxAttackerType.add(key);
					break;
				case "$attackerFaction":
					idxAttackerFaction.add(key);
					break;
				case "$attackedId":
					idxAttackedId.add(key);
					break;
				case "$attacked":
					idxAttacked.add(key);
					break;
				case "$attackedType":
					idxAttackedType.add(key);
					break;
				case "$targetComponent":
					idxTargetComponent.add(key);
					break;
				case "$attackedFaction":
					idxAttackedFaction.add(key);
					break;
				case "$sector":
					idxSector.add(key);
					break;
				case "$attackedPos":
					idxAttackedPos.add(key);
					break;
				}
			}
		}
	}

	private int globalEventsListId;

	public List<Component> getObjectList() {
		return objectList;
	}

	/**
	 * The reference map containing String values.<br>
	 * In the X4 Savegame structure, all "String" values in a list are actually
	 * references to the stringMap.<br>
	 * Only a few String values are directly saved within the list. E.g.
	 * <i>"xmlkeyword"</i> (Reserved words in X4).<br>
	 * The key is an Integer ID.
	 */
	private Map<Integer, String> stringMap = new HashMap<>();

	/**
	 * The listMap is a Map of all List values saved in the X4 savegame.<br>
	 * List values contain a Type and a value. The value can be the value
	 * itself,<br>
	 * or as in most cases, a reference to a different value.<br>
	 * E.g. to the stringMap in case of the type <i>"string"</i>, or to a different
	 * List in case of the type <i>"list"</i>.
	 * For x4tress, we only need a single list, referenced in <i>globalEventsListId</i>.
	 */
	private Map<Integer, List<ListValue>> listMap = new HashMap<>();

	/**
	 * The tableMap is a Map of all table values saved in the X4 savegame.<br>
	 * List values contain a Type and a value. The value can be the value
	 * itself,<br>
	 * or as in most cases, a reference to a different value.<br>
	 * E.g. to the stringMap in case of the type <i>"string"</i>, or to a different
	 * List in case of the type <i>"list"</i>.
	 * Each value has a unique key.<br>
	 * <br>
	 * X4 tables are comparable to Java Maps
	 */
	private Map<Integer, Map<Integer, ListValue>> tableMap = new HashMap<>();

	/**
	 * The reference map containing position values.<br>
	 * In the X4 Savegame structure, all "position" values in a list are actually
	 * references to the positionMap.<br>
	 * The key is an Integer ID.
	 */
	private Map<Integer, Position> positionMap = new HashMap<>();

	/**
	 * Set the savegame reference ID for the SoHGlobalEvents list,<br>
	 * allowing us to link the contained lists to it.
	 * 
	 * @param refId the reference ID from the SaveGame
	 */
	public void initializeGlobalEventsList(int refId) {
		LOGGER.debug("SoHGlobalEvents list Id is " + refId + ".");
		if (refId > 0) {
			globalEventsListId = refId;
		}
	}

	/**
	 * Get the reference ID of the Global Events list in the savegame
	 * 
	 * @return
	 */
	public int getGlobalEventsListId() {
		return globalEventsListId;
	}

	/**
	 * Add a component element from the savegame if it is valid<br>
	 * A component is valid if it is of any type of Ship or a station station<br>
	 * 
	 * @param attr The Attributes from the xml <i>"component"</i>
	 */
	public void addComponentIfValid(Attributes attr) {
		String code = attr.getValue("code");
		String objectClass = attr.getValue("class");

		if (code != null && objectClass != null && (objectClass.contains("station") || objectClass.contains("ship"))) {
			objectList.add(new Component(attr));
		}
	}

	/**
	 * Get the list map containing all List entries
	 * 
	 * @return the list map
	 */
	public Map<Integer, List<ListValue>> getListMap() {
		return listMap;
	}

	/**
	 * Get the table map containing all Global Event entries
	 * 
	 * @return the list map
	 */
	public Map<Integer, Map<Integer, ListValue>> getTableMap() {
		return tableMap;
	}

	/**
	 * Get the string map containing all String reference entries
	 * 
	 * @return the string map
	 */
	public Map<Integer, String> getStringMap() {
		return stringMap;
	}

	/**
	 * Get the string map containing all String reference entries
	 * 
	 * @return the string map
	 */
	public Map<Integer, Position> getPositionMap() {
		return positionMap;
	}

	/**
	 * Returns the given column index value with its string map reference
	 * 
	 * @param idx  the column index
	 * @param list The GlobalEvents list entry
	 * @return the String value
	 */
	private String getReferenceStringValue(Integer idx, Map<Integer, ListValue> list) {
		if (idx == null || list == null) {
			return null;
		}
		Integer valueAsInteger = list.get(idx).getValueAsInteger();
		return stringMap.get(valueAsInteger);
	}

	/**
	 * Returns the given column index value with its Position map reference
	 * 
	 * @param idx  the column index
	 * @param list The GlobalEvents list entry
	 * @return the String value
	 */
	private Position getReferencePositionValue(int idx, Map<Integer, ListValue> list) {
		Integer valueAsInteger = list.get(idx).getValueAsInteger();
		return positionMap.get(valueAsInteger);
	}

	/**
	 * Creates a Global Event object from a Savegame table entry
	 * 
	 * @param entry The GlobalEvents table entry
	 * @return The GlobalEvents object
	 */
	public GlobalEvent globalEventFromListEntry(Map<Integer, ListValue> entry) throws IndexOutOfBoundsException {
		GlobalEvent event = new GlobalEvent();
		event.setTimestamp(entry.get(getTableKeyForEntry(entry, idxTimestamp)).getValueAsTimestamp());
		event.setEventType(getReferenceStringValue(getTableKeyForEntry(entry, idxEventType), entry));
		event.setAttackerId(getReferenceStringValue(getTableKeyForEntry(entry, idxAttackerId), entry));
		event.setAttacker(getReferenceStringValue(getTableKeyForEntry(entry, idxAttacker), entry));
		event.setAttackerType(entry.get(getTableKeyForEntry(entry, idxAttackerType)).getValueAsString());
		event.setAttackerFaction(getReferenceStringValue(getTableKeyForEntry(entry, idxAttackerFaction), entry));
		event.setAttackedId(getReferenceStringValue(getTableKeyForEntry(entry, idxAttackedId), entry));
		event.setAttacked(getReferenceStringValue(getTableKeyForEntry(entry, idxAttacked), entry));
		event.setAttackedType(entry.get(getTableKeyForEntry(entry, idxAttackedType)).getValueAsString());
		event.setTargetComponent(getReferenceStringValue(getTableKeyForEntry(entry, idxTargetComponent), entry));
		event.setAttackedFaction(getReferenceStringValue(getTableKeyForEntry(entry, idxAttackedFaction), entry));
		event.setSector(getReferenceStringValue(getTableKeyForEntry(entry, idxSector), entry));
		event.setAttackedPos(getReferencePositionValue(getTableKeyForEntry(entry, idxAttackedPos), entry));
		return event;
	}
	
	/**
	 * Finds the correct key for the desired value.<br>
	 * Ugly hack for the possibility that a table key (e.g. $timestamp) has different references in the savegame
	 * @param entry the map entry
	 * @param idxList the key list
	 * @return the correct key or null if no key was found.
	 */
	private Integer getTableKeyForEntry(Map<Integer, ListValue> entry, List<Integer> idxList) {
		for (Integer key : idxList) {
			if (entry.containsKey(key)) {
				return key;
			}
		}
		return null;
	}

}
