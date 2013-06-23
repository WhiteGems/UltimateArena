package com.orange451.UltimateArena;

import org.bukkit.entity.Player;

public class testAPI 
{
	public UltimateArenaAPI ua;
	
	public void test(Player p)
	{
		/**Returns whether or not a player is IN an arena**/
		UltimateArenaAPI ua = UltimateArenaAPI.hookIntoUA();
		if (ua != null) 
		{
			boolean isPlayerPlayingArena = ua.isPlayerPlayingArena(p);
			boolean isPlayerInsideArena = ua.isPlayerInArenaLocation(p);
			System.out.println("玩家正在竞技场内游戏? " + isPlayerPlayingArena);
			System.out.println("玩家在竞技场内? " + isPlayerInsideArena);
		}
		else
		{
			System.out.println("请勾上 UA API");
		}
	}
}