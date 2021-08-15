package dev.th0m4s.bukkit.minance.games.floatpvp;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

public class MinAnceFLOATPVP extends JavaPlugin implements Listener {
	
	GameManager gm;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getServer().dispatchCommand(getServer().getConsoleSender(), "save-off");
		getLogger().info("[BUKKIT-SIDE] MINANCEFLOATPVP est activé !");
		
		gm = ((MinAnceGAMESCORE) getServer().getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI();
		gm.setState(GameState.wait);
	}
	
	@Override
	public void onDisable() {
		getLogger().info("[BUKKIT-SIDE] MINANCEFLOATPVP est désactivé !");
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
		e.getPlayer().sendMessage(ChatColor.AQUA + "Bienvenue au FloatPVP ! Pour connaître les règles, " +
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
					FloatPVPGame.startGame(pl, getServer(), getConfig().getInt("settings.maptype"));
				}
			}, gm.getGame().WAIT_TIME * 20);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getInventory().getName() == FloatPVPGame.CLASS_SEL_INV_NAME) {
			if((event.getCurrentItem().getType() != Material.BOW &&
					event.getCurrentItem().getType() != Material.ROTTEN_FLESH &&
					event.getCurrentItem().getType() != Material.GOLD_BLOCK &&
					event.getCurrentItem().getType() != Material.OBSIDIAN) || 
					event.getSlot() == 0) { if(event.getWhoClicked().isOp() == false) event.setCancelled(true); return; }
			
			Material item = event.getCurrentItem().getType();
			
			FloatPVPClass c = null;
			
			if(item.equals(Material.BOW)) c = FloatPVPClass.Archer;
			else if(item.equals(Material.ROTTEN_FLESH)) c = FloatPVPClass.Soldat;
			else if(item.equals(Material.OBSIDIAN)) c = FloatPVPClass.Tank;
			else if(item.equals(Material.GOLD_BLOCK)) c = FloatPVPClass.VIP;
			
			FloatPVPGame.classChange((Player) event.getWhoClicked(), c);
			
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
		String msg = "tellraw @a [\"\",{\"text\":\"{player}\",\"color\":\"dark_green\"},{\"text\":\" fera le bonheur des archéologues !\",\"color\":\"dark_blue\"}]";
		msg = msg.replace("{player}", e.getEntity().getDisplayName());
		getServer().dispatchCommand(getServer().getConsoleSender(), msg);
		
		FloatPVPGame.PLAYERS_IN_LIFE.remove(e.getEntity());
		
		FloatPVPGame.DEATH_POS.put(e.getEntity(), e.getEntity().getLocation());
		
		final Plugin pl = this;
		
		if(FloatPVPGame.PLAYERS_IN_LIFE.size() == 2) FloatPVPGame.p3 = e.getEntity();
		if(FloatPVPGame.PLAYERS_IN_LIFE.size() == 1) FloatPVPGame.p2 = e.getEntity();
		
		if(FloatPVPGame.PLAYERS_IN_LIFE.size() <= 1) {
			FloatPVPGame.p1 = e.getEntity();
			
			String msg2 = ChatColor.DARK_BLUE + "La partie est terminée !";
			
			getServer().broadcastMessage(msg2);
			getServer().getScheduler().runTaskLater(this, new Runnable() {
				
				@Override
				public void run() {
					FloatPVPGame.endGame(pl, getServer(), false);
				}
			}, 160);
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(e.getTo().getBlock().isLiquid() == false && FloatPVPGame.PLAYERS_IN_LIFE.contains(e.getPlayer())) {
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 999999, 2));
		} else {
			e.getPlayer().removePotionEffect(PotionEffectType.HUNGER);
		}
		
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if(FloatPVPGame.PLAYERS_IN_LIFE.contains(e.getPlayer()) == false) {
			e.setRespawnLocation(FloatPVPGame.DEATH_POS.get(e.getPlayer()));
			
			e.getPlayer().setAllowFlight(true);
			e.getPlayer().setFlying(true);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		if(FloatPVPGame.PLAYERS_IN_LIFE.contains(e.getPlayer()) == false) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerEnterVehicule(VehicleEnterEvent e) {
		if(e.getEntered() instanceof Player && FloatPVPGame.PLAYERS_IN_LIFE.contains(e.getEntered()) == false) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDestroyVehicule(VehicleDestroyEvent e) {
		if(e.getAttacker() instanceof Player && FloatPVPGame.PLAYERS_IN_LIFE.contains(e.getAttacker()) == false) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if(e.getEntityType() == EntityType.PLAYER) {
			if(FloatPVPGame.PLAYERS_IN_LIFE.contains(e.getEntity()) == false) {
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
			FloatPVPGame.PLAYERS_IN_LIFE.remove(e.getPlayer());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(e.getBlock().getType().getId() == 111) {
			FloatPVPGame.getLilypad(e.getPlayer(), true);
		}
		
		if(gm.getState() == GameState.wait) {
			if(e.getPlayer().isOp() == false) e.setCancelled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(e.getBlock().getType().getId() == 116 && gm.getState() == GameState.wait) {
			FloatPVPGame.openClassSelector(e.getPlayer(), e.getBlock(), getServer().getScheduler(), this);
		}
		
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
					FloatPVPGame.startGame(pl, getServer(), getConfig().getInt("settings.maptype"));
				}
			}, gm.getGame().WAIT_TIME * 20);
			
		} else if(cmd.getName().equalsIgnoreCase("endgame")) {
			FloatPVPGame.endGame(this, getServer());				
		} else if(cmd.getName().equalsIgnoreCase("gamerules")) {
			FloatPVPGame.rules(sender);
		}
		
		return true;
	}

}
