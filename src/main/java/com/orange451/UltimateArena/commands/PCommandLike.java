package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;

public class PCommandLike extends UltimateArenaCommand
{	
	public PCommandLike(UltimateArena plugin)
	{
		super(plugin);
		this.name = "like";
		this.requiredArgs.add("arena");
		this.description = "喜欢一个竞技场";
	}
	
	@Override
	public void perform()
	{
		String arenaname = args[0];
		ArenaZone az = plugin.getArenaZone(arenaname);
		if (az != null) 
		{
			if (az.canLike(player)) 
			{
				sendMessage("&a你已喜欢了: {0}!", az.arenaName);
				
				az.liked++;
				az.voted.add(player.getName());
			}
			else
			{
				sendMessage(ChatColor.RED + "你已经投票过给这个竞技场了!");
			}
		}
		else
		{
			sendMessage(ChatColor.RED + "那个竞技场并不存在!");
		}
	}
}