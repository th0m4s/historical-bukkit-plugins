package dev.th0m4s.bukkit.minance.coloredsigns;

import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MainClass extends JavaPlugin implements Listener{

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getLogger().info("[BUKKIT-SIDE] MinAnceCOLOREDSIGNS est initalisé !");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("[BUKKIT-SIDE] MinAnceCOLOREDSIGNS est désactivé !");
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if(e.getPlayer().isOp()) {
			e.setLine(0, e.getLine(0).replace("&", "§"));
			e.setLine(1, e.getLine(1).replace("&", "§"));
			e.setLine(2, e.getLine(2).replace("&", "§"));
			e.setLine(3, e.getLine(3).replace("&", "§"));
		}
	}
	
}
