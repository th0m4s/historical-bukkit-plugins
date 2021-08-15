package dev.th0m4s.bukkit.minance.games.core.api;

import dev.th0m4s.bukkit.minance.games.esc.ESCGame;
import dev.th0m4s.bukkit.minance.games.floatpvp.FloatPVPGame;
import dev.th0m4s.bukkit.minance.games.smw.SMWGame;


public class MinAnceGame {

	public String NAME = "";
	
	public int MAX_PLAYERS = 0;
	public int MIN_PLAYERS = 0;
	
	public boolean WITH_TEAMS = false;
	
	public int MAX_TIME = 0;
	
	public int WAIT_TIME = 0;
	
	public MinAnceGame(String name, int maxp, int minp, boolean teams, int maxt, 
			int waitt) {
		
		this.NAME = name;
		this.MAX_PLAYERS = maxp;
		this.MIN_PLAYERS = minp;
		this.WITH_TEAMS = teams;
		this.MAX_TIME = maxt;
		this.WAIT_TIME = waitt;
	}
	
	public String getName() {
		return NAME;
	}
	
	public int getMaxPlayers() {
		return MAX_PLAYERS;
	}

	public int getMinPlayers() {
		return MIN_PLAYERS;
	}
	
	public boolean getTeams() {
		return WITH_TEAMS;
	}
	
	public int getMaxTime() {
		return MAX_TIME;
	}

	public int getWaitTime() {
		return WAIT_TIME;
	}
	
	public static MinAnceGame getGame(String name) {
		if(name == "FLOATPVP") {
		
			return new MinAnceGame("FLOATPVP",
				FloatPVPGame.MAX_PLAYERS,
				FloatPVPGame.MIN_PLAYERS,
				FloatPVPGame.WITH_TEAMS,
				FloatPVPGame.MAX_TIME,
				FloatPVPGame.WAIT_TIME);
		}

		if(name == "SMW") {
			return new MinAnceGame("SMW",
				SMWGame.MAX_PLAYERS,
				SMWGame.MIN_PLAYERS,
				SMWGame.WITH_TEAMS,
				SMWGame.MAX_TIME,
				SMWGame.WAIT_TIME);
		}
		
		if(name == "ESC") {
			return new MinAnceGame("ESC",
				ESCGame.MAX_PLAYERS,
				ESCGame.MIN_PLAYERS,
				ESCGame.WITH_TEAMS,
				ESCGame.MAX_TIME,
				ESCGame.WAIT_TIME);
		}
		
		return null;	
	}
	
}
