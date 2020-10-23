package com.loohp.nar.Utils;

import java.util.Comparator;
import java.util.Set;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardUtils {
	
	private static final Comparator<ProtectedRegion> COMPARATOR_PRIORITY = Comparator.comparing(each -> each.getPriority());
	
	public static ProtectedRegion getHighestPriorityRegion(Set<ProtectedRegion> regions) {
		return regions.stream().max(COMPARATOR_PRIORITY).orElse(null);
	}

}
