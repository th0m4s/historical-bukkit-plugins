package dev.th0m4s.bukkit.minance.bungeecord.admin.commands;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

public class StatusCommand extends Command{

	public StatusCommand() {
		super("status");
	}

	@SuppressWarnings("deprecation")
	@Override
    public void execute(final CommandSender sender, String[] args) {
		
		for(final ServerInfo si : ProxyServer.getInstance().getServers().values()){
            
            si.ping(new Callback<ServerPing>() {
            	
				@Override
                public void done(ServerPing sp, Throwable throwable) {
					
                    if(throwable != null){
                    	
                        sender.sendMessage(si.getName()+" est §céteint");
                        
                    } else {
                    	
                        //sender.sendMessage(si.getName()+" : "+sp.getModinfo()+ " | "+sp.getDescription() +" | "+ sp.getPlayers().getOnline());
                    	sender.sendMessage(si.getName() + " : ");
                    	sender.sendMessage("   Description :" + sp.getDescription());
                    	sender.sendMessage("   "  + sp.getPlayers().getOnline() + "joueur(s) en ligne,");
                    	
                    }
 
                }
				
            });
            
        }
        
    }
	
}
