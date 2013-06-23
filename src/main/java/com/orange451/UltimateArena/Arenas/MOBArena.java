package com.orange451.UltimateArena.Arenas;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.orange451.UltimateArena.Arenas.Objects.ArenaPlayer;
import com.orange451.UltimateArena.Arenas.Objects.ArenaZone;
import com.orange451.UltimateArena.util.Util;

public class MOBArena extends Arena 
{
	private int mobspawn;
	private int mobPerWave;
	private int mobtimer = 0;
	private ArrayList<LivingEntity> mobs = new ArrayList<LivingEntity>();
	private ArrayList<String> spawning = new ArrayList<String>();
	
	public MOBArena(ArenaZone az) 
	{
		super(az);
		
		type = "Mob";
		starttimer = 80;
		maxgametime = 60 * 10;
		maxDeaths = 0;
		mobspawn = 0;
		mobtimer = 0;
		wave = 0;
		winningTeam = -1;
		
		spawning.add("ZOMBIE");
		spawning.add("ZOMBIE");
		spawning.add("ZOMBIE");
		
		newWave();
	}
	
	public void newWave()
	{
		try
		{
			if (wave > 0)
			{
				tellPlayers("&a你幸存过了这一波攻势!");
				tellPlayers("&a接下来请准备第 &c{0} &a波", wave);
			}
			wave++;
			this.mobPerWave = 4 + ((int)(wave * 1.5)) + (this.amtPlayersInArena * 3);
			mobtimer = (wave*4) + 20;
			if (wave <= 1) 
			{
				mobtimer = 1;
			}
			if (wave > 1)
			{
				spawning.add("ZOMBIE");
				spawning.add("ZOMBIE");
				spawning.add("SKELETON");
			}
			if (wave > 3) 
			{
				spawning.add("SPIDER");
			}
			if (wave > 6) 
			{
				spawning.add("BLAZE");
				spawning.add("BLAZE");
			}
			if (wave > 9) 
			{
				spawning.add("PIG_ZOMBIE");
				spawning.add("ENDERMAN");
			}
			if (wave > 12)
			{
				spawning.add("GHAST");
			}
		}
		catch(Exception e) 
		{
			plugin.getLogger().severe("准备下一波攻势时发生错误: " + e.getMessage());
		}
	}
	
	public void endPlayer(ArenaPlayer p, boolean end)
	{
		if (this.az.plugin.isInArena(p.player.getLocation())) 
		{
			if (!p.out)
			{
				super.endPlayer(p, end);
				this.reward(p, p.player, false);
			}
		}
	}
	
	@Override
	public void reward(ArenaPlayer p, Player pl, boolean half)
	{
		int amtGold = (int) Math.floor(p.XP / 500.0);
		int amtSlime = (int) Math.floor(p.XP / 550.0);
		int amtGlowStone = (int) Math.floor(p.XP / 450.0);
		int amtGunPowder = (int) Math.floor(p.XP / 425.0);
		try
		{
			if (pl.isOnline()) 
			{
				Inventory inv = pl.getInventory();
				if (amtGold > 0) { inv.setItem(0, new ItemStack(Material.GOLD_INGOT, amtGold)); }
				if (amtSlime > 0) { inv.setItem(1, new ItemStack(Material.SLIME_BALL, amtSlime)); }
				if (amtGlowStone > 0) { inv.setItem(2, new ItemStack(Material.GLOWSTONE_DUST, amtGlowStone)); }
				if (amtGunPowder > 0) { inv.setItem(3, new ItemStack(Material.SULPHUR, amtGunPowder)); }
			}
		
		}
		catch(Exception e) 
		{
			plugin.getLogger().severe("分发怪物竞技场奖励时出现错误: " + e.getMessage());
		}
	}
	
	@Override
	public void onOutOfTime()
	{
		this.setWinningTeam(-1);
		this.rewardTeam(winningTeam, ChatColor.BLUE + "你获胜了!", false);
	}
	
	@Override
	public void stop() 
	{
		super.stop();
		synchronized(mobs) 
		{
			for (int i = mobs.size() - 1; i >= 0; i--)
			{
				try
				{
					if (mobs.get(i) != null)
					{
						mobs.get(i).remove();
					}
				}
				catch(Exception e) 
				{
					plugin.getLogger().severe("结束怪物竞技场时发生错误: " + e.getMessage());
				}
			}
		}
	}
	
	@Override
	public void doKillStreak(ArenaPlayer ap)
	{
		try
		{
			Player pl = Util.matchPlayer(ap.player.getName());
			if (pl != null) 
			{
				if (ap.killstreak == 8)
					giveItem(pl, Material.POTION.getId(), (byte)9, 1, "八杀! 解锁力量药水!");
				
				if (ap.killstreak == 12)
					giveItem(pl, Material.POTION.getId(), (byte)2, 1, "十二杀! 解锁迅捷药水!");
				
				if (ap.killstreak == 16)
					giveItem(pl, Material.POTION.getId(), (byte)3, 1, "十六杀! 解锁防火药水!");
				
				if (ap.killstreak == 24) 
				{
					giveItem(pl, Material.POTION.getId(), (byte)1, 1, "二十四杀! 解锁生命药水!");
					giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "二十四杀! 解锁食物!");
				}
				
				if (ap.killstreak == 32) 
				{
					pl.sendMessage(ChatColor.GOLD + "三十二杀! 解锁军犬!");
					for (int i = 0; i < 3; i++)
					{
						Wolf wolf = (Wolf) pl.getLocation().getWorld().spawnEntity(pl.getLocation(), EntityType.WOLF);
						wolf.setOwner(pl);
					}
				}
				
				if (ap.killstreak == 40) 
				{
					giveItem(pl, Material.POTION.getId(), (byte)1, 1, "四十杀! 解锁生命药水!");
					giveItem(pl, Material.GRILLED_PORK.getId(), (byte)0, 2, "四十杀! 解锁食物!");
				}
				
				if (ap.killstreak == 72)
					giveItem(pl, Material.GOLDEN_APPLE.getId(), (byte)0, 2, "七十二杀! 解锁金苹果!");

				if (ap.killstreak == 112)
					giveItem(pl, Material.GOLDEN_APPLE.getId(), (byte)0, 2, "一百一十二杀! 解锁金苹果!");
			}
		}
		catch(Exception e)
		{
			plugin.getLogger().severe("载入怪物竞技场连杀时出现错误:");
			e.printStackTrace();
		}
	}
	
	@Override
	public void check() 
	{
		try
		{
			if (starttimer <= 0) 
			{
				mobtimer--;
				mobspawn--;
				if (mobspawn < 0) 
				{
					if (mobtimer < 0) 
					{
						newWave();
						synchronized(mobs)
						{
							for (int i = 0; i < mobPerWave; i++) 
							{
								Location loc = this.az.spawns.get(Util.random(this.az.spawns.size()));
								String mob = this.spawning.get(Util.random(spawning.size()));
								LivingEntity newMob = (LivingEntity) loc.getWorld().spawnEntity(loc, EntityType.valueOf(mob));
								this.mobs.add(newMob);
							}
						}
					}
				}
				
				if (amtPlayersInArena == 0) 
				{
					plugin.getLogger().info("停止怪物竞技场");
					stop();
				}
				if (wave > maxwave) 
				{
					setWinningTeam(-1);
					tellPlayers(ChatColor.GOLD + "你战胜了怪物竞技场!");
					stop();
					rewardTeam(-1, ChatColor.BLUE + "你获胜了!", false);
				}
			}
		}
		catch(Exception e) 
		{
			plugin.getLogger().severe("停止怪物竞技场时发生错误: " + e.getMessage());
		}
	}
}