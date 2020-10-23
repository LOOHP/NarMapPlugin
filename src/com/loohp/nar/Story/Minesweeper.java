package com.loohp.nar.Story;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.loohp.nar.NarMapPlugin;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Minesweeper implements Listener {
	
	private NarMapPlugin plugin;
	private World world;
	private AtomicBoolean isWon;
	private AtomicBoolean isEnd;
	
	private Block loseIndiactor;
	
	private ProtectedRegion board;
	private ProtectedRegion area;
	
	public Minesweeper(NarMapPlugin plugin) {
		this.plugin = plugin;
		this.world = plugin.mainWorld;
		this.isWon = new AtomicBoolean(false);
		this.isEnd = new AtomicBoolean(false);
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		this.loseIndiactor = world.getBlockAt(-71, 87, -76);
		
		Bukkit.getScheduler().runTaskLater(plugin, () -> { 
			RegionManager manager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
			board = manager.getRegion("minesweeperboard");
			area = manager.getRegion("minesweeper");
		}, 2);
		
		run();
	}
	
	public CompletableFuture<Void> reset() {
		CompletableFuture<Void> future = new CompletableFuture<>();
		world.getBlockAt(-70, 87, -80).setType(Material.REDSTONE_BLOCK);
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			world.getBlockAt(-70, 87, -80).setType(Material.WHITE_WOOL);
			isWon.set(false);
			isEnd.set(false);
			future.complete(null);
		}, 2);
		return future;
	}
	
	public boolean isWon() {
		return isWon.get();
	}
	
	public boolean isEnd() {
		return isEnd.get();
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event) {
		if (plugin.disableInteraction) {
			return;
		}
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !event.getHand().equals(EquipmentSlot.HAND)) {
			return;
		}
		Block block = event.getClickedBlock();
		Location location = block.getLocation();
		
		if (isEnd.get()) {
			if (block.equals(world.getBlockAt(-65, 93, -73))) {
				reset();
				Bukkit.getScheduler().runTaskLater(plugin, () -> reset(), 5);
			}
		} else {
			if (!board.contains(BukkitAdapter.asBlockVector(location))) {
				return;
			}
			
			if (block.getType().equals(Material.LIGHT_GRAY_CARPET)) {
				block.setType(Material.RED_CARPET);
			} else if (block.getType().equals(Material.RED_CARPET)) {
				block.setType(Material.LIGHT_GRAY_CARPET);
			}
		}
	}
	
	private void run() {
		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			if (!plugin.mainCharater.isPresent() || isEnd.get()) {
				return;
			}
			Player player = plugin.mainCharater.get();
			if (!area.contains(BukkitAdapter.asBlockVector(player.getLocation()))) {
				return;
			}
			
			if (loseIndiactor.getType().equals(Material.RED_WOOL)) {
				isEnd.set(true);
			} else {
				BlockVector3 max = board.getMaximumPoint();
				BlockVector3 min = board.getMinimumPoint();
				
				List<Location> covered = new ArrayList<>();
				
				for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
					for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
						for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
							Material material = world.getBlockAt(x, y, z).getType();
							if (material.equals(Material.LIGHT_GRAY_CARPET) || material.equals(Material.RED_CARPET)) {
								covered.add(new Location(world, x, y - 1, z));
							}
						}
					}
				}
				
				isWon.set(covered.size() == 40 && covered.stream().allMatch(each -> each.getBlock().getType().equals(Material.ANCIENT_DEBRIS)));
				if (isWon.get()) {
					isEnd.set(true);
				}
			}
		}, 4, 5);
	}

}
