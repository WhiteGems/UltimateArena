package com.orange451.UltimateArena.Arenas.Objects;

import org.bukkit.entity.Player;

import com.orange451.UltimateArena.util.FormatUtil;

public class ArenaStatistics 
{
	public ArenaZone arena;
	public double percentagePlayed;
	public int totalPlays;
	public int plays;
	public int likes;
	public int dislikes;
	
	public ArenaStatistics(ArenaZone az)
	{
		this.arena = az;
		stats();
	}
	
	public void stats() 
	{
		//calculates all the arenas stats
		this.totalPlays = arena.plugin.arenasPlayed;
		this.plays = arena.timesPlayed;
		//this.percentagePlayed = (int)(((int) ((double)plays/(double)totalPlays) * 100) / 100.0);
		this.percentagePlayed = (int) (((double)plays/(double)totalPlays) * 100);
		this.likes = arena.liked;
		this.dislikes = arena.disliked;
	}
	
	public void dumpStats(Player p) 
	{
		//dumps the arenas stats to the player
		p.sendMessage(FormatUtil.format("&8详细资料: &6{0}", arena.arenaName));
		p.sendMessage(FormatUtil.format("&7被玩次数: {0}/{1} (&c{2}%)", plays, totalPlays, percentagePlayed));
		p.sendMessage(FormatUtil.format("&7喜欢: &a{0}", likes));
		p.sendMessage(FormatUtil.format("&7不喜欢: &c{0}", dislikes));
	}
}
