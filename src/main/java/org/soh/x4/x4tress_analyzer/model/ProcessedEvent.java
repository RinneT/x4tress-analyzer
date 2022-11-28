package org.soh.x4.x4tress_analyzer.model;

import java.sql.Timestamp;
import java.util.HashSet;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * An Event entry as processed by the EventProcessor This is the summary of
 * several {@link org.soh.x4.x4tress_analyzer.model.GlobalEvent GlobalEvents}.
 * 
 * @author Son of Hubert
 *
 */
public class ProcessedEvent {

	private String eventId = null; // Must be unique. Used to link GlobalEvents in the UI 
	private String eventType = null;
	private Integer numberOfEvents = 0;
	private String scale = null;
	private String sector = null;
	private Integer numberOfParticipants = null;
	private HashSet<String> participants = new HashSet<>();
	private Timestamp startTime = null;
	private Timestamp endTime = null;
	/**
	 * Positional Center of the event
	 */
	private Position center = null;

	/**
	 * Creates an empty JavaFX TableView for representation of the ProcessedEvent in
	 * the UI
	 * 
	 * @return the TableView of GlobalEvent
	 */
	@SuppressWarnings("unchecked")
	public static TableView<ProcessedEvent> createUITable() {
		TableView<ProcessedEvent> eventTable = new TableView<>();
		// eventTable.setPrefWidth(1000);

		TableColumn<ProcessedEvent, String> eventType = new TableColumn<>("Event Type");
		eventType.setPrefWidth(80);
		eventType.setCellValueFactory(new PropertyValueFactory<ProcessedEvent, String>("eventType"));
		
		TableColumn<ProcessedEvent, Integer> numberOfEvents = new TableColumn<>("# of Events");
		numberOfEvents.setPrefWidth(80);
		numberOfEvents.setCellValueFactory(new PropertyValueFactory<ProcessedEvent, Integer>("numberOfEvents"));

		TableColumn<ProcessedEvent, String> scale = new TableColumn<>("Event Scale");
		scale.setPrefWidth(100);
		scale.setCellValueFactory(new PropertyValueFactory<ProcessedEvent, String>("scale"));

		TableColumn<ProcessedEvent, String> sector = new TableColumn<>("Sector");
		sector.setPrefWidth(150);
		sector.setCellValueFactory(new PropertyValueFactory<ProcessedEvent, String>("sector"));

		TableColumn<ProcessedEvent, Integer> numberOfParticipants = new TableColumn<>("Participants");
		numberOfParticipants.setPrefWidth(80);
		numberOfParticipants
				.setCellValueFactory(new PropertyValueFactory<ProcessedEvent, Integer>("numberOfParticipants"));

		TableColumn<ProcessedEvent, Timestamp> startTime = new TableColumn<>("Start time");
		startTime.setPrefWidth(160);
		startTime.setCellValueFactory(new PropertyValueFactory<ProcessedEvent, Timestamp>("startTime"));

		TableColumn<ProcessedEvent, Timestamp> endTime = new TableColumn<>("End time");
		endTime.setPrefWidth(160);
		endTime.setCellValueFactory(new PropertyValueFactory<ProcessedEvent, Timestamp>("endTime"));

		eventTable.getColumns().addAll(eventType, numberOfEvents, scale, sector, numberOfParticipants, startTime, endTime);

		return eventTable;
	}
	
	/**
	 * Checks if a given unit (ship / station) is involved in this Global Event
	 * 
	 * @param unitCode The unit code to compare to
	 * @return true if the unit is involved in this event.
	 */
	public Boolean matchesUnit(String unitCode) {
		if (unitCode != null) {
			return participants.contains(unitCode);
		}
		return false;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Integer getNumberOfEvents() {
		return numberOfEvents;
	}

	public void setNumberOfEvents(Integer numberOfEvents) {
		this.numberOfEvents = numberOfEvents;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public Integer getNumberOfParticipants() {
		return numberOfParticipants;
	}

	public HashSet<String> getParticipants() {
		return participants;
	}

	public void addParticipant(String participant) {
		this.participants.add(participant);
		numberOfParticipants = participants.size();
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public Position getCenter() {
		return center;
	}

	public void setCenter(Position center) {
		this.center = center;
	}

}
