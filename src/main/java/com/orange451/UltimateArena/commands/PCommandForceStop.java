package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandForceStop extends UltimateArenaCommand
{
	public PCommandForceStop(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "forcestop";
		this.aliases.add("fs");
		this.optionalArgs.add("arena");
		this.mode = "admin";
		this.description = "尝试种植一个竞技场";
		this.permission = PermissionType.CMD_FORCE_STOP.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform() 
	{
		if (args.length > 0)
		{
			plugin.forceStop(args[0]);
		}
		else
		{
			plugin.forceStop();
		}
	}
}