package com.homematic;

import org.simpleframework.xml.Attribute;

public class Datapoint
{
	public static final int VALUETYPE_BOOL = 2;
	public static final int VALUETYPE_FLOAT = 4;
	public static final int VALUETYPE_ENUM = 8;
	public static final int VALUETYPE_INTEGER = 16;
	public static final int VALUETYPE_STRING = 20;

	public static final String TYPE_TEMPERATURE = "TEMPERATURE";
	public static final String TYPE_ACTUAL_TEMPERATURE = "ACTUAL_TEMPERATURE";
	public static final String TYPE_HUMIDITY = "HUMIDITY";
	public static final String TYPE_LOWBAT = "LOWBAT";
	public static final String TYPE_STATE = "STATE";
	public static final String TYPE_SET_TEMPERATURE = "SET_TEMPERATURE";
	
	@Attribute
	public String name;
	@Attribute
	public String type;
	@Attribute
	public int ise_id;
	@Attribute
	public String value;
	@Attribute
	public int valuetype;
	@Attribute
	public String valueunit;
	@Attribute
	public long timestamp;
	@Attribute
	public int operations;
}
