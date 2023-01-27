package org.soh.x4.x4tress_analyzer.pocessor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.soh.x4.x4tress_analyzer.model.DisplayEvent;
import org.soh.x4.x4tress_analyzer.model.EventName;
import org.soh.x4.x4tress_analyzer.model.GlobalEvent;
import org.soh.x4.x4tress_analyzer.model.ProcessedEvent;
import org.soh.x4.x4tress_analyzer.model.ShipInfo;

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
	
	private final String playerName;
	
	public EventProcessor_En(String playerName) {
		this.playerName = playerName;
	}

	/**
	 * Generate text for a given Event for a given Unit
	 * 
	 * @param pEvent
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public DisplayEvent processEvent(ProcessedEvent pEvent, String unitCode) {
		if (pEvent != null) {
			String eventType = pEvent.getEventType();
			String text = null;
			if (eventType != null && unitCode != null) {
				switch (eventType) {
				case "battle":
					text = createBattleText(pEvent, unitCode);

				case "fight":
					text = createBattleText(pEvent, unitCode);

				case "skirmish":
					text = createBattleText(pEvent, unitCode);
				}
			}

			DisplayEvent displayEvent = new DisplayEvent();
			Timestamp displayTime = pEvent.getStartTime();
			displayTime.setMinutes(0);
			displayEvent.setDate(displayTime);
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
	private String createBattleText(ProcessedEvent pEvent, String unitCode) {
		String text = unitCode + " took part in ";
		if (pEvent.getEventName() == null) {
			text = text + "a " + pEvent.getScale() + " " + pEvent.getEventType() + " in " + pEvent.getSector() + " between "
					+ factionsToList(pEvent.getFactions()) + ".\n";

			// " with " + pEvent.getNumberOfParticipants() + " participants.\n";
		} else {
			text = text + " the " + generateEventName(pEvent.getEventName()) + "\n";
			text = text + " the " + pEvent.getEventType() + " counted " + pEvent.getNumberOfParticipants();

		}

		text = text + createBattleStatistics(pEvent);
		String significantParticipantsText = generateMajorParticipantsText(pEvent, unitCode);

		if (significantParticipantsText != null) {
			text = text + significantParticipantsText;
		}

		String majorEventsText = generateMajorEventsText(pEvent, unitCode);
		if (majorEventsText != null) {
			text = text + majorEventsText;
		}
		
		String selectedStatistics = createSelectedStatistics(pEvent, unitCode);
		if (selectedStatistics != null) {
			text = text + selectedStatistics;
		}

		return text;
	}
	
	/**
	 * Create statistics individual for the selected unit
	 * @param pEvent the processed Event
	 * @param unitCode the select units id
	 * @return the statistics text
	 */
	private String createSelectedStatistics(ProcessedEvent pEvent, String unitCode) {
		if (unitCode != null) {
			ShipInfo shipInfo = pEvent.getParticipants().get(unitCode);
			return unitCode + " destroyed " + shipInfo.getNoOfKills() + " ships during this " + pEvent.getEventType() + ".\n";
		}
		return null;
	}

	/**
	 * Return basic statistics of a battle
	 * 
	 * @param pEvent the processed event
	 * @return the statistics text
	 */
	private String createBattleStatistics(ProcessedEvent pEvent) {
		String resultText = "The " + pEvent.getEventType() + " involved " + pEvent.getNumberOfParticipants() + " participants in total, of which ";
		Map<String, Integer> shipsPerFaction = new HashMap<>();
		for (ShipInfo ship : pEvent.getParticipants().values()) {
			String faction = ship.getShipFaction();
			Integer noOfShips = shipsPerFaction.get(faction);
			if (noOfShips == null) {
				noOfShips = 1;
			} else {
				noOfShips = noOfShips + 1;
			}
			shipsPerFaction.put(faction, noOfShips);
		}
		
		for (Entry<String, Integer> stat : shipsPerFaction.entrySet()) {
			if (stat.getValue() == 1) {
				resultText = resultText + stat.getValue() + " was fielded by " + writeFactionName(stat.getKey()) + ",\n";
			} else {
				resultText = resultText + stat.getValue() + " were fielded by " + writeFactionName(stat.getKey()) + ",\n"; 				
			}
		}
		
		resultText = resultText.substring(0, resultText.length() - 2) + ".\n";
		
		return resultText;
	}
	
	private String writeFactionName(String faction) {
		if (playerName.equals(faction)) {
			return faction;
		}
		return "the " + faction;
	}

	/**
	 * Create a faction list
	 * 
	 * @param stringList
	 * @return
	 */
	private String factionsToList(List<String> stringList) {
		String text = "";
		if (stringList != null) {
			for (int i = 0; i < stringList.size() - 1; i++) {
				text = text + writeFactionName(stringList.get(i)) + ", ";
			}

			text = text + "and " + writeFactionName(stringList.get(stringList.size() - 1));
		}
		return text;
	}

	/**
	 * Generates a text block for major participants in a processed Event
	 * 
	 * @param pEvent   the processed event
	 * @param unitCode Unit Code of the currently selected unit
	 * @return the text block
	 */
	private String generateMajorParticipantsText(ProcessedEvent pEvent, String unitCode) {
		/*
		 * Map structure: Parent map: All ships by faction Child map: All faction ships
		 * by class
		 */

		Map<String, Map<String, List<ShipInfo>>> majorShips = new HashMap<>();
		// Collect all ships and separate them by faction and class
		for (Entry<String, ShipInfo> participant : pEvent.getParticipants().entrySet()) {
			ShipInfo shipInfo = participant.getValue();
			String shipType = shipInfo.getShipType();
			String shipFaction = shipInfo.getShipFaction();
			if (!unitCode.equals(participant.getKey()) && "destroyer".equals(shipType) || "battleship".equals(shipType)
					|| "carrier".equals(shipType) || "resupplier".equals(shipType)) {

				// The ugly part, can this be cleaner?
				Map<String, List<ShipInfo>> factionMap = majorShips.get(shipFaction);
				List<ShipInfo> classList = null;
				if (factionMap == null) {
					factionMap = new HashMap<>();
				}
				classList = factionMap.get(shipType);
				if (classList == null) {
					classList = new ArrayList<ShipInfo>();
				}

				classList.add(participant.getValue());
				factionMap.put(shipType, classList);
				majorShips.put(shipFaction, factionMap);
			}
		}

		String majorShipText = "Major participants were";

		for (Entry<String, Map<String, List<ShipInfo>>> factionMap : majorShips.entrySet()) {
			majorShipText = majorShipText + "\nthe " + factionMap.getKey() + " ";
			for (Entry<String, List<ShipInfo>> classList : factionMap.getValue().entrySet()) {
				List<ShipInfo> ships = classList.getValue();

				// Add the ship class designation. If more than one ship is included, add an "s"
				// for plural.
				majorShipText = majorShipText + classList.getKey();
				if (ships.size() > 1) {
					majorShipText = majorShipText + "s";
				}

				majorShipText = majorShipText + " ";

				// List all ships
				for (ShipInfo ship : ships) {
					majorShipText = majorShipText + ship.getShipId() + ", ";
				}

			}

		}

		majorShipText = majorShipText.substring(0, majorShipText.length() - 2) + ".";

		if (!majorShips.isEmpty()) {
			return majorShipText + "\n";
		}
		return null;
	}

	/**
	 * Generates a text block for major Events of a given processed event
	 * 
	 * @param pEvent the processed event
	 * @return the text block
	 */
	private String generateMajorEventsText(ProcessedEvent pEvent, String unitCode) {
		String majorEvents = "The " + pEvent.getEventType() + " resulted in the destruction of";
		int majorEventNo = 0;
		int numberOfMajorEvents = pEvent.getMajorEvents().size();

		for (GlobalEvent gEvent : pEvent.getMajorEvents()) {
			majorEventNo++;
			majorEvents = majorEvents + " the " + gEvent.getAttackedFaction() + " " + gEvent.getAttackedType() + " "
					+ gEvent.getAttacked() + " " + gEvent.getAttackedId();
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
	 * 
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
