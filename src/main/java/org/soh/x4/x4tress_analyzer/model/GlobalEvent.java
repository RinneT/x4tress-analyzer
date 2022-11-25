package org.soh.x4.x4tress_analyzer.model;

import java.sql.Timestamp;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Representation of a Global Event entry from x4tress.<br>
 * Any changes here must also reflect in
 * {@link org.soh.x4.x4tress_analyzer.savegame.sax.Savegame Savegame}
 * 
 * 
 * @author Son of Hubert
 *
 */
public class GlobalEvent {

	private Timestamp timestamp;
	private String eventType;
	private String attackerId;
	private String attacker;
	private String attackerFaction;
	private String attackedId;
	private String attacked;
	private String targetComponent;
	private String attackedFaction;
	private String sector;
	private Double attackedPosX;
	private Double attackedPosY;
	private Double attackedPosZ;

	/**
	 * Creates an empty JavaFX TableView for representation of the GlobalEvent in
	 * the UI
	 * 
	 * @return the TableView of GlobalEvent
	 */
	@SuppressWarnings("unchecked")
	public static TableView<GlobalEvent> createUITable() {
		TableView<GlobalEvent> eventTable = new TableView<>();
		// eventTable.setPrefWidth(1000);

		TableColumn<GlobalEvent, Timestamp> timestamp = new TableColumn<>("Timestamp");
		timestamp.setPrefWidth(160);
		timestamp.setCellValueFactory(new PropertyValueFactory<GlobalEvent, Timestamp>("timestamp"));

		TableColumn<GlobalEvent, String> eventType = new TableColumn<>("Event Type");
		eventType.setPrefWidth(80);
		eventType.setCellValueFactory(new PropertyValueFactory<GlobalEvent, String>("eventType"));

		TableColumn<GlobalEvent, String> attackerId = new TableColumn<>("Attacker Id");
		attackerId.setPrefWidth(80);
		attackerId.setCellValueFactory(new PropertyValueFactory<GlobalEvent, String>("attackerId"));

		TableColumn<GlobalEvent, String> attacker = new TableColumn<>("Attacker Name");
		attacker.setPrefWidth(200);
		attacker.setCellValueFactory(new PropertyValueFactory<GlobalEvent, String>("attacker"));

		TableColumn<GlobalEvent, String> attackerFaction = new TableColumn<>("Attacker Faction");
		attackerFaction.setPrefWidth(150);
		attackerFaction.setCellValueFactory(new PropertyValueFactory<GlobalEvent, String>("attackerFaction"));

		TableColumn<GlobalEvent, String> attackedId = new TableColumn<>("Attacked Id");
		attackedId.setPrefWidth(80);
		attackedId.setCellValueFactory(new PropertyValueFactory<GlobalEvent, String>("attackedId"));

		TableColumn<GlobalEvent, String> attacked = new TableColumn<>("Attacker Name");
		attacked.setPrefWidth(200);
		attacked.setCellValueFactory(new PropertyValueFactory<GlobalEvent, String>("attacked"));

		TableColumn<GlobalEvent, String> targetComponent = new TableColumn<>("Target Component");
		targetComponent.setPrefWidth(200);
		targetComponent.setCellValueFactory(new PropertyValueFactory<GlobalEvent, String>("targetComponent"));

		TableColumn<GlobalEvent, String> attackedFaction = new TableColumn<>("Attacked Faction");
		attackedFaction.setPrefWidth(150);
		attackedFaction.setCellValueFactory(new PropertyValueFactory<GlobalEvent, String>("attackedFaction"));

		TableColumn<GlobalEvent, String> sector = new TableColumn<>("Sector");
		sector.setPrefWidth(150);
		sector.setCellValueFactory(new PropertyValueFactory<GlobalEvent, String>("sector"));
		
		TableColumn<GlobalEvent, Double> attackedPosX = new TableColumn<>("Pos X");
		attackedPosX.setPrefWidth(80);
		attackedPosX.setCellValueFactory(new PropertyValueFactory<GlobalEvent, Double>("attackedPosX"));
		
		TableColumn<GlobalEvent, Double> attackedPosY = new TableColumn<>("Pos Y");
		attackedPosY.setPrefWidth(80);
		attackedPosY.setCellValueFactory(new PropertyValueFactory<GlobalEvent, Double>("attackedPosY"));
		
		TableColumn<GlobalEvent, Double> attackedPosZ = new TableColumn<>("Pos Z");
		attackedPosZ.setPrefWidth(80);
		attackedPosZ.setCellValueFactory(new PropertyValueFactory<GlobalEvent, Double>("attackedPosZ"));
	

		eventTable.getColumns().addAll(timestamp, eventType, attackerId, attacker, attackerFaction, attackedId,
				attacked, targetComponent, attackedFaction, sector, attackedPosX, attackedPosY, attackedPosZ);

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
			if (unitCode.equals(attackerId) || unitCode.equals(attackedId)) {
				return true;
			}
		}
		return false;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getAttackerId() {
		return attackerId;
	}

	public void setAttackerId(String attackerId) {
		this.attackerId = attackerId;
	}

	public String getAttacker() {
		return attacker;
	}

	public void setAttacker(String attacker) {
		this.attacker = attacker;
	}

	public String getAttackerFaction() {
		return attackerFaction;
	}

	public void setAttackerFaction(String attackerFaction) {
		this.attackerFaction = attackerFaction;
	}

	public String getAttackedId() {
		return attackedId;
	}

	public void setAttackedId(String attackedId) {
		this.attackedId = attackedId;
	}

	public String getAttacked() {
		return attacked;
	}

	public void setAttacked(String attacked) {
		this.attacked = attacked;
	}

	public String getTargetComponent() {
		return targetComponent;
	}

	public void setTargetComponent(String targetComponent) {
		this.targetComponent = targetComponent;
	}

	public String getAttackedFaction() {
		return attackedFaction;
	}

	public void setAttackedFaction(String attackedFaction) {
		this.attackedFaction = attackedFaction;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public Double getAttackedPosX() {
		return attackedPosX;
	}

	public void setAttackedPosX(Double attackedPosX) {
		this.attackedPosX = attackedPosX;
	}

	public Double getAttackedPosY() {
		return attackedPosY;
	}

	public void setAttackedPosY(Double attackedPosY) {
		this.attackedPosY = attackedPosY;
	}

	public Double getAttackedPosZ() {
		return attackedPosZ;
	}

	public void setAttackedPosZ(Double attackedPosZ) {
		this.attackedPosZ = attackedPosZ;
	}

}
