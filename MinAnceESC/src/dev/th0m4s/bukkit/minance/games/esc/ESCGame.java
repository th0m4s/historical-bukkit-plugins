package dev.th0m4s.bukkit.minance.games.esc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import dev.th0m4s.bukkit.minance.core.MinAnceCORE;
import dev.th0m4s.bukkit.minance.games.core.MinAnceGAMESCORE;
import dev.th0m4s.bukkit.minance.games.core.api.GameManager;
import dev.th0m4s.bukkit.minance.games.core.enums.GameState;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ESCGame {

	public static final int MAX_PLAYERS = 12;
	public static final int MIN_PLAYERS = 6;
	
	public static final boolean WITH_TEAMS = false;
	
	public static final int MAX_TIME = 0;
	
	public static final int WAIT_TIME = 15;
	
	public static final List<BukkitTask> TASKS = new ArrayList<BukkitTask>();
	
	public static int[] MAPS;
	
	public static Location[] SPAWN_LOCS = new Location[5];
	
	public static boolean[] DOUBLE_JUMP = new boolean[5];
	
	public static int actualMap = 0;
	
	public static Player[] mapWinners = new Player[3];
	
	public static int mapWinnersNb = 0;
	
	public static Map<String, Integer> POINTS = new HashMap<String, Integer>();
	
	public static Plugin PL;
	
	
	public static void startGame(final Plugin plugin, final Server server, final int map) {
		PL = plugin;

		if(plugin.getConfig().contains("number") == false) {
			server.broadcastMessage("§cImpossible de lancer le jeu. Les labyrinthes sont introuvables sur le serveur ...");
			return;
		}
		
	    final Random random = new Random();
	    final Set<Integer> intSet = new HashSet<>();
	    while (intSet.size() < 5) {
	        intSet.add(random.nextInt(plugin.getConfig().getInt("number")) + 1);
	    }

	    final int[] ints = new int[intSet.size()];
	    final Iterator<Integer> iter = intSet.iterator();
	    for (int i = 0; iter.hasNext(); ++i) {
	        ints[i] = iter.next();
	    }
	    
	    plugin.getLogger().info("Maps : " + Arrays.toString(ints));
	    MAPS = ints;
	    
	    for(int i = 0; i < 5; i++) {
	    	SPAWN_LOCS[i] = new Location(server.getWorld("game"), plugin.getConfig().getInt("maps.map" + MAPS[i] + ".start.x"), plugin.getConfig().getInt("maps.map" + MAPS[i] + ".start.y"), plugin.getConfig().getInt("maps.map" + MAPS[i] + ".start.z"));
	    	DOUBLE_JUMP[i] = plugin.getConfig().getBoolean("maps.map" + MAPS[i] + "doublejump");
	    }
	    
		GameManager gm = ((MinAnceGAMESCORE) server.getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI();
		gm.setState(GameState.game);
		
		plugin.getLogger().info("StartGame");
		server.getWorld("game").setPVP(false);
		
		List<Player> pls = ((MinAnceGAMESCORE) server.getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI().getPlayers();
		for(int i = 0; i < pls.size(); i++) {
			
			pls.get(i).teleport(SPAWN_LOCS[0]);
			if(POINTS.containsKey(pls.get(i).getName())) POINTS.remove(pls.get(i).getName());
			POINTS.put(pls.get(i).getName(), 0);
			
		}
		
		server.getWorld("game").setDifficulty(Difficulty.PEACEFUL);
		mapWinnersNb = 0;
	}

	public static void endGame(Plugin plugin, Server server) {
		endGame(plugin, server, true);
	}
	
	public static void endGame(final Plugin plugin, final Server server, final boolean broadcast) {	
		actualMap = 0;
		
		GameManager gm = ((MinAnceGAMESCORE) server.getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI();
		gm.setState(GameState.finish);
		
		for(int i = 0; i < TASKS.size(); i++) {
			
			TASKS.get(i).cancel();
			
		}
		
		String msg = ChatColor.DARK_BLUE + "La partie est terminée !";
		
		if(broadcast)server.broadcastMessage(msg);
	
		List<Player> pls = gm.getPlayers();
		
		for(int i = 0; i < pls.size(); i++) {
			
			((MinAnceCORE) server.getPluginManager().getPlugin("MinAnceCORE")).getAPI().changeServer(pls.get(i), "main_lobby");
			gm.removePlayer(pls.get(i));
			
		}

		gm.setState(GameState.wait);
	}
	
	public static void move(PlayerMoveEvent e) {
		Location goodPos = getActualGoodPos();

		if(e.getTo().getBlockX() == goodPos.getBlockX() &&
				e.getTo().getBlockY() == goodPos.getBlockY() &&
				e.getTo().getBlockZ() == goodPos.getBlockZ()) {
			
			if(mapWinnersNb < 3) {
				boolean already = false;
				
				if(mapWinners[0] != null && mapWinners[0].getName() == e.getPlayer().getName()) {
					already = true;
				}
				
				if(mapWinners[1] != null && mapWinners[1].getName() == e.getPlayer().getName()) {
					already = true;
				}
				
				if(mapWinners[2] != null && mapWinners[2].getName() == e.getPlayer().getName()) {
					already = true;
				}
				
				if(!already) {
					PL.getServer().broadcastMessage(e.getPlayer().getName() + ChatColor.DARK_BLUE + " a trouvé la solution !");
					
					mapWinners[mapWinnersNb] = e.getPlayer();
					mapWinnersNb++;
					
					if(mapWinnersNb == 3) {
						PL.getServer().broadcastMessage(ChatColor.DARK_GREEN + "Niveau terminé ! Téléportation ...");
						
						actualMap++;
						
						int p1 = POINTS.get(mapWinners[0].getName());
						POINTS.remove(mapWinners[0].getName());
						POINTS.put(mapWinners[0].getName(), p1+3);
						
						int p2 = POINTS.get(mapWinners[1].getName());
						POINTS.remove(mapWinners[1].getName());
						POINTS.put(mapWinners[1].getName(), p2+2);
						
						int p3 = POINTS.get(mapWinners[2].getName());
						POINTS.remove(mapWinners[2].getName());
						POINTS.put(mapWinners[2].getName(), p3+1);
						
						
						//mapWinners[2] = null;
						
						final Plugin pl = PL;
						
						PL.getServer().getScheduler().runTaskLater(PL, new Runnable() {
							
							@SuppressWarnings("unchecked")
							@Override
							public void run() {
								final List<Player> pls = ((MinAnceGAMESCORE) pl.getServer().getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI().getPlayers();
									
								if(actualMap != 5) {
									for(int i = 0; i < pls.size(); i++) {
										mapWinners[0] = null;
										mapWinners[1] = null;
										mapWinners[2] = null;
										mapWinnersNb = 0;
										pls.get(i).teleport(SPAWN_LOCS[actualMap]);
										if(DOUBLE_JUMP[actualMap]) {
											pls.get(i).addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, 1));
										} else {
											pls.get(i).removePotionEffect(PotionEffectType.JUMP);
										}
									}
								} else {
									for(int i = 0; i < pls.size(); i++) {
										pls.get(i).teleport(new Location(PL.getServer().getWorld("world"),
											26, 71, 95));
									}
									
									int nb = 1;
									
									PL.getServer().broadcastMessage(ChatColor.GREEN + "=====================================================");
									PL.getServer().broadcastMessage("  ");
									
									//pl.getServer().broadcastMessage("Fini, et j'ai pas codé le reste ...");
									HashMap<String, Integer> map = new HashMap<String, Integer>();
									map.putAll(POINTS);
									Object[] a = map.entrySet().toArray();
									Arrays.sort(a, new Comparator() {
										public int compare(Object o1, Object o2) {
											return ((Map.Entry<String, Integer>) o2).getValue().compareTo(
													((Map.Entry<String, Integer>) o1).getValue());
										}
									});

									for (Object e : a) {
										if(nb == 1) {
											int pieces = 20;
											pieces += POINTS.get(((Map.Entry<String, Integer>) e).getKey());
											PL.getServer().broadcastMessage(ChatColor.GOLD + "   1. " + ChatColor.RESET + ((Map.Entry<String, Integer>) e).getKey() + ChatColor.GRAY + "(" + ((Map.Entry<String, Integer>) e).getValue() + " point(s))." + ChatColor.GOLD + " + " + pieces + " pi�ces.");
											PL.getServer().dispatchCommand(PL.getServer().getConsoleSender(), 
													"sync console main_lobby money give " + ((Map.Entry<String, Integer>) e).getKey() + " " + pieces + " GameCoin");
										} else if(nb == 2) {
											int pieces = 15;
											pieces += POINTS.get(((Map.Entry<String, Integer>) e).getKey());
											PL.getServer().broadcastMessage(ChatColor.DARK_GRAY + "   2. " + ChatColor.RESET + ((Map.Entry<String, Integer>) e).getKey() + ChatColor.GRAY + "(" + ((Map.Entry<String, Integer>) e).getValue() + " point(s))" + ChatColor.GOLD + " + " + pieces + " pi�ces.");
											PL.getServer().dispatchCommand(PL.getServer().getConsoleSender(), 
													"sync console main_lobby money give " + ((Map.Entry<String, Integer>) e).getKey() + " " + pieces + " GameCoin");
										} else if(nb == 3) {
											int pieces = 5;
											pieces += POINTS.get(((Map.Entry<String, Integer>) e).getKey());
											PL.getServer().broadcastMessage(ChatColor.YELLOW + "   3. " + ChatColor.RESET + ((Map.Entry<String, Integer>) e).getKey() + ChatColor.GRAY + "(" + ((Map.Entry<String, Integer>) e).getValue() + " point(s))" + ChatColor.GOLD + " + " + pieces + " pi�ces.");
											PL.getServer().dispatchCommand(PL.getServer().getConsoleSender(), 
													"sync console main_lobby money give " + ((Map.Entry<String, Integer>) e).getKey() + " " + pieces + " GameCoin");
										}
										
										nb++;
										
										/* System.out.println(((Map.Entry<String, Integer>) e).getKey() + " : "
												+ ((Map.Entry<String, Integer>) e).getValue());*/
									}
									
									PL.getServer().broadcastMessage("  ");
									PL.getServer().broadcastMessage(ChatColor.AQUA + "Bravo !");
									PL.getServer().broadcastMessage("  ");
									PL.getServer().broadcastMessage(ChatColor.GREEN + "=====================================================");
									
									PL.getServer().getScheduler().runTaskLater(PL, new Runnable() {
										
										@Override
										public void run() {
											
											for(int i = 0; i < pls.size(); i++) {
												
												((MinAnceCORE) PL.getServer().getPluginManager().getPlugin("MinAnceCORE")).getAPI().changeServer(pls.get(i), "main_lobby");
												((MinAnceGAMESCORE) PL.getServer().getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI().removePlayer(pls.get(i));
											
											}
										
										}
									}, 10*20);
									
								}
								
							}
						}, 3*10);
					}
				}
			}
			
		}
		
	}
	
	public static Location getActualGoodPos() {
		int x = PL.getConfig().getInt("maps.map" + MAPS[actualMap] + ".end.x");
		int y = PL.getConfig().getInt("maps.map" + MAPS[actualMap] + ".end.y");
		int z = PL.getConfig().getInt("maps.map" + MAPS[actualMap] + ".end.z");
		
		return new Location(PL.getServer().getWorld("game"), x, y, z);
	}
	
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
	
	public static boolean checkPEXPerm(Player player, String perm) {
		if(player.isOp()) return true;
		
		PermissionUser u = PermissionsEx.getUser(player);
		return u.has(perm);
	}
	
	public static void rules(CommandSender s) {
		s.sendMessage(ChatColor.GREEN + "=====================================================");
		s.sendMessage(ChatColor.AQUA + "Bienvenue dans Escape. Voici les règles :");
		s.sendMessage(ChatColor.AQUA + "Entre 6 et 12 joueurs spawnent dans differents labyrinthes, jumps ..., dont"
				+ "il faut s'échaper en trouvant la plaque de pression en or ! Bonne chance");
		s.sendMessage(ChatColor.GREEN + "=====================================================");
	}
}
