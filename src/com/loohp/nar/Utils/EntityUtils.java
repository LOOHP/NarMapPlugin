package com.loohp.nar.Utils;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

public class EntityUtils {
	
	public static void faceLocation(LivingEntity entity, Location location) {
		Vector dirBetweenLocations = location.toVector().subtract(entity.getLocation().toVector());
		Location loc = entity.getLocation();
		loc.setDirection(dirBetweenLocations);
		entity.teleport(loc);
	}
	
	public static void face(LivingEntity entity, Vector vector) {
		Location loc = entity.getLocation();
		loc.setDirection(vector);
		entity.teleport(loc);
	}

}
