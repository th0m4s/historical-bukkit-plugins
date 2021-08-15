package dev.th0m4s.bukkit.minance.bungeecord.admin.events;

import dev.th0m4s.bukkit.minance.bungeecord.admin.MinAnceADMIN;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class SrvConnectEvent implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
    public void onConnect(ServerConnectEvent e){
		e.getPlayer().sendMessage("§7Envoi vers " + e.getTarget().getName() + " ...");
    }
	
	@SuppressWarnings("deprecation")
	@EventHandler
    public void onConnected(ServerConnectedEvent e){
		e.getPlayer().sendMessage("§7Reçu sur " + e.getServer().getInfo().getName());
    }
	
}
