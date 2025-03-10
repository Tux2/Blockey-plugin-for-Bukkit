package me.caske.blockey;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BlockeyPlayerListener extends PlayerListener{
	public static Blockey plugin;
	public int tasknumber;
	
	public BlockeyPlayerListener(Blockey instance){
		plugin = instance;
	}
	
	public void onPlayerMove(PlayerMoveEvent event){
		Player player = event.getPlayer();
		if(plugin.registered(player) && player.equals(plugin.getCarrier())){
			//Location start = event.getFrom();
			Location end = event.getTo();
			//System.out.println("From: X=" + start.getX() + " Z=" + start.getZ() + " To: X=" + end.getX() + " Z=" + end.getZ());
			if(!plugin.validMovement(end)){
				event.setCancelled(true);
				player.teleport(plugin.returnValidLocation(end));
			}
		}
	}
	
	public void onPlayerDropItem(PlayerDropItemEvent event){
		Player player = event.getPlayer();
		if(plugin.registered(player) && player.equals(plugin.getCarrier())){
			if(event.getItemDrop().getItemStack().getTypeId()==Material.SLIME_BALL.getId()){
				plugin.getServer().getScheduler().cancelTask(tasknumber);
				if(plugin.usespout != null) {
					SpoutPlayer splayer = SpoutManager.getPlayer(player);
					if(splayer.isSpoutCraftEnabled()) {
						splayer.sendNotification("Blockey", "You just dropped the ball.", Material.SLIME_BALL);
					}else {
						player.sendMessage(ChatColor.RED + "You dropped the ball. You are no longer the carrier");
					}
				}else {
					player.sendMessage(ChatColor.RED + "You dropped the ball. You are no longer the carrier");
				}
				plugin.setCarrier(null);
				player.setDisplayName(player.getName());
			}
		}
	}
	
	public void onPlayerPickupItem(PlayerPickupItemEvent event){
		Player player = event.getPlayer();
		if(plugin.registered(player) && event.getItem().getItemStack().getTypeId()==Material.SLIME_BALL.getId()){
			if(plugin.usespout != null) {
				SpoutPlayer splayer = SpoutManager.getPlayer(player);
				if(splayer.isSpoutCraftEnabled()) {
					splayer.sendNotification("Blockey", "You are now the carrier!", Material.SLIME_BALL);
				}else {
					player.sendMessage(ChatColor.GREEN + "You are now the carrier");
				}
			}else {
				player.sendMessage(ChatColor.GREEN + "You are now the carrier");
			}
			tasknumber = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new PlayerTimer(), 200);
			plugin.setCarrier(player);
			player.setDisplayName("The Carrier");
		}
	}
	
	public class PlayerTimer implements Runnable {
		
		public PlayerTimer(){}
		
		public void run(){
			Player player=plugin.getCarrier();
			if(player!=null){
				if(plugin.usespout != null) {
					SpoutPlayer splayer = SpoutManager.getPlayer(player);
					if(splayer.isSpoutCraftEnabled()) {
						splayer.sendNotification("Blockey", "You just lost the ball.", Material.SLIME_BALL);
					}else {
						player.sendMessage(ChatColor.RED + "You lost the ball. You are no longer the carrier");
					}
				}else {
					player.sendMessage(ChatColor.RED + "You lost the ball. You are no longer the carrier");
				}
				ItemStack ball = new ItemStack(Material.SLIME_BALL, 1);
				Location location = player.getTargetBlock(null,20).getLocation();
				player.getWorld().dropItem(location,ball);
				player.getInventory().remove(Material.SLIME_BALL);
				plugin.setCarrier(null);
				player.setDisplayName(player.getName());
			}
		}
	}
	
}
