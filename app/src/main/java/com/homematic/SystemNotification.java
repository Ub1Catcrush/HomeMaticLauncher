package com.homematic;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="systemNotification", strict=false)
public class SystemNotification
{
	@ElementList(inline=true,required=false)
	public List<Notification> notifications;
}
