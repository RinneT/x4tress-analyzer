package org.soh.x4.x4tress_analyzer.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	

	private final List<Component> objectList;
	
	private final List<GlobalEvent> globalEvents;
	
	private final Map<String, Integer> eventNames = new HashMap<>();
	
	private List<ProcessedEvent> processedEvents;
	
	private List<DisplayEvent> displayEvents;
	
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
	
	/**
	 * Get the current highest number for all known event names<br>
	 * 	
	 * @return the Event Names Map
	 */
	public Map<String,Integer> getEventNames() {
		return eventNames;
	}

	/**
	 * Get the list of processed SoH x4tress Events
	 * @return the Processed Events list
	 */
	public List<ProcessedEvent> getProcessedEvents() {
		return processedEvents;
	}

	public void setProcessedEvents(List<ProcessedEvent> processedEvents) {
		this.processedEvents = processedEvents;
	}
	
	/**
	 * Get the list of SoH x4tress Events ready for display
	 * @return the Display Events list
	 */
	public List<DisplayEvent> getDisplayEvents() {
		return displayEvents;
	}

	public void setDisplayEvents(List<DisplayEvent> displayEvents) {
		this.displayEvents = displayEvents;
	}
	
	

}
