package org.soh.x4.x4tress_analyzer.model;

import java.sql.Timestamp;

/**
 * An Event entry containing all the text to be displayed for a given
 * {@link org.soh.x4.x4tress_analyzer.model.ProcessedEvent ProcessedEvent}
 * 
 * @author Son of Hubert
 *
 */
public class DisplayEvent {
	
	private Timestamp date;
	
	private String displayText;
	
	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getDisplayText() {
		return displayText;
	}

	public void setDisplayText(String displayText) {
		this.displayText = displayText;
	}
}
