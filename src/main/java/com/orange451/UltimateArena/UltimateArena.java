/**
* UltimateArena - a bukkit plugin
* Copyright (C) 2013 Minesworn/dmulloy2
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.orange451.UltimateArena;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.orange451.UltimateArena.Arenas.*;
import com.orange451.UltimateArena.Arenas.Objects.*;
import com.orange451.UltimateArena.commands.*;
import com.orange451.UltimateArena.listeners.*;
import com.orange451.UltimateArena.permissions.PermissionHandler;
import com.orange451.UltimateArena.util.FormatUtil;
import com.orange451.UltimateArena.util.InventoryHelper;
import com.orange451.UltimateArena.util.Util;

public class UltimateArena extends JavaPlugin
{
	private @Getter Economy economy;
	private @Getter FileHelper fileHelper;
	private @Getter FileConverter fileConverter;
	
	private @Getter PermissionHandler permissionHandler;
	private @Getter CommandHandler commandHandler;
	
	public int arenasPlayed = 0;
	
	public List<ArenaClass> classes = new ArrayList<ArenaClass>();
	public List<ArenaCreator> makingArena = new ArrayList<ArenaCreator>();
	public List<ArenaZone> loadedArena = new ArrayList<ArenaZone>();
	public List<Arena> activeArena = new ArrayList<Arena>();
	public List<RemindTask> waiting = new ArrayList<RemindTask>();
	public List<ArenaConfig> configs = new ArrayList<ArenaConfig>();
	public List<String> fieldTypes = new ArrayList<String>();
	public WhiteListCommands wcmd = new WhiteListCommands();
	public List<SavedArenaPlayer> savedPlayers = new ArrayList<SavedArenaPlayer>();

	@Override
	public void onEnable()
	{
		long start = System.currentTimeMillis();
		
		createDirectories();
		saveDefaultConfig();
		
		permissionHandler =  new PermissionHandler(this);
		commandHandler = new CommandHandler(this);
		
		fileHelper = new FileHelper(this);
		fileConverter = new FileConverter(this);
		
		// Add fields
		fieldTypes.add("pvp");
		fieldTypes.add("mob");
		fieldTypes.add("cq");
		fieldTypes.add("koth");
		fieldTypes.add("bomb");
		fieldTypes.add("ffa");
		fieldTypes.add("spleef");
		fieldTypes.add("infect");
		fieldTypes.add("ctf");
		fieldTypes.add("hunger");

		// Add Commands
		commandHandler.setCommandPrefix("ua");
		commandHandler.registerCommand(new PCommandHelp(this));
		commandHandler.registerCommand(new PCommandInfo(this));
		commandHandler.registerCommand(new PCommandList(this));
		commandHandler.registerCommand(new PCommandJoin(this));
		commandHandler.registerCommand(new PCommandLeave(this));
		commandHandler.registerCommand(new PCommandStats(this));
		commandHandler.registerCommand(new PCommandLike(this));
		commandHandler.registerCommand(new PCommandDislike(this));
		
		commandHandler.registerCommand(new PCommandCreate(this));
		commandHandler.registerCommand(new PCommandSetPoint(this));
		commandHandler.registerCommand(new PCommandSetDone(this));
		commandHandler.registerCommand(new PCommandDelete(this));
		commandHandler.registerCommand(new PCommandStop(this));
		
		commandHandler.registerCommand(new PCommandForceStop(this));
		commandHandler.registerCommand(new PCommandRefresh(this));
		commandHandler.registerCommand(new PCommandForceJoin(this));
		commandHandler.registerCommand(new PCommandDisable(this));
		commandHandler.registerCommand(new PCommandEnable(this));
		commandHandler.registerCommand(new PCommandKick(this));
		commandHandler.registerCommand(new PCommandStart(this));
		commandHandler.registerCommand(new PCommandPause(this));
		
		commandHandler.registerCommand(new PCommandClasses(this));
		
		loadPlayers();

		PluginManager pm = getServer().getPluginManager();
		if (pm.isPluginEnabled("PVPGunPlus"))
			pm.registerEvents(new PVPGunPlusListener (this), this);
			
		pm.registerEvents(new EntityListener(this), this);
		pm.registerEvents(new BlockListener(this), this);
		pm.registerEvents(new PlayerListener(this), this);

		new ArenaUpdater().runTaskTimer(this, 2L, 20L);
			
		checkVault(pm);
		
		fileConverter.run();

		loadFiles();
		
		long finish = System.currentTimeMillis();
		
		getLogger().info(getDescription().getFullName() + " 已被启用 ("+(finish-start)+"毫秒)");
	}

	@Override
	public void onDisable()
	{
		long start = System.currentTimeMillis();

		for (int i=0; i<activeArena.size(); i++)
		{
			try
			{
				activeArena.get(i).onDisable();
			}
			catch (Exception e)
			{
				getLogger().severe("尝试停止竞技场 " + activeArena.get(i).name + " 时发生错误. (" + e.getMessage()+")");
			}
		}
		
		getServer().getServicesManager().unregisterAll(this);
		getServer().getScheduler().cancelTasks(this);
		
		clearMemory();
		
		long finish = System.currentTimeMillis();
		
		getLogger().info(getDescription().getFullName() + " 已被禁用 ("+(finish-start)+"毫秒)");
	}
	
	public void createDirectories()
	{
		File arenaFile = new File(getDataFolder(), "arenas");
		if (!arenaFile.exists())
		{
			arenaFile.mkdir();
		}
		
		File playersFile = new File(getDataFolder(), "players");
		if (!playersFile.exists())
		{
			playersFile.mkdir();
		}
		
		File classFile = new File(getDataFolder(), "classes");
		if (!classFile.exists())
		{
			classFile.mkdir();
		}
		
		File configsFile = new File(getDataFolder(), "configs");
		if (!configsFile.exists())
		{
			configsFile.mkdir();
		}
	}
	
	public void loadPlayers()
	{
		savedPlayers = fileHelper.getSavedPlayers();
		if (savedPlayers.size() > 0)
		{
			for (Player player : getServer().getOnlinePlayers())
			{
				for (SavedArenaPlayer savedArenaPlayer : savedPlayers)
				{
					if (savedArenaPlayer.getName().equals(player.getName()))
					{
						int levels = savedArenaPlayer.getLevels();
						Location loc = savedArenaPlayer.getLocation();
								
						normalize(player);
						player.setLevel(levels);
						player.teleport(loc);
						removePotions(player);
								
						fileHelper.deletePlayer(player);
								
						savedPlayers.remove(savedArenaPlayer);
					}
				}
			}
			
			getLogger().info("载入了 " + savedPlayers.size() + " 个保存的玩家!");
		}
	}

	public void onQuit(Player player)
	{
		if (isPlayerCreatingArena(player)) 
		{
			makingArena.remove(getArenaCreator(player));
		}
		
		if (isInArena(player))
		{
			Arena ar = getArena(player);
			ArenaPlayer ap = getArenaPlayer(player);
			if (ap != null)
			{
				getLogger().info("玩家 " + player.getName() + " 因退出游戏离开了竞技场 " + ar.name);
				SavedArenaPlayer loggedOut = new SavedArenaPlayer(player.getName(), ap.baselevel, ap.spawnBack);
						
				savedPlayers.add(loggedOut);
				fileHelper.savePlayer(loggedOut);
						
				removeFromArena(player.getName());
			}
		}
	}
	
	public void onJoin(Player player) 
	{
		/**Normalize Saved Players**/
		for (int i=0; i<savedPlayers.size(); i++)
		{
			SavedArenaPlayer savedArenaPlayer = savedPlayers.get(i);
			if (savedArenaPlayer.getName().equals(player.getName()))
			{
				int levels = savedArenaPlayer.getLevels();
				Location loc = savedArenaPlayer.getLocation();
						
				normalize(player);
				player.setLevel(levels);
				player.teleport(loc);
				removePotions(player);
						
				fileHelper.deletePlayer(player);
						
				savedPlayers.remove(savedArenaPlayer);
			}
		}
	}
	
	public void leaveArena(Player player)
	{
		if (isInArena(player))
		{
			Arena a = getArena(player);
			a.endPlayer(getArenaPlayer(player), false);
		}
		else
		{
			player.sendMessage(ChatColor.RED + "错误, 你不在一个竞技场内");
		}
	}
	
	public void loadArenas()
	{
		File folder = new File(getDataFolder(), "arenas");
		File[] children = folder.listFiles();
		for (File file : children)
		{
			ArenaZone az = new ArenaZone(this, file);
			if (az.loaded)
			{
				loadedArena.add(az);
			}
		}
		
		getLogger().info("已载入 " + children.length + " 个竞技场文件!");
	}
	
	public void loadConfigs() 
	{
		for (int i = 0; i < fieldTypes.size(); i++) 
		{
			loadConfig(fieldTypes.get(i));
		}
		
		getLogger().info("已载入 " + fieldTypes.size() + " 个竞技场配置文件!");
		
		loadWhiteListedCommands();
	}
	
	public void loadWhiteListedCommands()
	{
		File file = new File(getDataFolder(), "whiteListedCommands.yml");
		if (!file.exists())
		{
			getLogger().info("未找到白名单指令列表文件! 正在生成一个新的文件!");
			fileHelper.generateWhitelistedCmds();
		}
		
		YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
		List<String> whiteListedCommands = fc.getStringList("whiteListedCmds");
		for (String whiteListed : whiteListedCommands)
		{
			wcmd.addCommand(whiteListed);
		}
		
		getLogger().info("已载入 " + whiteListedCommands.size() + " 个白名单指令!");
	}
	
	public void loadConfig(String str)
	{
		File folder = new File(getDataFolder(), "configs");
		File file = new File(folder, str + "Config.yml");
		if (!file.exists())
		{
			getLogger().info("未找到\"" + str + "\" 的竞技场配置文件! 正在生产一个新的文件!");
			fileHelper.generateArenaConfig(str);
		}
		
		ArenaConfig a = new ArenaConfig(this, str, file);
		configs.add(a);
	}
	
	public void loadClasses() 
	{
		File folder = new File(getDataFolder(), "classes");
		File[] children = folder.listFiles();
		if (children.length == 0)
		{
			fileHelper.generateStockClasses();
			getLogger().info("未找到职业配置文件! 正在生成默认的职业配置!");
		}

		children = folder.listFiles();

		for (File file : children)
		{
			ArenaClass ac = new ArenaClass(this, file);
	        classes.add(ac);
		}
		
		getLogger().info("已载入 " + children.length + " 个职业文件!");
	}
	
	public ArenaConfig getConfig(String type) 
	{
		for (ArenaConfig ac : configs)
		{
			if (ac.arenaName.equalsIgnoreCase(type))
			{
				return ac;
			}
		}
		return null;
	}

	public void forceStop()
	{
		for (int i=0; i<activeArena.size(); i++)
		{
			Arena arena = activeArena.get(i);
			if (arena != null)
			{
				arena.startingAmount = 0;
				arena.stop();
			}
		}
		activeArena.clear();
	}
	
	public void forceStop(String str)
	{
		for (int i=0; i<activeArena.size(); i++)
		{
			Arena arena = activeArena.get(i);
			if (arena != null)
			{
				arena.forceStop = true;
				arena.stop();
			}
		}
	}
	
	public ArenaClass getArenaClass(String line)
	{
		for (ArenaClass ac : classes)
		{
			if (ac.name.equalsIgnoreCase(line))
			{
				return ac;
			}
		}
		return null;
	}
	
	public void deleteArena(Player player, String str) 
	{
		File folder = new File(getDataFolder(), "arenas");
		File file = new File(folder, str + ".dat");
		if (file.exists())
		{
			forceStop(str);
				
			loadedArena.remove(getArenaZone(str));
				
			file.delete();
			
			player.sendMessage(ChatColor.YELLOW + "成功删除竞技场: " + str + "!");
			getLogger().info("成功删除竞技场: " + str + "!");
		}
		else
		{
			player.sendMessage(ChatColor.RED + "不能找到以 \"" + str + "\"命名的竞技场!");
		}
	}
	
	public boolean isInArena(Block block) 
	{
		return isInArena(block.getLocation());
	}
	
	public Arena getArenaInside(Block block)
	{
		for (ArenaZone az : loadedArena)
		{
			if (az.checkLocation(block.getLocation()))
				return getArena(az.arenaName);
		}
		
		return null;
	}
	
	public boolean isInArena(Location loc)
	{
		for (ArenaZone az : loadedArena)
		{
			if (az.checkLocation(loc))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean isInArena(Player player) 
	{
		return (getArenaPlayer(player) != null);
	}
	
	public void removeFromArena(Player player)
	{
		if (player != null) 
		{
			for (int i=0; i<activeArena.size(); i++)
			{
				Arena a = activeArena.get(i);
				a.startingAmount--;
				ArenaPlayer ap = a.getArenaPlayer(player);
				if (ap != null) 
				{
					a.arenaplayers.remove(ap);
				}
			}
		}
	}
	
	public void removeFromArena(String str) 
	{
		for (int i=0; i<activeArena.size(); i++)
		{
			Arena a = activeArena.get(i);
			for (int ii=0; ii<a.arenaplayers.size(); ii++)
			{
				ArenaPlayer ap = a.arenaplayers.get(ii);
				if (ap != null)
				{
					Player player = Util.matchPlayer(ap.player.getName());
					if (player != null)
					{
						if (player.getName().equals(str))
						{
							a.arenaplayers.remove(ap);
						}
					}
				}
			}
		}
	}

	public ArenaPlayer getArenaPlayer(Player player) 
	{
		for (int i=0; i<activeArena.size(); i++)
		{
			Arena a = activeArena.get(i);
			ArenaPlayer ap = a.getArenaPlayer(player);
			if (ap != null)
			{
				if (!ap.out) 
				{
					if (ap.player.getName().equals(player.getName())) 
					{
						return ap;
					}
				}
			}
		}
		return null;
	}

	public void fight(Player player, String name)
	{
		if (player == null)
			return;
		
		if (isPlayerCreatingArena(player))
		{
			player.sendMessage(ChatColor.RED + "你已在制作竞技场的过程中!");
			return;
		}
		
		if (!InventoryHelper.isEmpty(player.getInventory()))
		{
			// TODO: Store inventories?
			player.sendMessage(ChatColor.RED + "请清空你的装备栏!");
			return;
		}
		
		ArenaZone a = getArenaZone(name);
		if (a == null)
		{
			player.sendMessage(ChatColor.RED + "那个竞技场不存在!");
			return;
		}
		
		if (isInArena(player))
		{
			player.sendMessage(ChatColor.RED + "你已在一个竞技场内!");
			return;
		}
		
		ArenaPlayer ap = getArenaPlayer(player);
		if (ap != null)
		{
			player.sendMessage(ChatColor.RED + "你不能离开并重新加入一个竞技场!");
			return;
		}
		
		for (int i = 0; i < waiting.size(); i++)
		{
			if (waiting.get(i).player.getName().equals(player.getName()))
			{
				player.sendMessage(ChatColor.RED + "你已在等待!");
				return;
			}
		}
		
		RemindTask rmd = new RemindTask(player, name);
		rmd.runTaskLater(this, 40L); // TODO: Make time configurable / essentials dependant?
		player.sendMessage(ChatColor.GOLD + "请站好不动并保持2秒钟!");
		waiting.add(rmd);				
	}
	
	public void joinBattle(boolean forced, Player player, String name) 
	{
		try
		{
			ArenaZone a = getArenaZone(name);
			if (getArena(name) != null)
			{
				if (getArena(name).starttimer < 1 && !forced) 
				{
					player.sendMessage(ChatColor.RED + "这个竞技场已经开始了!");
				}
				else
				{
					Arena tojoin = getArena(name);
					int maxplayers = tojoin.az.maxPlayers;
					int players = tojoin.amtPlayersInArena;
					if (players + 1 <= maxplayers)
					{
						getArena(name).addPlayer(player);
					}
					else
					{
						player.sendMessage(ChatColor.RED + "这个竞技场已满, 很抱歉!");
					}
				}
			}
			else
			{
				Arena ar = null;
				boolean disabled = false;
				for (Arena aar : activeArena)
				{
					if (aar.disabled && aar.az.equals(a))
					{
						disabled = true;
					}
				}
				for (ArenaZone aaz : loadedArena)
				{
					if (aaz.disabled && aaz.equals(a))
					{
						disabled = true;
					}
				}
				
				if (!disabled)
				{
					String arenaType = a.arenaType.toLowerCase();
					if (arenaType.equals("pvp"))
					{
						ar = new PVPArena(a);
					}
					else if (arenaType.equals("mob")) 
					{
						ar = new MOBArena(a);
					}
					else if (arenaType.equals("cq"))
					{
						ar = new CONQUESTArena(a);
					}
					else if (arenaType.equals("koth")) 
					{
						ar = new KOTHArena(a);
					}
					else if (arenaType.equals("bomb")) 
					{
						ar = new BOMBArena(a);
					}
					else if (arenaType.equals("ffa"))
					{
						ar = new FFAArena(a);
					}
					else if (arenaType.equals("hunger")) 
					{
						ar = new HUNGERArena(a);
					}
					else if (arenaType.equals("spleef")) 
					{
						ar = new SPLEEFArena(a);
					}
					else if (arenaType.equals("infect"))
					{
						ar = new INFECTArena(a);
					}
					else if (arenaType.equals("ctf"))
					{	
						ar = new CTFArena(a);
					}
					if (ar != null) 
					{
						activeArena.add(ar);
						ar.addPlayer(player);
						ar.announce();
					}
				}
				else
				{
					player.sendMessage(ChatColor.RED + "错误, 这个竞技场已被禁用!");
				}
			}
		}
		catch(Exception e) 
		{
			getLogger().severe("加入战斗时发生错误: " + e.getMessage());
		}
	}
	
	public Arena getArena(Player player)
	{
		for (Arena ac : activeArena)
		{
			ArenaPlayer ap = ac.getArenaPlayer(player);
			if (ap != null)
			{
				Player pl = Util.matchPlayer(ap.player.getName());
				if (pl != null && pl.isOnline())
				{
					if (pl.getName() == player.getName())
					{
						return ac;
					}
				}
			}
		}
		return null;
	}
	
	public Arena getArena(String name) 
	{
		for (Arena ac : activeArena)
		{
			if (ac.name.equals(name))
			{
				return ac;
			}
		}
		return null;
	}
	
	public ArenaZone getArenaZone(String name)
	{
		for (ArenaZone az : loadedArena)
		{
			if (az.arenaName.equals(name)) 
			{
				return az;
			}
		}
		return null;
	}
	
	public void setPoint(Player player) 
	{
		ArenaCreator ac = getArenaCreator(player);
		if (ac != null)
		{
			ac.setPoint(player);
			if (!(ac.msg.equals(""))) 
			{
				player.sendMessage(ChatColor.GRAY + ac.msg);
			}
		}
		else
		{
			player.sendMessage(ChatColor.RED + "错误, 你并未在编辑竞技场!");
		}
	}
	
	public void setDone(Player player)
	{
		ArenaCreator ac = getArenaCreator(player);
		if (ac != null) 
		{
			ac.setDone(player);
		}
		else
		{
			player.sendMessage(ChatColor.RED + "错误, 你并未在编辑竞技场!");
		}
	}
	
	public boolean isPlayerCreatingArena(Player player) 
	{
		return (getArenaCreator(player) != null);
	}
	
	public void stopCreatingArena(Player player)
	{ 
		for (int i=0; i<makingArena.size(); i++)
		{
			ArenaCreator ac = makingArena.get(i);
			if (ac.player.equalsIgnoreCase(player.getName()))
			{
				makingArena.remove(ac);
				player.sendMessage(FormatUtil.format("&e停止创造 {0}!", ac.arenaName));
			}
		}
	}

	public ArenaCreator getArenaCreator(Player player)
	{
		for (int i=0; i<makingArena.size(); i++)
		{
			ArenaCreator ac = makingArena.get(i);
			if (ac.player.equalsIgnoreCase(player.getName()))
			{
				return ac;
			}
		}
		return null;
	}
	
	public void createField(Player player, String name, String type)
	{
		if (isPlayerCreatingArena(player))
		{
			player.sendMessage(ChatColor.RED + "你已在创建一个新的竞技场!");
			return;
		}
		
		if (!fieldTypes.contains(type.toLowerCase()))
		{
			player.sendMessage(ChatColor.RED + "这不是一个可用的竞技场类型!");
			return;
		}
		
		for (ArenaZone az : loadedArena)
		{
			if (az.arenaName.equalsIgnoreCase(name))
			{
				player.sendMessage(ChatColor.RED + "已经有一个竞技场使用那个名字了!");
				return;
			}
		}
		
		getLogger().info(player.getName() + " 正在创建竞技场 " + name + ". 竞技场类型: " + type);
		ArenaCreator ac = new ArenaCreator(this, player);
		ac.setArena(name, type);
		makingArena.add(ac);
	}
	
	public void normalizeAll()
	{
		for (Player player : getServer().getOnlinePlayers())
		{
			Location loc = player.getLocation();
			if (isInArena(loc))
			{
				normalize(player);
				if (isInArena(player))
				{
					removeFromArena(player);
				}
			}
		}
	}
	
	public void normalize(Player player)
	{
		PlayerInventory inv = player.getInventory();
		
		inv.setHelmet(null);
		inv.setChestplate(null);
		inv.setLeggings(null);
		inv.setBoots(null);
		inv.clear();
	}

	public void loadFiles() 
	{
		loadClasses();
		loadConfigs();
		loadArenas();
	}
	
	public void clearMemory()
	{
		savedPlayers.clear();
		loadedArena.clear();
		activeArena.clear();
		makingArena.clear();
		fieldTypes.clear();
		waiting.clear();
		classes.clear();
		configs.clear();
		wcmd.clear();
	}
	
	public class ArenaUpdater extends BukkitRunnable
	{
		@Override
		public void run()
		{
			for (int i = 0; i < activeArena.size(); i++) 
			{
				try
				{
					activeArena.get(i).step();
				}
				catch(Exception e) 
				{
					//
				}
			} 
		}
	}
	
	public class RemindTask extends BukkitRunnable 
	{
		public Player player;
		public String name;
		public int t;
		
		public RemindTask(Player player, String name) 
		{
			this.player = player;
			this.name = name;
		}

		@Override
		public void run() 
		{
			waiting.remove(this);
			joinBattle(false, player, name);
			this.cancel();
		}
	}

	public void removePotions(Player pl) 
	{
		for (PotionEffect effect : pl.getActivePotionEffects())
		{
			pl.removePotionEffect(effect.getType());
		}
	}
	
    /**Vault Check**/
	private void checkVault(PluginManager pm) 
	{
		if (pm.isPluginEnabled("Vault"))
			setupEconomy();
	}
	
    /**Set up vault economy**/
    private boolean setupEconomy() 
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (economyProvider != null) 
		{
			economy = ((Economy)economyProvider.getProvider());
		}
 
		return economy != null;
	}
}