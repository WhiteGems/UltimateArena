package com.orange451.UltimateArena.commands;

import java.util.logging.Level;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandRefresh extends UltimateArenaCommand
{
	public PCommandRefresh(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "refresh";
		this.aliases.add("reload");
		this.aliases.add("rl");
		this.mode = "admin";
		this.description = "重新载入 UltimateArena";
		this.permission = PermissionType.CMD_REFRESH.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		try
		{
			sendMessage("&a重新载入 UltimateArena 中...");
			plugin.forceStop();
			plugin.clearMemory();
			plugin.onEnable();
			sendMessage("&a重新载入完毕! 由Zesty全力驱动!");
		}
		catch(Exception e) 
		{
			log(Level.SEVERE, "重新载入时发生错误: " + e.getMessage());
		}
	}
}