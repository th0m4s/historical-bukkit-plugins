package dev.th0m4s.bukkit.minance.adminwarps;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class MainClass extends JavaPlugin implements Listener{

	public static String INV_NAME = "§3§lMinAnce - AdminWARPS";
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		getLogger().info("[BUKKIT-SIDE] MinAnceADMINWARPS est initalisé !");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("[BUKKIT-SIDE] MinAnceADMINWARPS est désactivé !");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("minanceadminwarps")) {
			if(sender instanceof Player) {
				Player p = (Player) sender;
				if(p.isOp()) {
					if(args.length == 0) {
						int cases = 9;
						boolean has = true;
						
						if(getConfig().contains("wlist") == false) { cases = 9; has = false; }
						else {
							int nbWarps = getConfig().getStringList("wlist").size();
							if(nbWarps <= 9) cases = 9;
							else if(nbWarps <= 18) cases = 18;
							else if(nbWarps <= 27) cases = 27;
							else if(nbWarps <= 36) cases = 36;
							else if(nbWarps <= 54) cases = 54;
							else if(nbWarps <= 63) cases = 63;
						}
						
						Inventory inv = Bukkit.createInventory(null, cases, INV_NAME);
						ItemStack[] items = inv.getContents();
						if(has) {
							List<String> wlist = getConfig().getStringList("wlist");
							int nbWarps = wlist.size();
							for(int i = 0; i < nbWarps; i++) {
								Warp aw = (Warp) getConfig().get("warps." + wlist.get(i));
								ItemStack compass = new ItemStack(345);
								ItemMeta meta = compass.getItemMeta();
								meta.setDisplayName(aw.name);
								List<String> lore = new ArrayList<>();
								lore.add("Monde : " + aw.loc.getWorld().getName());
								lore.add("X : " + aw.loc.getBlockX() + "; Y : " +aw.loc.getBlockY() + "; Z : " + aw.loc.getBlockZ());
								meta.setLore(lore);
								compass.setItemMeta(meta);
								items[i] = compass;
							}
						} else {
							ItemStack empty = new ItemStack(160);
							empty.setDurability((short) 14);
							ItemMeta meta = empty.getItemMeta();
							meta.setDisplayName("§4Aucun warp trouvé");
							empty.setItemMeta(meta);
							items[4] = empty;
						}
						
						inv.setContents(items);
						p.openInventory(inv);
					} else if(args.length == 2) {
						if(args[0].equalsIgnoreCase("create")) {
							if(getConfig().contains("warps") == false) {
								String name = args[1];
								Warp newW = new Warp(p.getLocation(), name);
								getConfig().set("warps." + name, newW);
								List<String > wl = new ArrayList<>();
								wl.add(name);
								getConfig().set("wlist", wl);
								
								saveConfig();
							} else {
								String name = args[1];
								if(Warp.exists(name, getConfig())) {
									p.sendMessage("§4Ce warp existe déjà !");
								} else {
									Warp newW = new Warp(p.getLocation(), name);
									List<String > wl = getConfig().getStringList("wlist");
									wl.add(name);
									getConfig().set("warps." + newW.name, newW);

									saveConfig();
								}
							}
						} else {
							p.sendMessage("§4Mauvais argument(s)");
						}
						
					} else if(args.length == 1) {
						if(args[0].equalsIgnoreCase("reload")) {
							reloadConfig();
							p.sendMessage("§2Configuration rechargée !");
						}
					} else {
						p.sendMessage("§4Mauvais argument(s)");
					}
				}
			} else {
				if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {	
					reloadConfig();
					sender.sendMessage("§2Configuration rechargée !");
				}
			}
		}
		
		return true;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		if(inv.getName() == INV_NAME || inv.getTitle() == INV_NAME) {
			e.setCancelled(true);
			
			if(e.getCurrentItem().getTypeId() == 345) {
				ItemStack clickItem = e.getCurrentItem();
				((CommandSender) e.getWhoClicked()).sendMessage("§6Warp : " + clickItem.getItemMeta().getDisplayName());

				Warp warp = Warp.getWarp(clickItem.getItemMeta().getDisplayName(), getConfig());
				if(warp != null) {
					e.getWhoClicked().teleport(warp.loc);
					((CommandSender) e.getWhoClicked()).sendMessage("§2Téléportation ...");
				} else {
					((CommandSender) e.getWhoClicked()).sendMessage("§4Impossible de lire les données du warp !");
				}
			}
		}
	}
}
