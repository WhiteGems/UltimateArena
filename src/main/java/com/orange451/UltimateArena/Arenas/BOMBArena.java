package com.orange451.UltimateArena.Arenas;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.Arenas.Objects.BombFlag;
import com.orange451.UltimateArena.util.Util;

public class BOMBArena extends Arena 
{
	public int REDTEAMPOWER = 100;
	public BombFlag bomb1;
	public BombFlag bomb2;
	
	public BOMBArena(ArenaZone az) 
	{
		super(az);
		
		type = "Bomb";
		starttimer = 120;
		gametimer = 0;
		maxgametime = 60 * 15;
		maxDeaths = 990;
		
		bomb1 = new BombFlag(this, az.flags.get(0));
		bomb2 = new BombFlag(this, az.flags.get(1));
		bomb1.bnum = 1;
		bomb2.bnum = 2;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		this.REDTEAMPOWER = amtPlayersInArena * 3;
		if (REDTEAMPOWER < 10)
		{
			REDTEAMPOWER = 10;
		}
		if (REDTEAMPOWER > 150) 
		{
			REDTEAMPOWER = 150;
		}
	}
	
	@Override
	public void onOutOfTime() 
	{
		setWinningTeam(2);
		rewardTeam(2, ChatColor.BLUE + "你获胜了!", false);
	}
	
	public synchronized void onPlayerDeath(ArenaPlayer pl)
	{
		az.plugin.getLogger().info("炸弹: 玩家("+pl.player.getName()+") 已阵亡!");
		if (pl.team == 1) 
		{
			REDTEAMPOWER--;
			for (int i = 0; i < arenaplayers.size(); i++) 
			{
				if (!arenaplayers.get(i).out)
				{ 
					Player pl1 = arenaplayers.get(i).player;
					if (pl1 != null) {
						if (arenaplayers.get(i).team == 1) 
						{
							pl1.sendMessage(ChatColor.RED + "你的能量现已: " + ChatColor.GOLD + REDTEAMPOWER);
						}
						else
						{
							pl1.sendMessage(ChatColor.RED + "其他队伍的能量现已: " + ChatColor.GOLD + REDTEAMPOWER);
						}
					}
				}
			}
		}
	}
	
	@Override
	public int getTeam() 
	{
		return getBalancedTeam();
	}

	@Override
	public void check() 
	{
		if (starttimer <= 0) 
		{
			simpleTeamCheck(true);
		}
		bomb1.checkNear(arenaplayers);
		bomb2.checkNear(arenaplayers);
		
		if (bomb1.exploded && bomb2.exploded)
		{
			setWinningTeam(1);
			tellPlayers(ChatColor.GRAY + "红队获胜!");
			stop();
			rewardTeam(1, ChatColor.BLUE + "你获胜了!", false);
			return;
		}
		
		if (REDTEAMPOWER <= 0) 
		{
			setWinningTeam(2);
			tellPlayers(ChatColor.GRAY + "蓝队获胜!");
			for (int i = 0; i < arenaplayers.size(); i++)
			{
				ArenaPlayer ap = arenaplayers.get(i);
				if (!ap.out) {
					if (ap.team == 1)
					{
						ap.out = true;
						updatedTeams = true;
						Player p = Util.matchPlayer(ap.player.getName());
						if (p != null) 
						{
							p.sendMessage(ChatColor.RED + "你的队伍输了! :(");
							endPlayer(ap, false);
						}
					}
				}
			}
			stop();
			rewardTeam(2, ChatColor.BLUE + "你获胜了!", false);
		}
	}
}