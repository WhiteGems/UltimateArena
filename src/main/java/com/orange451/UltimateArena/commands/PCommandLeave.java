package com.orange451.UltimateArena.commands;

import com.orange451.UltimateArena.UltimateArena;

public class PCommandLeave extends UltimateArenaCommand
{	
	public PCommandLeave(UltimateArena plugin)
	{
		super(plugin);
		this.name = "leave";
		this.aliases.add("l");
		this.description = "离开一个竞技场";
	}
	
	@Override
	public void perform() 
	{
		plugin.leaveArena(player);
	}
}