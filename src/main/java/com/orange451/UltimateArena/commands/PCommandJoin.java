package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;

public class PCommandJoin extends UltimateArenaCommand
{
	public PCommandJoin(UltimateArena plugin) 
	{
		super(plugin);
		this.name = "join";
		this.aliases.add("j");
		this.requiredArgs.add("arena");
		this.description = "加入或开始一个 UltimateArena 竞技场";
		
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform() 
	{
		String name = args[0];
		plugin.fight(player, name);
	}
}
