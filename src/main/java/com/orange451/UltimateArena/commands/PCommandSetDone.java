package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandSetDone extends UltimateArenaCommand
{
	public PCommandSetDone(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "done";
		this.aliases.add("d");
		this.mode = "build";
		this.description = "结束 UA 中的创建过程";
		this.permission = PermissionType.CMD_SET_DONE.permission;
	}
	
	@Override
	public void perform() 
	{
		plugin.setDone(player);
	}
}
