package dev.th0m4s.bukkit.minance.lobby;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dev.th0m4s.bukkit.minance.core.MinAnceCORE;
import dev.th0m4s.bukkit.minance.games.core.MinAnceGAMESCORE;
import dev.th0m4s.bukkit.minance.games.core.api.GameManager;

public class MinAnceLOBBY extends JavaPlugin {

	@Override
	public void onEnable() {
		getLogger().info("[BUKKIT-SIDE] MINANCELOBBY est activé !");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("[BUKKIT-SIDE] MINANCELOBBY est désactivé !");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("lobby")) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				GameManager manager = ((MinAnceGAMESCORE) getServer().getPluginManager().getPlugin("MinAnceBUKKITGAMESCORE")).getAPI();
				if(manager.playerExist(player))
				sender.sendMessage(ChatColor.RED + "Tu es dans un jeu ! Fini d'abord ta partie !");	
			} else {
				sender.sendMessage(ChatColor.RED + "Ho, je te rapelle que t'es la console, et as-tu déjà vu un ordi se téléporter ? Non, alors ne tape pas cette commande !");
			}
		
		}
		
		return true;
	}
	
}
