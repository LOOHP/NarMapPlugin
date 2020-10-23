package com.loohp.nar.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public class BoundingBoxUtils {
	
	public static List<Location> getHollowCube(World world, BoundingBox box, double spacing) {
        List<Location> result = new ArrayList<>();
        
        Location corner1 = box.getMax().toLocation(world);
        Location corner2 = box.getMin().toLocation(world);

        double minX = Math.min(corner1.getX(), corner2.getX());
        double minY = Math.min(corner1.getY(), corner2.getY());
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        double maxX = Math.max(corner1.getX(), corner2.getX());
        double maxY = Math.max(corner1.getY(), corner2.getY());
        double maxZ = Math.max(corner1.getZ(), corner2.getZ());
       
        // 2 areas
        for (double x = minX; x <= maxX; x = x + spacing) {
            for (double z = minZ; z <= maxZ; z = z + spacing) {
                result.add(new Location(world, x, minY, z));
                result.add(new Location(world, x, maxY, z));
            }
        }
       
        // 2 sides (front & back)
        for (double x = minX; x <= maxX; x = x + spacing) {
            for (double y = minY; y <= maxY; y = y + spacing) {
                result.add(new Location(world, x, y, minZ));
                result.add(new Location(world, x, y, maxZ));
            }
        }
       
        // 2 sides (left & right)
        for (double z = minZ; z <= maxZ; z = z + spacing) {
            for (double y = minY; y <= maxY; y = y + spacing) {
                result.add(new Location(world, minX, y, z));
                result.add(new Location(world, maxX, y, z));
            }
        }
       
        return result;
    }
	
	public static Location getRandomLocation(World world, BoundingBox box, Random random) {
		return new Location(world, random.nextDouble() * (box.getMaxX() - box.getMinX()) + box.getMinX(), random.nextDouble() * (box.getMaxY() - box.getMinY()) + box.getMinY(), random.nextDouble() * (box.getMaxZ() - box.getMinZ()) + box.getMinZ());
	}
	
	public static List<Location> getRandomLocation(World world, BoundingBox box, Random random, int amount) {
		List<Location> locations = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			locations.add(getRandomLocation(world, box, random));
		}
		return locations;
	}


}
