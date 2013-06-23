package com.orange451.UltimateArena.commands;

import java.util.List;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;

public class PCommandList extends UltimateArenaCommand
{
	public PCommandList(UltimateArena plugin)
	{
		super(plugin);
		this.name = "list";
		this.aliases.add("li");
		this.description = "列出所有的 UltimateArena 竞技场";
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		sendMessage(ChatColor.DARK_RED + "==== " + ChatColor.GOLD + "UltimateArenas" + ChatColor.DARK_RED + " ====");
		List<ArenaZone> arenas = this.plugin.loadedArena;
		List<Arena> activearenas = this.plugin.activeArena;
			
		for (int i = 0; i < arenas.size(); i++) 
		{
			String arena = arenas.get(i).arenaName;
			String type = arenas.get(i).arenaType;
				
			String arenaType = ChatColor.GOLD + "[" + ChatColor.RED + type + " 竞技场" + ChatColor.GOLD + "]";
			String arenaName = ChatColor.RED + arena;
			String arenaMode = "";
			String plays = ChatColor.YELLOW + "[" + arenas.get(i).timesPlayed + "]";
			arenaMode = ChatColor.GREEN + "[自由]";
			if (arenas.get(i).disabled)
				arenaMode = ChatColor.DARK_RED + "[禁用]";
				
			for (int ii = 0; ii < activearenas.size(); ii++) 
			{
				Arena ar = activearenas.get(ii);
				if (ar.az.equals(arenas.get(i))) 
				{
					if (!ar.disabled)
					{
						if (ar.starttimer > 0)
						{
							arenaMode = ChatColor.YELLOW + "[大厅  |  " + Integer.toString(ar.starttimer) + " 秒]";
						}
						else
						{
							arenaMode = ChatColor.DARK_RED + "[正忙]";
						}
					}
					else
					{
						arenaMode = ChatColor.DARK_RED + "[禁用]";
					}
				}
			}
			sendMessage(arenaType + " " + arenaName + " " + arenaMode + "        " + plays);
		}
	}
}