package org.soh.x4.x4tress_analyzer.model;

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

	private final List<Component> objectList;
	
	private final List<GlobalEvent> globalEvents;
	
	private List<ProcessedEvent> processedEvents;
	
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
	 * Get the list of processed SoH x4tress Events
	 * @return the Processed Events list
	 */
	public List<ProcessedEvent> getProcessedEvents() {
		return processedEvents;
	}

	public void setProcessedEvents(List<ProcessedEvent> processedEvents) {
		this.processedEvents = processedEvents;
	}
	
	

}
