package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandStop extends UltimateArenaCommand
{
	public PCommandStop(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "stop";
		this.aliases.add("s");
		this.mode = "build";
		this.description = "停止建立一个竞技场";
		this.permission = PermissionType.CMD_STOP.permission;
	}
	
	@Override
	public void perform() 
	{
		if (plugin.isPlayerCreatingArena(player)) 
		{
			plugin.stopCreatingArena(player);
		}
		else
		{
			sendMessage("&c你没有在建立竞技场!");
		}
	}
}