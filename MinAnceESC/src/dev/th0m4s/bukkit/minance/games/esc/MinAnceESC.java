package dev.th0m4s.bukkit.minance.games.esc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.th0m4s.bukkit.minance.games.core.MinAnceGAMESCORE;
import dev.th0m4s.bukkit.minance.games.core.api.GameManager;
import dev.th0m4s.bukkit.minance.games.core.enums.GameState;

public class MinAnceESC extends JavaPlugin implements Listener {

	GameManager gm;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getServer().dispatchCommand(getServer().getConsoleSender(), "save-off");
		getLogger().info("[BUKKIT-SIDE] MINANCEESC est activé !");

		gm = ((MinAnceGAMESCORE) getServer().getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI();
		gm.setState(GameState.wait);
	}

	@Override
	public void onDisable() {
		getLogger().info("[BUKKIT-SIDE] MINANCEESC est désactivé !");
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (gm.getState() == GameState.game) {
			ESCGame.move(e);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.setJoinMessage("");
		gm.addPlayer(e.getPlayer());

		String msg = ChatColor.GREEN + "{player} " + ChatColor.DARK_BLUE + "a rejoint la partie ! " + ChatColor.GOLD
				+ "{nb}" + ChatColor.DARK_BLUE + "/" + ChatColor.GOLD + "{max} " + ChatColor.DARK_GRAY
				+ "(minimum {min})";

		msg = msg.replace("{player}", e.getPlayer().getDisplayName());

		String mins = String.valueOf(gm.getGame().MIN_PLAYERS);
		msg = msg.replace("{min}", mins);

		String maxs = String.valueOf(gm.getGame().MAX_PLAYERS);
		msg = msg.replace("{max}", maxs);

		String nb = String.valueOf(gm.getPlayers().size());
		msg = msg.replace("{nb}", nb);

		getServer().broadcastMessage(msg);

		e.getPlayer().sendMessage(ChatColor.GREEN + "=====================================================");
		e.getPlayer().sendMessage(ChatColor.RESET + " ");
		e.getPlayer().sendMessage(
				ChatColor.AQUA + "Bienvenue dans Escape ! Pour connaître les règles, " + "fait /gamerules");
		e.getPlayer().sendMessage(ChatColor.RESET + " ");
		e.getPlayer().sendMessage(ChatColor.GREEN + "=====================================================");

		if (gm.getPlayers().size() >= gm.getGame().MIN_PLAYERS) {
			String msg2 = ChatColor.DARK_BLUE + "La partie va commencer dans {time}s";
			String time = String.valueOf(gm.getGame().WAIT_TIME);
			msg2 = msg2.replace("{time}", ChatColor.GOLD + time + ChatColor.DARK_BLUE);

			getServer().broadcastMessage(msg2);

			final Plugin pl = this;
			getServer().getScheduler().runTaskLater(this, new Runnable() {

				@Override
				public void run() {
					ESCGame.startGame(pl, getServer(), getConfig().getInt("settings.maptype"));
				}
			}, gm.getGame().WAIT_TIME * 20);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		event.setCancelled(!event.getWhoClicked().isOp());
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if (gm.getState() == GameState.wait) {
			if (e.getPlayer().isOp() == false)
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.setQuitMessage("");
		gm.removePlayer(e.getPlayer());

		if (gm.getState() == GameState.wait) {
			String msg = ChatColor.GREEN + "{player} " + ChatColor.DARK_BLUE + "à quitté la partie ! "
					+ ChatColor.GOLD + "{nb}" + ChatColor.DARK_BLUE + "/" + ChatColor.GOLD + "{max} "
					+ ChatColor.DARK_GRAY + "(minimum {min})";

			msg = msg.replace("{player}", e.getPlayer().getDisplayName());

			String mins = String.valueOf(gm.getGame().MIN_PLAYERS);
			msg = msg.replace("{min}", mins);

			String maxs = String.valueOf(gm.getGame().MAX_PLAYERS);
			msg = msg.replace("{max}", maxs);

			String nb = String.valueOf(gm.getPlayers().size());
			msg = msg.replace("{nb}", nb);

			getServer().broadcastMessage(msg);
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (gm.getState() == GameState.game) {
			event.setDeathMessage("");
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(ESCGame.SPAWN_LOCS[ESCGame.actualMap]);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		e.setCancelled(!e.getPlayer().isOp());
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		e.setCancelled(!e.getPlayer().isOp());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO: a retirer en prod
		if (cmd.getName().equalsIgnoreCase("startgame")) {

			String msg3 = ChatColor.DARK_BLUE + "La partie va commencer dans {time}s";
			msg3 = msg3.replace("{time}", String.valueOf(gm.getGame().WAIT_TIME));
			getServer().broadcastMessage(msg3);

			final Plugin pl = this;
			getServer().getScheduler().runTaskLater(this, new Runnable() {

				@Override
				public void run() {
					ESCGame.startGame(pl, getServer(), getConfig().getInt("settings.maptype"));
				}
			}, gm.getGame().WAIT_TIME * 20);
		} else if (cmd.getName().equalsIgnoreCase("gamerules")) {
			ESCGame.rules(sender);
		} else if (cmd.getName().equalsIgnoreCase("add")) {
			if (args[0].toString().equalsIgnoreCase("reload")) {
				reloadConfig();
				return true;
			}

			getConfig().set("maps.map" + args[0] + "." + args[1] + ".x", ((Entity) sender).getLocation().getBlockX());
			getConfig().set("maps.map" + args[0] + "." + args[1] + ".y", ((Entity) sender).getLocation().getBlockY());
			getConfig().set("maps.map" + args[0] + "." + args[1] + ".z", ((Entity) sender).getLocation().getBlockZ());

			saveConfig();
			reloadConfig();

		}

		return true;
	}
}
