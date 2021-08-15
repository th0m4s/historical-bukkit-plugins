package dev.th0m4s.bukkit.minance.bungeecord.admin.events;

import dev.th0m4s.bukkit.minance.bungeecord.admin.MinAnceADMIN;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class MOTDEvent implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
    public void onProxyPing(ProxyPingEvent e){
		ServerPing sp = e.getResponse();
		
		if(sp == null) return;
        if(MinAnceADMIN.getOff()) sp.setVersion(new ServerPing.Protocol("§cServeur en maintenance", 1));
 
        e.setResponse(sp);
    }
	
}
