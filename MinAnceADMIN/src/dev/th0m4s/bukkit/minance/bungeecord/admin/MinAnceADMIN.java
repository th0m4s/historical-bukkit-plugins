package dev.th0m4s.bukkit.minance.bungeecord.admin;

import java.io.File;
import java.io.IOException;

import dev.th0m4s.bukkit.minance.bungeecord.admin.commands.MaintenanceCommand;
import dev.th0m4s.bukkit.minance.bungeecord.admin.commands.StatusCommand;
import dev.th0m4s.bukkit.minance.bungeecord.admin.events.MOTDEvent;
import dev.th0m4s.bukkit.minance.bungeecord.admin.events.PlayerJoinEvent;
import dev.th0m4s.bukkit.minance.bungeecord.admin.events.SrvConnectEvent;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class MinAnceADMIN extends Plugin{

	private static Plugin pl;
	private static boolean isOff = false;
	
	@Override
    public void onLoad() {
        System.out.println("[BUNGEECORD-SIDE] MINANCEADMIN est en cours de chargement...");
    }
   
    @Override
    public void onEnable() {
    	
    	getProxy().getPluginManager().registerCommand(this, new StatusCommand());
    	getProxy().getPluginManager().registerCommand(this, new MaintenanceCommand());
		
    	getProxy().getPluginManager().registerListener(this, new PlayerJoinEvent());
    	getProxy().getPluginManager().registerListener(this, new MOTDEvent());
    	getProxy().getPluginManager().registerListener(this, new SrvConnectEvent());
    	
		pl = this;
		
		Configuration defaultConfig = new Configuration();
		defaultConfig.set("motd", "A Minecraft Proxy");
		
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(defaultConfig, new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	System.out.println("[BUNGEECORD-SIDE] MINANCEADMIN est chargé !");
    }
   
    @Override
    public void onDisable() {
    	System.out.println("[BUNGEECORD-SIDE] MINANCEADMIN est désactivé !");
    }
    
    
    public static Plugin getInstance() { 
    	return pl;
    }
    
    public static void setOff(boolean off) {
    	isOff = off;
    }
    
    public static boolean getOff() {
    	return isOff;
    }
	
}
