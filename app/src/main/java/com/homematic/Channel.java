package com.homematic;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "channel", strict=false)
public class Channel
{
	@Attribute(required = false)
	public String name;
	@Attribute(required = false)
	public int type;
	@Attribute(required = false)
	public String address;
	@Attribute
	public int ise_id;
	@Attribute(required = false)
	public String direction;
	@Attribute(required = false)
	public int parent_device;
	@Attribute(required = false)
	public int index;
	@Attribute(required = false)
	public String group_partner;
	@Attribute(required = false)
	public boolean aes_available;
	@Attribute(required = false)
	public String transmission_mode;
	@Attribute(required = false)
	public String archive;
	@Attribute(required = false)
	public boolean visible;
	@Attribute(required = false)
	public boolean ready_config;
	@Attribute(required = false)
	public int link_count;
	@Attribute(required = false)
	public int program_count;
	@Attribute(required = false)
	public boolean virtual;
	@Attribute(required = false)
	public boolean readable;
	@Attribute(required = false)
	public boolean writable;
	@Attribute(required = false)
	public boolean eventable;
	@Attribute(required = false)
	public boolean operate;

	@ElementList(inline = true, required = false)
	public List<Datapoint> datapoints;
}
