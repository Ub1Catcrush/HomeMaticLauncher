package com.homematic;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="room", strict=false)
public class Room
{
	@Attribute
	public String name;
	@Attribute
	public int ise_id;
	
	@ElementList(inline=true,required=false)
	public List<Channel> channels;
}
