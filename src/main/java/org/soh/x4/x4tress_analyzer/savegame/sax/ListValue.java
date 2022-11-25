package org.soh.x4.x4tress_analyzer.savegame.sax;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

/**
 * Pair object representing an entry in a X4 list.
 * 
 * @author Son of Hubert
 *
 */
public class ListValue {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ListValue.class);

	private static final String TYPE_TIME = "time";
	
	private static final String TYPE_STRING = "xmlkeyword";
	
	/**
	 * As pure strings and lists are saved as references,
	 * the value type "string" and "list" are actually Integers
	 */
	private static final String[] TYPE_INTEGER = {"string" , "list"};
	
	private static final String[] TYPE_DOUBLE = {"length"};

	/**
	 * Define the starting time of X4 in Milliseconds<br>
	 * The starting time is defined as <i>825-02-08 : 11:00</i> (yyy-MM-dd : HH:SS).<br>
	 * Due to some time shenanigans LocalDateTime.of(825, 2, 8, 11, 0) results in the actual time being<br>
	 * <i>825-02-04 12:00</i>! I don't wish to spend time on finding the cause, hence the below workaround time
	 */
	private static final Long X4_STARTING_TIME = java.sql.Timestamp
			.from(LocalDateTime.of(825, 2, 12, 10, 0).toInstant(ZoneOffset.ofHours(0))).getTime();

	/**
	 * Creates a new ListValue object.
	 * @param type  the list entry type.
	 * @param value the list entry value.
	 */
	public ListValue(String type, Object value) {
		this.type = type;
		this.value = value;
	}

	/**
	 * The list entry type.<br>
	 * This can be
	 * <ul>
	 * <li><i>"string"</i></li>
	 * <li><i>"list"</i></li>
	 * <li><i>"xmlkeyword"</i></li>
	 * <li><i>"time"</i></li>
	 * <li><i>"length"</i></li>
	 * </ul>
	 * <br>
	 * Check
	 * {@link org.soh.x4.x4tress_analyzer.savegame.SaveGameHandler#handleTagValue(Attributes attr)}
	 * for further implementations
	 */
	private String type;

	/**
	 * The value of the list entry.<br>
	 * In most cases this is a reference to a different list.<br>
	 * E.g. in the case of <i>"string"</i> or <i>"list"</i>, it is a reference to
	 * the string map or other list values.<br>
	 * In this case, parse this as an Integer. In the case of <i>"xmlkeyword"</i>,
	 * it is a standalone String. In the case of <i>"time"</i> or <i>"length"</i>, parse it as a
	 * float/double.
	 */
	private Object value;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * Return the list value as a timestamp.<br>
	 * In X4, time starts at 825-02-08 : 11:00 (YYY-MM-DD : HH:MM)<br>
	 * The <i>time</i> value in the savegame is the time passed in game in seconds, with milliseconds after the decimal point.<br>
	 * Time in X4 passes the same as on earth (1sec = 100ms, 1min = 60sec, 1h = 60min, 1d = 24h)
	 * @return The Timestamp in X4 time
	 */
	public Timestamp getValueAsTimestamp() {
		if (TYPE_TIME.equalsIgnoreCase(type) && value != null && value instanceof Double) {
			Double timeAsDouble = (Double) value;
			Long timeAsLong = (long) (timeAsDouble * 1000);
			Timestamp gameTime = new Timestamp(X4_STARTING_TIME + timeAsLong);
			LOGGER.debug("Created timestamp: " + gameTime);
			return gameTime;
		}
		return null;
	}
	
	/**
	 * Returns the list value as a String.
	 * @return The String value
	 */
	public String getValueAsString() {
		if (TYPE_STRING.equalsIgnoreCase(type) && value != null && value instanceof String) {
			String valueAsString = (String) value;
			return valueAsString;
		}
		return null;
	}
	
	/**
	 * Returns the list value as Integer.<br>
	 * Note that references (e.g. strings or lists) are also stored as Integers!
	 * @return The Integer value
	 */
	public Integer getValueAsInteger() {
		for (String type : TYPE_INTEGER) {
			if (type.equalsIgnoreCase(type) && value != null && value instanceof Integer) {
				Integer valueAsInt = (Integer) value;
				return valueAsInt;
			}			
		}
		return null;
	}
	
	/**
	 * Returns the list value as Integer.<br>
	 * Note that references (e.g. strings or lists) are also stored as Integers!
	 * @return The Integer value
	 */
	public Double getValueAsDouble() {
		for (String type : TYPE_DOUBLE) {
			if (type.equalsIgnoreCase(type) && value != null && value instanceof Double) {
				Double valueAsDouble = (Double) value;
				return valueAsDouble;
			}			
		}
		return null;
	}

}
