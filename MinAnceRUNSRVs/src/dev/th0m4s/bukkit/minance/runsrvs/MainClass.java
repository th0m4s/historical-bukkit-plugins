package dev.th0m4s.bukkit.minance.runsrvs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class MainClass extends JavaPlugin implements Listener{

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if(e.getPlayer().isOp()) {
			e.getPlayer().sendMessage("Pour utiliser /srvmanager, voir l'article de cette page : https://minecraft.codeemo.com/mineoswiki/index.php?title=0.5.0");
		}
	}
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("[BUKKIT-SIDE] MinAnceRUNSRVs est initialisé !");
	}
	
	public boolean onCommand(final CommandSender sender, Command cmd, String label, String args[]) {
		if (cmd.getName().equalsIgnoreCase("srvmanager")) {
			if(sender.isOp() && args.length >= 1) {
				String cmdLine = "/usr/games/minecraft/mineos_console.py ";
				
				for(int i = 0; i < args.length; i++) {
					cmdLine += args[i] + " ";
				}
				
				sender.sendMessage("Execute command : " + cmdLine + ", ...");
				
				Process p;
				try {
					p = Runtime.getRuntime().exec(cmdLine);
					p.waitFor();
					
					BufferedReader reader = 
					         new BufferedReader(new InputStreamReader(p.getInputStream()));

					    String line = "";			
					    while ((line = reader.readLine())!= null) {
					    	sender.sendMessage(line);
					    }
					
				} catch (Exception e) {
					sender.sendMessage("§cUnable to execute command line");
				}
			}
		}
		
		return true;
	}
	
	@Override
	public void onDisable() {
		getLogger().info("[BUKKIT-SIDE] MinAnceRUNSRVs est désactivé !");
	}
	
}
