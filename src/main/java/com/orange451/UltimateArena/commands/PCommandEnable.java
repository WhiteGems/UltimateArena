package com.orange451.UltimateArena.commands;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.permissions.PermissionType;

public class PCommandEnable extends UltimateArenaCommand
{	
	public PCommandEnable(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "enable";
		this.aliases.add("en");
		this.optionalArgs.add("arena");
		this.mode = "admin";
		this.description = "启用一个竞技场";
		this.permission = PermissionType.CMD_ENABLE.permission;
		
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform()
	{
		if (args.length == 1)
		{
			String at = args[0];
			for (int ii = 0; ii < plugin.activeArena.size(); ii++) 
			{
				Arena aa = plugin.activeArena.get(ii);
				if (aa.name.equals(at))
				{
					aa.disabled = false;
					sendMessage(ChatColor.GRAY + "已启用 " + at);
				}
				else if (aa.az.arenaType.equals(at))
				{
					aa.disabled = false;
					sendMessage(ChatColor.GRAY + "已启用 " + at);
				}
			}
			for (int ii = 0; ii < plugin.loadedArena.size(); ii++)
			{
				ArenaZone aa = plugin.loadedArena.get(ii);
				if (aa.arenaType.equals(at))
				{
					aa.disabled = false;
					sendMessage(ChatColor.GRAY + "已启用 " + at);
				}
				else if (aa.arenaName.equals(at)) 
				{
					aa.disabled = false;
					sendMessage(ChatColor.GRAY + "已启用 " + at);
				}
			}
		}
		else
		{
			for (int ii = 0; ii < plugin.activeArena.size(); ii++)
				plugin.activeArena.get(ii).disabled = false;
			for (int ii = 0; ii < plugin.loadedArena.size(); ii++)
				plugin.loadedArena.get(ii).disabled = false;
			sendMessage(ChatColor.GRAY + "已启用所有服务器");
		}
	}
}
