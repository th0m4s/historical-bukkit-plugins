package dev.th0m4s.bukkit.minance.games.smw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import dev.th0m4s.bukkit.minance.core.MinAnceCORE;
import dev.th0m4s.bukkit.minance.games.core.MinAnceGAMESCORE;
import dev.th0m4s.bukkit.minance.games.core.api.GameManager;
import dev.th0m4s.bukkit.minance.games.core.enums.GameState;

public class SMWGame {

	public static final int MAX_PLAYERS = 24;
	public static final int MIN_PLAYERS = 16;
	
	public static final boolean WITH_TEAMS = true;
	
	public static final int MAX_TIME = 900;
	
	public static final int WAIT_TIME = 20;
	
	public static final List<BukkitTask> TASKS = new ArrayList<BukkitTask>();
	
	public static Map<String, SMWClass> CLASSES = new HashMap<String, SMWClass>();
	
	public static List<Player> PLAYERS_IN_LIFE = new ArrayList<Player>();
	
	public static Map<Player, Location> DEATH_POS = new HashMap<Player, Location>();
	
	public static final String CLASS_SEL_INV_NAME = "Sélection de classe";
	
	public static int MECHANTS = 0;
	public static int GENTILS = 0;
	
	@SuppressWarnings("serial")
	public static final Map<SMWClass ,String> CLASSES_NAMES = new HashMap<SMWClass,String>(){{
	    put(SMWClass.Clone, ChatColor.DARK_GRAY + "Clone");
	    put(SMWClass.DarkVador, ChatColor.DARK_GRAY + "DarkVador");
	    put(SMWClass.Jedi, ChatColor.BLUE + "Jedi");
	    put(SMWClass.R2D2, ChatColor.YELLOW + "R2D2");
	}};
	
	@SuppressWarnings({ "serial", "deprecation" })
	public static final Map<SMWClass, ItemStack[]> CLASSES_ITEMS = new HashMap<SMWClass,ItemStack[]>(){{
	    
		ItemStack E_FER_SHARP2 = new ItemStack(Material.IRON_SWORD);
	    E_FER_SHARP2.addEnchantment(Enchantment.getById(16), 2);
	    ItemStack P_MAILLES_PRO2 = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
	    P_MAILLES_PRO2.addEnchantment(Enchantment.getById(0), 2);
		put(SMWClass.Clone, new ItemStack[] {E_FER_SHARP2, P_MAILLES_PRO2});
		
		ItemStack A_PUNCH2 = new ItemStack(Material.BOW);
		A_PUNCH2.addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
		ItemStack P_MAILLES_PRO1 = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
	    P_MAILLES_PRO1.addEnchantment(Enchantment.getById(0), 2);
	    put(SMWClass.Jedi, new ItemStack[] {new ItemStack(Material.IRON_SWORD), A_PUNCH2, P_MAILLES_PRO2, new ItemStack(Material.ARROW, 30)});
	    
	    ItemStack E_DIAM_SHARP2 = new ItemStack(Material.DIAMOND_SWORD);
	    E_DIAM_SHARP2.addEnchantment(Enchantment.getById(16), 2);
	    put(SMWClass.DarkVador, new ItemStack[] {E_DIAM_SHARP2, new ItemStack(Material.IRON_HELMET, 1),
	    														new ItemStack(Material.IRON_CHESTPLATE, 1),
	    														new ItemStack(Material.IRON_LEGGINGS, 1),
	    														new ItemStack(Material.IRON_BOOTS, 1)});
	    
	    ItemStack E_DIAM_SHARP3 = new ItemStack(Material.DIAMOND_SWORD);
	    E_DIAM_SHARP3.addEnchantment(Enchantment.getById(16), 3);
	    ItemStack H_GOLD_PRO3 = new ItemStack(Material.GOLD_HELMET);
	    H_GOLD_PRO3.addEnchantment(Enchantment.getById(0), 3);
	    ItemStack C_GOLD_PRO3 = new ItemStack(Material.GOLD_CHESTPLATE);
	    C_GOLD_PRO3.addEnchantment(Enchantment.getById(0), 3);
	    ItemStack L_GOLD_PRO3 = new ItemStack(Material.GOLD_LEGGINGS);
	    L_GOLD_PRO3.addEnchantment(Enchantment.getById(0), 3);
	    ItemStack B_GOLD_PRO3 = new ItemStack(Material.GOLD_BOOTS);
	    B_GOLD_PRO3.addEnchantment(Enchantment.getById(0), 3);
	    put(SMWClass.R2D2, new ItemStack[] {E_DIAM_SHARP3, H_GOLD_PRO3, C_GOLD_PRO3, L_GOLD_PRO3, B_GOLD_PRO3, new ItemStack(Material.BOW, 1), new ItemStack(Material.ARROW, 32)});
	
	}};
	
	@SuppressWarnings("serial")
	public static final List<ItemStack> DEFAULT_ITEMS = new ArrayList<ItemStack>(){{
		
		ItemStack H_FER_RESP2 = new ItemStack(Material.IRON_HELMET);
		H_FER_RESP2.addEnchantment(Enchantment.OXYGEN, 2);
		add(H_FER_RESP2);
		
	}};
	
	
	
	@SuppressWarnings("deprecation")
	public static void startGame(final Plugin plugin, final Server server, final int map) {
		
		
		GameManager gm = ((MinAnceGAMESCORE) server.getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI();
		gm.setState(GameState.game);
		
		plugin.getLogger().info("StartGame");
		
		Location spawnLoc = null;
		
		/*switch(map) {
		
		case 1: 
			plugin.getLogger().info("incase 1");
			spawnLoc = new Location(server.getWorld("game"), 14, 63, 131);
			break;
		
		}*/
		
		server.getWorld("game").setPVP(false);
		
		List<Player> pls = ((MinAnceGAMESCORE) server.getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI().getPlayers();
		
		for(int i = 0; i < pls.size(); i++) {
			
			pls.get(i).teleport(spawnLoc);
			for(int y = 0; y < DEFAULT_ITEMS.size(); y++) pls.get(i).getInventory().addItem(DEFAULT_ITEMS.get(y));
			pls.get(i).updateInventory();
			pls.get(i).getInventory().setContents(CLASSES_ITEMS.get(getClass(pls.get(i).getName())));
			pls.get(i).updateInventory();
			PLAYERS_IN_LIFE.add(pls.get(i));
			
		}
		
		
		server.getWorld("game").setDifficulty(Difficulty.NORMAL);
		
		
	}
	
	
	public static void endGame(Plugin plugin, Server server) {
		
		endGame(plugin, server, true);
		
	}
	
	public static void endGame(final Plugin plugin, final Server server, final boolean broadcast) {

		server.getScheduler().runTaskLater(plugin, new Runnable() {
			
			@Override
			public void run() {
				GameManager gm = ((MinAnceGAMESCORE) server.getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI();
				gm.setState(GameState.finish);
				
				for(int i = 0; i < TASKS.size(); i++) {
					
					TASKS.get(i).cancel();
					
				}
				
				String msg = ChatColor.DARK_BLUE + "La partie est terminée !";
				
				if(broadcast)server.broadcastMessage(msg);
			
				List<Player> pls = ((MinAnceGAMESCORE) server.getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI().getPlayers();
				
				for(int i = 0; i < pls.size(); i++) {
					
					((MinAnceCORE) server.getPluginManager().getPlugin("MinAnceCORE")).getAPI().changeServer(pls.get(i), "main_lobby");
					((MinAnceGAMESCORE) server.getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI().removePlayer(pls.get(i));
					
				}
			
				//plugin.getLogger().info("Unload world 'game' - result : " + Bukkit.unloadWorld("game", false));
				
				server.dispatchCommand(server.getConsoleSender(), "mvdelete game");
				server.dispatchCommand(server.getConsoleSender(), "mvconfirm");
				
				/*delete(new File(server.getWorldContainer(), "game"));*/
				try {
					copyDir(new File(plugin.getDataFolder(), "bak_game"), new File(server.getWorldContainer(), "game"));
				} catch (IOException e) {
					server.broadcastMessage("ERREUR DE COPIE DU MONDE DE JEU ! SHUTDOWN !!!");
					e.printStackTrace();
					server.shutdown();
				}
				
				server.dispatchCommand(server.getConsoleSender(), "mvimport game normal");
				
				gm.setState(GameState.wait);
				
			}
		}, 200);
		
	}
	
	/*private static boolean delete(File file) {
        if (file.isDirectory()) {
            File[] arrfile = file.listFiles();
            int n = arrfile.length;
            int n2 = 0;
            while (n2 < n) {
                File subfile = arrfile[n2];
                if (!delete(subfile)) {
                    return false;
                }
                ++n2;
            }
        }
        if (!file.delete()) {
            return false;
        }
        return true;
    }*/

	
	static void copyDir(File source, File target) throws IOException {
        if (source.isDirectory()) {
            /*String[] files;*/
            if (!target.exists()) {
                target.mkdir();
            }
            String[] arrstring = source.list();
            int n = arrstring.length;
            int n2 = 0;
            while (n2 < n) {
                String file = arrstring[n2];
                File srcFile = new File(source, file);
                File destFile = new File(target, file);
                copyDir(srcFile, destFile);
                ++n2;
            }
        } else {
            int length;
            FileInputStream in = new FileInputStream(source);
            FileOutputStream out = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

	public static void openClassSelector(Player player, BukkitScheduler sc, Plugin plugin) {
		
		Inventory classSelectorInv = Bukkit.createInventory(null, 9, CLASS_SEL_INV_NAME);
		
		classSelectorInv.setItem(0, itemStackFromClass(getClass(player.getName()), true));
		classSelectorInv.setItem(3, itemStackFromClass(SMWClass.Clone));
		classSelectorInv.setItem(4, itemStackFromClass(SMWClass.Jedi));
		classSelectorInv.setItem(5, itemStackFromClass(SMWClass.R2D2));
		classSelectorInv.setItem(6, itemStackFromClass(SMWClass.DarkVador));
		
		player.openInventory(classSelectorInv);
		
		
	}
	
	public static void classChange(Player p, SMWClass classe) {
		
		SMWClass acualClass = getClass(p.getName());
		
		boolean canChange = false;
		
		if(acualClass == SMWClass.R2D2 || acualClass == SMWClass.Jedi) {
			
			GENTILS--;
			if(GENTILS >= MECHANTS) canChange = true;
			
		} else {
			
			MECHANTS--;
			if(MECHANTS >= GENTILS) canChange = true;
		}
		
		int res = Math.max(MECHANTS, GENTILS) - Math.min(MECHANTS, GENTILS);

		if(MECHANTS != GENTILS && res == 1 && canChange) {
		
			if((classe == SMWClass.R2D2 || classe == SMWClass.DarkVador) && !checkPEXPerm(p, "minance.games.classes.vip")) {
				
				p.sendMessage(ChatColor.RED + "Tu n'es pas " + ChatColor.GOLD + "[VIP]" + ChatColor.RED + " ! Achètes ce grade sur http://shop.minance.fr/ !");
				return;
				
			}
			
			p.sendMessage(ChatColor.AQUA + "Tu es maintenant " + getClassName(classe));
			
			if(classe == SMWClass.R2D2 || classe == SMWClass.Jedi) {
				
				p.sendMessage(ChatColor.AQUA + "Tu passes du bon côté de la force");
				GENTILS++;
				
			} else {
				
				p.sendMessage(ChatColor.DARK_BLUE + "Tu passes du mauvais côté de la force (tu peux y rester)");
				MECHANTS++;
			}
			
			setClass(p.getName(), classe);
			
		} else {
			
			p.sendMessage("§cTu ne peux pas changer de côté de la force");
			
		}
		
	}
	
	public static String getClassName(SMWClass c) {
		
		return CLASSES_NAMES.get(c);
		
	}
	
	public static SMWClass getClass(String player) {
		
		if(CLASSES.containsKey(player) == false) defaultTeam(player);
		
		return CLASSES.get(player);
		
	}
	
	public static void setClass(String p, SMWClass classe) {
		
		if(CLASSES.containsKey(p)) {
			
			CLASSES.remove(p);
			
		}
		
		CLASSES.put(p, classe);
		
	}
	
	public static Material materialFromClass(SMWClass c) {
		
		if(c.equals(SMWClass.Clone)) return Material.BOW;
		else if(c.equals(SMWClass.Jedi)) return Material.DIAMOND_SWORD;
		else if(c.equals(SMWClass.R2D2)) return Material.RECORD_12;
		else if(c.equals(SMWClass.DarkVador)) return Material.IRON_HELMET;
		
		return null;
		
	}
	
	public static void defaultTeam(String p) {
		
		if(GENTILS == MECHANTS) { setClass(p, SMWClass.Jedi);
		
			GENTILS++;
		
		}
		else {
			
			if(GENTILS > MECHANTS) { setClass(p, SMWClass.Clone); MECHANTS++; }
			else { setClass(p, SMWClass.Jedi); GENTILS++;}
			
		}
		
	}
	
	public static ItemStack itemStackFromClass(SMWClass c, boolean actual) {
		
		ItemStack i = new ItemStack(materialFromClass(c), 1);
		ItemMeta im = i.getItemMeta();
		if(actual) im.setDisplayName("Classe actuelle : " + getClassName(c));
		else im.setDisplayName(getClassName(c));
		
		ArrayList<String> lore = new ArrayList<String>();
		
		lore.add(ChatColor.WHITE + "Cette classe contient : ");
		
		if(c.equals(SMWClass.Clone)) {
			
			lore.add(ChatColor.WHITE + " - Epée en fer, tranchant (sharpness) 2");
			lore.add(ChatColor.WHITE + " - Plastron en mailles, protection 2");
			
		}
		else if(c.equals(SMWClass.Jedi)) {
			
			lore.add(ChatColor.WHITE + " - Arc, frappe (punch) 2");
			lore.add(ChatColor.WHITE + " - 30 flèches");
			lore.add(ChatColor.WHITE + " - Plastron en mailles, protection 2");
			
			
		}
		else if(c.equals(SMWClass.R2D2)) {
			
			lore.add(ChatColor.WHITE + " - Epée en diamant, tranchant (sharpness) 2");
			lore.add(ChatColor.WHITE + " - Armure en fer complète");
			lore.add(" ");
			lore.add(ChatColor.AQUA + "Pour obtenir cette classe, achetez le"); 
			lore.add(ChatColor.AQUA + "grade " + ChatColor.GOLD + "[VIP] " + ChatColor.AQUA + "sur http://shop.minance.fr/");
			
			
		}
		else if(c.equals(SMWClass.DarkVador)) {
			
			lore.add(ChatColor.WHITE + " - Epée en diamant, tranchant (sharpness) 3");
			lore.add(ChatColor.WHITE + " - Armure en or, protection 2");
			lore.add(ChatColor.WHITE + " - Arc et 32 flèches");
			lore.add(" ");
			lore.add(ChatColor.AQUA + "Pour obtenir cette classe, achetez le"); 
			lore.add(ChatColor.AQUA + "grade " + ChatColor.GOLD + "[VIP] " + ChatColor.AQUA + "sur http://shop.minance.fr/");
			
		}
		
		if(!actual) im.setLore(lore);
		
		i.setItemMeta(im);
		
		return i;
		
	}
	
	public static ItemStack itemStackFromClass(SMWClass c) {
		
		return itemStackFromClass(c, false);
		
	}
	
	public static boolean checkPEXPerm(Player player, String perm) {
		
		if(player.isOp()) return true;
		
		PermissionUser u = PermissionsEx.getUser(player);
		
		return u.has(perm);
		
	}
	
	public static void rules(CommandSender s) {
		
		s.sendMessage(ChatColor.GREEN + "=====================================================");
		s.sendMessage(ChatColor.AQUA + "Bienvenue au StarMineWars, voici les règles :");
		s.sendMessage(ChatColor.AQUA + "Entre 16 et 24 joueurs spawnent sur la carte choisie. Deux équipes s'affrontent dans l'espace et leurs vaisseaux " +
				"spatiaux." +
				"L'équipe ayant éliminé tous les joueurs adverses gagne. Les joueurs ne respawnent que si le beacon de leur vaiseau mère est présent. Choisi bien ta classe en posant la table d'enchantement !");
		s.sendMessage(ChatColor.GREEN + "=====================================================");
		
	}
	
}
