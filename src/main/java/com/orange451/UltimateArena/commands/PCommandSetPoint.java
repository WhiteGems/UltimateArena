package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandSetPoint extends UltimateArenaCommand
{	
	public PCommandSetPoint(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "setpoint";
		this.aliases.add("sp");
		this.mode = "build";
		this.description = "为场地设置顶点";
		this.permission = PermissionType.CMD_SET_POINT.permission;
	}
	
	@Override
	public void perform() 
	{
		plugin.setPoint(player);
	}
}
