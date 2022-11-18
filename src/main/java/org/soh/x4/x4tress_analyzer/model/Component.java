package org.soh.x4.x4tress_analyzer.model;

import org.xml.sax.Attributes;

/**
 * X4 Component class
 * Represents e.g. Ships, weapons, shields, Stations
 * @author Son of Hubert
 *
 */
public class Component {
	
	private final String objectClass;
	private final String objectCode;
	private final String objectOwner;

	/**
	 * Constructor from the Sax attributes
	 * @param attr the Sax attributes
	 */
	public Component(Attributes attr) {
		objectClass = attr.getValue("class");
		objectCode = attr.getValue("code");
		objectOwner = attr.getValue("owner");
	}
	
	public Component (String attrObjectClass, String attrCode, String attrOwner) {
		this.objectClass = attrObjectClass;
		this.objectCode = attrCode;
		this.objectOwner = attrOwner;
	}


	public String getObjectClass() {
		return objectClass;
	}


	public String getObjectCode() {
		return objectCode;
	}


	public String getObjectOwner() {
		return objectOwner;
	}
	
	/**
	 * Checks if one of the object properties contains the search string
	 * @param str the search string
	 * @return true if any of the attributes contain the search string. False otherwise.
	 */
	public boolean contains(String str) {
		if (objectClass != null && objectClass.contains(str)) {
			return true;
		}
		if (objectCode != null && objectCode.contains(str)) {
			return true;
		}
		if (objectOwner != null && objectOwner.contains(str)) {
			return true;
		}
		return false;
	}
	
	
	
}
