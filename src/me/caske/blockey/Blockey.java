package me.caske.blockey;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spout.Spout;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;


public class Blockey extends JavaPlugin{
	
	public static Blockey plugin;
	private Player carrier = null;
	private Location carrierloc = null;
	//defining the playerListener
	private final BlockeyPlayerListener playerListener = new BlockeyPlayerListener(this);
	//defining the HashMap with the Players in it
	private HashMap<Player,Boolean> players = new HashMap<Player,Boolean>();
	//defining the logger
	public final Logger logger = Logger.getLogger("Minecraft");
	public Spout usespout = null;
	
	
	//Adding a method that runs when the server boots or when he starts the plugin.
	@Override
	public void onEnable(){
		setupSpout();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM,playerListener,Event.Priority.Normal,this);
		pm.registerEvent(Event.Type.PLAYER_MOVE,playerListener,Event.Priority.Normal,this);
		pm.registerEvent(Event.Type.PLAYER_DROP_ITEM,playerListener,Event.Priority.Normal,this);
		PluginDescriptionFile descfile = this.getDescription();
		logger.info(descfile.getFullName() + " is enabled");
	}
	
	//Adding a method that runs when the server shuts down or shuts the plug down.
	@Override
	public void onDisable(){
		PluginDescriptionFile descfile = this.getDescription();
		logger.info(descfile.getFullName() + " is disabled");
	}
	
	
	//check if the player is registered as a Blockey Player.
	public boolean registered(Player player){
		if(players.containsKey(player)){
			return players.get(player);
		} else {
			return false;
		}
	}
	
	//register the player if he isn't yet else unregister him.
	public void toggleRegister(Player player){
		if(registered(player)){
			players.put(player, false);
			if(usespout != null) {
				SpoutPlayer splayer = SpoutManager.getPlayer(player);
				if(splayer.isSpoutCraftEnabled()) {
					splayer.sendNotification("Blockey", "You left the game.", Material.SLIME_BALL);
				}else {
					player.sendMessage(ChatColor.BLUE + "You can no longer play Blockey!");
				}
			}else {
				player.sendMessage(ChatColor.BLUE + "You can no longer play Blockey!");
			}
		} else {
			players.put(player,true);
			if(usespout != null) {
				SpoutPlayer splayer = SpoutManager.getPlayer(player);
				if(splayer.isSpoutCraftEnabled()) {
					splayer.sendNotification("Blockey", "You joined the game!", Material.SLIME_BALL);
				}else {
					player.sendMessage(ChatColor.BLUE + "You can now play Blockey!");
				}
			}else {
				player.sendMessage(ChatColor.BLUE + "You can now play Blockey!");
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(label.equalsIgnoreCase("Blockey")){
			toggleRegister((Player) sender);
			return true;
		}
		return false;
	}
	
	public Player getCarrier(){
		return carrier;
	}
	
	public boolean validMovement(Location loc) {
		double xdiff = carrierloc.getX() - loc.getX();
		double zdiff = carrierloc.getZ() - loc.getZ();
		if((-0.5d < xdiff && xdiff < 0.5d) && (-0.5d < zdiff && zdiff < 0.5d)) {
			return true;
		}else {
			return false;
		}
	}
	
	public Location returnValidLocation(Location loc) {
		return new Location(carrierloc.getWorld(), carrierloc.getX(), carrierloc.getY(), carrierloc.getZ(), loc.getYaw(), loc.getPitch());
	}
	
	public void setCarrier(Player player){
		carrier = player;
		if(player != null) {
			carrierloc = player.getLocation().clone();
		}
	}
    
    private void setupSpout() {
    	Plugin p = getServer().getPluginManager().getPlugin("Spout");
		if(p == null){
			usespout = null;
			System.out.println("[Blockey] Spout not detected. Disabling spout support.");
		} else {
			usespout = (Spout)p;
			System.out.println("[Blockey] Spout detected. Spout support enabled.");
		}
    }
}
