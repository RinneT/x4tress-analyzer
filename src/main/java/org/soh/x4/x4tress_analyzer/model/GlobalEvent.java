package org.soh.x4.x4tress_analyzer.model;

import java.sql.Timestamp;

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
	
	
	
	public GlobalEvent(Timestamp timestamp, String eventType, String attackerId, String attacker,
			String attackerFaction, String attackedId, String attacked, String targetComponent, String attackedFaction,
			String sector) {
		this.timestamp = timestamp;
		this.eventType = eventType;
		this.attackerId = attackerId;
		this.attacker = attacker;
		this.attackerFaction = attackerFaction;
		this.attackedId = attackedId;
		this.attacked = attacked;
		this.targetComponent = targetComponent;
		this.attackedFaction = attackedFaction;
		this.sector = sector;
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

	
	
}
