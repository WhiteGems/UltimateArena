package com.orange451.UltimateArena.Arenas;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.Arenas.Objects.CTFFlagBase;

public class CTFArena extends Arena
{
	public CTFFlagBase flagred;
	public CTFFlagBase flagblue;
	public int redcap;
	public int bluecap;
	private BukkitTask  ExecuteMove;
	private String lastcap;
	
	public CTFArena(ArenaZone az)
	{
		super(az);
		
		type = "Ctf";
		starttimer = 120;
		gametimer = 0;
		maxgametime = 60 * 15;
		maxDeaths = 990;

		flagred = new CTFFlagBase(this, az.flags.get(0), 1);
		flagblue = new CTFFlagBase(this, az.flags.get(1), 2);
			
		flagred.initialize();
		flagblue.initialize();
		
		
		ExecuteMove = new ExecuteMove().runTaskTimer(plugin, 12, 1);
	}
	
	@Override
	public void check()
	{
		if (starttimer <= 0) 
		{
			if (!simpleTeamCheck(false)) 
			{
				this.tellPlayers(ChatColor.BLUE + "一支队伍空了! 游戏已结束!");
				this.stop();
			}
			else
			{
				if (this.amtPlayersStartingInArena <= 1) 
				{
					this.tellPlayers(ChatColor.BLUE + "玩家数量不足以开始游戏!");
					this.stop();
				}
			}
		}
		
		if (redcap >= 3 || bluecap >= 3) 
		{
			this.setWinningTeam(1);
			lastcap = ChatColor.RED + "红队";
			if (bluecap >= 3) 
			{
				this.setWinningTeam(2);
				lastcap = ChatColor.BLUE + "蓝队";
			}
			winGame();
		}
		else
		{
			flagred.flag.tick();
			flagblue.flag.tick();
		}
	}
	
	@Override
	public int getTeam()
	{
		return getBalancedTeam();
	}
	
	public void winGame() 
	{
		if (redcap >= 3 && bluecap >= 3)
		{
			this.setWinningTeam(-1);
			this.stop();
			this.rewardTeam(-1, "平局! 奖品对半分给所有人", true);
			return;
		}
		
		this.tellPlayers(lastcap + ChatColor.GRAY + " 获胜!");
		this.stop();
		this.rewardTeam(this.winningTeam, "你获胜了!", false);
	}
	
	@Override
	public void onStop()
	{
		flagred.flag.stopped = true;
		flagblue.flag.stopped = true;

		flagred.flag.returnto.getBlock().setTypeIdAndData(0, (byte)0, false);
		flagblue.flag.returnto.getBlock().setTypeIdAndData(0, (byte)0, false);
		flagred.flag.despawn();
		flagblue.flag.despawn();

		ExecuteMove.cancel();
	}
	
	public class ExecuteMove extends BukkitRunnable 
	{
		@Override
		public void run()
		{
			if (!stopped)
			{
				flagred.checkNear(arenaplayers);
				flagblue.checkNear(arenaplayers);
			}
			else
			{
				onStop();
			}
		}
	}
}