package com.loohp.nar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.loohp.nar.Data.NSound;
import com.loohp.nar.Listeners.Events;
import com.loohp.nar.Listeners.Events.ThrownItemData;
import com.loohp.nar.Story.ItemCollection;
import com.loohp.nar.Story.MainSequence;
import com.loohp.nar.Utils.ChatUtils;
import com.loohp.nar.Utils.CustomStringUtils;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!label.equalsIgnoreCase("nar")) {
			return true;
		}
		
		args = CustomStringUtils.splitStringToArgs(String.join(" ", args));
		
		if (args.length > 1 && args[0].equalsIgnoreCase("setmain") && sender.hasPermission("nar.admin")) {
			Player player = Bukkit.getPlayer(args[1]);
			if (player != null) {
				if (!NarMapPlugin.plugin.gameOngoing.get()) {
					NarMapPlugin.plugin.mainCharater = Optional.of(player);
					NarMapPlugin.plugin.getConfig().set("MainCharacter", player.getName());
					sender.sendMessage(ChatColor.GREEN + "Set main character to " + player.getName());
					NarMapPlugin.plugin.saveConfig();
				} else {
					sender.sendMessage(ChatColor.RED + "Game on-going");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Player not found");
			}
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("getmain") && sender.hasPermission("nar.admin")) {
			if (NarMapPlugin.plugin.mainCharater.isPresent()) {
				sender.sendMessage(ChatColor.GREEN + "The main character is " + NarMapPlugin.plugin.mainCharater.get().getName());
			} else {
				sender.sendMessage(ChatColor.YELLOW + "The main character is not set");
			}
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("minesweeperreset") && sender.hasPermission("nar.admin")) {	
			NarMapPlugin.plugin.minesweeper.reset();
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("start") && sender.hasPermission("nar.player")) {	
			Bukkit.getScheduler().runTaskAsynchronously(NarMapPlugin.plugin, () -> {
				try {
					MainSequence.stageBeginning().get();
					MainSequence.stageNightmareVillage().get();
					MainSequence.stageNightmareTerminal().get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			});
		}
		
		if (args.length == 2 && args[0].equalsIgnoreCase("itemcollection") && sender.hasPermission("nar.admin")) {
			for (Method method : ItemCollection.STATUS.getClass().getMethods()) {
				if (method.getName().toLowerCase().startsWith("set" + args[1].toLowerCase()) && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(boolean.class)) {
					try {
						method.invoke(ItemCollection.STATUS, true);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("movement") && sender.hasPermission("nar.admin")) {
			NarMapPlugin.plugin.disableMovement = !NarMapPlugin.plugin.disableMovement; 
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("slp") && sender.hasPermission("nar.admin")) {
			NarMapPlugin.plugin.disableCustomSLP = !NarMapPlugin.plugin.disableCustomSLP; 
		}
		
		if (args.length > 1 && args[0].equalsIgnoreCase("specialtitle") && sender.hasPermission("nar.admin")) {
			ChatUtils.sendLetterByLetterTitleToAllPlayers(args[1], (args.length > 2 ? args[2] : ""), 2, 80, 15, NSound.SFX_BEEP);
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("sendpack") && sender.hasPermission("nar.player")) {
			if (sender instanceof Player) {
				NarMapPlugin.sendResourcePack((Player) sender);
			}
		}
		
		if (args.length == 2 && args[0].equalsIgnoreCase("choose") && sender.hasPermission("nar.player")) {
			if (NarMapPlugin.plugin.mainCharater.isPresent() && sender.equals(NarMapPlugin.plugin.mainCharater.get())) {
				try {
					NarMapPlugin.plugin.chosenOption = Optional.of(Integer.parseInt(args[1]));
				} catch (Exception e) {}
			}
		}
		
		if (args[0].equalsIgnoreCase("delaymultiplier") && sender.hasPermission("nar.player")) {
			if (args.length == 2) {
				try {
					double value = Double.parseDouble(args[1]);
					NarMapPlugin.plugin.chatDelayMultiplier = Math.pow(value, -1);
					NarMapPlugin.plugin.getConfig().set("ChatDelayMultiplier", NarMapPlugin.plugin.chatDelayMultiplier);
					NarMapPlugin.plugin.saveConfig();
					Bukkit.broadcastMessage(ChatColor.YELLOW + "遊戲對話速度已設置為 " + new DecimalFormat("0.##").format(value) + " 倍");
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "請輸入數字 (E.g. 1.5)");
				}
			} else {
				sender.sendMessage(ChatColor.AQUA + "現時遊戲對話速度設置為 " + new DecimalFormat("0.##").format(Math.pow(NarMapPlugin.plugin.chatDelayMultiplier, -1)) + " 倍");
			}
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("toggle") && sender.hasPermission("nar.player")) {
			NarMapPlugin.plugin.playerToggle.set(true);
		}
		
		if (args.length == 2 && args[0].equalsIgnoreCase("retrieveitem") && sender.hasPermission("nar.player")) {
			if (sender instanceof Player) {
				try {
					Player player = (Player) sender;
					UUID key = UUID.fromString(args[1]);
					
					ThrownItemData data = Events.thrownItems.remove(key);
					if (data != null) {
						if (data.getItem().isValid()) {
							data.getItem().teleport(player);
						} else {
							Item item = player.getWorld().dropItem(player.getEyeLocation(), data.getItemstack());
							item.setVelocity(new Vector(0, 0, 0));
						}
					} else {
						player.sendMessage(ChatColor.RED + "無法取回掉落物");
					}
				} catch (Exception e) {}
			}
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> tab = new ArrayList<String>();
		if (!label.equalsIgnoreCase("nar")) {
			return tab;
		}
		
		switch (args.length) {
		case 0:
			if (sender.hasPermission("nar.admin")) {
				tab.add("setmain");
			}
			if (sender.hasPermission("nar.player")) {
				tab.add("delaymultiplier");
				tab.add("sendpack");
			}
			return tab;
		case 1:
			if (sender.hasPermission("nar.admin")) {
				if ("setmain".startsWith(args[0].toLowerCase())) {
					tab.add("setmain");
				}
			}
			if (sender.hasPermission("nar.player")) {
				if ("delaymultiplier".startsWith(args[0].toLowerCase())) {
					tab.add("delaymultiplier");
				}
				if ("sendpack".startsWith(args[0].toLowerCase())) {
					tab.add("sendpack");
				}
			}
			return tab;
		case 2:
			if (sender.hasPermission("nar.admin")) {
				if ("setmain".equalsIgnoreCase(args[0])) {
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
							tab.add(player.getName());
						}
					}
				}
			}
			if (sender.hasPermission("nar.player")) {
				if ("delaymultiplier".equalsIgnoreCase(args[0])) {
					tab.add("1");
					tab.add("0.5");
				}
			}
			return tab;
		default:
			return tab;
		}
	}

}
