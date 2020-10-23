package com.loohp.nar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.loohp.nar.Events.RegionsChangedEvent;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class WorldGuardRegionManager implements Listener {
	
	private NarMapPlugin plugin;
	private Map<Player, Set<ProtectedRegion>> current = new HashMap<>();
	
	public WorldGuardRegionManager(NarMapPlugin plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
		run();
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			current.put(player, new HashSet<>());
		}
	}
	
	private void run() {
		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			for (Player player : current.keySet()) {
				com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(player.getLocation().clone());
				RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
				RegionQuery query = container.createQuery();
				Set<ProtectedRegion> set = query.getApplicableRegions(loc).getRegions();
				
				Set<ProtectedRegion> previous = current.get(player);
				if (!previous.containsAll(set) || !set.containsAll(previous)) {
					Bukkit.getPluginManager().callEvent(new RegionsChangedEvent(player.getUniqueId(), new HashSet<>(previous), new HashSet<>(set)));
				}
				
				current.put(player, set);
			}
		}, 0, 1);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		current.put(event.getPlayer(), new HashSet<>());
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		current.remove(event.getPlayer());
	}

}
