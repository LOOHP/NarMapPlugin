package com.loohp.nar.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class RayTrace {
	
	public static Entity getLookingEntity(LivingEntity entity, double range, EntityType type) {
		RayTrace ray = new RayTrace(entity.getEyeLocation().toVector(), entity.getEyeLocation().getDirection());
		List<Entity> entities = entity.getNearbyEntities(range, range, range);
		Entity closest = null;
		double distanceSquared = range * range + 1;
		for (Entity each : entities) {
			if (each.getType().equals(type)) {
				Vector intersect = ray.positionOfIntersection(each.getBoundingBox(), range, 0.01);
				if (intersect != null) {
					double dis = entity.getEyeLocation().distanceSquared(intersect.toLocation(each.getWorld()));
					if (dis < distanceSquared) {
						closest = each;
						distanceSquared = dis;
					}
				}
			}
		}
		return closest;
	}
	
	public static int getFirstIntersectedBoundingBoxLookingAt(LivingEntity entity, double range, BoundingBox... boxes) {
		RayTrace ray = new RayTrace(entity.getEyeLocation().toVector(), entity.getEyeLocation().getDirection());
		int closest = -1;
		double distanceSquared = range * range + 1;
		for (int i = 0; i < boxes.length; i++) {
			BoundingBox box = boxes[i];
			Vector intersect = ray.positionOfIntersection(box, range, 0.01);
			if (intersect != null) {
				double dis = entity.getEyeLocation().distanceSquared(intersect.toLocation(entity.getWorld()));
				if (dis < distanceSquared) {
					closest = i;
					distanceSquared = dis;
				}
			}
		}
		return closest;
	}
	
	public static class TraceResult {
		
		private int index;
		private Location location;
		
		public TraceResult(int index, Location location) {
			this.index = index;
			this.location = location;
		}
		
		public int getIndex() {
			return index;
		}
		public Location getLocation() {
			return location;
		}
	}
	
	public static TraceResult getFirstIntersectedBoundingBoxAndLocationLookingAt(LivingEntity entity, double range, BoundingBox... boxes) {
		RayTrace ray = new RayTrace(entity.getEyeLocation().toVector(), entity.getEyeLocation().getDirection());
		int closest = -1;
		Vector closesIntersect = null;
		double distanceSquared = range * range + 1;
		for (int i = 0; i < boxes.length; i++) {
			BoundingBox box = boxes[i];
			Vector intersect = ray.positionOfIntersection(box, range, 0.01);
			if (intersect != null) {
				double dis = entity.getEyeLocation().distanceSquared(intersect.toLocation(entity.getWorld()));
				if (dis < distanceSquared) {
					closest = i;
					closesIntersect = intersect;
					distanceSquared = dis;
				}
			}
		}
		return new TraceResult(closest, closesIntersect == null ? null : closesIntersect.toLocation(entity.getWorld()));
	}
	
	// ==================

    //origin = start position
    //direction = direction in which the raytrace will go
    private Vector origin;
    private Vector direction;

    public RayTrace(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }

    //get a point on the raytrace at X blocks away
    public Vector getPostion(double blocksAway) {
        return origin.clone().add(direction.clone().multiply(blocksAway));
    }

    //checks if a position is on contained within the position
    public boolean isOnLine(Vector position) {
        double t = (position.getX() - origin.getX()) / direction.getX();
        ;
        if (position.getBlockY() == origin.getY() + (t * direction.getY()) && position.getBlockZ() == origin.getZ() + (t * direction.getZ())) {
            return true;
        }
        return false;
    }

    //get all postions on a raytrace
    public List<Vector> traverse(double blocksAway, double accuracy) {
    	List<Vector> positions = new ArrayList<>();
        for (double d = 0; d <= blocksAway; d += accuracy) {
            positions.add(getPostion(d));
        }
        return positions;
    }

    //intersection detection for current raytrace with return
    public Vector positionOfIntersection(Vector min, Vector max, double blocksAway, double accuracy) {
        List<Vector> positions = traverse(blocksAway, accuracy);
        for (Vector position : positions) {
            if (intersects(position, min, max)) {
                return position;
            }
        }
        return null;
    }

    //intersection detection for current raytrace
    public boolean intersects(Vector min, Vector max, double blocksAway, double accuracy) {
    	List<Vector> positions = traverse(blocksAway, accuracy);
        for (Vector position : positions) {
            if (intersects(position, min, max)) {
                return true;
            }
        }
        return false;
    }

    //bounding box instead of vector
    public Vector positionOfIntersection(BoundingBox boundingBox, double blocksAway, double accuracy) {
    	List<Vector> positions = traverse(blocksAway, accuracy);
        for (Vector position : positions) {
            if (intersects(position, boundingBox.getMin(), boundingBox.getMax())) {
                return position;
            }
        }
        return null;
    }

    //bounding box instead of vector
    public boolean intersects(BoundingBox boundingBox, double blocksAway, double accuracy) {
    	List<Vector> positions = traverse(blocksAway, accuracy);
        for (Vector position : positions) {
            if (intersects(position, boundingBox.getMin(), boundingBox.getMax())) {
                return true;
            }
        }
        return false;
    }

    //general intersection detection
    public static boolean intersects(Vector position, Vector min, Vector max) {
        if (position.getX() < min.getX() || position.getX() > max.getX()) {
            return false;
        } else if (position.getY() < min.getY() || position.getY() > max.getY()) {
            return false;
        } else if (position.getZ() < min.getZ() || position.getZ() > max.getZ()) {
            return false;
        }
        return true;
    }

    //debug / effects
    public void highlight(World world, double blocksAway, double accuracy){
        for (Vector position : traverse(blocksAway,accuracy)){
            world.spawnParticle(Particle.REDSTONE, position.toLocation(world), 1, new DustOptions(Color.fromRGB(0, 255, 0), 1));
        }
    }

}