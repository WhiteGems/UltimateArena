package com.orange451.UltimateArena.Arenas.Objects;

import org.bukkit.ChatColor;

public class TeamHelper
{
	/**
	 * @param team - Team Number
	 * @return Team name and color
	 */
	public static String getTeam(int team)
	{
		if (team == 1)
			return ChatColor.RED + "红队";
		
		if (team == 2)
			return ChatColor.BLUE + "蓝队";
		
		return ChatColor.DARK_GRAY + "无队伍";
	}
}