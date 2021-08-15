package dev.th0m4s.bukkit.minance.bungeecord.admin.events;

import dev.th0m4s.bukkit.minance.bungeecord.admin.MinAnceADMIN;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerJoinEvent implements Listener {

	@EventHandler
    public void onPlayerJoin(PreLoginEvent e){
        // System.out.println("PreLoginEvent: "+ e.getConnection().getName());
    }
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(LoginEvent e){
        // System.out.println("LoginEvent: "+ e.getConnection().getName());
    }
 
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerJoin(PostLoginEvent e){
        // System.out.println("PostLoginEvent: "+ e.getPlayer().getName());
   
        if(MinAnceADMIN.getOff()) {
        	//ProxiedPlayer p = ProxyServer.getInstance().getPlayer(e.getPlayer().getUUID());
        	
        	ProxiedPlayer p = e.getPlayer();
        	
        	if(!p.hasPermission("bungeecord.admin.connect")) {
        		p.disconnect("Serveur en maintenance");
        	}
        }
    }
	
}
