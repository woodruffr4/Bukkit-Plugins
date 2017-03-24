package me.richard12799.parkour;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Parkour extends JavaPlugin {
	
	public static List<Double> times;
	private static List<ParkourMap> maps;
	public List<ParkourPlayer> players;
	public List<ParkourGame> games;
	private List<String> listCmds;
	
	private List<ParkourMap> easy;
	private List<ParkourMap> medium;
	private List<ParkourMap> hard;
	
	
	@Override
	public void onEnable() {
		//Set up Config
		if(!(new File(getDataFolder()+"/config.yml").exists())) {
			saveDefaultConfig();
		} else {
			saveConfig();
		}
		
		
		//Set up the possible commands
		listCmds=new ArrayList<String>();
		listCmds.add("§6/pa open - Opens difficulty GUI through an NPC. Pay for parkour maps with tokens here as well as teleport to parkour maps. - totalparkour.open");
		listCmds.add("§6/pa set1 [Parkour] - Sets first coordinate so when players fall, their timer is reset and they are teleported back to parkour map spawn .- totalparkour.create");
		listCmds.add("§6/pa set2 [Parkour] - Sets second coordinate so when players fall, their timer is reset and they are teleported back to parkour map spawn. - totalparkour.create");
		listCmds.add("§6/pa create [MapName] [easy/medium/hard] [TokensA] [TokensB] [BuycraftOnly]- Creates a parkour with [MapName] within [Difficulty] on the coordinates they're standing on. TokensA tells how much is issued to the player when the map is completed. TokenB tells how much the map is to play. BuycraftOnly is whether this map is only available through Buycraft. - totalparkour.create");
		listCmds.add("§6/pa delete [MapName] [Difficulty] - Deletes a parkour with [MapName] within [Difficulty]. - totalparkour.delete");
		listCmds.add("§6/pa token give [player] [amount] - Gives player tokens. - totalparkour.tokens");
		listCmds.add("§6/pa token take [player] [amount] - Takes away tokens from player. - totalparkour.tokens");
		listCmds.add("§6/pa complete [player] [MapName] - Player is complete, awarded [MapName]'s amount of set tokens. - totalparkour.complete");
		listCmds.add("§6/pa leaderboards [MapName] - View the top times from [MapName]. - totalparkour.leaderboard");
		listCmds.add("§6/pa givemap [player] [MapName] - Give [player] Parkour [MapName]. - totalparkour.givemap");
		listCmds.add("§6/pa load - Load all Parkour Maps from Config. - totalparkour.load");
		
		//Register Permissions
		Permissions p=new Permissions();
		getServer().getPluginManager().addPermission(p.open);
		getServer().getPluginManager().addPermission(p.create);
		getServer().getPluginManager().addPermission(p.delete);
		getServer().getPluginManager().addPermission(p.tokens);
		getServer().getPluginManager().addPermission(p.complete);
		getServer().getPluginManager().addPermission(p.leaderboard);
		getServer().getPluginManager().addPermission(p.load);
		
		//Initialize Lists
		times=new ArrayList<Double>();
		maps=new ArrayList<ParkourMap>();
		players=new ArrayList<ParkourPlayer>();
		games=new ArrayList<ParkourGame>();
		easy=new ArrayList<ParkourMap>();
		medium=new ArrayList<ParkourMap>();
		hard=new ArrayList<ParkourMap>();
		
		super.onEnable();
		
		//Register Events
		getServer().getPluginManager().registerEvents(new Events(this), this);
	}
	
	@Override
	public void onDisable() {
		//Save to Config
		FileConfiguration config=this.getConfig();
		for(ParkourMap map: maps) {
			String path="Maps."+map.getName();
			config.set(path+".prize", map.getPrize());
			config.set(path+".cost", map.getCost());
			config.set(path+".diff", map.getDifficulty());
			config.set(path+".buycraft", map.isBuycraftOnly());
			
			config.set(path+".start.world", map.getStart().getWorld().getName());
			config.set(path+".start.x", map.getStart().getX());
			config.set(path+".start.y", map.getStart().getY());
			config.set(path+".start.z", map.getStart().getZ());
			
			config.set(path+".finish.world", map.getFinish().getWorld().getName());
			config.set(path+".finish.x", map.getFinish().getX());
			config.set(path+".finish.y", map.getFinish().getY());
			config.set(path+".finish.z", map.getFinish().getZ());
			try {
				config.set(path+".p1.world", map.getPoint1().getWorld().getName());
				config.set(path+".p1.x", map.getPoint1().getX());
				config.set(path+".p1.y", map.getPoint1().getY());
				config.set(path+".p1.z", map.getPoint1().getZ());
			} catch(Exception e) {
				
			}
			
			try {
				config.set(path+".p2.world", map.getPoint2().getWorld().getName());
				config.set(path+".p2.x", map.getPoint2().getX());
				config.set(path+".p2.y", map.getPoint2().getY());
				config.set(path+".p2.z", map.getPoint2().getZ());
			} catch (Exception e) {
				
			}
			
			for(Time t: map.times) {
				config.set(path+".times."+t.getUUID().toString(), t.getTime());
			}
		}
		for(ParkourPlayer player: players) {
			String path="Players."+player.getUUID();
			config.set(path+".tokens", player.getMoney());
			List<String> maps=new ArrayList<String>();
			for(ParkourMap map: player.getGUI().getMaps()) {
				maps.add(map.getName());
			}
			config.set(path+".maps", maps);
		}
		saveConfig();
		super.onDisable();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		 if(label.equalsIgnoreCase("pa")) {
			 
			 if(!(sender instanceof Player)) {
				 sender.sendMessage("§cOnly players may use Parkour commands!");
				 return false;
			 }
			 
			 Player player=(Player) sender;
			 
			 if(args.length==0) {
				 listCommands(player);
				 return true;
			 }
			 
			 if(args.length==1) {
				 
				 if(args[0].equalsIgnoreCase("open")) {
					 if(!player.hasPermission(new Permissions().open)) {
						 player.sendMessage("§cYou do not have permission to use this command!");
						 return false;
					 }
					 ParkourPlayer pp=getParkourPlayerFromUUID(player.getUniqueId());
					 pp.openGUI();
					 return true;
				 }
				 else if(args[0].equalsIgnoreCase("load")) {
					 if(!player.hasPermission(new Permissions().load)) {
						 player.sendMessage("§cYou do not have permission to use this command!");
						 return false;
					 }
					 setUpParkour();
					 player.sendMessage("§6Successfully loaded Parkours!");
					 return true;
				 }
				 else {
					 listCommands(player);
					 return false;
				 }
			 }
			 else if(args.length==2) {
				 
				 if(args[0].equalsIgnoreCase("set1")) {
					 if(!player.hasPermission(new Permissions().create)) {
						 player.sendMessage("§cYou do not have permission to use this command!");
						 return false;
					 }
					 for(ParkourMap map: maps) {
						 if(map.getName().equalsIgnoreCase(args[1])) {
							 Location pLoc=player.getLocation();
							 map.setPoint1(pLoc);
							 player.sendMessage("§6You successfully set Point 1 for the "+map.getName()+" Parkour.");
							 return true;
						 }
					 }
					 player.sendMessage("§cThat Parkour Map does not exist!");
					 return false;
				 }
				 else if(args[0].equalsIgnoreCase("set2")) {
					 if(!player.hasPermission(new Permissions().create)) {
						 player.sendMessage("§cYou do not have permission to use this command!");
						 return false;
					 }
					 for(ParkourMap map: maps) {
						 if(map.getName().equalsIgnoreCase(args[1])) {
							 Location pLoc=player.getLocation();
							 map.setPoint2(pLoc);
							 player.sendMessage("§6You successfully set Point 2 for the "+map.getName()+" Parkour.");
							 return true;
						 }
					 }
					 player.sendMessage("§cThat Parkour Map does not exist!");
					 return false;
				 }
				 else if(args[0].equalsIgnoreCase("setfinish")) {
					 if(!player.hasPermission(new Permissions().create)) {
						 player.sendMessage("§cYou do not have permission to use this command!");
						 return false;
					 }
					 for(ParkourMap map: maps) {
						 if(map.getName().equalsIgnoreCase(args[1])) {
							 map.setFinish(player.getLocation());
							 player.sendMessage("§6You successfully set Parkour Map "+map.getName()+"'s finish point.");
							 return true;
						 }
					 }
					 player.sendMessage("§cThat Parkour Map does not exist!");
					 return false;
				 }
				 else if(args[0].equalsIgnoreCase("leaderboards")) {
					 if(!player.hasPermission(new Permissions().leaderboard)) {
						 player.sendMessage("§cYou do not have permission to use this command!");
						 return false;
					 }
					 for(ParkourMap map: maps) {
						 if(map.getName().equalsIgnoreCase(args[1])) {
							 map.sendLeaderboards(player);
							 return true;
						 }
					 }
					 player.sendMessage("§cThat Parkour Map does not exist!");
					 return false;
				 }
				 else {
					 listCommands(player);
					 return false;
				 }
			 }
			 else if(args.length==3) {
				 
				 if(args[0].equalsIgnoreCase("delete")) {
					 if(!player.hasPermission(new Permissions().delete)) {
						 player.sendMessage("§cYou do not have permission to use this command!");
						 return false;
					 }
					 ParkourMap m=null;
					 int diff=0;
					 List<ParkourMap> listToRemove;
					 if(args[2].equalsIgnoreCase("easy")) {
						 diff=1;
						 listToRemove=easy;
					 }
					 else if(args[2].equalsIgnoreCase("medium")) {
						 diff=2;
						 listToRemove=medium;
					 }
					 else if(args[2].equalsIgnoreCase("hard")) {
						 diff=3;
						 listToRemove=hard;
					 }
					 else {
						 player.sendMessage("§cPlease enter a valid difficulty!");
						 return false;
					 }
					 for(ParkourMap map: maps) {
						 if(map.getName().equalsIgnoreCase(args[1]) && diff==map.getDifficulty()) {
							 m=map;
						 }
					 }
					 if(m==null) {
						 player.sendMessage("§cThat Parkour Map does not exist!");
						 return false;
					 }
					 player.sendMessage("§6Removing map from players...");
					 for(ParkourPlayer pp: players) {
						 pp.removeMap(m);
					 }
					 this.getConfig().set("Maps."+m.getName(), null);
					 listToRemove.remove(m);
					 maps.remove(m);
					 player.sendMessage("§6Parkour Map "+m.getName()+" has been removed!");
					 return true;
				 }
				 else if(args[0].equalsIgnoreCase("complete")) {
					 if(!player.hasPermission(new Permissions().complete)) {
						 player.sendMessage("§cYou do not have permission to use this command!");
						 return false;
					 }
					 UUID uuid=null;
					 for(Player p: Bukkit.getOnlinePlayers()) {
						 if(p.getName().equalsIgnoreCase(args[1])) {
							 uuid=p.getUniqueId();
						 }
					 }
					 if(uuid==null) {
						 player.sendMessage("§cThat player is not currently online!");
						 return false;
					 }
					 ParkourPlayer pp=getParkourPlayerFromUUID(uuid);
					 ParkourMap pm=null;
					 for(ParkourMap map: maps) {
						 if(map.getName().equalsIgnoreCase(args[2])) {
							 pm=map;
						 }
					 }
					 if(pm==null) {
						 player.sendMessage("§cThat Parkour Map does not exist!");
						 return false;
					 }
					 pp.addMoney(pm.getPrize());
					 player.sendMessage("§6"+args[1]+" was awarded "+pm.getPrize()+" tokens for completing the "+pm.getName()+" Parkour!");
					 return true;
				 }
				 else if(args[0].equalsIgnoreCase("givemap")) {
					 if(!player.hasPermission(new Permissions().givemap)) {
						 player.sendMessage("§cYou do not have permission to use this command!");
						 return false;
					 }
					 OfflinePlayer pl;
					 try {
						 pl=Bukkit.getOfflinePlayer(args[1]);
					 } catch(Exception e) {
						 player.sendMessage("§cThat player does not exist!");
						 return false;
					 }
					 ParkourPlayer pp=getParkourPlayerFromUUID(pl.getUniqueId());
					 ParkourMap m=null;
					 for(ParkourMap map: maps) {
						 if(map.getName().equalsIgnoreCase(args[2])) {
							 m=map;
						 }
					 }
					 if(m==null) {
						 player.sendMessage("§cThat Parkour Map does not exist!");
						 return false;
					 }
					 pp.addMap(m);
					 player.sendMessage("§6Successfully gave "+pl.getName()+" the "+m.getName()+" Parkour!");
					 return true;
				 }
				 else {
					 listCommands(player);
					 return false;
				 }
			 }
			 else if(args.length==4) {
				 
				 // give/take tokens
				 if(args[0].equalsIgnoreCase("token")) {
					 if(!player.hasPermission(new Permissions().tokens)) {
						 player.sendMessage("§cYou do not have permission to use this command!");
						 return false;
					 }
					 if(args[1].equalsIgnoreCase("give")) {
						 OfflinePlayer op=null;
						 try {
							 op=Bukkit.getOfflinePlayer(args[2]);
						 } catch(Exception e) {
							 player.sendMessage("§cThat player does not exist!");
							 return false;
						 }
						 int amount=0;
						 try {
							 amount=Integer.parseInt(args[3]);
							 if(amount<=0) throw new Exception();
						 } catch(Exception e) {
							 player.sendMessage("§cPlease enter a valid amount of tokens!");
						 }
						 ParkourPlayer pp=getParkourPlayerFromUUID(op.getUniqueId());
						 pp.addMoney(amount);
						 player.sendMessage("§6You successfully gave "+amount+" tokens to "+args[2]+"!");
						 return true;
						 
					 }
					 else if(args[1].equalsIgnoreCase("take")) {
						 OfflinePlayer op=null;
						 try {
							 op=Bukkit.getOfflinePlayer(args[2]);
						 } catch(Exception e) {
							 player.sendMessage("§cThat player does not exist!");
							 return false;
						 }
						 int amount=0;
						 try {
							 amount=Integer.parseInt(args[3]);
							 if(amount<=0) throw new Exception();
						 } catch(Exception e) {
							 player.sendMessage("§cPlease enter a valid amount of tokens!");
						 }
						 ParkourPlayer pp=getParkourPlayerFromUUID(op.getUniqueId());
						 pp.takeMoney(amount);
						 player.sendMessage("§6You successfully took "+amount+" tokens from "+args[2]+"!");
						 return true;
					 }
					 else {
						 player.sendMessage("§cUsage: /pa token [give/take] [amount]");
						 return false;
					 }
				 }
			 }
			 else if(args.length==6) {
				 if(args[0].equalsIgnoreCase("create")) {
					 if(!player.hasPermission(new Permissions().create)) {
						 player.sendMessage("§cYou do not have permission to use this command!");
						 return false;
					 }
					 String name=args[1];
					 for(ParkourMap map: maps) {
						 if(map.getName().equalsIgnoreCase(name)) {
							 player.sendMessage("§cThat name has already been used!");
							 return false;
						 }
					 }
					 int diff=0;
					 if(args[2].equalsIgnoreCase("easy")) diff=1;
					 else if(args[2].equalsIgnoreCase("medium")) diff=2;
					 else if(args[2].equalsIgnoreCase("hard")) diff=3;
					 else {
						 listCommands(player);
						 return false;
					 }
					 double tokena=0;
					 try {
						 tokena=Integer.parseInt(args[3]);
					 } catch(Exception e) {
						 player.sendMessage("§cPlease provide a valid amount of tokens!");
						 return false;
					 }
					 double tokenb=0;
					 try {
						 tokenb=Integer.parseInt(args[4]);
					 } catch(Exception e) {
						 player.sendMessage("§cPlease provide a valid amount of tokens!");
						 return false;
					 }
					 
					 boolean buy=false;
					 if(args[5].equalsIgnoreCase("false")) {
						 buy=false;
					 }
					 else if(args[5].equalsIgnoreCase("true")) {
						 buy=true;
					 }
					 else {
						 player.sendMessage("§cProvide either true or false for the last argument!");
						 return false;
					 }
					 
					 ParkourMap map=new ParkourMap(name,player.getLocation(),tokena,tokenb,buy,diff);
					 maps.add(map);
					 if(map.getDifficulty()==1) easy.add(map);
					 else if(map.getDifficulty()==2) medium.add(map);
					 else if(map.getDifficulty()==3) hard.add(map);
					 player.sendMessage("§6Parkour Map "+name+" successfully created!");
					 return true;
				 }
			 }
		 }
		 
		 return false;
	}
	 
	public ParkourPlayer getParkourPlayerFromUUID(UUID id) {
		for(ParkourPlayer player: players) {
			if(player.getUUID().equals(id)) {
				return player;
			}
		}
		ParkourPlayer newPlayer=new ParkourPlayer(this,id,0);
		players.add(newPlayer);
		return newPlayer;
	}
	
	public void listCommands(Player pl) {
		for(String cmd: listCmds) {
			pl.sendMessage(cmd);
		}
	}
	
	public List<ParkourGame> getGames() {
		return games;
	}
	
	public void addGame(ParkourGame g) {
		games.add(g);
	}
	
	public boolean removeGame(UUID id) {
		ParkourGame pg=null;
		for(ParkourGame g: games) {
			if(g.getUUID().equals(id)) {
				pg=g;
			}
		}
		if(pg==null) {
			return false;
		}
		games.remove(pg);
		return true;
	}
	
	public List<ParkourMap> getEasyMaps() {
		return easy;
	}
	
	public List<ParkourMap> getMedMaps() {
		return medium;
	}
	
	public List<ParkourMap> getHardMaps() {
		return hard;
	}
	
	public List<ParkourMap> getMaps() {
		return maps;
	}
	
	public void setUpParkour() {
		maps.clear();
		players.clear();
		easy.clear();
		medium.clear();
		hard.clear();
		FileConfiguration config=this.getConfig();
		if(config.getConfigurationSection("Maps")!=null) {
			Set<String> keys=config.getConfigurationSection("Maps").getKeys(false);
			if(keys!=null && !keys.isEmpty()) {
				for(String key: keys) {
					String name=key;
					String path="Maps."+key;
					double prize=config.getDouble(path+".prize");
					double cost=config.getDouble(path+".cost");
					int diff=config.getInt(path+".diff");
					boolean buycraft=config.getBoolean(path+".buycraft");
					
					String startWorld=config.getString(path+".start.world");
					double sx=config.getDouble(path+".start.x");
					double sy=config.getDouble(path+".start.y");
					double sz=config.getDouble(path+".start.z");
					System.out.println(startWorld+" "+sx+" "+sy+" "+sz);
					
					for(World w: Bukkit.getWorlds()) {
						System.out.println(w.getName());
					}
					
					Location start=new Location(Bukkit.getWorld(startWorld),sx,sy,sz);
					
					System.out.println("loc "+start.getWorld()+" "+start.getX()+" "+start.getY()+" "+start.getZ());
					
					String finWorld=config.getString(path+".finish.world");
					double fx=config.getDouble(path+".finish.x");
					double fy=config.getDouble(path+".finish.y");
					double fz=config.getDouble(path+".finish.z");
					
					Location finish=new Location(Bukkit.getWorld(finWorld),fx,fy,fz);
					
					ParkourMap map=new ParkourMap(name,start,prize,cost,buycraft,diff);
					map.setFinish(finish);
					System.out.println(map.getStart());
					
					try {
						String p1World=config.getString(path+".p1.world");
						double p1x=config.getDouble(path+".p1.x");
						double p1y=config.getDouble(path+".p1.y");
						double p1z=config.getDouble(path+".p1.z");
						
						Location p1=new Location(Bukkit.getWorld(p1World),p1x,p1y,p1z);
						map.setPoint1(p1);
					} catch(Exception e) {
						
					}
					
					try {
						String p2World=config.getString(path+".p2.world");
						double p2x=config.getDouble(path+".p2.x");
						double p2y=config.getDouble(path+".p2.y");
						double p2z=config.getDouble(path+".p2.z");
						
						Location p2=new Location(Bukkit.getWorld(p2World),p2x,p2y,p2z);
						map.setPoint2(p2);
					} catch(Exception e) {
						
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
					
					maps.add(map);
					if(map.getDifficulty()==1) easy.add(map);
					else if(map.getDifficulty()==2) medium.add(map);
					else if(map.getDifficulty()==3) hard.add(map);
					
				}
			}
		}
		if(config.getConfigurationSection("Players")!=null) {
			Set<String> keys=config.getConfigurationSection("Players").getKeys(false);
			if(keys!=null && !keys.isEmpty()) {
				for(String key: keys) {
					String path="Players."+key;
					UUID id=UUID.fromString(key);
					double tokens=config.getDouble(path+".tokens");
					ParkourPlayer pp=new ParkourPlayer(this,id,tokens);
					List<String> maps=config.getStringList(path+".maps");
					for(String s: maps) {
						for(ParkourMap map: Parkour.maps) {
							if(s.equalsIgnoreCase(map.getName())) {
								pp.addMap(map);
							}
						}
					}
					players.add(pp);
				}
			}
		}
	}
}
