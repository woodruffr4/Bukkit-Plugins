package me.richard12799.parkour;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ParkourGUI {
	
	Parkour plugin;
	ParkourPlayer player;
	
	private Inventory inv;
	private List<ParkourMap> mapsAvailable;
	private ItemStack e;
	private ItemStack m;
	private ItemStack h;
	private ItemStack bal;
	
	public ParkourGUI() { //default Events Constructor
		
	}
	
	public ParkourGUI(Parkour pl, ParkourPlayer p) {
		plugin=pl;
		player=p;
		inv=Bukkit.createInventory(null, 9, "Difficulties");
		setUpDifficulties();
		mapsAvailable=new ArrayList<ParkourMap>();
	}
	
	public Inventory loadMaps(int i) { // 1=easy, 2=medium, 3=hard
		Inventory maps=Bukkit.createInventory(null, 54, "Parkour Maps");
		for(ParkourMap map: mapsAvailable) {
			if(map.getDifficulty()==i) {
				ItemStack item=new ItemStack(Material.FEATHER);
				ItemMeta meta=item.getItemMeta();
				meta.setDisplayName("§7"+map.getName());
				item.setItemMeta(meta);
				maps.addItem(item);
			}
		}
		if(i==1) { //easy
			for(ParkourMap map: plugin.getEasyMaps()) {
				if(!mapsAvailable.contains(map)) {
					ItemStack item=new ItemStack(Material.FEATHER);
					ItemMeta meta=item.getItemMeta();
					meta.setDisplayName("§7"+map.getName());
					List<String> lore=new ArrayList<String>();
					lore.add("");
					if(map.isBuycraftOnly()) {
						lore.add("§6Buycraft Only");
					} else {
						lore.add("§6Click to purchase this Parkour Map for "+map.getCost()+" tokens");
					}
					lore.add("");
					meta.setLore(lore);
					item.setItemMeta(meta);
					maps.addItem(item);
				}
			}
		}
		else if(i==2) { //medium
			for(ParkourMap map: plugin.getMedMaps()) {
				if(!mapsAvailable.contains(map)) {
					ItemStack item=new ItemStack(Material.FEATHER);
					ItemMeta meta=item.getItemMeta();
					meta.setDisplayName("§7"+map.getName());
					List<String> lore=new ArrayList<String>();
					lore.add("");
					if(map.isBuycraftOnly()) {
						lore.add("§6Buycraft Only");
					} else {
						lore.add("§6Click to purchase this Parkour Map for "+map.getCost()+" tokens");
					}
					lore.add("");
					meta.setLore(lore);
					item.setItemMeta(meta);
					maps.addItem(item);
				}
			}
		}
		else if(i==3) { //hard
			for(ParkourMap map: plugin.getHardMaps()) {
				if(!mapsAvailable.contains(map)) {
					ItemStack item=new ItemStack(Material.FEATHER);
					ItemMeta meta=item.getItemMeta();
					meta.setDisplayName("§7"+map.getName());
					List<String> lore=new ArrayList<String>();
					lore.add("");
					if(map.isBuycraftOnly()) {
						lore.add("§6Buycraft Only");
					} else {
						lore.add("§6Click to purchase this Parkour Map for "+map.getCost()+" tokens");
					}
					lore.add("");
					meta.setLore(lore);
					item.setItemMeta(meta);
					maps.addItem(item);
				}
			}
		}
		return maps;
	}
	
	public void setUpDifficulties() {
		e=new ItemStack(Material.WOOL,1, (byte) 5);
		ItemMeta ime=e.getItemMeta();
		ime.setDisplayName("§7Easy");
		e.setItemMeta(ime);
		inv.addItem(e);
		
		m=new ItemStack(Material.WOOL,1,(byte) 4);
		ItemMeta imm=m.getItemMeta();
		imm.setDisplayName("§7Medium");
		m.setItemMeta(imm);
		inv.addItem(m);
		
		h=new ItemStack(Material.WOOL,1,(byte) 14);
		ItemMeta imh=h.getItemMeta();
		imh.setDisplayName("§7Hard");
		h.setItemMeta(imh);
		inv.addItem(h);
	}
	
	public void openInventory(Player p) {
		//Update Balance
		updateBal();
		
		//OpenInventory
		p.openInventory(inv);
	}
	
	public void updateBal() {
		bal=new ItemStack(Material.YELLOW_FLOWER,1);
		ItemMeta imbal=bal.getItemMeta();
		imbal.setDisplayName("§b"+player.getMoney()+" Tokens");
		List<String> lore=new ArrayList<String>();
		lore.add("§7You may use Tokens to purchase Parkour Maps.");
		lore.add("§7You can earn more Tokens by completing Parkours.");
		imbal.setLore(lore);
		bal.setItemMeta(imbal);
		inv.setItem(8, bal);
	}
	
	public boolean addMap(ParkourMap map) {
		if(mapsAvailable.contains(map)) return false;
		mapsAvailable.add(map);
		return true;
	}
	
	public boolean removeMap(ParkourMap map) {
		if(mapsAvailable.contains(map)) {
			mapsAvailable.remove(map);
			return true;
		}
		return false;
	}
	
	public Inventory getInventory() {
		return inv;
	}
	
	public ItemStack getEasy() {
		return e;
	}
	
	public ItemStack getMed() {
		return m;
	}
	
	public ItemStack getHard() {
		return h;
	}
	
	public List<ParkourMap> getMaps() {
		return mapsAvailable;
	}
}
