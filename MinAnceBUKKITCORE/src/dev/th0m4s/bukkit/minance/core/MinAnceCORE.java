package dev.th0m4s.bukkit.minance.core;

import org.bukkit.plugin.java.JavaPlugin;

import dev.th0m4s.bukkit.minance.core.api.ChangeServer;

public class MinAnceCORE extends JavaPlugin{

	ChangeServer api;
	
	@Override
	public void onEnable() {
		api = new ChangeServer(this);
		
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getLogger().info("[BUKKIT-SIDE] MINANCECORE est activé !");
		
	}
	
	@Override
	public void onDisable() {
		getLogger().info("[BUKKIT-SIDE] MINANCECORE est désactivé !");
	}
	
	public ChangeServer getAPI() {
		return api;
	}
	
}
