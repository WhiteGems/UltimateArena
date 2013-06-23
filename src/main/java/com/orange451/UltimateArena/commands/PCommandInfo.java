package com.orange451.UltimateArena.commands;

import java.util.List;

import org.bukkit.ChatColor;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.Arenas.Arena;
import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;

public class PCommandInfo extends UltimateArenaCommand
{	
	public PCommandInfo(UltimateArena plugin)
	{
		super(plugin);
		this.name = "info";
		this.optionalArgs.add("arena");
		this.description = "查看你目前所在的竞技场的详细信息";
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform()
	{
		if (args.length == 0)
		{
			if (plugin.isInArena(player)) 
			{
				Arena ar = plugin.getArena(player);
				if (ar != null) 
				{
					sendMessage(ChatColor.DARK_RED + "==== " + ChatColor.GOLD + ar.az.arenaName + ChatColor.DARK_RED + " ====");
					ArenaPlayer ap = plugin.getArenaPlayer(player);
					if (ap != null)
					{
						String out = ChatColor.GREEN + "未离开";
						if (ap.out) 
						{
							out = ChatColor.RED + "离开";
						}
						sendMessage(ChatColor.GRAY + "你处于: " + out);
					}
					List<ArenaPlayer> arr = ar.arenaplayers;
					for (int i = 0; i < arr.size(); i++)
					{
						try
						{
							if (arr.get(i).out != true)
							{
								String playerName = ChatColor.BLUE + "[" + ChatColor.GRAY + arr.get(i).player.getName() + ChatColor.BLUE + "]";
								String playerHealth = ChatColor.BLUE + "[" + ChatColor.GRAY + ((arr.get(i).player.getHealth() / 20.0) * 100) + ChatColor.BLUE + "%]";
								sendMessage(playerName + playerHealth);
							}
						}
						catch (Exception e)
						{
							//
						}
					}
					
				}
			}
			else
			{
				sendMessage(ChatColor.RED + "你不在竞技场内!");
			}
		}
		else if (args.length == 1)
		{
			String arenaname = args[0];
			Arena ar = this.plugin.getArena(arenaname);
			if (ar != null)
			{
				sendMessage(ChatColor.GRAY + "竞技场: " + ChatColor.GOLD + arenaname);
				sendMessage(ChatColor.GRAY + "类型: " + ChatColor.GOLD + ar.az.arenaType);
				sendMessage(ChatColor.GRAY + "玩家:");
				List<ArenaPlayer> arr = ar.arenaplayers;
				if (arr.size() > 0)
				{
					for (int i = 0; i < arr.size(); i++)
					{
						try
						{
							if (arr.get(i).out != true) 
							{
								String playerName = ChatColor.BLUE + "[" + ChatColor.GRAY + arr.get(i).player.getName() + ChatColor.BLUE + "]";
								String playerHealth = ChatColor.BLUE + "[" + ChatColor.GRAY + ((arr.get(i).player.getHealth() / 20.0) * 100) + ChatColor.BLUE + "%]";
								sendMessage(playerName + playerHealth);
							}
						}
						catch(Exception e) 
						{
							//
						}
					}
				}
				else
				{
					sendMessage(ChatColor.RED + "竞技场内没有玩家");
				}
			}
			else
			{
				sendMessage(ChatColor.GRAY + "这个竞技场尚未开始!");
			}
		}
		else
		{
			sendMessage(ChatColor.GRAY + "请指明一个竞技场的名字");
		}
	}
}