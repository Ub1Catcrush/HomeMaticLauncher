package com.homematic;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;


@Root(name="statelist")
public class Statelist
{
	@ElementList(inline=true)
	public List<Device> devices;
}
