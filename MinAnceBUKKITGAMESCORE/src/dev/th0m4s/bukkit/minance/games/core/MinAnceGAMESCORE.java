package dev.th0m4s.bukkit.minance.games.core;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import dev.th0m4s.bukkit.minance.core.MinAnceCORE;
import dev.th0m4s.bukkit.minance.games.core.api.GameManager;
import dev.th0m4s.bukkit.minance.games.core.api.MinAnceGame;

import de.inventivegames.rpapi.ResourcePackAPI;
import de.inventivegames.rpapi.ResourcePackStatusEvent;
import de.inventivegames.rpapi.Status;

public class MinAnceGAMESCORE extends JavaPlugin implements Listener {

	GameManager api = new GameManager();
	boolean isActive = false;
	
	@Override
	public void onEnable() {
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		if(GameManager.setLoadedGames(this, getServer())) { 
			getServer().getPluginManager().registerEvents(this, this);
			isActive = true;
			getLogger().info("[BUKKIT-SIDE] MINANCEGAMESCORE est activé !");
		}
	}
	
	@Override
	public void onDisable() {
		getLogger().info("[BUKKIT-SIDE] MINANCEGAMESCORE est désactivé !");
	}
	
	public GameManager getAPI() {
		return api;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		getConfig().set("erreurressp." + e.getPlayer().getName(), false);
	}
	
	@EventHandler
	public void onResourcePackStatus(ResourcePackStatusEvent e) {
		if(getConfig().contains("erreurressp." + e.getPlayer().getName()) == false) { getConfig().set("erreurressp." + e.getPlayer().getName(), false); saveConfig(); }
		if(getConfig().getBoolean("erreurressp." + e.getPlayer().getName())) return;
		
		getConfig().set("erreurressp." + e.getPlayer().getName(), true); saveConfig();
		
		if(e.getStatus() == Status.DECLINED) {
			e.getPlayer().sendMessage(ChatColor.RED + "Vous devez accepter le ressource pack pour jouer !");
			e.getPlayer().sendMessage(ChatColor.AQUA + "Si vous avez refusé ce dernier, retournez dans la liste des serveurs de votre minecraft, éditez la ligne MinAnce et changer l'option Pack de ressources ");
			((MinAnceCORE) getServer().getPluginManager().getPlugin("MinAnceCORE")).getAPI().changeServer(e.getPlayer(), "main_lobby");
			
			return;
		}
		
		if(e.getStatus() == Status.FAILED_DOWNLOAD) {
			e.getPlayer().sendMessage(ChatColor.RED + "Impossible de télécharger le ressource pack");
			((MinAnceCORE) getServer().getPluginManager().getPlugin("MinAnceCORE")).getAPI().changeServer(e.getPlayer(), "main_lobby");
			
			return;
		}
	}
}
