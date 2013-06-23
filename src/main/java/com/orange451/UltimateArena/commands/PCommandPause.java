package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandPause extends UltimateArenaCommand
{
	public PCommandPause(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "pause";
		this.requiredArgs.add("arena");
		this.mode = "admin";
		this.description = "暂停一个竞技场内的开始计时器";
		this.permission = PermissionType.CMD_PAUSE.permission;
	}
	
	@Override
	public void perform() 
	{
		String name = args[0];
		Arena arena = plugin.getArena(name);
		if (arena == null)
		{
			player.sendMessage(ChatColor.GOLD + "没有竞技场用了那个名字...");
			return;
		}		
		
		arena.pauseStartTimer = !arena.pauseStartTimer;
		player.sendMessage(ChatColor.GOLD + "竞技场 " + ChatColor.AQUA + arena.name + ChatColor.GOLD + " 的倒计时现在是 " + (arena.pauseStartTimer ? "暂停的" : "继续的"));
	}	
}