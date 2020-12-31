package com.homematic;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="devicelist", strict=false)
public class Devicelist
{
	@ElementList(inline=true)
	public List<Device> devices;
}
