package com.homematic;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="device", strict=false)
public class Device
{
	@Attribute
	public String name;
	@Attribute(required=false)
	public String address;
	@Attribute
	public int ise_id;
	@Attribute(name = "interface", required=false)
	public String interfacetype;
	@Attribute(required=false)
	public String device_type;
	@Attribute(required=false)
	public boolean ready_config;
	@Attribute(required=false)
	public boolean unreach;
	@Attribute(required=false)
	public boolean sticky_unreach;
	@Attribute(required=false)
	public boolean config_pending;
	
	@ElementList(inline=true,required=false)
	public List<Channel> channels;
}