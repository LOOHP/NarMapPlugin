package com.loohp.nar.Story;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.loohp.nar.NarMapPlugin;
import com.loohp.nar.Data.NSound;
import com.loohp.nar.Events.RegionsChangedEvent;
import com.loohp.nar.Utils.WorldGuardUtils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class MusicManager implements Listener {
	
	public static final String MENU = "menu";
	public static final String PUMPKIN_STREET = "pumpkin_street";
	public static final String NANA_HOUSE = "nana_house";
	public static final String BIGGY_HOUSE = "biggy_house";
	public static final String CLOSET = "closet";
	public static final String TERMINAL = "terminal";
	public static final String CLASSROOM = "classroom";
	public static final String OLDBASE = "oldbase";
	public static final String MUSHROOM = "mushroom";
	public static final String NEWBASE = "newbase";
	public static final String S3BASE = "s3base";
	public static final String ENDING = "ending";
	
	public static Map<String, String> mapping = new HashMap<>();
	public static Map<String, Long> duration = new HashMap<>();
	public static Map<UUID, UUID> current = new HashMap<>();
	
	public static void setup() {
		Bukkit.getPluginManager().registerEvents(new MusicManager(), NarMapPlugin.plugin);
		
		mapping.put(MENU, NSound.BGM_MENU);
		mapping.put(NANA_HOUSE, NSound.BGM_NANA_HOUSE);
		mapping.put(PUMPKIN_STREET, NSound.BGM_PUMPKIN_STREET);
		mapping.put(TERMINAL, NSound.BGM_TERMINAL);
		mapping.put(CLOSET, NSound.BGM_CLOSET);
		mapping.put(NEWBASE, NSound.BGM_NEWBASE);
		mapping.put(BIGGY_HOUSE, NSound.BGM_BIGGY_HOUSE);
		mapping.put(CLASSROOM, NSound.BGM_CLASSROOM);
		mapping.put(ENDING, NSound.BGM_ENDING);
		
		duration.put(NSound.BGM_MENU, 126000L);
		duration.put(NSound.BGM_NANA_HOUSE, 114000L);
		duration.put(NSound.BGM_PUMPKIN_STREET, 98000L);
		duration.put(NSound.BGM_TERMINAL, 210000L);
		duration.put(NSound.BGM_CLOSET, 187000L);
		duration.put(NSound.BGM_NEWBASE, 180000L);
		duration.put(NSound.BGM_BIGGY_HOUSE, 583000L);
		duration.put(NSound.BGM_CLASSROOM, 200000L);
		duration.put(NSound.BGM_ENDING, 175000L);
	}
	
	@EventHandler
	public void onRegionChange(RegionsChangedEvent event) {
		Player player = event.getPlayer();
		
		ProtectedRegion currentRegion = WorldGuardUtils.getHighestPriorityRegion(event.getCurrentRegions());
		ProtectedRegion previousRegion = WorldGuardUtils.getHighestPriorityRegion(event.getPreviousRegions());
		
		//player.sendMessage("Current: " + (currentRegion == null ? "null" : currentRegion.getId()) + " Size:" + event.getCurrentRegions().size());
		//player.sendMessage("Previous: " + (previousRegion == null ? "null" : previousRegion.getId()) + " Size:" + event.getPreviousRegions().size());
		
		if (previousRegion != null) {
			String wasPlaying = mapping.get(previousRegion.getId());
			if (wasPlaying != null) {
				player.stopSound(wasPlaying, SoundCategory.RECORDS);
				current.remove(player.getUniqueId());
			}
		}
		
		if (currentRegion != null) {
			String toPlay = mapping.get(currentRegion.getId());
			if (toPlay != null) {
				player.playSound(player.getLocation(), toPlay, SoundCategory.RECORDS, 200, 1);
				UUID key = UUID.randomUUID();
				current.put(player.getUniqueId(), key);
				
				new BukkitRunnable() {
					@Override
					public void run() {
						UUID compareKey = current.get(player.getUniqueId());
						if (compareKey != null && key.equals(compareKey)) {
							player.playSound(player.getLocation(), toPlay, SoundCategory.RECORDS, 200, 1);
						} else {
							this.cancel();
						}
					}
				}.runTaskTimer(NarMapPlugin.plugin, (long) Math.ceil(duration.get(toPlay) / 50), (long) Math.ceil(duration.get(toPlay) / 50));
			}
		}
	}

}
