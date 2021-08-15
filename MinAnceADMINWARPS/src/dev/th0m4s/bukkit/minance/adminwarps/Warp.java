package dev.th0m4s.bukkit.minance.adminwarps;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;


public class Warp {

	public Location loc;
	public String name;
	
	public Warp(Location l, String n) {
		this.loc = l;
		this.name = n;
	}
	
	/*public static boolean exists(String name, List<Warp> warps) {
		for(int i =0; i < warps.size(); i++) {
			if(warps.get(i).name == name) return true;
		}

		return false;
	}
	
	public static Warp getWarpIn(String name, List<Warp> warps) {
		for(int i =0; i < warps.size(); i++) {
			if(warps.get(i).name == name) return warps.get(i);
		}
		
		return null;
	}*/
	
	public static Warp getWarp(String name, FileConfiguration config) {
		if(config.contains("warps." + name) == false) return null;
		
		return (Warp) config.get("warps." + name);
	}
	
	public static boolean exists(String name, FileConfiguration config) {
		return config.contains("warps." + name);
	}

}
