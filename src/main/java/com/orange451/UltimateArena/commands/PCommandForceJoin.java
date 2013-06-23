package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandForceJoin extends UltimateArenaCommand
{
	public PCommandForceJoin(UltimateArena plugin)
	{
		super(plugin);
		this.name = "forcejoin";
		this.aliases.add("fj");
		this.requiredArgs.add("arena");
		this.mode = "admin";
		this.description = "强制加入一个竞技场";
		this.permission = PermissionType.CMD_FORCE_JOIN.permission;
	}
	
	@Override
	public void perform() 
	{
		String name = args[0];
		player.sendMessage(ChatColor.GOLD + "尝试加入竞技场: " + name);
		plugin.joinBattle(true, player, name);
	}
}
