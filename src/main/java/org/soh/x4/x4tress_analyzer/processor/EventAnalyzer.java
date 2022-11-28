package org.soh.x4.x4tress_analyzer.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soh.x4.x4tress_analyzer.model.GlobalEvent;
import org.soh.x4.x4tress_analyzer.model.Position;
import org.soh.x4.x4tress_analyzer.model.ProcessedEvent;

/**
 * The Event Analyzer.<br>
 * <br>
 * Analyzes the list of {@link org.soh.x4.x4tress_analyzer.model.GlobalEvent
 * GlobalEvents} and combines them into
 * {@link org.soh.x4.x4tress_analyzer.model.ProcessedEvent ProcessedEvents}
 * 
 * The Event Processor is also responsible for clearing up deprecated data /
 * keeping memory in check.
 * 
 * This is the reference implementation for soh_analyzer.xml in soh_x4tress
 * 
 * @author Son of Hubert
 *
 */
public class EventAnalyzer {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventAnalyzer.class);

	/**
	 * The maximum time that may pass for two Global Events to be combined in
	 * milliseconds;
	 */
	private static final long EVENT_MAX_TIME = 60000 * 5; // 5min

	/**
	 * The maximum range in meters for two Global Events to be combined
	 */
	private static final double EVENT_MAX_RANGE = 30000.0; // 30km

	/**
	 * Event size to be classified as a battle
	 */
	private static final int MIN_SIZE_FIGHT = 2;

	/**
	 * Event size to be classified as a skirmish
	 */
	private static final int MIN_SIZE_SKIRMISH = 4;

	/**
	 * Event size to be classified as a battle
	 */
	private static final int MIN_SIZE_BATTLE = 30;

	/**
	 * Process the Global Events
	 * 
	 * @param globalEvents
	 */
	public List<ProcessedEvent> processGlobalEvents(List<GlobalEvent> globalEvents) {

		LOGGER.debug("Starting to process " + globalEvents.size() + " global events!");
		List<ProcessedEvent> processedEvents = new ArrayList<>();

		// Split events by sector
		HashMap<String, List<GlobalEvent>> eventsBySector = new HashMap<>();

		for (GlobalEvent events : globalEvents) {
			String sector = events.getSector();
			List<GlobalEvent> sectorEvents = eventsBySector.get(sector);
			if (sectorEvents == null) {
				sectorEvents = new ArrayList<>();
			}

			sectorEvents.add(events);
			eventsBySector.put(sector, sectorEvents);

		}

		LOGGER.debug("Recorded Events in " + eventsBySector.size() + " sectors!");

		// Collect / summarize the events for each sector
		for (List<GlobalEvent> sectorEvents : eventsBySector.values()) {
			processedEvents.addAll(collectSectorEvents(sectorEvents));
		}

		// Post Process the Events
		for (ProcessedEvent pEvent : processedEvents) {
			Integer numberOfParticipants = pEvent.getNumberOfParticipants();
			if (numberOfParticipants < MIN_SIZE_FIGHT) {
				pEvent.setEventType("nothing");
				pEvent.setScale("false");
			} else if (numberOfParticipants >= MIN_SIZE_FIGHT && numberOfParticipants < MIN_SIZE_SKIRMISH) {
				pEvent.setEventType("fight");
				pEvent.setScale("tiny");
			} else if (numberOfParticipants >= MIN_SIZE_SKIRMISH && numberOfParticipants < MIN_SIZE_BATTLE) {
				pEvent.setEventType("skirmish");
				pEvent.setScale("medium");
			} else if (numberOfParticipants >= MIN_SIZE_BATTLE) {
				pEvent.setEventType("battle");
				pEvent.setScale("large");
			}
		}

		return processedEvents;
	}

	/**
	 * 
	 * @param sectorEvents
	 * @return
	 */
	private List<ProcessedEvent> collectSectorEvents(List<GlobalEvent> sectorEvents) {
		ArrayList<ProcessedEvent> processedEvents = new ArrayList<ProcessedEvent>();
		LOGGER.info(
				"Processing " + sectorEvents.size() + " events for sector '" + sectorEvents.get(0).getSector() + "'!");

		for (GlobalEvent sectorEvent : sectorEvents) {
			boolean foundEvent = false;
			// Check if any existing processed Events are in range and in time
			for (ProcessedEvent pEvent : processedEvents) {
				foundEvent = belongsToEvent(pEvent, sectorEvent);
				if (foundEvent) {
					break;
				}
			}

			// If no processed events exist, create a new one (unless the position is unknown!)
			if (!foundEvent && sectorEvent.getAttackedPos() != null) {
				LOGGER.debug("Creating new Event in sector '" + sectorEvents.get(0).getSector() + "'!");
				ProcessedEvent pEvent = new ProcessedEvent();
				pEvent.setSector(sectorEvent.getSector());
				pEvent.setNumberOfEvents(1);
				pEvent.setStartTime(sectorEvent.getTimestamp());
				pEvent.setEndTime(sectorEvent.getTimestamp());
				pEvent.setCenter(sectorEvent.getAttackedPos());
				pEvent.addParticipant(sectorEvent.getAttackerId());
				pEvent.addParticipant(sectorEvent.getAttackedId());
				processedEvents.add(pEvent);
			}
		}

		return processedEvents;
	}

	/**
	 * Checks if a Global Event belongs to a Processed event and manipulates the
	 * processed Event if true
	 * 
	 * @param pEvent the processed Event
	 * @param gEvent the global Event
	 * @return true if the Global Event should be considered as part of the
	 *         Processed Event
	 */
	private boolean belongsToEvent(ProcessedEvent pEvent, GlobalEvent gEvent) {
		if (IsInTime(pEvent, gEvent) && isInDistance(pEvent, gEvent)) {
			// if within distance and time, shift the processed events center point
			// according to its weight
			LOGGER.debug("Adding Global Event at timestamp '" + gEvent.getTimestamp()
					+ "' to processed Event starting at " + pEvent.getStartTime() + "!");
			pEvent = ShiftEventCenter(pEvent, gEvent);
			pEvent.setEndTime(gEvent.getTimestamp());
			pEvent.addParticipant(gEvent.getAttackerId());
			pEvent.addParticipant(gEvent.getAttackedId());
			pEvent.setNumberOfEvents(pEvent.getNumberOfEvents() + 1);
			return true;
		}
		return false;
	}

	/**
	 * Checks if a given Global Event is within time range of a processed Event.<br>
	 * 
	 * @param pEvent the processed Event
	 * @param gEvent the global Event
	 * @return true if the global Event is within time range
	 */
	private boolean IsInTime(ProcessedEvent pEvent, GlobalEvent gEvent) {
		return (gEvent.getTimestamp().getTime() - pEvent.getEndTime().getTime()) < EVENT_MAX_TIME;
	}

	/**
	 * Checks if a given Global Event is within range of a processed Event<br>
	 * 
	 * @param pEvent the processed Event
	 * @param gEvent the global Event
	 * @return true if the global Event is within range
	 */
	private boolean isInDistance(ProcessedEvent pEvent, GlobalEvent gEvent) {
		// Check distance
		Position center = pEvent.getCenter();
		Position attackedPos = gEvent.getAttackedPos();
		if (attackedPos == null || center == null) {
			LOGGER.error("Processed Event " + pEvent.getStartTime() + " center is : " + pEvent.getCenter()
					+ " Global Event " + gEvent.getTimestamp() + " Attacked Pos is: " + gEvent.getAttackedPos());
			return false;
		}
		double distance = Math.sqrt(Math.pow(attackedPos.getX() - center.getX(), 2)
				+ Math.pow(attackedPos.getY() - center.getY(), 2) + Math.pow(attackedPos.getZ() - center.getZ(), 2));

		return distance < EVENT_MAX_RANGE;
	}

	/**
	 * Shift the center of a processed Event by including a new global Event
	 * 
	 * @param pEvent the Processed Event
	 * @param gEvent the Global Event
	 * @return the Processed Event
	 */
	private ProcessedEvent ShiftEventCenter(ProcessedEvent pEvent, GlobalEvent gEvent) {
		Integer numberOfEvents = pEvent.getNumberOfEvents();
		Position center = pEvent.getCenter();
		Double centerX = center.getX() * numberOfEvents;
		Double centerY = center.getY() * numberOfEvents;
		Double centerZ = center.getZ() * numberOfEvents;

		centerX = centerX + gEvent.getAttackedPos().getX();
		centerY = centerY + gEvent.getAttackedPos().getY();
		centerZ = centerZ + gEvent.getAttackedPos().getZ();

		numberOfEvents++;
		centerX = centerX / numberOfEvents;
		centerY = centerY / numberOfEvents;
		centerZ = centerZ / numberOfEvents;

		LOGGER.debug("Shifting center of processed Event starting at '" + pEvent.getStartTime() + "' from x: "
				+ pEvent.getCenter().getX() + " y: " + pEvent.getCenter().getY() + " z: " + pEvent.getCenter().getZ()
				+ " to x: " + centerX + " y: " + centerY + " z: " + centerZ + "!");

		center.setX(centerX);
		center.setX(centerY);
		center.setX(centerZ);
		pEvent.setCenter(center);

		return pEvent;
	}
}
