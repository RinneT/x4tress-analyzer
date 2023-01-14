package org.soh.x4.x4tress_analyzer.model;

public class ShipInfo {

	String shipId = null;
	String shipType = null;
	String shipFaction = null;
	int	noOfKills = 0;

	public ShipInfo() {
	}

	public ShipInfo(String shipId, String shipType, String shipFaction) {
		this.shipId = shipId;
		this.shipType = shipType;
		this.shipFaction = shipFaction;
	}

	public String getShipId() {
		return shipId;
	}

	public void setShipId(String shipId) {
		this.shipId = shipId;
	}

	public String getShipType() {
		return shipType;
	}

	public void setShipType(String shipType) {
		this.shipType = shipType;
	}

	public String getShipFaction() {
		return shipFaction;
	}

	public void setShipFaction(String shipFaction) {
		this.shipFaction = shipFaction;
	}

	public int getNoOfKills() {
		return noOfKills;
	}

	public void setNoOfKills(int noOfKills) {
		this.noOfKills = noOfKills;
	}
	
	public void addKill() {
		noOfKills = noOfKills + 1;
	}
}
