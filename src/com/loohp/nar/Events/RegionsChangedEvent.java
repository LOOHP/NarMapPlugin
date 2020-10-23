package com.loohp.nar.Events;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Contract;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionsChangedEvent extends Event {
	
	private static final HandlerList handlers = new HandlerList();

	private final UUID uuid;
	private final Set<ProtectedRegion> previousRegions = new HashSet<>();
	private final Set<ProtectedRegion> currentRegions = new HashSet<>();
	private final Set<String> previousRegionsNames = new HashSet<>();
	private final Set<String> currentRegionsNames = new HashSet<>();

	public RegionsChangedEvent(UUID playerUUID, Set<ProtectedRegion> previous, Set<ProtectedRegion> current) {
		this.uuid = playerUUID;
		previousRegions.addAll(previous);
		currentRegions.addAll(current);

		for (ProtectedRegion r : current) {
			currentRegionsNames.add(r.getId());
		}

		for (ProtectedRegion r : previous) {
			previousRegionsNames.add(r.getId());
		}

	}

	@Contract(pure = true)
	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public UUID getUUID() {
		return uuid;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public Set<String> getCurrentRegionsNames() {
		return currentRegionsNames;
	}

	public Set<String> getPreviousRegionsNames() {
		return previousRegionsNames;
	}

	public Set<ProtectedRegion> getCurrentRegions() {
		return currentRegions;
	}

	public Set<ProtectedRegion> getPreviousRegions() {
		return previousRegions;
	}
}
