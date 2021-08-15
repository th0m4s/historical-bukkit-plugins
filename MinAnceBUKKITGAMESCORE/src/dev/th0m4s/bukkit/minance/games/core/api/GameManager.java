package dev.th0m4s.bukkit.minance.games.core.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import dev.th0m4s.bukkit.minance.games.core.MinAnceGAMESCORE;
import dev.th0m4s.bukkit.minance.games.core.enums.GameState;

public class GameManager {
	
	public static String[] ALL_GAMES = {"FLOATPVP", "SMW", "ESC"};
	public static int NB_LOADED_GAMES = 0;
	
	List<Player> players = 
			new ArrayList<Player>();
	
	MinAnceGame loadedGame = null;
	
	GameState state;
	
	public static boolean setLoadedGames(Plugin p, Server srv) {
		MinAnceGame[] games = {};
		int g = 0;
		
		MinAnceGame game = null;
		for(int i = 0; i < GameManager.ALL_GAMES.length; i++) {
			srv.getConsoleSender().sendMessage("En cours de recherche pour " + ALL_GAMES[i] + " ...");
			Plugin actual_pl = srv.getPluginManager().getPlugin("MinAnce" + GameManager.ALL_GAMES[i]);
			
			if(actual_pl != null) { g++;  game = MinAnceGame.getGame(GameManager.ALL_GAMES[i]); }
		}
		
		NB_LOADED_GAMES = g;
		
		if(NB_LOADED_GAMES != 1) {
			if(NB_LOADED_GAMES == 0) {
				srv.getConsoleSender().sendMessage(ChatColor.GRAY + "Aucun plugin de jeu trouvé ! Désactivation du GameManager !");
				srv.getConsoleSender().sendMessage("L'erreur est peut-être prévue. Le .jar du GameManager peut seulement vous fournir des constantes statiques");
			} else {
				srv.getConsoleSender().sendMessage(ChatColor.RED + "Plusieurs plugins de jeu trouvés ! Désactivation du GameManager !");
			}

			srv.getPluginManager().disablePlugin(p);
			
			return false; 
		} else {
			srv.getConsoleSender().sendMessage(ChatColor.GREEN + game.NAME + " trouvé ! Affichage :");
			srv.getConsoleSender().sendMessage("MAX_PLAYERS " + game.MAX_PLAYERS + " / MIN_PLAYERS " + game.MIN_PLAYERS);
			srv.getConsoleSender().sendMessage("WITH_TAEMS " + game.WITH_TEAMS);
			srv.getConsoleSender().sendMessage("WAIT_TIME " + game.WAIT_TIME);
			srv.getConsoleSender().sendMessage("MAX_TIME " + game.MAX_TIME);
		
			GameManager gm = ((MinAnceGAMESCORE) srv.getPluginManager().getPlugin("MinAnceGAMESCORE")).getAPI();

			gm.setGame(game);
			gm.setState(GameState.wait);
		
		}
		
		return true;
	}
	
	public List<Player> getPlayers() {
		return players;
	}
	
	
	public void addPlayer(Player p) {
		players.add(p);
	}
	
	public void removePlayer(Player p) {
		players.remove(p);
	}
	
	
	public boolean playerExist(Player player) {
		return players.contains(player);
	}
	
	public void setGame(MinAnceGame g) {
		loadedGame = g;
	}
	
	public GameState getState() {
		return state;
	}
	
	public void setState(GameState s) {
		state = s;
	}
	
	public MinAnceGame getGame() {
		return loadedGame;
	}
	
}
