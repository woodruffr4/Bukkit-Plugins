package me.richard12799.templerun;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class TempleRun extends JavaPlugin {
	
	private List<Runner> runners=new ArrayList<Runner>();
	private List<Arena> arenas=new ArrayList<Arena>();
	public ItemStack emeraldBlock;
	public ItemStack redstoneBlock;
	public Economy economy = null;
	private Events e;
	public String prefix="§6§lTotal. §8§l> §7";
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		emeraldBlock=new ItemStack(Material.EMERALD_BLOCK);
		ItemMeta em=emeraldBlock.getItemMeta();
		em.setDisplayName("§a§lBACK TO LAST CHECKPOINT");
		emeraldBlock.setItemMeta(em);
		
		redstoneBlock=new ItemStack(Material.REDSTONE_BLOCK);
		ItemMeta rem=redstoneBlock.getItemMeta();
		rem.setDisplayName("§c§lLEAVE THIS GAME");
		redstoneBlock.setItemMeta(rem);
		
		setupEconomy();
		
		this.getServer().getPluginManager().addPermission(new Permissions().admin);
		this.getServer().getPluginManager().addPermission(new Permissions().use);
		
		//Set up Config
		if(!(new File(getDataFolder()+"/config.yml").exists())) {
			saveDefaultConfig();
		} else {
			saveConfig();
		}
		
		this.getServer().getPluginManager().registerEvents(e=new Events(this), this);
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		FileConfiguration config=this.getConfig();
		for(Arena map: arenas) {
			String path="Maps."+map.getName();
			config.set(path+".prize", map.getPrize());
			
			config.set(path+".start.world", map.getStart().getWorld().getName());
			config.set(path+".start.x", map.getStart().getX());
			config.set(path+".start.y", map.getStart().getY());
			config.set(path+".start.z", map.getStart().getZ());
			config.set(path+".start.pitch", map.getStart().getPitch());
			config.set(path+".start.yaw", map.getStart().getYaw());
			
			config.set(path+".finish.world", map.getFinish().getWorld().getName());
			config.set(path+".finish.x", map.getFinish().getX());
			config.set(path+".finish.y", map.getFinish().getY());
			config.set(path+".finish.z", map.getFinish().getZ());
			config.set(path+".finish.pitch", map.getFinish().getPitch());
			config.set(path+".finish.yaw", map.getFinish().getYaw());
			
			int count=1;
			for(Location l: map.getCheckpoints()) {
				config.set(path+".checkpoints."+count+".world", l.getWorld().getName());
				config.set(path+".checkpoints."+count+".x", l.getX());
				config.set(path+".checkpoints."+count+".y", l.getY());
				config.set(path+".checkpoints."+count+".z", l.getZ());
				config.set(path+".checkpoints."+count+".pitch", l.getPitch());
				config.set(path+".checkpoints."+count+".yaw", l.getYaw());
				count++;
			}
			
			config.set(path+".enabled", map.isEnabled());
			
			for(Time t: map.times) {
				config.set(path+".times."+t.getUUID().toString(), t.getTime());
			}
		}
		saveConfig();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(label.equalsIgnoreCase("templerun")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(prefix+"Only players can use this command!");
				return false;
			}
			Player player=(Player) sender;
			if(args.length==0) {
				if(player.hasPermission(new Permissions().admin)) {
					sendAdminCommands(player); //
					sendPlayerCommands(player); //
					return true;
				}
				if(player.hasPermission(new Permissions().use)) {
					sendPlayerCommands(player); //
					return true;
				}
				player.sendMessage(prefix+"§cYou do not have permission use this command!");
				return false;
			}
			else if(args.length==1) {
				if(args[0].equalsIgnoreCase("leave")) {
					if(!player.hasPermission(new Permissions().use)) {
						player.sendMessage(prefix+"§cYou do not have permission to use this command!");
						return false;
					}
					Runner r=getRunnerFromUUID(player.getUniqueId());
					if(r!=null) {
						runners.remove(r);
						player.teleport(player.getWorld().getSpawnLocation());
						player.getInventory().clear();
						player.getInventory().setContents(r.getItems());
						player.getInventory().setArmorContents(r.getArmor());
						player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
						
						if(e.move.noMove.contains(player.getUniqueId())) {
							e.move.noMove.remove(player.getUniqueId());
						}
						if(e.move.map.containsKey(player.getUniqueId())) {
							e.move.map.get(player.getUniqueId()).cancel();
							e.move.map.remove(player.getUniqueId());
						}
						
						player.sendMessage(prefix+"You successfully left the Map.");
						return true;
					} else {
						player.sendMessage(prefix+"§cYou are not currently in a game!");
						return false;
					}
				}
				else if(args[0].equalsIgnoreCase("load")) {
					if(!player.hasPermission(new Permissions().admin)) {
						player.sendMessage(prefix+"§cYou do not have permission to use this command!");
						return false;
					}
					loadMaps();
					player.sendMessage(prefix+"Successfully loaded the Maps!");
					return true;
				}
			}
			else if(args.length==2) {
				String arg=args[0];
				if(arg.equalsIgnoreCase("create")) {
					if(!player.hasPermission(new Permissions().admin)) {
						player.sendMessage(prefix+"§cYou do not have permission to use this command!");
						return false;
					}
					Arena a=getArena(args[1]);
					if(a!=null) {
						player.sendMessage(prefix+"§cThat arena already exists!");
						return false;
					} else {
						Arena na=new Arena(args[1]);
						arenas.add(na);
						player.sendMessage(prefix+"Arena created!");
						return true;
					}
					
				}
				else if(arg.equalsIgnoreCase("setspawn")) {
					if(!player.hasPermission(new Permissions().admin)) {
						player.sendMessage(prefix+"§cYou do not have permission to use this command!");
						return false;
					}
					Arena a=getArena(args[1]);
					if(a!=null) {
						a.setStart(player.getLocation());
						player.sendMessage(prefix+"Spawn location set!");
						return true;
					} else {
						player.sendMessage(prefix+"§cThat arena does not exist!");
						return false;
					}
				}
				else if(arg.equalsIgnoreCase("remove")) {
					if(!player.hasPermission(new Permissions().admin)) {
						player.sendMessage(prefix+"§cYou do not have permission to use this command!");
						return false;
					}
					Arena a=getArena(args[1]);
					if(a!=null) {
						arenas.remove(a);
						this.getConfig().set("Maps."+a.getName(), null);
						saveConfig();
						player.sendMessage(prefix+"Map removed!");
						return true;
					} else {
						player.sendMessage(prefix+"§cThat arena doesn't exist!");
						return false;
					}
				}
				else if(arg.equalsIgnoreCase("enable")) {
					if(!player.hasPermission(new Permissions().admin)) {
						player.sendMessage(prefix+"§cYou do not have permission to use this command!");
						return false;
					}
					Arena a=getArena(args[1]);
					if(a!=null) {
						a.setEnabled(true);
						player.sendMessage(prefix+"Map enabled!");
						return true;
					} else {
						player.sendMessage(prefix+"§cThat arena doesn't exist!");
						return false;
					}
				}
				else if(arg.equalsIgnoreCase("disable")) {
					if(!player.hasPermission(new Permissions().admin)) {
						player.sendMessage(prefix+"§cYou do not have permission to use this command!");
						return false;
					}
					Arena a=getArena(args[1]);
					if(a!=null) {
						a.setEnabled(false);
						player.sendMessage(prefix+"Map disabled!");
						return true;
					} else {
						player.sendMessage(prefix+"§cThat arena doesn't exist!");
						return false;
					}
				}
				else if(arg.equalsIgnoreCase("setfinish")) {
					if(!player.hasPermission(new Permissions().admin)) {
						player.sendMessage(prefix+"§cYou do not have permission to use this command!");
						return false;
					}
					Arena a=getArena(args[1]);
					if(a!=null) {
						a.setFinish(player.getLocation());
						player.sendMessage(prefix+"Finish location set!");
						return true;
					} else {
						player.sendMessage(prefix+"§cThat arena doesn't exist!");
						return false;
					}
				}
				else if(arg.equalsIgnoreCase("join")) {
					if(!player.hasPermission(new Permissions().use)) {
						player.sendMessage(prefix+"§cYou do not have permission to use this command!");
						return false;
					}
					Arena a=getArena(args[1]);
					if(a!=null) {
						if(!a.isEnabled()) {
							player.sendMessage(prefix+"§cThat map is not enabled!");
							return false;
						}
						if(getRunnerFromUUID(player.getUniqueId())==null) {
							Runner r=new Runner(this,player.getUniqueId(),a);
							r.setItems(player.getInventory().getContents());
							r.setArmor(player.getInventory().getArmorContents());
							r.setArena(a);
							player.getInventory().clear();
							
							player.getInventory().setItem(0, emeraldBlock);
							player.getInventory().setItem(8, redstoneBlock);
							player.setScoreboard(r.getArena().getScoreboard());
							
							runners.add(r);
							player.sendMessage(prefix+"You have successfully joined the "+a.getName()+" Map!");
							player.teleport(a.getStart());
							r.start();
							return true;
						} else {
							player.sendMessage(prefix+"§cYou are already in a Game!");
							return false;
						}
					} else {
						player.sendMessage(prefix+"§cThat arena doesn't exist!");
						return false;
					}
				}
				else if(arg.equalsIgnoreCase("addCheckpoint")) {
					if(!player.hasPermission(new Permissions().admin)) {
						player.sendMessage(prefix+"§cYou do not have permission to use this command!");
						return false;
					}
					Arena a=getArena(args[1]);
					if(a!=null) {
						a.addCheckpoint(player.getLocation());
						player.sendMessage(prefix+"Checkpoint added!");
						return true;
					} else {
						player.sendMessage(prefix+"§cThat arena doesn't exist!");
						return false;
					}
				}
				else if(arg.equalsIgnoreCase("removeCheckpoint")) {
					if(!player.hasPermission(new Permissions().admin)) {
						player.sendMessage(prefix+"§cYou do not have permission to use this command!");
						return false;
					}
					Arena a=getArena(args[1]);
					if(a!=null) {
						a.removeLastCheckpoint();
						player.sendMessage(prefix+"Last Checkpoint removed!");
						return true;
					} else {
						player.sendMessage(prefix+"§cThat arena doesn't exist!");
						return false;
					}
				}
			}
			else if(args.length==3) {
				String arg=args[0];
				if(arg.equalsIgnoreCase("setCheckpoint")) {
					if(!player.hasPermission(new Permissions().admin)) {
						player.sendMessage(prefix+"§cYou do not have permission to use this command!");
						return false;
					}
					int num=-1;
					try {
						num=Integer.parseInt(args[1]);
					} catch(Exception e) {
						player.sendMessage(prefix+"§cPlease enter a valid number.");
						return false;
					}
					if(num<=0) {
						player.sendMessage(prefix+"§cPlease enter a valid number.");
						return false;
					}
					Arena a=getArena(args[2]);
					if(a!=null) {
						if(a.setCheckpoint(num, player.getLocation())) {
							player.sendMessage(prefix+"Checkpoint set!");
							return true;
						} else {
							player.sendMessage(prefix+"§cYou only have "+a.getCheckpoints().size()+" checkpoints.");
							return false;
						}
					} else {
						player.sendMessage(prefix+"§cThat arena doesn't exist!");
						return false;
					}
				}
				else if(arg.equalsIgnoreCase("delCheckpoint")) {
					if(!player.hasPermission(new Permissions().admin)) {
						player.sendMessage(prefix+"§cYou do not have permission to use this command!");
						return false;
					}
					int num=-1;
					try {
						num=Integer.parseInt(args[1]);
					} catch(Exception e) {
						player.sendMessage(prefix+"§cPlease enter a valid number.");
						return false;
					}
					if(num<=0) {
						player.sendMessage(prefix+"§cPlease enter a valid number.");
						return false;
					}
					Arena a=getArena(args[2]);
					if(a!=null) {
						if(a.removeCheckpoint(num)) {
							player.sendMessage(prefix+"Checkpoint removed!");
							return true;
						} else {
							player.sendMessage(prefix+"§cYou only have "+a.getCheckpoints().size()+" checkpoints.");
							return false;
						}
					} else {
						player.sendMessage(prefix+"§cThat arena doesn't exist!");
						return false;
					}
				}
				else if(arg.equalsIgnoreCase("setprize")) {
					if(!player.hasPermission(new Permissions().admin)) {
						player.sendMessage(prefix+"§cYou do not have permission to use this command!");
						return false;
					}
					double amount=-1;
					try {
						amount=Double.parseDouble(args[1]);
					} catch(Exception e) {
						player.sendMessage("§cPlease enter a valid amount of money!");
						return false;
					}
					if(amount<0) {
						player.sendMessage("§cPlease enter a valid amount of money!");
						return false;
					}
					Arena a=getArena(args[2]);
					if(a!=null) {
						a.setPrize(amount);
						player.sendMessage(prefix+"You set the prize to $"+amount+" for Arena "+a.getName()+"!");
						return true;
					} else {
						player.sendMessage("§cThat arena does not exist!");
						return false;
					}
				}
			}
		}
		
		return false;
	}
	
	public void sendAdminCommands(Player player) {
		player.sendMessage(prefix
				+"\n§b/templerun create [Map]"
				+ "\n§b/templerun setSpawn [Map]"
				+ "\n§b/templerun addCheckpoint [Map]"
				+ "\n§b/templerun setCheckpoint [number] [Map]"
				+ "\n§b/templerun delCheckpoint [number] [Map]"
				+ "\n§b/templerun removeCheckpoint [Map]"
				+ "\n§b/templerun setFinish [Map]"
				+ "\n§b/templerun setPrize [amount] [Map]"
				+ "\n§b/templerun enable [Map]"
				+ "\n§b/templerun disable [Map]"
				+ "\n§b/templerun remove [Map]"
				+ "\n§b/templerun load");
	}
	
	public void sendPlayerCommands(Player player) {
		player.sendMessage(prefix+"\n§b/templerun join [Map]\n§b/templerun leave");
	}
	
	public Runner getRunnerFromUUID(UUID id) {
		for(Runner r: runners) {
			if(r.getUUID().equals(id)) {
				return r;
			}
		}
		return null;
	}
	
	public Arena getArena(String s) {
		for(Arena a: arenas) {
			if(a.getName().equalsIgnoreCase(s)) {
				return a;
			}
		}
		return null;
	}
	
	public boolean removeRunner(Runner r) {
		if(runners.remove(r)) {
			return true;
		}
		return false;
	}
	
	private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	
	public List<Runner> getRunners() {
		return runners;
	}
	
	public void loadMaps() {
		arenas.clear();
		runners.clear();
		
		FileConfiguration config=this.getConfig();
		if(config.getConfigurationSection("Maps")!=null) {
			Set<String> keys=config.getConfigurationSection("Maps").getKeys(false);
			if(keys!=null && !keys.isEmpty()) {
				for(String key: keys) {
					String name=key;
					String path="Maps."+key;
					double prize=config.getDouble(path+".prize");
					boolean enabled=config.getBoolean(path+".enabled");
					
					String startWorld=config.getString(path+".start.world");
					double sx=config.getDouble(path+".start.x");
					double sy=config.getDouble(path+".start.y");
					double sz=config.getDouble(path+".start.z");
					double p=config.getDouble(path+".start.pitch");
					double yaw=config.getDouble(path+".start.yaw");
					
					System.out.println(startWorld+" "+sx+" "+sy+" "+sz);
					
					for(World w: Bukkit.getWorlds()) {
						System.out.println(w.getName());
					}
					
					Location start=new Location(Bukkit.getWorld(startWorld),sx,sy,sz,(float)yaw,(float)p);
					
					System.out.println("loc "+start.getWorld()+" "+start.getX()+" "+start.getY()+" "+start.getZ());
					
					String finWorld=config.getString(path+".finish.world");
					double fx=config.getDouble(path+".finish.x");
					double fy=config.getDouble(path+".finish.y");
					double fz=config.getDouble(path+".finish.z");
					double pf=config.getDouble(path+".finish.pitch");
					double yf=config.getDouble(path+".finish.yaw");
					
					Location finish=new Location(Bukkit.getWorld(finWorld),fx,fy,fz,(float)yf,(float)pf);
					
					Arena map=new Arena(name);
					map.setFinish(finish);
					map.setStart(start);
					map.setPrize(prize);
					map.setEnabled(enabled);
					System.out.println(map.getStart());
					
					if(config.getConfigurationSection(path+".checkpoints")!=null) {
						Set<String> nums=config.getConfigurationSection(path+".checkpoints").getKeys(false);
						if(nums!=null && !nums.isEmpty()) {
							for(String s: nums) {
								String world=config.getString(path+".checkpoints."+s+".world");
								double x=config.getDouble(path+".checkpoints."+s+".x");
								double y=config.getDouble(path+".checkpoints."+s+".y");
								double z=config.getDouble(path+".checkpoints."+s+".z");
								double pc=config.getDouble(path+".checkpoints."+s+".pitch");
								double yc=config.getDouble(path+".checkpoints."+s+".yaw");
								Location l=new Location(Bukkit.getWorld(world),x,y,z,(float)yc,(float)pc);
								map.addCheckpoint(l);
							}
						}
					}
					
					
					if(config.getConfigurationSection(path+".times")!=null) {
						Set<String> uuids=config.getConfigurationSection(path+".times").getKeys(false);
						if(uuids!=null && !uuids.isEmpty()) {
							for(String id: uuids) {
								Time t=new Time(UUID.fromString(id),config.getDouble(path+".times."+id));
								map.addTime(t);
							}
						}
					}
					
					arenas.add(map);
					
				}
			}
		}
	}
}
