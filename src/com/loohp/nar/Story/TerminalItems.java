package com.loohp.nar.Story;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.loohp.nar.NarMapPlugin;
import com.loohp.nar.Utils.ItemStackUtils;

public class TerminalItems {
	
	public static final UUID UUID_A = UUID.fromString("75b8cb1a-051f-40cc-8842-f9b8562d911f");
	public static final UUID UUID_B = UUID.fromString("2ce9c256-85b0-4b77-a7e5-b11dbdaaa67a");
	public static final UUID UUID_C = UUID.fromString("aeebe78b-333f-4291-8dfb-c85a07576329");
	public static final UUID UUID_D = UUID.fromString("ac3c355c-401b-4a88-b7e8-28292547a282");
	public static final UUID UUID_E = UUID.fromString("2c20e81c-be73-4f19-a913-58b511c61622");
	public static final UUID UUID_F = UUID.fromString("299a0048-21bb-42bf-8f01-df5ecb388c80");
	
	public static final UUID DONUT_SCREEN = UUID.fromString("bb39a8f1-9acc-4158-a13c-0cfa4390358a");
	
	public static final AtomicInteger count = new AtomicInteger(0);
	
	public static void detect() {
		Bukkit.getScheduler().runTaskTimer(NarMapPlugin.plugin, () -> {
			World world = NarMapPlugin.plugin.mainWorld;
			if (world.getNearbyEntities(new Location(world, -1020, 68, -1000), 10, 10, 10, each -> each instanceof Player).isEmpty()) {
				return;
			}
			ItemFrame a = (ItemFrame) Bukkit.getEntity(UUID_A);
			ItemFrame b = (ItemFrame) Bukkit.getEntity(UUID_B);
			ItemFrame c = (ItemFrame) Bukkit.getEntity(UUID_C);
			ItemFrame d = (ItemFrame) Bukkit.getEntity(UUID_D);
			ItemFrame e = (ItemFrame) Bukkit.getEntity(UUID_E);
			ItemFrame f = (ItemFrame) Bukkit.getEntity(UUID_F);
			
			ItemStack itemstackA = ((Container) a.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(0);
			ItemStack itemstackB = ((Container) b.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(0);
			ItemStack itemstackC = ((Container) c.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(0);
			ItemStack itemstackD = ((Container) d.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(0);
			ItemStack itemstackE = ((Container) e.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(0);
			ItemStack itemstackF = ((Container) f.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(0);
			
			int n = 0;
			if (a.getItem() != null && ItemStackUtils.isSimilarIgnoreDurabiblty(a.getItem(), itemstackA)) {
				world.getBlockAt(-1019, 66, -998).setType(Material.REDSTONE_BLOCK);
				n++;
			} else {
				world.getBlockAt(-1019, 66, -998).setType(Material.AIR);
			}
			if (b.getItem() != null && ItemStackUtils.isSimilarIgnoreDurabiblty(b.getItem(), itemstackB)) {
				world.getBlockAt(-1020, 66, -998).setType(Material.REDSTONE_BLOCK);
				n++;
			} else {
				world.getBlockAt(-1020, 66, -998).setType(Material.AIR);
			}
			if (c.getItem() != null && ItemStackUtils.isSimilarIgnoreDurabiblty(c.getItem(), itemstackC)) {
				world.getBlockAt(-1021, 66, -998).setType(Material.REDSTONE_BLOCK);
				n++;
			} else {
				world.getBlockAt(-1021, 66, -998).setType(Material.AIR);
			}
			if (d.getItem() != null && ItemStackUtils.isSimilarIgnoreDurabiblty(d.getItem(), itemstackD)) {
				world.getBlockAt(-1021, 66, -1002).setType(Material.REDSTONE_BLOCK);
				n++;
			} else {
				world.getBlockAt(-1021, 66, -1002).setType(Material.AIR);
			}
			if (e.getItem() != null && ItemStackUtils.isSimilarIgnoreDurabiblty(e.getItem(), itemstackE)) {
				world.getBlockAt(-1020, 66, -1002).setType(Material.REDSTONE_BLOCK);
				n++;
			} else {
				world.getBlockAt(-1020, 66, -1002).setType(Material.AIR);
			}
			if (f.getItem() != null && ItemStackUtils.isSimilarIgnoreDurabiblty(f.getItem(), itemstackF)) {
				world.getBlockAt(-1019, 66, -1002).setType(Material.REDSTONE_BLOCK);
				n++;
			} else {
				world.getBlockAt(-1019, 66, -1002).setType(Material.AIR);
			}
			
			count.set(n);
			
			resetHints();
		}, 0, 10);
	}
	
	public static void resetHints() {
		Bukkit.getScheduler().runTask(NarMapPlugin.plugin, () -> {
			ItemFrame a = (ItemFrame) Bukkit.getEntity(UUID_A);
			ItemFrame b = (ItemFrame) Bukkit.getEntity(UUID_B);
			ItemFrame c = (ItemFrame) Bukkit.getEntity(UUID_C);
			ItemFrame d = (ItemFrame) Bukkit.getEntity(UUID_D);
			ItemFrame e = (ItemFrame) Bukkit.getEntity(UUID_E);
			ItemFrame f = (ItemFrame) Bukkit.getEntity(UUID_F);
			
			ItemStack itemstackA = ((Container) a.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(1);
			ItemStack itemstackB = ((Container) b.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(1);
			ItemStack itemstackC = ((Container) c.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(1);
			ItemStack itemstackD = ((Container) d.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(1);
			ItemStack itemstackE = ((Container) e.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(1);
			ItemStack itemstackF = ((Container) f.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(1);
			/*
			Bukkit.getConsoleSender().sendMessage((a == null) + "");
			Bukkit.getConsoleSender().sendMessage((a.getItem() == null) + "");
			Bukkit.getConsoleSender().sendMessage(a.getLocation().add(0, -3, 0).getBlock().getType().toString());
			Bukkit.getConsoleSender().sendMessage((a.getLocation().add(0, -3, 0).getBlock() instanceof Container) + "");
			Bukkit.getConsoleSender().sendMessage(((Container) a.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(0) + "");
			*/
			((Container) a.getLocation().getBlock().getRelative(a.getAttachedFace()).getRelative(BlockFace.DOWN).getState()).getInventory().setItem(13, itemstackA == null ? null : itemstackA.clone());
			((Container) b.getLocation().getBlock().getRelative(b.getAttachedFace()).getRelative(BlockFace.DOWN).getState()).getInventory().setItem(13, itemstackB == null ? null : itemstackB.clone());
			((Container) c.getLocation().getBlock().getRelative(c.getAttachedFace()).getRelative(BlockFace.DOWN).getState()).getInventory().setItem(13, itemstackC == null ? null : itemstackC.clone());
			((Container) d.getLocation().getBlock().getRelative(d.getAttachedFace()).getRelative(BlockFace.DOWN).getState()).getInventory().setItem(13, itemstackD == null ? null : itemstackD.clone());
			((Container) e.getLocation().getBlock().getRelative(e.getAttachedFace()).getRelative(BlockFace.DOWN).getState()).getInventory().setItem(13, itemstackE == null ? null : itemstackE.clone());
			((Container) f.getLocation().getBlock().getRelative(f.getAttachedFace()).getRelative(BlockFace.DOWN).getState()).getInventory().setItem(13, itemstackF == null ? null : itemstackF.clone());
		});
	}
	
	public static void resetProgressionItems() {
		Bukkit.getScheduler().runTask(NarMapPlugin.plugin, () -> {
			ItemFrame a = (ItemFrame) Bukkit.getEntity(UUID_A);
			ItemFrame b = (ItemFrame) Bukkit.getEntity(UUID_B);
			ItemFrame c = (ItemFrame) Bukkit.getEntity(UUID_C);
			ItemFrame d = (ItemFrame) Bukkit.getEntity(UUID_D);
			ItemFrame e = (ItemFrame) Bukkit.getEntity(UUID_E);
			ItemFrame f = (ItemFrame) Bukkit.getEntity(UUID_F);
			
			if (a != null) a.setItem(null, false);
			if (b != null) b.setItem(null, false);
			if (c != null) c.setItem(null, false);
			if (d != null) d.setItem(null, false);
			if (e != null) e.setItem(null, false);
			if (f != null) f.setItem(null, false);
			
			World world = NarMapPlugin.plugin.mainWorld;
			
			boolean shouldunload = false;
			if (!world.getChunkAt(-125, -125).isLoaded()) {
				world.loadChunk(-125, -125);
				shouldunload = true;
			}
			ItemFrame donut = (ItemFrame) Bukkit.getEntity(DONUT_SCREEN);
			ItemStack donutItem = ((Container) donut.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(0);
			donut.setItem(donutItem.clone(), false);
			if (shouldunload) {
				world.loadChunk(-125, -125);
			}
			
			Location mushroom = new Location(world, 2969, 64, 1226);
			Inventory mushroomInv = ItemStackUtils.deepClone(((Container) mushroom.clone().add(0, -2, 0).getBlock().getState()).getInventory());
			((Container) mushroom.getBlock().getState()).getInventory().setContents(mushroomInv.getContents());
			
			Location disc = new Location(world, 4085, 26, 14180);
			Inventory discInv = ItemStackUtils.deepClone(((Container) disc.clone().add(0, -2, 0).getBlock().getState()).getInventory());
			((Container) disc.getBlock().getState()).getInventory().setContents(discInv.getContents());
			
			Location we = new Location(world, 4085, 24, 14187);
			Inventory weInv = ItemStackUtils.deepClone(((Container) we.clone().add(0, -2, 0).getBlock().getState()).getInventory());
			((Container) we.getBlock().getState()).getInventory().setContents(weInv.getContents());
		});
	}
	
	public static int getCompletedCount() {
		return count.get();
	}

}
