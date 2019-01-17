package me.richard12799.templerun;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitScheduler;

public class Events implements Listener {
	
	private TempleRun plugin;
	private List<UUID> freeze;
	public NoMove move;
	
	public Events(TempleRun pl) {
		plugin=pl;
		freeze=new ArrayList<UUID>();
		move=new NoMove(plugin);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) { //put them in a list every 10 seconds and remove them when they move
		final Player player=event.getPlayer();
		//check if frozen
		if(freeze.contains(player.getUniqueId())) {
			Location from=event.getFrom();
			Location to=event.getTo();
			if(from.getX()!=to.getX() || from.getY()!=to.getY() || from.getZ()!=to.getZ()) {
				player.teleport(from);
			}
			return;
		}
		final Runner r=plugin.getRunnerFromUUID(player.getUniqueId());
		if(r!=null) {
			//remove from not moving
			if(move.noMove.contains(player.getUniqueId())) {
				move.noMove.remove(player.getUniqueId());
			}
			if(move.map.containsKey(player.getUniqueId())) {
				move.map.get(player.getUniqueId()).cancel();
				move.map.remove(player.getUniqueId());
			}
			
			
			//check if falls in water
			if(player.getLocation().getBlock().getType()==Material.STATIONARY_WATER) {
				player.teleport(r.getArena().getStart());
				return;
			}
			
			List<Location> list=r.getArena().getCheckpoints();
			Location ploc=player.getLocation();
			Location finish=r.getArena().getFinish();
			
			//check if finishes
			if(ploc.getBlockX()==finish.getBlockX() && ploc.getBlockY()==finish.getBlockY() && ploc.getBlockZ()==finish.getBlockZ()) {
				plugin.removeRunner(r);
				
				freeze.add(player.getUniqueId());
				double d=r.getArena().getPrize();
				player.sendMessage(plugin.prefix+"You have successfully completed this Map!");
				player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				if(d!=0.00) {
					plugin.economy.depositPlayer(player, d);
					player.sendMessage(plugin.prefix+"$"+d+" has been added to your account!");
				}
				for(int i=0;i<5;i++) {
					Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
					FireworkMeta fwm = fw.getFireworkMeta();
					FireworkEffect effect = FireworkEffect.builder().withColor(Color.AQUA).with(Type.STAR).build();

					fwm.addEffect(effect);
					fwm.setPower(50);

					fw.setFireworkMeta(fwm);
				}
				r.finish();
				
				BukkitScheduler sd = plugin.getServer().getScheduler();
				sd.scheduleSyncDelayedTask(plugin, new Runnable(){public void run() {
					freeze.remove(player.getUniqueId());
					player.teleport(player.getWorld().getSpawnLocation());
					player.getInventory().clear();
					player.getInventory().setContents(r.getItems());
					player.getInventory().setArmorContents(r.getArmor());
				}}, 3*20);
			}
			
			//check if completes checkpoint
			int i=1;
			for(Location l: list) {
				if(l.getBlockX()==ploc.getBlockX() && l.getBlockY()==ploc.getBlockY() && l.getBlockZ()==ploc.getBlockZ()) {
					if(r.getLastCheckpoint()<i) {
						r.setCheckpoint(i);
					}
				}
				i++;
			}
			
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if(event.getItemInHand().equals(plugin.emeraldBlock)) {
			event.setCancelled(true);
		}
		else if(event.getItemInHand().equals(plugin.redstoneBlock)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if(event.getItemDrop().getItemStack().equals(plugin.emeraldBlock)) {
			event.setCancelled(true);
		}
		else if(event.getItemDrop().getItemStack().equals(plugin.redstoneBlock)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onUse(PlayerInteractEvent event) {
		if(event.getAction()==Action.RIGHT_CLICK_AIR || event.getAction()==Action.RIGHT_CLICK_BLOCK) {
			if(event.getItem().equals(plugin.emeraldBlock)) {
				Runner r=plugin.getRunnerFromUUID(event.getPlayer().getUniqueId());
				if(r!=null) {
					event.setCancelled(true);
					if(r.getLastCheckpoint()==0) {
						event.getPlayer().teleport(r.getArena().getStart());
					} else {
						event.getPlayer().teleport(r.getArena().getNthCheckpoint(r.getLastCheckpoint()));
					}
				}
			}
			else if(event.getItem().equals(plugin.redstoneBlock)) {
				event.setCancelled(true);
				event.getPlayer().performCommand("templerun leave");
			}
		}
	}
}
