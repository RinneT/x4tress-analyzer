package org.soh.x4.x4tress_analyzer.savegame.sax;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

public class Savegame {
	
	private List<Component> objectList = new ArrayList<>();

	public List<Component> getObjectList() {
		return objectList;
	}
	
	/**
	 * Add a component element from the savegame if it is valid
	 * A component is valid if it is of any type of Ship or a station station
	 * @param attr The Attributes from the xml "component"
	 */
	public void addComponentIfValid(Attributes attr) {
		String code = attr.getValue("code");
		String objectClass = attr.getValue("class");
		
		if (code != null && objectClass != null && (objectClass.contains("station") || objectClass.contains("ship"))) {
			objectList.add(new Component(attr));
		}
	}
}
