package dev.th0m4s.minance.messages;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import me.confuser.barapi.BarAPI;

public class MinAnceMESSAGE extends JavaPlugin implements Listener{

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("[BUKKIT-SIDE] MINANCEMESS est activé !");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("[BUKKIT-SIDE] MINANCEMESS est désactivé !");
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		ActionBarAPI.sendActionBar(p, "§9Visite notre site web et amuses toi !", 99999);
		
		//BarAPI.setHealth(p, 100.0f);
		//BarAPI.setMessage(p, "§6Tu es actuelemnt sur §3MinAnce §6!");
		
		if(BarAPI.hasBar(p)) BarAPI.removeBar(p);
	}	
}
