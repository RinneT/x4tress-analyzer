package org.soh.x4.x4tress_analyzer.savegame.sax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soh.x4.x4tress_analyzer.model.Component;
import org.soh.x4.x4tress_analyzer.model.GlobalEvent;
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
	 * How to use: The IDX_ values refer to column indices in the
	 * global.$SoHGlobalEvents List in soh_x_fourtress, as defined in soh_observer.xml
	 * If the list definition in soh_x_fourtress changes, change this list
	 * accordingly, as well as the globalEventFromListEntry(List<ListValue>)
	 * function and the GlobalEvent class.
	 */

	private static final int IDX_TIMESTAMP = 0;

	private static final int IDX_EVENT_TYPE = 1;

	private static final int IDX_ATTACKER_ID = 2;

	private static final int IDX_ATTACKER = 3;

	private static final int IDX_ATTACKER_FACTION = 4;

	private static final int IDX_ATTACKED_ID = 5;

	private static final int IDX_ATTACKED = 6;

	private static final int IDX_TARGET_COMPONENT = 7;

	private static final int IDX_ATTACKED_FACTION = 8;

	private static final int IDX_SECTOR = 9;
	
	private static final int IDX_ATTACKED_POSX = 10;
	
	private static final int IDX_ATTACKED_POSY = 11;
	
	private static final int IDX_ATTACKED_POSZ = 12;

	private List<Component> objectList = new ArrayList<>();

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
	private HashMap<Integer, String> stringMap = new HashMap<>();

	/**
	 * The listMap is a Map of all List values saved in the X4 savegame.<br>
	 * List values contain a Type and a value. The value can be the value
	 * itself,<br>
	 * or as in most cases, a reference to a different value.<br>
	 * E.g. to the stringMap in case of the type <i>"string"</i>, or to a different
	 * List in case of the type <i>"list"</i>.
	 */
	private HashMap<Integer, List<ListValue>> listMap = new HashMap<>();

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
	 * Get the list map containing all Global Event entries
	 * 
	 * @return the list map
	 */
	public HashMap<Integer, List<ListValue>> getListMap() {
		return listMap;
	}
	
	/**
	 * Get the string map containing all String reference entries
	 * 
	 * @return the string map
	 */
	public HashMap<Integer, String> getStringMap() {
		return stringMap;
	}

	/**
	 * Returns the given column index value with its string map reference
	 * 
	 * @param idx       the column index
	 * @param list      The GlobalEvents list entry
	 * @param stringMap string reference map for String lookup
	 * @return the String value
	 */
	private String getReferenceValue(int idx, List<ListValue> list) {
		Integer valueAsInteger = list.get(idx).getValueAsInteger();
		return stringMap.get(valueAsInteger);
	}

	/**
	 * Creates a Global Event object from a Savegame List entry
	 * 
	 * @param list The GlobalEvents list entry
	 * @return The GlobalEvents object
	 */
	public GlobalEvent globalEventFromListEntry(List<ListValue> list) throws IndexOutOfBoundsException {
		GlobalEvent event = new GlobalEvent();
		event.setTimestamp(list.get(IDX_TIMESTAMP).getValueAsTimestamp());
		event.setEventType(getReferenceValue(IDX_EVENT_TYPE, list));
		event.setAttackerId(getReferenceValue(IDX_ATTACKER_ID, list));
		event.setAttacker(getReferenceValue(IDX_ATTACKER, list));
		event.setAttackerFaction(getReferenceValue(IDX_ATTACKER_FACTION, list));
		event.setAttackedId(getReferenceValue(IDX_ATTACKED_ID, list));
		event.setAttacked(getReferenceValue(IDX_ATTACKED, list));
		event.setTargetComponent(getReferenceValue(IDX_TARGET_COMPONENT, list));
		event.setAttackedFaction(getReferenceValue(IDX_ATTACKED_FACTION, list));
		event.setSector(getReferenceValue(IDX_SECTOR, list));
		event.setAttackedPosX(list.get(IDX_ATTACKED_POSX).getValueAsDouble());
		event.setAttackedPosY(list.get(IDX_ATTACKED_POSY).getValueAsDouble());
		event.setAttackedPosZ(list.get(IDX_ATTACKED_POSZ).getValueAsDouble());
		return event;
	}

}
