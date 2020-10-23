package com.loohp.nar.Story;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.Lightable;
import org.bukkit.inventory.Inventory;

import com.loohp.nar.NarMapPlugin;

public class MailBox {
	
	public static final Block CHEST = NarMapPlugin.plugin.mainWorld.getBlockAt(32, 71, -82);
	public static final Block LAMP = NarMapPlugin.plugin.mainWorld.getBlockAt(28, 71, -82);
	
	public static void check() {
		Bukkit.getScheduler().runTaskTimer(NarMapPlugin.plugin, () -> {
			Inventory inv = ((Container) CHEST.getState()).getInventory();
			if (Arrays.asList(inv.getContents()).stream().anyMatch(each -> each != null)) {
				Lightable lightable = (Lightable) LAMP.getBlockData();
				lightable.setLit(true);
				LAMP.setBlockData(lightable);
			} else {
				Lightable lightable = (Lightable) LAMP.getBlockData();
				lightable.setLit(false);
				LAMP.setBlockData(lightable);
			}
		}, 0, 10);
	}

}
