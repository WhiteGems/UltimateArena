package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandStart extends UltimateArenaCommand
{
	public PCommandStart(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "start";
		this.requiredArgs.add("arena");
		this.mode = "admin";
		this.description = "强制开始一个竞技场";
		this.permission = PermissionType.CMD_START.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform() 
	{
		String name = args[0];
		Arena arena = plugin.getArena(name);
		if (arena == null)
		{
			player.sendMessage(ChatColor.GOLD + "没有以那个名字命名的竞技场...");
			return;
		}
			
		arena.start();
		player.sendMessage(ChatColor.GOLD + "开始竞技场.. " + ChatColor.AQUA + arena.name );
	}
}