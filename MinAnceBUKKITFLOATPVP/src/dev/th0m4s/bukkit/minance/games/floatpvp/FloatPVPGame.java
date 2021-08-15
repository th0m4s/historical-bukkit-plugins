package dev.th0m4s.bukkit.minance.games.floatpvp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Location;
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
import org.bukkit.Material;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import dev.th0m4s.bukkit.minance.core.MinAnceCORE;
import dev.th0m4s.bukkit.minance.games.core.MinAnceGAMESCORE;
import dev.th0m4s.bukkit.minance.games.core.api.GameManager;
import dev.th0m4s.bukkit.minance.games.core.enums.GameState;

public class FloatPVPGame {

	public static final int MAX_PLAYERS = 18;
	public static final int MIN_PLAYERS = 14;

	public static final boolean WITH_TEAMS = false;

	public static final int MAX_TIME = 900;

	public static final int WAIT_TIME = 20;

	public static final List<BukkitTask> TASKS = new ArrayList<BukkitTask>();

	public static Map<String, FloatPVPClass> CLASSES = new HashMap<String, FloatPVPClass>();

	public static List<Player> PLAYERS_IN_LIFE = new ArrayList<Player>();

	public static Map<Player, Location> DEATH_POS = new HashMap<Player, Location>();

	public static Player p1;
	public static Player p2;
	public static Player p3;

	public static final String CLASS_SEL_INV_NAME = "Sélection de classe";

	@SuppressWarnings("serial")
	public static final Map<FloatPVPClass, String> CLASSES_NAMES = new HashMap<FloatPVPClass, String>() {
		{
			put(FloatPVPClass.Archer, ChatColor.DARK_GRAY + "Archer");
			put(FloatPVPClass.Soldat, ChatColor.DARK_GREEN + "Soldat");
			put(FloatPVPClass.Tank, ChatColor.DARK_PURPLE + "Tank");
			put(FloatPVPClass.VIP, ChatColor.GOLD + "Classe VIP");
		}
	};

	@SuppressWarnings({ "serial", "deprecation" })
	public static final Map<FloatPVPClass, ItemStack[]> CLASSES_ITEMS = new HashMap<FloatPVPClass, ItemStack[]>() {
		{

			ItemStack E_FER_SHARP2 = new ItemStack(Material.IRON_SWORD);
			E_FER_SHARP2.addEnchantment(Enchantment.getById(16), 2);
			ItemStack P_MAILLES_PRO2 = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
			P_MAILLES_PRO2.addEnchantment(Enchantment.getById(0), 2);
			put(FloatPVPClass.Soldat, new ItemStack[] { E_FER_SHARP2, P_MAILLES_PRO2 });

			ItemStack A_PUNCH2 = new ItemStack(Material.BOW);
			A_PUNCH2.addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
			ItemStack P_MAILLES_PRO1 = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
			P_MAILLES_PRO1.addEnchantment(Enchantment.getById(0), 2);
			put(FloatPVPClass.Archer, new ItemStack[] { new ItemStack(Material.IRON_SWORD), A_PUNCH2, P_MAILLES_PRO2,
					new ItemStack(Material.ARROW, 30) });

			ItemStack E_DIAM_SHARP2 = new ItemStack(Material.DIAMOND_SWORD);
			E_DIAM_SHARP2.addEnchantment(Enchantment.getById(16), 2);
			put(FloatPVPClass.Tank,
					new ItemStack[] { E_DIAM_SHARP2, new ItemStack(Material.IRON_HELMET, 1),
							new ItemStack(Material.IRON_CHESTPLATE, 1), new ItemStack(Material.IRON_LEGGINGS, 1),
							new ItemStack(Material.IRON_BOOTS, 1) });

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
			put(FloatPVPClass.VIP, new ItemStack[] { E_DIAM_SHARP3, H_GOLD_PRO3, C_GOLD_PRO3, L_GOLD_PRO3, B_GOLD_PRO3,
					new ItemStack(Material.BOW, 1), new ItemStack(Material.ARROW, 32) });

		}
	};

	@SuppressWarnings("serial")
	public static final List<ItemStack> DEFAULT_ITEMS = new ArrayList<ItemStack>() {
		{
			ItemStack H_FER_RESP2 = new ItemStack(Material.IRON_HELMET);
			H_FER_RESP2.addEnchantment(Enchantment.OXYGEN, 2);
			add(H_FER_RESP2);
		}
	};

	@SuppressWarnings("deprecation")
	public static void startGame(final Plugin plugin, final Server server, final int map) {
		GameManager gm = ((MinAnceGAMESCORE) server.getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI();
		gm.setState(GameState.game);

		plugin.getLogger().info("StartGame");

		Location spawnLoc = null;

		switch (map) {
			case 1:
				plugin.getLogger().info("incase 1");
				spawnLoc = new Location(server.getWorld("game"), 14, 63, 131);
				break;
		}

		server.getWorld("game").setPVP(false);

		List<Player> pls = ((MinAnceGAMESCORE) server.getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI()
				.getPlayers();

		for (int i = 0; i < pls.size(); i++) {
			pls.get(i).teleport(spawnLoc);
			for (int y = 0; y < DEFAULT_ITEMS.size(); y++)
				pls.get(i).getInventory().addItem(DEFAULT_ITEMS.get(y));
			pls.get(i).updateInventory();
			pls.get(i).getInventory().setContents(FloatPVPGame.CLASSES_ITEMS.get(getClass(pls.get(i).getName())));
			pls.get(i).updateInventory();
			PLAYERS_IN_LIFE.add(pls.get(i));
		}

		/* Start repeating tasks */

		BukkitScheduler scheduler = server.getScheduler();

		server.getWorld("game").setDifficulty(Difficulty.PEACEFUL);
		scheduler.runTaskLater(plugin, new Runnable() {

			@Override
			public void run() {
				String startMsg = ChatColor.GOLD + "Go!" + ChatColor.DARK_BLUE + " Activation du " + ChatColor.DARK_RED
						+ "PvP" + ChatColor.DARK_BLUE + " et des " + ChatColor.DARK_GREEN + "mobs" + ChatColor.DARK_BLUE
						+ " dans 20s, préparez vous !";

				server.broadcastMessage(startMsg);
			}
		}, 100);

		scheduler.runTaskLater(plugin, new Runnable() {

			@Override
			public void run() {
				String pvpMsg = ChatColor.GREEN + "Activation du " + ChatColor.DARK_RED + "PvP " + ChatColor.GREEN
						+ " et des " + ChatColor.DARK_GREEN + "mobs" + ChatColor.GREEN + " !";

				server.broadcastMessage(pvpMsg);

				server.getWorld("game").setPVP(true);
				server.getWorld("game").setDifficulty(Difficulty.NORMAL);
			}
		}, 25 * 20);

		/* Summon boat */
		TASKS.add(scheduler.runTaskTimer(plugin, new Runnable() {

			@Override
			public void run() {
				Location locSummonBoat = null;

				switch (map) {
					case 1:
						locSummonBoat = new Location(server.getWorld("game"), -2, 62, 151);
						break;
				}

				server.getWorld("game").spawnEntity(locSummonBoat, EntityType.BOAT);
			}

		}, 100, 200));
	}

	@SuppressWarnings("deprecation")
	public static void getLilypad(Player p, boolean random) {

		if (random) {
			int randomNum = 1 + (int) (Math.random() * 50);

			switch (randomNum) {
				case 1:
				case 4:
				case 30:
				case 15:
				case 6:
				case 26:
				case 18:
				case 22:
				case 9:
					random = false;
			}
		}

		if (!random) {
			int randomNum2 = 1 + (int) (Math.random() * 3);

			switch (randomNum2) {
				case 1:
					p.getInventory().addItem(new ItemStack(315, 1));
					p.getInventory().addItem(new ItemStack(364, 16));
					break;

				case 2:
					p.getInventory().addItem(new ItemStack(322, 2));
					break;

				case 3:
					p.getInventory().addItem(new ItemStack(262, 32));
					ItemStack encharc = new ItemStack(261, 1);
					encharc.addEnchantment(Enchantment.ARROW_FIRE, 1);
					p.getInventory().addItem(encharc);
					break;
			}
		}
	}

	public static void endGame(Plugin plugin, Server server) {
		endGame(plugin, server, true);
	}

	public static void endGame(final Plugin plugin, final Server server, final boolean broadcast) {
		plugin.getServer().broadcastMessage(ChatColor.GREEN + "=====================================================");
		plugin.getServer().broadcastMessage("  ");

		int pieces1 = 60;
		String n1 = p1.getName();
		plugin.getServer().broadcastMessage(
				ChatColor.GOLD + "   1. " + ChatColor.RESET + n1
						+ ChatColor.GOLD + "  + " + pieces1 + " pièces.");
		pieces1-=15;
		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "sync console main_lobby money give "
				+ n1 + " " + pieces1 + " GameCoin");

		
		int pieces2 = 45;
		String n2 = p2.getName();
		plugin.getServer().broadcastMessage(
				ChatColor.GOLD + "   2. " + ChatColor.DARK_GRAY + n2
						+ ChatColor.GOLD + "  + " + pieces2 + " pièces.");
		pieces2-=15;
		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "sync console main_lobby money give "
				+ n2 + " " + pieces2 + " GameCoin");

		int pieces3 = 30;
		String n3 = p3.getName();
		plugin.getServer().broadcastMessage(
				ChatColor.YELLOW + "   3. " + ChatColor.RESET + n3
						+ ChatColor.GOLD + "  + " + (pieces3+10) + " pièces.");
		pieces3-=15;
		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "sync console main_lobby money give "
				+ n3 + " " + pieces3 + " GameCoin");
		
		plugin.getServer().broadcastMessage("  ");
		plugin.getServer().broadcastMessage(ChatColor.AQUA + "Bravo !");
		plugin.getServer().broadcastMessage(ChatColor.GOLD + "Tous les joueurs gagnent 15 pièces !");
		plugin.getServer().broadcastMessage("  ");
		plugin.getServer().broadcastMessage(ChatColor.GREEN + "=====================================================");

		server.getScheduler().runTaskLater(plugin, new Runnable() {

			@Override
			public void run() {
				GameManager gm = ((MinAnceGAMESCORE) server.getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI();
				gm.setState(GameState.finish);

				for (int i = 0; i < TASKS.size(); i++) {
					TASKS.get(i).cancel();
				}

				String msg = ChatColor.DARK_BLUE + "La partie est terminée !";

				if (broadcast)
					server.broadcastMessage(msg);

				List<Player> pls = ((MinAnceGAMESCORE) server.getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI()
						.getPlayers();

				for (int i = 0; i < pls.size(); i++) {

					plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "sync console main_lobby money give "
							+ pls.get(i).getName() + " 15 GameCoin");
					((MinAnceCORE) server.getPluginManager().getPlugin("MinAnceCORE")).getAPI().changeServer(pls.get(i),
							"main_lobby");
					((MinAnceGAMESCORE) server.getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI()
							.removePlayer(pls.get(i));
				}

				server.dispatchCommand(server.getConsoleSender(), "mvdelete game");
				server.dispatchCommand(server.getConsoleSender(), "mvconfirm");

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

	static void copyDir(File source, File target) throws IOException {
		if (source.isDirectory()) {
			/* String[] files; */
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

	public static void openClassSelector(Player player, final Block bloc, BukkitScheduler sc, Plugin plugin) {
		Inventory classSelectorInv = Bukkit.createInventory(null, 9, CLASS_SEL_INV_NAME);

		classSelectorInv.setItem(0, itemStackFromClass(getClass(player.getName()), true));
		classSelectorInv.setItem(3, itemStackFromClass(FloatPVPClass.Soldat));
		classSelectorInv.setItem(4, itemStackFromClass(FloatPVPClass.Archer));
		classSelectorInv.setItem(5, itemStackFromClass(FloatPVPClass.Tank));
		classSelectorInv.setItem(6, itemStackFromClass(FloatPVPClass.VIP));

		player.openInventory(classSelectorInv);
	}

	public static void classChange(Player p, FloatPVPClass classe) {
		if (classe == FloatPVPClass.VIP && !checkPEXPerm(p, "minance.games.classes.vip")) {

			p.sendMessage(ChatColor.RED + "Tu n'es pas " + ChatColor.GOLD + "[VIP]" + ChatColor.RED
					+ " ! Achètes ce grade sur http://shop.minance.fr/ !");
			return;

		}

		setClass(p.getName(), classe);
		p.sendMessage(ChatColor.AQUA + "Tu es maintenant " + FloatPVPGame.getClassName(classe));
	}

	public static String getClassName(FloatPVPClass c) {
		return CLASSES_NAMES.get(c);
	}

	public static FloatPVPClass getClass(String player) {
		if (CLASSES.containsKey(player) == false)
			CLASSES.put(player, FloatPVPClass.Soldat);

		return CLASSES.get(player);
	}

	public static void setClass(String p, FloatPVPClass classe) {
		if (CLASSES.containsKey(p)) {
			CLASSES.remove(p);
		}

		CLASSES.put(p, classe);
	}

	public static Material materialFromClass(FloatPVPClass c) {
		if (c.equals(FloatPVPClass.Archer))
			return Material.BOW;
		else if (c.equals(FloatPVPClass.Soldat))
			return Material.ROTTEN_FLESH;
		else if (c.equals(FloatPVPClass.Tank))
			return Material.OBSIDIAN;
		else if (c.equals(FloatPVPClass.VIP))
			return Material.GOLD_BLOCK;

		return null;
	}

	public static ItemStack itemStackFromClass(FloatPVPClass c, boolean actual) {
		ItemStack i = new ItemStack(materialFromClass(c), 1);
		ItemMeta im = i.getItemMeta();
		if (actual)
			im.setDisplayName("Classe actuelle : " + getClassName(c));
		else
			im.setDisplayName(getClassName(c));

		ArrayList<String> lore = new ArrayList<String>();

		lore.add(ChatColor.WHITE + "Cette classe contient : ");

		if (c.equals(FloatPVPClass.Archer)) {

			lore.add(ChatColor.WHITE + " - Epée en fer, tranchant (sharpness) 2");
			lore.add(ChatColor.WHITE + " - Plastron en mailles, protection 2");

		} else if (c.equals(FloatPVPClass.Soldat)) {

			lore.add(ChatColor.WHITE + " - Arc, frappe (punch) 2");
			lore.add(ChatColor.WHITE + " - 30 flèches");
			lore.add(ChatColor.WHITE + " - Plastron en mailles, protection 2");

		} else if (c.equals(FloatPVPClass.Tank)) {

			lore.add(ChatColor.WHITE + " - Epée en diamant, tranchant (sharpness) 2");
			lore.add(ChatColor.WHITE + " - Armure en fer complète");

		} else if (c.equals(FloatPVPClass.VIP)) {

			lore.add(ChatColor.WHITE + " - Epée en diamant, tranchant (sharpness) 3");
			lore.add(ChatColor.WHITE + " - Armure en or, protection 2");
			lore.add(ChatColor.WHITE + " - Arc et 32 flèches");
			lore.add(" ");
			lore.add(ChatColor.AQUA + "Pour obtenir cette classe, achetez le");
			lore.add(ChatColor.AQUA + "grade " + ChatColor.GOLD + "[VIP] " + ChatColor.AQUA
					+ "sur http://shop.minance.fr/");
		}

		if (!actual)
			im.setLore(lore);

		i.setItemMeta(im);

		return i;
	}

	public static ItemStack itemStackFromClass(FloatPVPClass c) {
		return itemStackFromClass(c, false);
	}

	public static boolean checkPEXPerm(Player player, String perm) {
		if (player.isOp())
			return true;

		PermissionUser u = PermissionsEx.getUser(player);

		return u.has(perm);
	}

	public static void rules(CommandSender s) {
		s.sendMessage(ChatColor.GREEN + "=====================================================");
		s.sendMessage(ChatColor.AQUA + "Bienvenue au FloatPVP, voici les règles :");
		s.sendMessage(ChatColor.AQUA
				+ "Entre 14 et 18 joueurs spawnent sur la carte choisie. Il n'y a pas d'équipes, le jeu est individuel."
				+ "Mais pourquoi FloatPVP ? Car en dehors de l'eau, tu prends un effet de faim II (Hunger II)."
				+ "Le dernier joueur encore vie gagne ! Choisi bien ta classe en posant la table d'enchantement ! Petite note : "
				+ "Les potions Hunger ne servent pas !");
		s.sendMessage(ChatColor.GREEN + "=====================================================");
	}

}
