package com.orange451.UltimateArena.Arenas.Objects;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.orange451.UltimateArena.UltimateArena;
import com.orange451.UltimateArena.util.Util;

public class ArenaCreator 
{
	public String player;
	public String arenaName = "";
	public String arenaType = "";
	public Location lobby1 = null;
	public Location lobby2 = null;
	public Location arena1 = null;
	public Location arena2 = null;
	public Location flag1point = null;
	public Location flag2point = null;
	public Location team1spawn = null;
	public Location team2spawn = null;
	public Location lobbyREDspawn = null;
	public Location lobbyBLUspawn = null;
	public int amtLobbys = 2;
	public int amtSpawnpoints = 2;
	public String step;
	public ArrayList<String> steps = new ArrayList<String>();
	public ArrayList<Location> spawns = new ArrayList<Location>();
	public ArrayList<Location> flags = new ArrayList<Location>();
	public int stepnum;
	public String msg = "";
	public UltimateArena plugin;
	
	public ArenaCreator(UltimateArena plugin, Player player)
	{
		this.player = player.getName();
		this.plugin = plugin;
	}
	
	public void setArena(String arenaName, String arenaType)
	{
		this.arenaName = arenaName;
		this.arenaType = arenaType;
		
		this.steps.add("Lobby");
		this.steps.add("Arena");
		this.steps.add("LobbySpawn1");
		if (arenaType.equals("pvp") || arenaType.equals("infect")) 
		{
			this.steps.add("LobbySpawn2");
			this.steps.add("ArenaSpawn1");
			this.steps.add("ArenaSpawn2");
		}
		if (arenaType.equals("ctf"))
		{
			this.steps.add("LobbySpawn2");
			this.steps.add("ArenaSpawn1");
			this.steps.add("ArenaSpawn2");
			this.steps.add("FlagSpawn");
		}
		if (arenaType.equals("cq"))
		{
			this.steps.add("LobbySpawn2");
			this.steps.add("ArenaSpawn1");
			this.steps.add("ArenaSpawn2");
			this.steps.add("FlagSpawn");
		}
		if (arenaType.equals("koth"))
		{
			amtLobbys = 1;
			this.steps.add("playerspawn");
			this.steps.add("kothflag");
		}
		if (arenaType.equals("ffa") || arenaType.equals("hunger")) 
		{
			amtLobbys = 1;
			this.steps.add("playerspawn");
		}
		if (arenaType.equals("spleef")) 
		{
			amtLobbys = 1;;
			this.steps.add("spleefzone");
			this.steps.add("outzone");
		}
		if (arenaType.equals("bomb"))
		{
			this.steps.add("LobbySpawn2");
			this.steps.add("ArenaSpawn1");
			this.steps.add("ArenaSpawn2");
			this.steps.add("FlagSpawn");
		}
		if (arenaType.equals("mob"))
		{
			amtLobbys = 1;
			amtSpawnpoints = 1;
			this.steps.add("ArenaSpawn1");
			this.steps.add("MobSpawn");
		}
		
		this.step = steps.get(stepnum);
		
		Player pl = Util.matchPlayer(player);
		pl.sendMessage(ChatColor.GRAY + "竞技场: " + ChatColor.GOLD + arenaName + ChatColor.GRAY + " 已创建. 类型: " + ChatColor.GOLD + arenaType);
		pl.sendMessage(ChatColor.GRAY + "请为大厅选择两个顶点! " + ChatColor.GOLD + "/ua setpoint");
	}
	
	@SuppressWarnings("unchecked")
	public void saveArena(Player player)
	{
		try
		{
			ArenaZone az = new ArenaZone(plugin, arenaName);
			az.arenaType = arenaType;
			az.lobbyBLUspawn = lobbyBLUspawn;
			az.lobbyREDspawn = lobbyREDspawn;
			az.team1spawn = team1spawn;
			az.team2spawn = team2spawn;
			az.lobby1 = lobby1;
			az.lobby2 = lobby2;
			az.arena1 = arena1;
			az.arena2 = arena2;
			az.spawns = (ArrayList<Location>) spawns.clone();
			az.flags = (ArrayList<Location>) flags.clone();
			az.maxPlayers = 24;
			az.defaultClass = plugin.classes.get(0).name;
			az.world = lobby1.getWorld();
			az.save();
			az.initialize();
			plugin.getLogger().info("竞技场已被创建并保存: " + arenaName + "  类型: " + arenaType);
			plugin.loadedArena.add(az);
			player.sendMessage(ChatColor.GRAY + "完成创建竞技场!");
			player.sendMessage(ChatColor.GRAY + "使用 " + ChatColor.GOLD + "/ua join " + arenaName + ChatColor.GRAY + " 来加入游戏!");
		}
		catch(Exception e)
		{
			plugin.getLogger().severe("创建竞技场时出错!");
			player.sendMessage(ChatColor.RED + "创建竞技场时出错: " + e.getMessage());
			player.sendMessage(ChatColor.RED + "请检查控制台寻找更多错误细节");
			e.printStackTrace();
		}
		plugin.makingArena.remove(this);
	}
	
	public void setDone(Player player)
	{
		if (step.equalsIgnoreCase("lobby")) 
		{
			if (lobby1 != null && lobby2 != null)
			{
				player.sendMessage(ChatColor.GRAY + "成功设置了大厅! 请为竞技场选择两个顶点");
				stepUp();
				return;
			}
			else
			{
				player.sendMessage(ChatColor.GRAY + "你还没设置完大厅!");
			}
		}
		else if (step.equalsIgnoreCase("arena")) 
		{
			if (arena1 != null && arena2 != null)
			{
				player.sendMessage(ChatColor.GRAY + "已设置完竞技场! 请为红队设置大厅出生点");
				stepUp();
				return;
			}
			else
			{
				player.sendMessage(ChatColor.GRAY + "你还没设置完竞技场!");
			}
		}
		else if (step.equalsIgnoreCase("lobbyspawn1")) 
		{
			if (lobbyREDspawn != null) 
			{
				if (amtLobbys > 1) 
				{
					player.sendMessage(ChatColor.GRAY + "已设置大厅出生点!");
					player.sendMessage(ChatColor.GRAY + "请设置红队竞技场出生点");
					stepUp();
				}
				else
				{
					if (arenaType.equals("koth") || arenaType.equals("ffa") || arenaType.equals("hunger"))
					{
						player.sendMessage(ChatColor.GRAY + "请加入一些玩家出生点  " + ChatColor.LIGHT_PURPLE + "/ua setpoint");
						player.sendMessage(ChatColor.GRAY + "使用 " + ChatColor.GOLD + "/ua done" + ChatColor.GRAY + " 来结束加入");
					}
					else
					{
						if (arenaType.equals("spleef"))
						{
							player.sendMessage(ChatColor.GRAY + "设置大厅出生点完毕");
							player.sendMessage(ChatColor.GRAY + "   -请设置两个spleef区域点");
						}
						else
						{
							player.sendMessage(ChatColor.GRAY + "设置大厅出生点完毕, 请设置竞技场出生点");
						}
					}
				}
				stepUp();
				return;
			}
			else
			{
				player.sendMessage(ChatColor.GRAY + "你没有设置完出生点!");
			}
		}
		else if (step.equalsIgnoreCase("arenaspawn1")) 
		{
			if (team1spawn != null)
			{
				stepUp();
				if (steps.contains("ArenaSpawn2"))
				{
					if (team2spawn != null) 
					{
						player.sendMessage(ChatColor.GRAY + "设置玩家出生点完毕");
						stepUp();//get passed arenaspawn2 step, since it's created already :3 (fail coding, I know)
						if (arenaType.equals("cq"))
						{
							player.sendMessage(ChatColor.GRAY + "请添加旗帜点");
							player.sendMessage(ChatColor.GRAY + "输入 " + ChatColor.GOLD + "/ua done" + ChatColor.GRAY + " 来完成添加");
						}
						if (arenaType.equals("bomb") || arenaType.equals("ctf"))
						{
							player.sendMessage(ChatColor.GRAY + "请添加两个旗帜点");
							player.sendMessage(ChatColor.GRAY + "输入 " + ChatColor.GOLD + "/ua done" + ChatColor.GRAY + " 来完成添加");
						}
					}
					else
					{
						player.sendMessage(ChatColor.GRAY + "你还没设置蓝队的竞技场出生点!");
						stepDown();
					}
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "设置玩家出生点完毕");
					if (arenaType.equals("mob"))
					{
						player.sendMessage(ChatColor.GRAY + "请设置怪物出生点");
						player.sendMessage(ChatColor.GRAY + "使用 " + ChatColor.GOLD + "/ua done" + ChatColor.GRAY + " 来完成添加");
					}
				}
			}
			else
			{
				player.sendMessage(ChatColor.GRAY + "你还没添加红队经经常出生点!");
			}
		}
		else
		{
			if (step.equalsIgnoreCase("playerspawn"))
			{
				if (spawns.size() > 0) 
				{
					stepUp();
					player.sendMessage(ChatColor.GRAY + "成功设置玩家出生点");
					if (arenaType.equals("koth"))
					{
						player.sendMessage(ChatColor.GRAY + "请添加旗帜点");	
					}
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "你不需要再添加玩家出生点了");
				}
			}
			if (step.equalsIgnoreCase("spleefzone"))
			{
				if (flags.size() == 2)
				{
					stepUp();
					player.sendMessage(ChatColor.GRAY + "成功设置完Spleef区域!");
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "你需要为Spleef区域设置两个顶点!");
				}
			}
			if (step.equalsIgnoreCase("outzone")) 
			{
				if (flags.size() == 4) 
				{
					stepUp();
					player.sendMessage(ChatColor.GRAY + "成功设置观战区!");
				}
				else
				{
					player.sendMessage("你需要为观战区设置两个顶点!");
				}
			}
			if (step.equalsIgnoreCase("kothflag")) 
			{
				if (flags.size() > 0) 
				{
					stepUp();
					player.sendMessage(ChatColor.GRAY + "成功设置旗帜点");
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "你需要添加一个旗帜点");
				}
			}
			if (step.equalsIgnoreCase("mobspawn"))
			{
				if (spawns.size() > 0) 
				{
					stepUp();
					player.sendMessage(ChatColor.GRAY + "设置怪物出生点结束!");
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "请设置一些怪物出生点");
				}
			}
			if (step.equalsIgnoreCase("flagspawn"))
			{
				if (flags.size() > 0)
				{
					if (arenaType.equals("cq")) 
					{
						if (flags.size() % 2 == 0) 
						{
							player.sendMessage(ChatColor.GRAY + "你需要奇数个旗帜点!");
						}
						else
						{
							stepUp();
							player.sendMessage(ChatColor.GRAY + "完成设置旗帜点!");
						}
					}
					if (arenaType.equals("bomb") || arenaType.equals("ctf"))
					{
						if (flags.size() != 2) 
						{
							player.sendMessage("你需要至少设置两个旗帜点");
						}
						else
						{
							stepUp();
							player.sendMessage(ChatColor.GRAY + "已设置旗帜点!");
						}
					}
				}
				else
				{
					player.sendMessage(ChatColor.GRAY + "请设置旗帜点");
				}
			}
		}
		if (stepnum >= steps.size()) 
		{
			saveArena(player);
		}
	}
	
	public void stepUp()
	{
		stepnum++;
		if (stepnum < steps.size()) 
		{
			step = steps.get(stepnum);
		}
	}
	
	public void stepDown()
	{
		stepnum--;
		step = steps.get(stepnum);
	}
	
	public void setPoint(Player player) 
	{
		Location loc = player.getLocation();
		msg = "";
		if (lobby1 == null)
		{
			lobby1 = loc; msg = "大厅第一个点已设置, 请设置第二个!";
		}
		else if (lobby2 == null)
		{
			lobby2 = loc; msg = "大厅第二个点已设置, 如果确定两个点都设置好后, 请输入" + ChatColor.GOLD + " /ua done";
		}
		else if (arena1 == null) 
		{
			arena1 = loc; msg = "竞技场第一个点已设置, 请设置第二个!";
		}
		else if (arena2 == null)
		{
			arena2 = loc; msg = "竞技场第二个点已设置, 如果确定两个点都设置好后, 请输入" + ChatColor.GOLD + " /ua done";
		}
		else
		{
			try
			{
				if (lobbyREDspawn == null) 
				{
					lobbyREDspawn = loc; msg = "红队大厅点已设置";
					if (amtLobbys > 1) 
					{
						msg += ", 请为蓝队设置第二个点";
					}
					else
					{
						msg += ", 如果大厅点设置完毕, 请输入" + ChatColor.GOLD + " /ua done";
					}
					return;
				}
				if (lobbyBLUspawn == null)
				{
					if (amtLobbys>1)
					{
						msg = "蓝队大厅点已设置!, 如果大厅点设置完毕, 请输入" + ChatColor.GOLD + " /ua done";
						lobbyBLUspawn = loc;
						return;
					}
				}
				if (step.contains("ArenaSpawn")) 
				{
					if (team1spawn == null)
					{
						team1spawn = loc; msg = "红队出生点已设置";
						if (amtSpawnpoints > 1)
						{
							msg += ", 请为蓝队设置第二个点";
						}
						else
						{
							msg += ", 如果出生点设置完毕, 请输入" + ChatColor.GOLD + " /ua done";
						}
						return;
					}
					if (team2spawn == null) 
					{
						if (amtSpawnpoints > 1) 
						{
							team2spawn = loc;
							msg = "蓝队出生点已设置!, 如果出生点设置完毕, 请输入" + ChatColor.GOLD + " /ua done";
							return;
						}
					}
				}
				if (step.equalsIgnoreCase("playerspawn"))
				{
					this.spawns.add(player.getLocation());
					player.sendMessage(ChatColor.GRAY + "添加一个玩家出生点!");
					return;
				}
				if (step.equalsIgnoreCase("spleefzone")) 
				{
					this.flags.add(player.getLocation());
					player.sendMessage(ChatColor.GRAY + "添加一个Spleef区域!");
					return;
				}
				if (step.equalsIgnoreCase("outzone"))
				{
					this.flags.add(player.getLocation());
					player.sendMessage(ChatColor.GRAY + "添加一个观战区!");
					return;
				}
				if (step.equalsIgnoreCase("kothflag"))
				{
					if (flags.size() == 0) {
						this.flags.add(player.getLocation());
						player.sendMessage("添加旗帜点!");
						msg = "请输入 " + ChatColor.GOLD + "/ua done";
						return;
					}
				}
				if (step.equalsIgnoreCase("MobSpawn"))
				{
					this.spawns.add(player.getLocation());
					player.sendMessage("添加刷怪点!");
					return;
				}
				if (step.equalsIgnoreCase("flagspawn"))
				{
					if (arenaType.equals("bomb") || arenaType.equals("ctf"))
					{
						if (flags.size() < 2) 
						{
							this.flags.add(player.getLocation());
							player.sendMessage(ChatColor.GRAY + "添加旗帜点!");
						}
						else
						{
							player.sendMessage(ChatColor.GRAY + "已有两个旗帜!");
						}
					}
					else
					{
						this.flags.add(player.getLocation());
						player.sendMessage(ChatColor.GRAY + "添加一个旗帜点!");
					}
					return;
				}
			}
			catch(Exception e) 
			{
				player.sendMessage(ChatColor.RED + "创建竞技场时出错. 请检查控制台.");
				e.printStackTrace();
			}
		}
	}
}