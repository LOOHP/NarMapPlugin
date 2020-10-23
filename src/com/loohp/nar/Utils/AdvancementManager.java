package com.loohp.nar.Utils;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import com.loohp.nar.NarMapPlugin;
import com.loohp.nar.Story.SpecialItems;

import hu.trigary.advancementcreator.Advancement;
import hu.trigary.advancementcreator.AdvancementFactory;
import hu.trigary.advancementcreator.shared.ItemObject;
import hu.trigary.advancementcreator.trigger.ImpossibleTrigger;
import hu.trigary.advancementcreator.trigger.InventoryChangedTrigger;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class AdvancementManager {
	
	public static final String TIP_STAGE = "stage";
	
	public static final String PROG_BEGIN = "begin";
	
	public static final String PROG_CHALLENGE_BIGGY = "challenge_biggy";
	public static final String PROG_CHALLENGE_LOOHP = "challenge_loohp";
	public static final String PROG_CHALLENGE_HENRY = "challenge_henry";
	public static final String PROG_CHALLENGE_CARRR = "challenge_carrr";
	
	public static final String PROG_NIGHTMARE = "nightmare";
	public static final String PROG_NANA_VILLAGE = "nana_village";
	public static final String PROG_TERMINAL = "terminal";
	public static final String PROG_NANA_CLASSROOM = "nana_classroom";
	public static final String PROG_NANA_MUSHROOM = "nana_mushroom";
	public static final String PROG_NANA_OLDBASE = "nana_oldbase";
	public static final String PROG_NANA_NEWBASE = "nana_newbase";
	public static final String PROG_NANA_S3BASE = "nana_s3base";
	
	public static final String PROG_COMPLETE = "complete";
	
	public static final String ACHIEVEMENT_IMPOSTER = "imposter";
	public static final String ACHIEVEMENT_SATURN = "saturn";
	public static final String ACHIEVEMENT_BUBBLETEA = "bubble_tea";
	public static final String ACHIEVEMENT_MINEWON = "minesweeper_won";
	public static final String ACHIEVEMENT_MINELOST = "minesweeper_lost";
	public static final String ACHIEVEMENT_CLOUDLAND = "cloudland";
	
	private NarMapPlugin plugin;
	
	public AdvancementManager(NarMapPlugin plugin) {
		this.plugin = plugin;
		
		File advancemnts = new File(Bukkit.getServer().getWorldContainer().getPath() + "/" + Bukkit.getWorlds().get(0).getName() + "/datapacks/bukkit/data/" + plugin.getName().toLowerCase(), "advancements");
		CustomFileUtils.deleteFolderRecrusively(advancemnts);
		
		Bukkit.getScheduler().runTaskLater(plugin, () -> Bukkit.reloadData(), 2);
		
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			
			AdvancementFactory factory = new AdvancementFactory(NarMapPlugin.plugin, true, false);
			
			new Advancement(new NamespacedKey(plugin, "tip/" + TIP_STAGE), new ItemObject().setItem(Material.CHEST),
	                new TextComponent("階段完成"), new TextComponent("你現在可以直接由主畫面回到這裡"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.TASK)
	                .setHidden(true)
	                .setAnnounce(false)
	                .activate(false);
			
	        //===================
	        //Progression
	        
	        Advancement root = factory.getRoot("progression/root", "Nana的旅程", "", Material.HONEY_BLOCK, "block/yellow_wool");
	        
	        Advancement begins = new Advancement(new NamespacedKey(plugin, "progression/" + PROG_BEGIN), new ItemObject().setItem(Material.GOLD_BLOCK),
	                new TextComponent("旅途開始"), new TextComponent("坐穩啦！"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.TASK)
	                .setHidden(true)
	                .setAnnounce(false)
	                .makeChild(root.getId());
	        begins.activate(false);
	        
	        Advancement biggy_item = new Advancement(new NamespacedKey(plugin, "progression/" + PROG_CHALLENGE_BIGGY), new ItemObject().setItem(plugin.specialitems.get(SpecialItems.CHALLENGE_BIGGY).getType()).setNbt(NBTUtils.getNBTCompound(plugin.specialitems.get(SpecialItems.CHALLENGE_BIGGY), "tag").toString()),
	                new TextComponent(ChatColor.LIGHT_PURPLE + "Biggy的禮物"), new TextComponent("拿到Biggy的禮物"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.GOAL)
	                .setHidden(true)
	                .setAnnounce(false)
	                .makeChild(begins.getId());
	        biggy_item.activate(false);
	        
	        Advancement henry_item = new Advancement(new NamespacedKey(plugin, "progression/" + PROG_CHALLENGE_HENRY), new ItemObject().setItem(plugin.specialitems.get(SpecialItems.CHALLENGE_HENRY).getType()).setNbt(NBTUtils.getNBTCompound(plugin.specialitems.get(SpecialItems.CHALLENGE_HENRY), "tag").toString()),
	                new TextComponent(ChatColor.DARK_GREEN + "Henry的禮物"), new TextComponent("拿到Henry的禮物"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.GOAL)
	                .setHidden(true)
	                .setAnnounce(false)
	                .makeChild(biggy_item.getId());
	        henry_item.activate(false);	    
			
	        Advancement car_item = new Advancement(new NamespacedKey(plugin, "progression/" + PROG_CHALLENGE_CARRR), new ItemObject().setItem(plugin.specialitems.get(SpecialItems.CHALLENGE_CARRR).getType()).setNbt(NBTUtils.getNBTCompound(plugin.specialitems.get(SpecialItems.CHALLENGE_CARRR), "tag").toString()),
	                new TextComponent(ChatColor.RED + "LIARCAR的禮物"), new TextComponent("拿到LIARCAR的禮物"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.GOAL)
	                .setHidden(true)
	                .setAnnounce(false)
	                .makeChild(henry_item.getId());
	        car_item.activate(false);	    
			
	        Advancement loohp_item = new Advancement(new NamespacedKey(plugin, "progression/" + PROG_CHALLENGE_LOOHP), new ItemObject().setItem(plugin.specialitems.get(SpecialItems.CHALLENGE_LOOHP).getType()).setNbt(NBTUtils.getNBTCompound(plugin.specialitems.get(SpecialItems.CHALLENGE_LOOHP), "tag").toString()),
	                new TextComponent(ChatColor.DARK_AQUA + "LOOHP的禮物"), new TextComponent("拿到LOOHP的禮物"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.GOAL)
	                .setHidden(true)
	                .setAnnounce(false)
	                .makeChild(car_item.getId());
	        loohp_item.activate(false);
	        
	        Advancement nightmare = new Advancement(new NamespacedKey(plugin, "progression/" + PROG_NIGHTMARE), new ItemObject().setItem(Material.BLACK_BED),
	                new TextComponent("噩夢..."), new TextComponent("哦哦哦哦哦哦哦不..."))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.TASK)
	                .setHidden(true)
	                .setAnnounce(false)
	                .makeChild(loohp_item.getId());
	        nightmare.activate(false);
			
	        Advancement village = new Advancement(new NamespacedKey(plugin, "progression/" + PROG_NANA_VILLAGE), new ItemObject().setItem(Material.BLAZE_ROD),
	                new TextComponent("重遊故地"), new TextComponent("探索Nana 18-20年的村莊..."))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.TASK)
	                .setHidden(true)
	                .setAnnounce(false)
	                .makeChild(nightmare.getId());
	        village.activate(false);
			
	        Advancement terminal = new Advancement(new NamespacedKey(plugin, "progression/" + PROG_TERMINAL), new ItemObject().setItem(Material.DARK_PRISMARINE),
	                new TextComponent("終端空間"), new TextComponent("進入無底的記憶深淵"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.TASK)
	                .setHidden(true)
	                .setAnnounce(false)
	                .makeChild(village.getId());
	        terminal.activate(false);
	        
	        Advancement classroom = new Advancement(new NamespacedKey(plugin, "progression/" + PROG_NANA_CLASSROOM), new ItemObject().setItem(Material.WRITTEN_BOOK),
	                new TextComponent("Biggy的生日禮物"), new TextComponent("那個熟悉的課室..."))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.TASK)
	                .setHidden(true)
	                .setAnnounce(false)
	                .makeChild(terminal.getId());
	        classroom.activate(false);
			
	        Advancement oldbase = new Advancement(new NamespacedKey(plugin, "progression/" + PROG_NANA_OLDBASE), new ItemObject().setItem(Material.CONDUIT),
	                new TextComponent("故地重遊2"), new TextComponent("SK第四季"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.TASK)
	                .setHidden(true)
	                .setAnnounce(false)
	                .makeChild(classroom.getId());
	        oldbase.activate(false);
	        
	        Advancement mushroom = new Advancement(new NamespacedKey(plugin, "progression/" + PROG_NANA_MUSHROOM), new ItemObject().setItem(Material.RED_MUSHROOM),
	                new TextComponent("失落的蘑菇島"), new TextComponent("那個沒有人知道這個地方"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.TASK)
	                .setHidden(true)
	                .setAnnounce(false)
	                .makeChild(oldbase.getId());
	        mushroom.activate(false);
	        
	        Advancement newbase = new Advancement(new NamespacedKey(plugin, "progression/" + PROG_NANA_NEWBASE), new ItemObject().setItem(Material.WARPED_PLANKS),
	                new TextComponent("詭異之地"), new TextComponent("探索被改變的未來"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.TASK)
	                .setHidden(true)
	                .setAnnounce(false)
	                .makeChild(mushroom.getId());
	        newbase.activate(false);
	        
	        Advancement s3base = new Advancement(new NamespacedKey(plugin, "progression/" + PROG_NANA_S3BASE), new ItemObject().setItem(Material.OAK_PLANKS),
	                new TextComponent("故地重遊3"), new TextComponent("這要由2017年開始說起..."))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.TASK)
	                .setHidden(true)
	                .setAnnounce(false)
	                .makeChild(newbase.getId());
	        s3base.activate(false);
	        
	        Advancement complete = new Advancement(new NamespacedKey(plugin, "progression/" + PROG_COMPLETE), new ItemObject().setItem(Material.HONEY_BOTTLE).setNbt("{Enchantments: [{id:\"minecraft:luck_of_the_sea\", lvl:1s}]}"),
	                new TextComponent("完成"), new TextComponent(ChatColor.YELLOW + "這個就是小女孩的旅程!"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.CHALLENGE)
	                .setHidden(true)
	                .setAnnounce(false)
	                .makeChild(s3base.getId());
	        complete.activate(false);
	        
	        //Achievements
	        
	        Advancement achroot = factory.getRoot("achievements/root", "成就", "", Material.GOLD_BLOCK, "block/cyan_wool");
	        
	        Advancement imposter = new Advancement(new NamespacedKey(plugin, "achievements/" + ACHIEVEMENT_IMPOSTER), new ItemObject().setItem(Material.IRON_SWORD),
	                new TextComponent(ChatColor.RED + "Imposter"), new TextComponent(ChatColor.YELLOW + "掉進虛空，被發現自己是Imposter"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.CHALLENGE)
	                .setHidden(false)
	                .setAnnounce(true)
	                .makeChild(achroot.getId());
	        imposter.activate(false);
	        
	        Advancement saturn = new Advancement(new NamespacedKey(plugin, "achievements/" + ACHIEVEMENT_SATURN), new ItemObject().setItem(Material.HONEY_BLOCK),
	                new TextComponent(ChatColor.GOLD + "Saturn"), new TextComponent("閱讀家裡海報上的標準銀河字母"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.GOAL)
	                .setHidden(false)
	                .setAnnounce(true)
	                .makeChild(imposter.getId());
	        saturn.activate(false);
	        
	        Advancement bubble_tea = new Advancement(new NamespacedKey(plugin, "achievements/" + ACHIEVEMENT_BUBBLETEA), new ItemObject().setItem(plugin.specialitems.get(SpecialItems.BUBBLE_TEA).getType()).setNbt(NBTUtils.getNBTCompound(plugin.specialitems.get(SpecialItems.BUBBLE_TEA), "tag").toString()),
	                new TextComponent(ChatColor.WHITE + "珍珠奶茶"), new TextComponent("來喝一杯珍珠奶茶！"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.GOAL)
	                .setHidden(false)
	                .setAnnounce(true)
	                .makeChild(saturn.getId());
	        bubble_tea.activate(false);
	        
	        Advancement cloudland = new Advancement(new NamespacedKey(plugin, "achievements/" + ACHIEVEMENT_CLOUDLAND), new ItemObject().setItem(plugin.specialitems.get(SpecialItems.CLOUDLAND).getType()).setNbt(NBTUtils.getNBTCompound(plugin.specialitems.get(SpecialItems.CLOUDLAND), "tag").toString()),
	                new TextComponent(ChatColor.WHITE + "消失的雲島"), new TextComponent("都說了別重疊聲軌"))
	                .addTrigger("impossible", new ImpossibleTrigger())
	                .setFrame(Advancement.Frame.GOAL)
	                .setHidden(false)
	                .setAnnounce(true)
	                .makeChild(bubble_tea.getId());
	        cloudland.activate(false);
	        
	        Advancement minesweeper_won = new Advancement(new NamespacedKey(plugin, "achievements/" + ACHIEVEMENT_MINEWON), new ItemObject().setItem(plugin.specialitems.get(SpecialItems.MINEWON).getType()).setNbt(NBTUtils.getNBTCompound(plugin.specialitems.get(SpecialItems.MINEWON), "tag").toString()),
	                new TextComponent(plugin.specialitems.get(SpecialItems.MINEWON).getItemMeta().getDisplayName()), new TextComponent("完成掃雷遊戲"))
	                .addTrigger("minesweeper_won", new InventoryChangedTrigger().addItem(new ItemObject().setItem(plugin.specialitems.get(SpecialItems.MINEWON).getType()).setNbt(NBTUtils.getNBTCompound(plugin.specialitems.get(SpecialItems.MINEWON), "tag").toString())))
	                .setFrame(Advancement.Frame.CHALLENGE)
	                .setHidden(false)
	                .setAnnounce(true)
	                .makeChild(cloudland.getId());
	        minesweeper_won.activate(false);
	        
	        Advancement minesweeper_lost = new Advancement(new NamespacedKey(plugin, "achievements/" + ACHIEVEMENT_MINELOST), new ItemObject().setItem(plugin.specialitems.get(SpecialItems.MINELOST).getType()).setNbt(NBTUtils.getNBTCompound(plugin.specialitems.get(SpecialItems.MINELOST), "tag").toString()),
	                new TextComponent(plugin.specialitems.get(SpecialItems.MINELOST).getItemMeta().getDisplayName()), new TextComponent("完成「踩地雷」遊戲"))
	                .addTrigger("minesweeper_lost", new InventoryChangedTrigger().addItem(new ItemObject().setItem(plugin.specialitems.get(SpecialItems.MINELOST).getType()).setNbt(NBTUtils.getNBTCompound(plugin.specialitems.get(SpecialItems.MINELOST), "tag").toString())))
	                .setFrame(Advancement.Frame.GOAL)
	                .setHidden(true)
	                .setAnnounce(true)
	                .makeChild(cloudland.getId());
	        minesweeper_lost.activate(false);
			
			Bukkit.reloadData();
		}, 3);
	}
	
	public boolean checkAchievement(Player player, String tagId) {
		return player.getAdvancementProgress(Bukkit.getAdvancement(new NamespacedKey(plugin, "achievements/" + tagId))).isDone();
	}
	
	public void sendAchievement(Player player, String tagId) {
		Bukkit.getScheduler().runTask(NarMapPlugin.plugin, () -> {
			if (!player.getAdvancementProgress(Bukkit.getAdvancement(new NamespacedKey(plugin, "achievements/" + tagId))).isDone()) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement grant " + player.getName() + " only " + plugin.getName().toLowerCase() + ":achievements/" + tagId);
			}
		});
	}
	
	public boolean checkProgression(Player player, String tagId) {
		return player.getAdvancementProgress(Bukkit.getAdvancement(new NamespacedKey(plugin, "progression/" + tagId))).isDone();
	}
	
	public void sendProgression(Player player, String tagId) {
		Bukkit.getScheduler().runTask(NarMapPlugin.plugin, () -> {
			if (!player.getAdvancementProgress(Bukkit.getAdvancement(new NamespacedKey(plugin, "progression/" + tagId))).isDone()) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement grant " + player.getName() + " only " + plugin.getName().toLowerCase() + ":progression/" + tagId);
			}
		});
	}
	
	public void sendTip(Player player, String tagId) {
		Bukkit.getScheduler().runTask(NarMapPlugin.plugin, () -> {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement grant " + player.getName() + " only " + plugin.getName().toLowerCase() + ":tip/" + tagId);
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + player.getName() + " only " + plugin.getName().toLowerCase() + ":tip/" + tagId);
			}, 5);
		});
	}
	
	public void clearProgression(Player player) {
		Bukkit.getScheduler().runTask(NarMapPlugin.plugin, () -> {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + player.getName() + " until " + plugin.getName().toLowerCase() + ":progression/" + PROG_COMPLETE);
		});
	}

}
