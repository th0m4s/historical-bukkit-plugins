package dev.th0m4s.bukkit.minance.games.smw;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
import dev.th0m4s.bukkit.minance.games.smw.SMWClass;
import dev.th0m4s.bukkit.minance.games.smw.SMWGame;

public class MinAnceBUKKITSMW extends JavaPlugin implements Listener {

	GameManager gm;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getServer().dispatchCommand(getServer().getConsoleSender(), "save-off");
		
		getLogger().info("[BUKKIT-SIDE] MINANCESMW est activé !");
		
		gm = ((MinAnceGAMESCORE) getServer().getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI();
		gm.setState(GameState.wait);
	}
	
	@Override
	public void onDisable() {
		getLogger().info("[BUKKIT-SIDE] MINANCESMW est désactivé !");
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.setJoinMessage("");
		
		ItemStack[] inv = new ItemStack[27];
		
		inv[0] = new ItemStack(116, 1);
		ItemMeta im = inv[0].getItemMeta();
		im.setDisplayName("Sélection de classe");
		inv[0].setItemMeta(im);
		
		e.getPlayer().getInventory().setContents(inv);
		e.getPlayer().updateInventory();
		
		gm.addPlayer(e.getPlayer());		
		
		String msg = ChatColor.GREEN + "{player} " + ChatColor.DARK_BLUE + "à rejoint la partie ! " + ChatColor.GOLD + "{nb}" +
				ChatColor.DARK_BLUE + "/" + ChatColor.GOLD + "{max} " + ChatColor.DARK_GRAY + "(minimum {min})";
		
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
		e.getPlayer().sendMessage(ChatColor.AQUA + "Bienvenue au StarMineWars ! Si tu ne te souviens pas (ou que tu ne connais pas) les règles, " +
				"fait /gamerules");
		e.getPlayer().sendMessage(ChatColor.RESET + " ");
		e.getPlayer().sendMessage(ChatColor.GREEN + "=====================================================");
		
		if(gm.getPlayers().size() >= gm.getGame().MIN_PLAYERS) {
			String msg2 = ChatColor.DARK_BLUE + "La partie va commencer dans {time}s";
			
			String time = String.valueOf(gm.getGame().WAIT_TIME);
			msg2 = msg2.replace("{time}", ChatColor.GOLD + time + ChatColor.DARK_BLUE);
			
			getServer().broadcastMessage(msg2);
			
			final Plugin pl = this;
			getServer().getScheduler().runTaskLater(this, new Runnable() {
				
				@Override
				public void run() {
					SMWGame.startGame(pl, getServer(), getConfig().getInt("settings.maptype"));
				}
			}, gm.getGame().WAIT_TIME * 20);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getInventory().getName() == SMWGame.CLASS_SEL_INV_NAME) {
			Material item = event.getCurrentItem().getType();
			if((item != Material.BOW &&
					item != Material.DIAMOND_SWORD &&
					item != Material.IRON_HELMET &&
					item != Material.RECORD_12) || 
					event.getSlot() == 0) { if(event.getWhoClicked().isOp() == false) event.setCancelled(true); return; }
			
			SMWClass c = null;
			
			if(item.equals(Material.BOW)) c = SMWClass.Clone;
			else if(item.equals(Material.DIAMOND_SWORD)) c = SMWClass.Jedi;
			else if(item.equals(Material.IRON_HELMET)) c = SMWClass.DarkVador;
			else if(item.equals(Material.RECORD_12)) c = SMWClass.R2D2;
			
			SMWGame.classChange((Player) event.getWhoClicked(), c);

			event.getWhoClicked().closeInventory();
		}
		
		if(gm.getState() == GameState.wait && event.getWhoClicked().isOp() == false) {
			event.setCancelled(true);
		} else event.setCancelled(false);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if(gm.getState() == GameState.wait) {
			if(e.getPlayer().isOp() == false) e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		String msg = "tellraw @a [\"\",{\"text\":\"{player}\",\"color\":\"dark_green\"},{\"text\":\" à chuté dans le vide interstellaire !\",\"color\":\"dark_blue\"}]";
		msg = msg.replace("{player}", e.getEntity().getDisplayName());
		getServer().dispatchCommand(getServer().getConsoleSender(), msg);
		
		SMWGame.PLAYERS_IN_LIFE.remove(e.getEntity());
		SMWGame.DEATH_POS.put(e.getEntity(), e.getEntity().getLocation());
		
		final Plugin pl = this;
		if(SMWGame.PLAYERS_IN_LIFE.size() <= 1) {
			String msg2 = ChatColor.DARK_BLUE + "La partie est terminée !";
			
			getServer().broadcastMessage(msg2);
			getServer().getScheduler().runTaskLater(this, new Runnable() {
				
				@Override
				public void run() {
					SMWGame.endGame(pl, getServer(), false);
				}
			}, 160);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();

		if(SMWGame.PLAYERS_IN_LIFE.contains(p) == false) {
			e.setRespawnLocation(SMWGame.DEATH_POS.get(p));
			
			p.setAllowFlight(true);
			p.setFlying(true);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		if(SMWGame.PLAYERS_IN_LIFE.contains(e.getPlayer()) == false) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerEnterVehicule(VehicleEnterEvent e) {
		if(e.getEntered() instanceof Player && SMWGame.PLAYERS_IN_LIFE.contains(e.getEntered()) == false) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDestroyVehicule(VehicleDestroyEvent e) {
		if(e.getAttacker() instanceof Player && SMWGame.PLAYERS_IN_LIFE.contains(e.getAttacker()) == false) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent e) {
		Acttion action = e.getAction();

		if(action == Action.RIGHT_CLICK_AIR ||
			action == Action.RIGHT_CLICK_BLOCK ||
			action == Action.LEFT_CLICK_AIR ||
			action == Action.LEFT_CLICK_BLOCK) {
			
			if(e.getItem().getTypeId() == 116 && gm.getState() == GameState.wait) {
				SMWGame.openClassSelector(e.getPlayer(), getServer().getScheduler(), this);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getEntityType() == EntityType.PLAYER) {
			if(SMWGame.PLAYERS_IN_LIFE.contains(e.getEntity()) == false) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		e.setQuitMessage("");
		gm.removePlayer(e.getPlayer());
		
		if(gm.getState() == GameState.wait) {
			
			String msg = ChatColor.GREEN + "{player} " + ChatColor.DARK_BLUE + "à quitté la partie ! " + ChatColor.GOLD + "{nb}" +
					ChatColor.DARK_BLUE + "/" + ChatColor.GOLD + "{max} " + ChatColor.DARK_GRAY + "(minimum {min})";
			
			msg = msg.replace("{player}", e.getPlayer().getDisplayName());
			
			String mins = String.valueOf(gm.getGame().MIN_PLAYERS);
			msg = msg.replace("{min}", mins);
			
			String maxs = String.valueOf(gm.getGame().MAX_PLAYERS);
			msg = msg.replace("{max}", maxs);
			
			String nb = String.valueOf(gm.getPlayers().size());
			msg = msg.replace("{nb}", nb);
			
			getServer().broadcastMessage(msg);
		} else {
			SMWGame.PLAYERS_IN_LIFE.remove(e.getPlayer()),
		}
		
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(gm.getState() == GameState.wait) {
			if(e.getPlayer().isOp() == false) e.setCancelled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(gm.getState() == GameState.wait) {	
			if(e.getPlayer().isOp() == false) e.setCancelled(true);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO: a retirer en prod
		if(cmd.getName().equalsIgnoreCase("startgame")) {
			String msg3 = ChatColor.DARK_BLUE + "La partie va commencer dans {time}s";
			msg3 = msg3.replace("{time}", String.valueOf(gm.getGame().WAIT_TIME));
			getServer().broadcastMessage(msg3);
			
			final Plugin pl = this;
			getServer().getScheduler().runTaskLater(this, new Runnable() {
				
				@Override
				public void run() {
					SMWGame.startGame(pl, getServer(), getConfig().getInt("settings.maptype"));
				}
			}, gm.getGame().WAIT_TIME * 20);
			
		} else if(cmd.getName().equalsIgnoreCase("endgame")) {
			SMWGame.endGame(this, getServer());				
		} else if(cmd.getName().equalsIgnoreCase("gamerules")) {
			SMWGame.rules(sender);
		}
		
		return true;
	}
}
