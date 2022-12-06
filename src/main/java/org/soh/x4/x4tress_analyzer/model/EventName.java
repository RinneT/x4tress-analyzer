package org.soh.x4.x4tress_analyzer.model;

/**
 * Event Name object.
 * Keeps data to allow for complex generation of names.
 * 
 * @author Son of Hubert
 *
 */
public class EventName {
	
	/**
	 * The event number. Can e.g. be used to generate the "5. battle of <eventName>"
	 */
	private int eventNo;
	
	/**
	 * The event name
	 */
	private String eventName;
	
	public int getEventNo() {
		return eventNo;
	}
	public void setEventNo(int eventNo) {
		this.eventNo = eventNo;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	
	

}
