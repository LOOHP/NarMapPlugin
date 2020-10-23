package com.loohp.nar.Utils;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.loohp.nar.NarMapPlugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class OptionUtils {
	
	public static final String BAR_CHAR = "⬛";
	public static final int COMPLETE_BAR_SIZE = 40;
	
	public static CompletableFuture<Integer> sendRequest(int options, int defaultOption, long timeout, BaseComponent... display) {
		CompletableFuture<Integer> future = new CompletableFuture<>();
		if (!NarMapPlugin.plugin.mainCharater.isPresent()) {
			future.completeExceptionally(new RuntimeException("No main charater"));
		} else {
			NarMapPlugin.plugin.chosenOption = Optional.empty();
			Player main = NarMapPlugin.plugin.mainCharater.get();
			Collection<? extends Player> players = Bukkit.getOnlinePlayers();
			long start = System.currentTimeMillis();
			long end = start + timeout;
			
			for (Player player : players) {
				player.sendMessage("選項: [點擊]");
			}
			for (int i = 0; i < options && i < display.length; i++) {
				BaseComponent base = display[i];
				for (Player player : players) {
					if (!player.equals(main)) {
						player.spigot().sendMessage(base);
					}
				}
				
				BaseComponent chooseable = base.duplicate();
				chooseable.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/nar choose " + i));
				main.spigot().sendMessage(chooseable);
			}
			
			Bukkit.getScheduler().runTaskAsynchronously(NarMapPlugin.plugin, () -> {
				while (end > System.currentTimeMillis()) {
					int squares =  (int) (((double) (end - System.currentTimeMillis()) / (double) timeout) * COMPLETE_BAR_SIZE);
					String actionbar = "";
					for (int i = 0; i < squares; i++) {
						actionbar += BAR_CHAR;
					}
					for (Player player : players) {
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + actionbar));
					}
					if (NarMapPlugin.plugin.chosenOption.isPresent()) {
						int chosen = NarMapPlugin.plugin.chosenOption.get();
						NarMapPlugin.plugin.chosenOption = Optional.empty();
						future.complete(chosen);
						return;
					}
					try {
						TimeUnit.MILLISECONDS.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if (!NarMapPlugin.plugin.chosenOption.isPresent()) {
					future.complete(defaultOption);
				} else {
					int chosen = NarMapPlugin.plugin.chosenOption.get();
					NarMapPlugin.plugin.chosenOption = Optional.empty();
					future.complete(chosen);
				}
			});
		}
		return future;
	}

}
