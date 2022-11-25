package org.soh.x4.x4tress_analyzer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data storage for x4tress emulation
 * 
 * @author Son of Hubert
 *
 */
public class DataStorage {
	
	public DataStorage(List<Component> objectList, List<GlobalEvent> globalEvents) {
		this.objectList = objectList;
		this.globalEvents = globalEvents;
	}

	private List<Component> objectList = new ArrayList<>();
	
	private List<GlobalEvent> globalEvents = new ArrayList<>();
	
	/**
	 * Get the list of loaded Objects (Ships / Stations)
	 * @return the object list
	 */
	public List<Component> getObjectList() {
		return objectList;
	}
	
	/**
	 * Get the list of loaded SoH x4tress Global Events
	 * @return the Global Events list
	 */
	public List<GlobalEvent> getGlobalEvents() {
		return globalEvents;
	}

}
