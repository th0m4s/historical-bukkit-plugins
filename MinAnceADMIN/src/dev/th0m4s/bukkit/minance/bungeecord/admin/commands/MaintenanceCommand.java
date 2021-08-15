package dev.th0m4s.bukkit.minance.bungeecord.admin.commands;

import dev.th0m4s.bukkit.minance.bungeecord.admin.MinAnceADMIN;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;


public class MaintenanceCommand extends Command {

	public MaintenanceCommand() {
		super("maintenance", "bungeecord.admin.set.off");
	}

	@SuppressWarnings("deprecation")
	@Override
    public void execute(final CommandSender sender, String[] args) {
		
		MinAnceADMIN.setOff(!MinAnceADMIN.getOff());
		
		if(MinAnceADMIN.getOff()) {
			
			sender.sendMessage("§aLa maintenance est maintenant activée !");
			
		} else sender.sendMessage("§cLa maintenance est maintenant désactivée !");
        
    }
	
}
