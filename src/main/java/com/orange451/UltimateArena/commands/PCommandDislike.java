package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;

public class PCommandDislike extends UltimateArenaCommand
{	
	public PCommandDislike(UltimateArena plugin)
	{
		super(plugin);
		this.name = "dislike";
		this.aliases.add("d");
		this.requiredArgs.add("arena");
		this.description = "不喜欢一个竞技场";
	}
	
	@Override
	public void perform()
	{
		String arenaname = args[0];
		ArenaZone az = this.plugin.getArenaZone(arenaname);
		if (az != null)
		{
			if (az.canLike(player))
			{
				sendMessage("&c你选择了不喜欢: " + az.arenaName);
				
				az.disliked++;
				az.voted.add(player.getName());
			}
			else
			{
				sendMessage(ChatColor.RED + "你已为这个竞技场投过票!");
			}
		}
		else
		{
			sendMessage(ChatColor.RED + "这个竞技场并不存在!");
		}
	}
}