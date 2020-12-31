package com.homematic;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;


@Root(strict=false)
public class Roomlist
{
	@ElementList(inline=true)
	public List<Room> rooms;
}
