package me.richard12799.parkour;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Events implements Listener {
	
	private Parkour plugin;
	
	public Events(Parkour pl) {
		plugin=pl;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player=(Player) event.getWhoClicked();
		ParkourPlayer pl=plugin.getParkourPlayerFromUUID(player.getUniqueId());
		ItemStack clicked=event.getCurrentItem();
		ParkourGUI gui=pl.getGUI();
		if(event.getInventory().equals(pl.getGUI().getInventory())) {
			event.setCancelled(true);
			if(clicked.equals(gui.getEasy())) {
				player.openInventory(gui.loadMaps(1));
				return;
			}
			else if(clicked.equals(gui.getMed())) {
				player.openInventory(gui.loadMaps(2));
				return;
			}
			else if(clicked.equals(gui.getHard())) {
				player.openInventory(gui.loadMaps(3));
				return;
			}
			else return;
		}
		if(clicked.getType()==Material.FEATHER && clicked.getItemMeta()!=null && clicked.getItemMeta().getDisplayName()!=null) {
			String name=ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
			if(isPurchasable(clicked.getItemMeta().getLore())) {
				event.setCancelled(true);
				ItemMeta im=clicked.getItemMeta();
				double cost=Double.parseDouble(im.getLore().get(1).split(" ")[7]);
				if(pl.hasMoney(cost)) {
					pl.takeMoney(cost);
					im.setLore(new ArrayList<String>());
					clicked.setItemMeta(im);
					for(ParkourMap map: plugin.getMaps()) {
						if(map.getName().equalsIgnoreCase(name)) {
							pl.addMap(map);
						}
					}
					player.sendMessage("§6You bought the "+name+" Parkour for "+cost+" tokens!");
					return;
				} else {
					player.sendMessage("§cYou do not have enough tokens to purchase this Parkour!");
					return;
				}
			}
			else if(isBuycraft(clicked.getItemMeta().getLore())) {
				event.setCancelled(true);
				player.sendMessage("§cThis Parkour Map is only available through Buycraft!");
				return;
				
			} else {
				for(ParkourMap map: gui.getMaps()) {
					if(map.getName().equalsIgnoreCase(name)) {
						event.setCancelled(true);
						ParkourGame pg=new ParkourGame(plugin,plugin.getParkourPlayerFromUUID(player.getUniqueId()),map);
						player.teleport(map.getStart());
						pg.start();
						pl.setInGame(true);
						pl.setCurrentGame(pg);
						plugin.addGame(pg);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		ParkourPlayer player=plugin.getParkourPlayerFromUUID(event.getPlayer().getUniqueId());
		if(player.isInGame()) {
			ParkourGame pg=player.getCurrentGame();
			int fx=pg.getMap().getFinish().getBlockX();
			int fy=pg.getMap().getFinish().getBlockY();
			int fz=pg.getMap().getFinish().getBlockZ();
			if(event.getTo().getBlockX()==fx && event.getTo().getBlockY()==fy && event.getTo().getBlockZ()==fz) {
				pg.finish();
				player.setCurrentGame(null);
				player.setInGame(false);
				plugin.removeGame(player.getUUID());
			}
			if(pg.getMap().getPoint1()!=null && pg.getMap().getPoint2()!=null) {
				Location p1=pg.getMap().getPoint1();
				Location p2=pg.getMap().getPoint2();
				int x=event.getTo().getBlockX();
				int y=event.getTo().getBlockY();
				int z=event.getTo().getBlockZ();
				if(x>=Math.min(p1.getBlockX(), p2.getBlockX()) && x<=Math.max(p1.getBlockX(), p2.getBlockX())
						&& y>=Math.min(p1.getBlockY(), p2.getBlockY()) && y<=Math.max(p1.getBlockY(), p2.getBlockY())
						&& z>=Math.min(p1.getBlockZ(), p2.getBlockZ()) && z<=Math.max(p1.getBlockZ(), p2.getBlockZ())) {
					event.getPlayer().teleport(pg.getMap().getStart());
					pg.getTimer().resetCounter();
				}
			}
		}
	}
	
	public boolean isPurchasable(List<String> lore) {
		if(lore==null || lore.isEmpty()) return false;
		for(String s: lore) {
			String strip=ChatColor.stripColor(s);
			if(strip.matches("Click to purchase this Parkour Map for \\d+\\.\\d+ tokens")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isBuycraft(List<String> lore) {
		if(lore==null || lore.isEmpty()) return false;
		for(String s: lore) {
			String strip=ChatColor.stripColor(s);
			if(strip.equalsIgnoreCase("Buycraft Only")) {
				return true;
			}
		}
		return false;
	}
}
