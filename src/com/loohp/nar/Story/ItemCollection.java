package com.loohp.nar.Story;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import com.loohp.nar.NarMapPlugin;
import com.loohp.nar.Utils.RayTrace;

import net.md_5.bungee.api.ChatColor;

public class ItemCollection implements Listener {
	
	public static final String SCOREBOARD_TAG = "item_collection";
	public static final String PLACE_NOTICE = "右鍵放置";
	
	public enum UUIDS {
		GREEN(UUID.fromString("27a09d40-f569-46ab-94b2-dfd9c16dbf1e"), Color.fromRGB(0, 200, 0)),
		BLUE(UUID.fromString("0da3fa0f-dad6-4e35-838a-0bf67cfb87b1"), Color.fromRGB(0, 102, 255)),
		RED(UUID.fromString("b77afcc5-982d-47e5-ade0-7ad943d653be"), Color.RED),
		PURPLE(UUID.fromString("72159c5c-3148-4e22-8b4b-0fd2a5846f53"), Color.fromRGB(204, 0, 204));
		
		private UUID uuid;
		private Color color;
		private BoundingBox selectionBox;
		
		UUIDS(UUID uuid, Color color) {
			this.uuid = uuid;
			this.color = color;
			this.selectionBox = null;
		}
		
		public UUID getUniqueId() {
			return uuid;
		}
		
		public Color getColor() {
			return color;
		}
		
		public Entity getEntity() {
			return Bukkit.getEntity(uuid);
		}
		
		public BoundingBox getSelectionBox() {
			if (selectionBox != null) {
				return selectionBox;
			}
			Block block = getEntity().getLocation().getBlock();
			BoundingBox box = new BoundingBox(block.getX(), block.getY(), block.getZ(), block.getX() + 1, block.getY() + 0.1, block.getZ() + 1);
			selectionBox = box;
			return box;
		}
		
		public static UUIDS fromUUID(UUID uuid) {
			for (UUIDS uuids : values()) {
				if (uuids.getUniqueId().equals(uuid)) {
					return uuids;
				}
			}
			return null;
		}
		
		public static boolean contains(UUID uuid) {
			return UUIDS.fromUUID(uuid) != null;
		}
		
		private static List<UUID> uuids = null;
		
		public static List<UUID> getUUIDs() {
			if (uuids != null) {
				return new ArrayList<>(uuids);
			}
			List<UUID> list = new ArrayList<>();
			for (UUIDS each : values()) {
				list.add(each.getUniqueId());
			}
			uuids = list;
			return new ArrayList<>(uuids);
		}
		
		private static List<BoundingBox> boxes = null;
		
		public static List<BoundingBox> getSelectionBoxes() {
			if (boxes != null) {
				return new ArrayList<>(boxes);
			}
			List<BoundingBox> list = new ArrayList<>();
			for (UUIDS each : values()) {
				list.add(each.getSelectionBox());
			}
			boxes = list;
			return new ArrayList<>(boxes);
		}
	}
	
	public static class Status {
		
		private boolean green;
		private boolean blue;
		private boolean red;
		private boolean purple;
		
		public Status(boolean green, boolean blue, boolean red, boolean purple) {
			this.green = green;
			this.blue = blue;
			this.red = red;
			this.purple = purple;
		}
		
		public boolean getGreen() {
			return green;
		}
		
		public void setGreen(boolean green) {
			this.green = green;
		}
		
		public boolean getBlue() {
			return blue;
		}
		
		public void setBlue(boolean blue) {
			this.blue = blue;
		}
		
		public boolean getRed() {
			return red;
		}
		
		public void setRed(boolean red) {
			this.red = red;
		}
		
		public boolean getPurple() {
			return purple;
		}
		
		public void setPurple(boolean purple) {
			this.purple = purple;
		}
	}
	
	public static final Status STATUS = new Status(false, false, false, false);
	public static final Set<Item> ITEMS_CREATED = new HashSet<>();
	
	public static void _init_() {
		Bukkit.getPluginManager().registerEvents(new ItemCollection(), NarMapPlugin.plugin);
		NarMapPlugin.plugin.mainWorld.setChunkForceLoaded(1, -6, true);
		update();
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		BlockFace face = event.getBlockFace();
		if (block == null) {
			return;
		}
		Player player = event.getPlayer();
		Entity c = UUIDS.BLUE.getEntity();
		if (player.getWorld().equals(c.getWorld()) && player.getLocation().distanceSquared(c.getLocation()) > 100) {
			return;
		}
		block = block.getRelative(face);
		List<UUID> uuids = UUIDS.getUUIDs();
		for (Entity entity : block.getWorld().getNearbyEntities(BoundingBox.of(block))) {
			if (uuids.contains(entity.getUniqueId())) {
				onInteractEntity(new PlayerInteractAtEntityEvent(player, entity, entity.getLocation().toVector()));
				return;
			}
		}
	}
	
	@EventHandler
	public void onInteractEntity(PlayerInteractAtEntityEvent event) {
		Entity entity = event.getRightClicked();
		Player player = event.getPlayer();
		if (!UUIDS.contains(entity.getUniqueId())) {
			return;
		}
		World world = player.getWorld();
		PlayerInventory inv = player.getInventory();
		int heldSlot = inv.getHeldItemSlot();
		switch (UUIDS.fromUUID(entity.getUniqueId())) {
		case GREEN:
			if (!STATUS.getGreen()) {
				ItemStack compare = NarMapPlugin.plugin.specialitems.get(SpecialItems.CHALLENGE_HENRY);
				if (inv.getItem(heldSlot) != null && inv.getItem(heldSlot).isSimilar(compare)) {
					inv.setItem(heldSlot, null);
					Location placement = entity.getLocation().clone().add(0, 0.2, 0);
					Item item = world.dropItem(placement, compare);
					item.setVelocity(new Vector(0, 0, 0));
					item.setGravity(false);
					item.setPickupDelay(32767);
					item.addScoreboardTag(SCOREBOARD_TAG);
					item.teleport(placement);
					ITEMS_CREATED.add(item);
					world.playSound(placement, Sound.BLOCK_BEACON_ACTIVATE, 5, 1);
				}
				STATUS.setGreen(true);
			}
			break;
		case BLUE:
			if (!STATUS.getBlue()) {
				ItemStack compare = NarMapPlugin.plugin.specialitems.get(SpecialItems.CHALLENGE_LOOHP);
				if (inv.getItem(heldSlot) != null && inv.getItem(heldSlot).isSimilar(compare)) {
					inv.setItem(heldSlot, null);
					Location placement = entity.getLocation().clone().add(0, 0.2, 0);
					Item item = world.dropItem(placement, compare);
					item.setVelocity(new Vector(0, 0, 0));
					item.setGravity(false);
					item.setPickupDelay(32767);
					item.addScoreboardTag(SCOREBOARD_TAG);
					item.teleport(placement);
					ITEMS_CREATED.add(item);
					world.playSound(placement, Sound.BLOCK_BEACON_ACTIVATE, 5, 1);
				}
				STATUS.setBlue(true);
			}
			break;
		case RED:
			if (!STATUS.getRed()) {
				ItemStack compare = NarMapPlugin.plugin.specialitems.get(SpecialItems.CHALLENGE_CARRR);
				if (inv.getItem(heldSlot) != null && inv.getItem(heldSlot).isSimilar(compare)) {
					inv.setItem(heldSlot, null);
					Location placement = entity.getLocation().clone().add(0, 0.2, 0);
					Item item = world.dropItem(placement, compare);
					item.setVelocity(new Vector(0, 0, 0));
					item.setGravity(false);
					item.setPickupDelay(32767);
					item.addScoreboardTag(SCOREBOARD_TAG);
					item.teleport(placement);
					ITEMS_CREATED.add(item);
					world.playSound(placement, Sound.BLOCK_BEACON_ACTIVATE, 5, 1);
				}
				STATUS.setRed(true);
			}
			break;
		case PURPLE:
			if (!STATUS.getPurple()) {
				ItemStack compare = NarMapPlugin.plugin.specialitems.get(SpecialItems.CHALLENGE_BIGGY);
				if (inv.getItem(heldSlot) != null && inv.getItem(heldSlot).isSimilar(compare)) {
					inv.setItem(heldSlot, null);
					Location placement = entity.getLocation().clone().add(0, 0.2, 0);
					Item item = world.dropItem(placement, compare);
					item.setVelocity(new Vector(0, 0, 0));
					item.setGravity(false);
					item.setPickupDelay(32767);
					item.addScoreboardTag(SCOREBOARD_TAG);
					item.teleport(placement);
					ITEMS_CREATED.add(item);
					world.playSound(placement, Sound.BLOCK_BEACON_ACTIVATE, 5, 1);
				}
				STATUS.setPurple(true);
			}
			break;
		}
		event.setCancelled(true);
	}
	
	public static void update() {
		Bukkit.getScheduler().runTaskTimer(NarMapPlugin.plugin, () -> {
			Player player = NarMapPlugin.plugin.mainCharater.orElse(null);
			if (player == null) {
				return;
			}
			
			World world = NarMapPlugin.plugin.mainWorld;
			if (!player.getWorld().equals(world)) {
				return;
			}
			
			if (player.getLocation().distanceSquared(UUIDS.BLUE.getEntity().getLocation()) > 100) {
				return;
			}
			
			ItemStack held = player.getEquipment().getItemInMainHand();
			if (held == null) {
				return;
			}
			
			int looking = RayTrace.getFirstIntersectedBoundingBoxLookingAt(player, 5, UUIDS.getSelectionBoxes().toArray(new BoundingBox[0]));
			
			if (NarMapPlugin.plugin.specialitems.get(SpecialItems.CHALLENGE_BIGGY).isSimilar(held)) {
				world.spawnParticle(Particle.REDSTONE, UUIDS.PURPLE.getEntity().getLocation().clone().add(0, 0.4, 0), 5, 0.2, 0.2, 0.2, new DustOptions(UUIDS.PURPLE.getColor(), 1));
				if (looking == UUIDS.PURPLE.ordinal()) {
					player.sendTitle("", ChatColor.of(new java.awt.Color(UUIDS.PURPLE.getColor().asRGB())) + PLACE_NOTICE, 0, 7, 10);
				}
			} else if (NarMapPlugin.plugin.specialitems.get(SpecialItems.CHALLENGE_LOOHP).isSimilar(held)) {
				world.spawnParticle(Particle.REDSTONE, UUIDS.BLUE.getEntity().getLocation().clone().add(0, 0.4, 0), 5, 0.2, 0.2, 0.2, new DustOptions(UUIDS.BLUE.getColor(), 1));
				if (looking == UUIDS.BLUE.ordinal()) {
					player.sendTitle("", ChatColor.of(new java.awt.Color(UUIDS.BLUE.getColor().asRGB())) + PLACE_NOTICE, 0, 7, 10);
				}
			} else if (NarMapPlugin.plugin.specialitems.get(SpecialItems.CHALLENGE_HENRY).isSimilar(held)) {
				world.spawnParticle(Particle.REDSTONE, UUIDS.GREEN.getEntity().getLocation().clone().add(0, 0.4, 0), 5, 0.2, 0.2, 0.2, new DustOptions(UUIDS.GREEN.getColor(), 1));
				if (looking == UUIDS.GREEN.ordinal()) {
					player.sendTitle("", ChatColor.of(new java.awt.Color(UUIDS.GREEN.getColor().asRGB())) + PLACE_NOTICE, 0, 7, 10);
				}
			} else if (NarMapPlugin.plugin.specialitems.get(SpecialItems.CHALLENGE_CARRR).isSimilar(held)) {
				world.spawnParticle(Particle.REDSTONE, UUIDS.RED.getEntity().getLocation().clone().add(0, 0.4, 0), 5, 0.2, 0.2, 0.2, new DustOptions(UUIDS.RED.getColor(), 1));
				if (looking == UUIDS.RED.ordinal()) {
					player.sendTitle("", ChatColor.of(new java.awt.Color(UUIDS.RED.getColor().asRGB())) + PLACE_NOTICE, 0, 7, 10);
				}
			}
		}, 0, 5);
	}
	
	public static void clear() {
		if (Bukkit.isPrimaryThread()) {
			STATUS.setBlue(false);
			STATUS.setGreen(false);
			STATUS.setRed(false);
			STATUS.setPurple(false);
			
			Iterator<Item> itr = ITEMS_CREATED.iterator();
			while (itr.hasNext()) {
				Item item = itr.next();
				item.remove();
				itr.remove();
			}
			
			World world = NarMapPlugin.plugin.mainWorld;
			Collection<Entity> items = world.getNearbyEntities(BoundingBox.of(world.getBlockAt(22, 76, -87), world.getBlockAt(26, 76, -87)), each -> each instanceof Item);
			for (Entity entity : items) {
				Item item = (Item) entity;
				if (item.getScoreboardTags().contains(SCOREBOARD_TAG)) {
					item.remove();
				}
			}
		} else {
			CompletableFuture<Void> future = new CompletableFuture<Void>();
			Bukkit.getScheduler().runTask(NarMapPlugin.plugin, () -> {
				clear();
				future.complete(null);
			});
			try {
				future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
	

}
