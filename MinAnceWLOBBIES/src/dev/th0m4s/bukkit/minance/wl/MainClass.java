package dev.th0m4s.bukkit.minance.wl;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MainClass extends JavaPlugin implements Listener {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("wlobbies")) {
			if(sender instanceof Player) {
				if(sender.isOp()) {
					if(args.length != 2) {
						sender.sendMessage("§cUsage: /wl <action> <warp>");
					} else {
						if(args[0].equalsIgnoreCase("add")) {
							String n = args[1];
							
							if(!getConfig().contains("lobbies." + n)) {
								Location loc = ((Player) sender).getLocation();
				        		int x = loc.getBlockX();
				        		int y = loc.getBlockY();
				        		int z = loc.getBlockZ();
				        		String w = loc.getWorld().getName();
				        		
				        		getConfig().set("lobbies." + n + ".spawn.x", x);
				        		getConfig().set("lobbies." + n + ".spawn.y", y);
				        		getConfig().set("lobbies." + n + ".spawn.z", z);
				        		getConfig().set("lobbies." + n + ".spawn.w", w);
				        		
				        		saveConfig();
				        		reloadConfig();
				        		
				        		sender.sendMessage("§2Enregistré!");
							}
						} else if(args[0].equalsIgnoreCase("edit")) {
							String n = args[1];
							
							if(getConfig().contains("lobbies." + n)) {
								Location loc = ((Player) sender).getLocation();
				        		int x = loc.getBlockX();
				        		int y = loc.getBlockY();
				        		int z = loc.getBlockZ();
				        		String w = loc.getWorld().getName();
				        		
				        		getConfig().set("lobbies." + n + ".spawn.x", x);
				        		getConfig().set("lobbies." + n + ".spawn.y", y);
				        		getConfig().set("lobbies." + n + ".spawn.z", z);
				        		getConfig().set("lobbies." + n + ".spawn.w", w);
				        		
				        		saveConfig();
				        		reloadConfig();
				        		
				        		sender.sendMessage("§2Enregistré!");
							} else {
								sender.sendMessage("§cCe warplobby n'existe pas !");	
							}
						}
					}
				}
			} else {
				sender.sendMessage("§cTu n'es pas un joueur !");
			}
		}
		
		return true;
	}
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("[BUKKIT-SIDE] MINANCEWL est activé !");		
	}
	
	@Override
	public void onDisable() {
		getLogger().info("[BUKKIT-SIDE] MINANCEWL est désactivé !");		
	}
	
}
