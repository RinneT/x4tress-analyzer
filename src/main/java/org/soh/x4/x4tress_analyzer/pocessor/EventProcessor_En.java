package org.soh.x4.x4tress_analyzer.pocessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.soh.x4.x4tress_analyzer.model.DisplayEvent;
import org.soh.x4.x4tress_analyzer.model.EventName;
import org.soh.x4.x4tress_analyzer.model.GlobalEvent;
import org.soh.x4.x4tress_analyzer.model.ProcessedEvent;

/**
 * Event Processor Class<br>
 * Converts a {@link org.soh.x4.x4tress_analyzer.model.ProcessedEvent
 * ProcessedEvent} into a {@link org.soh.x4.x4tress_analyzer.model.DisplayEvent
 * DisplayEvent}
 * 
 * @author Son of Hubert
 *
 */
public class EventProcessor_En {

	/**
	 * Generate text for a given Event for a given Unit
	 * 
	 * @param pEvent
	 * @return
	 */
	public DisplayEvent processEvent(ProcessedEvent pEvent, String unitCode) {
		if (pEvent != null) {
			String eventType = pEvent.getEventType();
			String text = null;
			if (eventType != null && unitCode != null) {
				switch (eventType) {
				case "battle":
					text = createBattleText(pEvent, unitCode, eventType);

				case "fight":
					text = createBattleText(pEvent, unitCode, eventType);

				case "skirmish":
					text = createBattleText(pEvent, unitCode, eventType);
				}
			}

			DisplayEvent displayEvent = new DisplayEvent();
			displayEvent.setDisplayText(text);
			return displayEvent;
		}
		return null;
	}

	/**
	 * Create battle specific text
	 * 
	 * @param pEvent
	 * @param unitCode
	 * @return the battle text
	 */
	private String createBattleText(ProcessedEvent pEvent, String unitCode, String eventType) {
		String text = unitCode + " took part in ";
		if (pEvent.getEventName() == null) {
			text = text + "a " + eventType + " with " + pEvent.getNumberOfParticipants() + " participants.\n";
		} else {
			text = text + " the " + generateEventName(pEvent.getEventName()) + "\n";
			text = text + " the " + pEvent.getEventType() + " counted " + pEvent.getNumberOfParticipants();
			
		}

		String significantParticipantsText = generateMajorParticipantsText(pEvent, unitCode);
		
		if (significantParticipantsText != null) {
			text = text + significantParticipantsText;
		}
		
		String majorEventsText = generateMajorEventsText(pEvent, unitCode);
		if (majorEventsText != null) {
			text = text + majorEventsText;
		}

		return text;
	}
	
	/**
	 * Generates a text block for major participants in a processed Event
	 * @param pEvent the processed event
	 * @param unitCode Unit Code of the currently selected unit
	 * @return the text block
	 */
	private String generateMajorParticipantsText(ProcessedEvent pEvent, String unitCode) {
		List<Entry<String,String>> majorShips = new ArrayList<>();
		for (Entry<String, String> participant : pEvent.getParticipants().entrySet()) {
			String shipType = participant.getValue();
			if (!unitCode.equals(participant.getKey()) && "destroyer".equals(shipType) || "battleship".equals(shipType)
					|| "carrier".equals(shipType) || "resupplier".equals(shipType)) {
				majorShips.add(participant);
			}
		}
		
		String majorShipText = "Other major participants were ";
		int majorShipNo = 0;
		int numberOfMajorShips = majorShips.size();
		
		for (Entry<String, String> ship : majorShips) {
			majorShipNo++;
			if (majorShipNo == numberOfMajorShips) {
				majorShipText = majorShipText + "and ";
			}
			majorShipText = majorShipText + "the " + ship.getValue() + " " + ship.getKey();
			if (majorShipNo < numberOfMajorShips - 1) {
				majorShipText = majorShipText + ",";
			}
			if (majorShipNo == numberOfMajorShips) {
				majorShipText = majorShipText + ".";
			}
			majorShipText = majorShipText + "\n";
		}
		
		if (numberOfMajorShips > 0) {
			return majorShipText + "\n";
		}
		return null;
	}
	
	/**
	 * Generates a text block for major Events of a given processed event
	 * @param pEvent the processed event
	 * @return the text block
	 */
	private String generateMajorEventsText(ProcessedEvent pEvent, String unitCode) {
		String majorEvents = "The " + pEvent.getEventType() + " resulted in the destruction of ";
		int majorEventNo = 0;
		int numberOfMajorEvents = pEvent.getMajorEvents().size();
		
		for (GlobalEvent gEvent : pEvent.getMajorEvents()) {
			majorEventNo++;
			majorEvents = majorEvents + " the " + gEvent.getAttackedFaction() + " " + gEvent.getAttacked() + " " + gEvent.getAttackedId();
			if (unitCode.equals(gEvent.getAttackerId())) {
				majorEvents = majorEvents + " by " + unitCode;
			}
			if (numberOfMajorEvents > 1 && numberOfMajorEvents == majorEventNo + 1) {
				majorEvents = majorEvents + " as well as ";
			} else if (numberOfMajorEvents > 1 && numberOfMajorEvents > majorEventNo + 1) {
				majorEvents = majorEvents + ", ";
			}
			majorEvents = majorEvents + "\n";
		}
		if (majorEventNo > 0) {
			return majorEvents + "\n";
		}
		return null;
	}
	
	/**
	 * Generate an eventName based on the EventName object
	 * @param eventName the Event Name object
	 * @return the generated name
	 */
	private String generateEventName(EventName eventName) {
		if (eventName.getEventNo() == 1) {
			return "first " + eventName;
		} else if (eventName.getEventNo() == 2) {
			return "second " + eventName;
		} else if (eventName.getEventNo() == 3) {
			return "third " + eventName;
		} else if (eventName.getEventNo() > 3) {
			return eventName.getEventNo() + ". " + eventName;
		}
		return eventName.getEventName();
	}

}
