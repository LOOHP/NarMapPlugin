package com.loohp.nar.Utils;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.loohp.nar.NarMapPlugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatUtils {
	
	public static void clearChat(Player... players) {
		for (Player player : players) {
			player.spigot().sendMessage(new TextComponent("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"));
		}
	}
	
	public static void sendToAllPlayers(String str) {
		sendToAllPlayers(new TextComponent(str));
	}
	
	public static void sendToAllPlayers(BaseComponent base) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.spigot().sendMessage(base);
		}
	}
	
	public static void sendTitleToAllPlayers(String title, String subtitle, int fadeIn, int duration, int fadeOut) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendTitle(title, subtitle, fadeIn, duration, fadeOut);
		}
	}
	
	public static CompletableFuture<Void> sendLetterByLetterTitle(Player player, String title, String subtitle, int spacing, int duration, int fadeOut, String soundEffect) {	
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		Bukkit.getScheduler().runTaskAsynchronously(NarMapPlugin.plugin, () -> {
			char[] titleArray = title.toCharArray();
			char[] subtitleArray = subtitle.toCharArray();
			
			String currentTitle = "";
			String currentSubtitle = "";
			
			for (int i = 0; i < titleArray.length; i++) {
				char c = titleArray[i];
				if (c == ChatColor.COLOR_CHAR) {
					currentTitle += String.valueOf(c);
					if (titleArray.length > i + 1) {
						char following = titleArray[i + 1];
						if (following == 'x') {
							for (int u = 1; u < 14; u++) {
								currentTitle += String.valueOf(titleArray[i + u]);
							}
							i += 13;
						} else {
							currentTitle += String.valueOf(following);
							i++;
						}
					}
				} else {
					currentTitle += String.valueOf(c);
					player.sendTitle(currentTitle, currentSubtitle, 0, duration, fadeOut);
					if (c != ' ' && soundEffect != null) {
						player.playSound(player.getLocation(), soundEffect, 200, 1);
					}
					WaitUtils.waitTicks(spacing);
				}
			}
			
			for (int i = 0; i < subtitleArray.length; i++) {
				char c = subtitleArray[i];
				if (c == ChatColor.COLOR_CHAR) {
					currentSubtitle += String.valueOf(c);
					if (subtitleArray.length > i + 1) {
						char following = subtitleArray[i + 1];
						if (following == 'x') {
							for (int u = 1; u < 14; u++) {
								currentSubtitle += String.valueOf(subtitleArray[i + u]);
							}
							i += 13;
						} else {
							currentSubtitle += String.valueOf(following);
							i++;
						}
					}
				} else {
					currentSubtitle += String.valueOf(c);
					player.sendTitle(currentTitle, currentSubtitle, 0, duration, fadeOut);
					if (c != ' ' && soundEffect != null) {
						player.playSound(player.getLocation(), soundEffect, 200, 1);
					}
					WaitUtils.waitTicks(spacing);
				}
			}
			future.complete(null);
		});
		return future;
	}
	
	public static CompletableFuture<Void> sendLetterByLetterTitleToAllPlayers(String title, String subtitle, int spacing, int duration, int fadeOut, String soundEffect) {	
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		Bukkit.getScheduler().runTaskAsynchronously(NarMapPlugin.plugin, () -> {
			char[] titleArray = title.toCharArray();
			char[] subtitleArray = subtitle.toCharArray();
			
			String currentTitle = "";
			String currentSubtitle = "";
			
			for (int i = 0; i < titleArray.length; i++) {
				char c = titleArray[i];
				if (c == ChatColor.COLOR_CHAR) {
					currentTitle += String.valueOf(c);
					if (titleArray.length > i + 1) {
						char following = titleArray[i + 1];
						if (following == 'x') {
							for (int u = 1; u < 14; u++) {
								currentTitle += String.valueOf(titleArray[i + u]);
							}
							i += 13;
						} else {
							currentTitle += String.valueOf(following);
							i++;
						}
					}
				} else {
					currentTitle += String.valueOf(c);
					sendTitleToAllPlayers(currentTitle, currentSubtitle, 0, duration, fadeOut);
					if (c != ' ' && soundEffect != null) {
						for (Player player : Bukkit.getOnlinePlayers()) {
							player.playSound(player.getLocation(), soundEffect, 200, 1);
						}
					}
					WaitUtils.waitTicks(spacing);
				}
			}
			
			for (int i = 0; i < subtitleArray.length; i++) {
				char c = subtitleArray[i];
				if (c == ChatColor.COLOR_CHAR) {
					currentSubtitle += String.valueOf(c);
					if (subtitleArray.length > i + 1) {
						char following = subtitleArray[i + 1];
						if (following == 'x') {
							for (int u = 1; u < 14; u++) {
								currentSubtitle += String.valueOf(subtitleArray[i + u]);
							}
							i += 13;
						} else {
							currentSubtitle += String.valueOf(following);
							i++;
						}
					}
				} else {
					currentSubtitle += String.valueOf(c);
					sendTitleToAllPlayers(currentTitle, currentSubtitle, 0, duration, fadeOut);
					if (c != ' ' && soundEffect != null) {
						for (Player player : Bukkit.getOnlinePlayers()) {
							player.playSound(player.getLocation(), soundEffect, 200, 1);
						}
					}
					WaitUtils.waitTicks(spacing);
				}
			}
			future.complete(null);
		});
		return future;
	}

}
