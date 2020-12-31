package com.homematic;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="notification", strict=false)
public class Notification
{
	@Attribute
	public String name;
	@Attribute
	public int ise_id;
	@Attribute
	public String type;
	@Attribute
	public long timestamp;
}
