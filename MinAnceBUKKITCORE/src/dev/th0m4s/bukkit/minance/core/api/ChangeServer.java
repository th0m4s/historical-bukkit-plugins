package dev.th0m4s.bukkit.minance.core.api;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class ChangeServer {

	Plugin pl;
	
	public ChangeServer(Plugin p) {
		pl = p;
	}
	
	public void changeServer(Player player, String server) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(pl, "BungeeCord", out.toByteArray());
	}
	
}
