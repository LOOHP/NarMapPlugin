package com.loohp.nar.Story;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftItemFrame;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.loohp.nar.NarMapPlugin;
import com.loohp.nar.Events.RegionsChangedEvent;
import com.loohp.nar.Utils.AdvancementManager;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R2.EntityItemFrame;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

public class Ending implements Listener {
	
	public static final String SCOREBOARD_TAG = "achievement_item_";
	public static final String OBTAINED = ChatColor.GREEN + "已獲得";
	public static final String UNOBTAINED = ChatColor.RED + "" + ChatColor.BOLD + "未獲得";
	public static final String WG_REGION = "ending";
	
	private static NarMapPlugin plugin = NarMapPlugin.plugin;
	
	public static void startup() {
		plugin.mainWorld.setChunkForceLoaded(626, 626, true);
		Bukkit.getPluginManager().registerEvents(new Ending(), plugin);
		
		List<Location> lightInvis = new ArrayList<>();
		lightInvis.add(new Location(plugin.mainWorld, 10002, 100, 10008));
		lightInvis.add(new Location(plugin.mainWorld, 10013, 100, 10019));
		lightInvis.add(new Location(plugin.mainWorld, 10002, 100, 10030));
		lightInvis.add(new Location(plugin.mainWorld, 9991, 100, 10019));
		Bukkit.getScheduler().runTaskTimer(NarMapPlugin.plugin, () -> {
			List<Location> locs = new LinkedList<>();
			for (Location location : lightInvis) {
				LightAPI.createLight(location.clone(), LightType.BLOCK, 15, false);			
				locs.add(location.clone());
			}
			
			Set<ChunkInfo> infos = new HashSet<>();
			for (Location location : locs) {
				infos.addAll(LightAPI.collectChunks(location, LightType.BLOCK, 15));
			}
			
			for (ChunkInfo info : infos) {
				LightAPI.updateChunk(info, LightType.BLOCK);
			}
		}, 0, 100);
		
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			ItemFrame frame = (ItemFrame) Bukkit.getEntity(UUID.fromString("3edd0ac1-f21f-4445-b483-51772c4997aa"));
			EntityItemFrame nmsFrame = ((CraftItemFrame) frame).getHandle();
			Bukkit.getScheduler().runTaskTimer(NarMapPlugin.plugin, () -> {
				nmsFrame.setRotation(nmsFrame.getRotation() >= 7 ? 0 : nmsFrame.getRotation() + 1);
			}, 0, 20);
			
			ArmorStand a = (ArmorStand) Bukkit.getEntity(UUID.fromString("31b6832a-27ae-4a1c-b286-78becc7e1fcc"));
			ArmorStand b = (ArmorStand) Bukkit.getEntity(UUID.fromString("8c094247-df00-4b66-971c-3550b27a272d"));
			ArmorStand c = (ArmorStand) Bukkit.getEntity(UUID.fromString("0cee3d72-26ea-41e5-bf96-8fa7be2a6737"));
			ArmorStand d = (ArmorStand) Bukkit.getEntity(UUID.fromString("d060dc44-3b8f-480c-bf10-4a0845ca92e6"));
			ArmorStand e = (ArmorStand) Bukkit.getEntity(UUID.fromString("17b98f38-cd5c-4470-9ef3-1274666078e1"));
			
			Block signA = plugin.mainWorld.getBlockAt(10020, 100, 10017);
			Block signB = plugin.mainWorld.getBlockAt(10024, 100, 10019);
			Block signC = plugin.mainWorld.getBlockAt(10023, 100, 10021);
			Block signD = plugin.mainWorld.getBlockAt(10020, 100, 10021);
			Block signE = plugin.mainWorld.getBlockAt(10023, 100, 10017);
			
			ItemStack itemImposter = new ItemStack(Material.IRON_SWORD);
			itemImposter.addEnchantment(Enchantment.DAMAGE_ALL, 1);
			
			ItemStack itemSaturn = new ItemStack(Material.HONEY_BLOCK);
			
			Bukkit.getScheduler().runTaskTimer(plugin, () -> {
				try {
					if (!plugin.mainCharater.isPresent()) {
						return;
					}
					Player player = plugin.mainCharater.get();
					World world = plugin.mainWorld;
					List<Entity> entities = a.getNearbyEntities(0.5, 0.5, 0.5);
					if (plugin.advan.checkAchievement(player, AdvancementManager.ACHIEVEMENT_IMPOSTER)) {
						if (entities.stream().noneMatch(each -> each.getScoreboardTags().contains(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_IMPOSTER))) {
							Location placement = a.getLocation().clone().add(0, 0.2, 0);
							Item item = world.dropItem(placement, itemImposter.clone());
							item.setVelocity(new Vector(0, 0, 0));
							item.setGravity(false);
							item.setPickupDelay(32767);
							item.addScoreboardTag(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_IMPOSTER);
							item.teleport(placement);
						}
						Sign sign = (Sign) signA.getState();
						sign.setLine(1, ChatColor.RED + "Imposter");
						sign.setLine(2, OBTAINED);
						sign.update();
					} else {
						entities.forEach(each -> {
							if (each.getScoreboardTags().contains(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_IMPOSTER)) {
								each.remove();
							}
						});
						Sign sign = (Sign) signA.getState();
						sign.setLine(1, ChatColor.RED + "Imposter");
						sign.setLine(2, UNOBTAINED);
						sign.update();
					}
					entities = b.getNearbyEntities(0.5, 0.5, 0.5);
					if (plugin.advan.checkAchievement(player, AdvancementManager.ACHIEVEMENT_SATURN)) {
						if (entities.stream().noneMatch(each -> each.getScoreboardTags().contains(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_SATURN))) {
							Location placement = b.getLocation().clone().add(0, 0.2, 0);
							Item item = world.dropItem(placement, itemSaturn.clone());
							item.setVelocity(new Vector(0, 0, 0));
							item.setGravity(false);
							item.setPickupDelay(32767);
							item.addScoreboardTag(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_SATURN);
							item.teleport(placement);
						}
						Sign sign = (Sign) signB.getState();
						sign.setLine(1, ChatColor.GOLD + "Saturn");
						sign.setLine(2, OBTAINED);
						sign.update();
					} else {
						entities.forEach(each -> {
							if (each.getScoreboardTags().contains(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_SATURN)) {
								each.remove();
							}
						});
						Sign sign = (Sign) signB.getState();
						sign.setLine(1, ChatColor.GOLD + "Saturn");
						sign.setLine(2, UNOBTAINED);
						sign.update();
					}
					entities = c.getNearbyEntities(0.5, 0.5, 0.5);
					if (plugin.advan.checkAchievement(player, AdvancementManager.ACHIEVEMENT_BUBBLETEA)) {
						if (entities.stream().noneMatch(each -> each.getScoreboardTags().contains(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_BUBBLETEA))) {
							Location placement = c.getLocation().clone().add(0, 0.2, 0);
							Item item = world.dropItem(placement, plugin.specialitems.get(SpecialItems.BUBBLE_TEA));
							item.setVelocity(new Vector(0, 0, 0));
							item.setGravity(false);
							item.setPickupDelay(32767);
							item.addScoreboardTag(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_BUBBLETEA);
							item.teleport(placement);
						}
						Sign sign = (Sign) signC.getState();
						sign.setLine(1, ChatColor.WHITE + "珍珠奶茶");
						sign.setLine(2, OBTAINED);
						sign.update();
					} else {
						entities.forEach(each -> {
							if (each.getScoreboardTags().contains(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_BUBBLETEA)) {
								each.remove();
							}
						});
						Sign sign = (Sign) signC.getState();
						sign.setLine(1, ChatColor.WHITE + "珍珠奶茶");
						sign.setLine(2, UNOBTAINED);
						sign.update();
					}
					entities = d.getNearbyEntities(0.5, 0.5, 0.5);
					if (plugin.advan.checkAchievement(player, AdvancementManager.ACHIEVEMENT_MINEWON)) {
						if (entities.stream().noneMatch(each -> each.getScoreboardTags().contains(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_MINEWON))) {
							Location placement = d.getLocation().clone().add(0, 0.2, 0);
							Item item = world.dropItem(placement, plugin.specialitems.get(SpecialItems.MINEWON));
							item.setVelocity(new Vector(0, 0, 0));
							item.setGravity(false);
							item.setPickupDelay(32767);
							item.addScoreboardTag(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_MINEWON);
							item.teleport(placement);
						}
						Sign sign = (Sign) signD.getState();
						sign.setLine(1, ChatColor.RED + "完成掃雷遊戲");
						sign.setLine(2, OBTAINED);
						sign.update();
					} else if (plugin.advan.checkAchievement(player, AdvancementManager.ACHIEVEMENT_MINELOST)) {
						if (entities.stream().noneMatch(each -> each.getScoreboardTags().contains(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_MINELOST))) {
							Location placement = d.getLocation().clone().add(0, 0.2, 0);
							Item item = world.dropItem(placement, plugin.specialitems.get(SpecialItems.MINELOST));
							item.setVelocity(new Vector(0, 0, 0));
							item.setGravity(false);
							item.setPickupDelay(32767);
							item.addScoreboardTag(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_MINELOST);
							item.teleport(placement);
						}
						Sign sign = (Sign) signD.getState();
						sign.setLine(1, ChatColor.GRAY + "完成「踩地雷」遊戲");
						sign.setLine(2, OBTAINED);
						sign.update();
					} else {
						entities.forEach(each -> {
							if (each.getScoreboardTags().contains(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_MINEWON) || each.getScoreboardTags().contains(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_MINELOST)) {
								each.remove();
							}
						});
						Sign sign = (Sign) signD.getState();
						sign.setLine(1, ChatColor.RED + "完成掃雷遊戲");
						sign.setLine(2, UNOBTAINED);
						sign.update();
					}
					entities = e.getNearbyEntities(0.5, 0.5, 0.5);
					if (plugin.advan.checkAchievement(player, AdvancementManager.ACHIEVEMENT_CLOUDLAND)) {
						if (entities.stream().noneMatch(each -> each.getScoreboardTags().contains(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_CLOUDLAND))) {
							Location placement = e.getLocation().clone().add(0, 0.2, 0);
							Item item = world.dropItem(placement, plugin.specialitems.get(SpecialItems.CLOUDLAND));
							item.setVelocity(new Vector(0, 0, 0));
							item.setGravity(false);
							item.setPickupDelay(32767);
							item.addScoreboardTag(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_CLOUDLAND);
							item.teleport(placement);
						}
						Sign sign = (Sign) signE.getState();
						sign.setLine(1, ChatColor.WHITE + "消失的雲島");
						sign.setLine(2, OBTAINED);
						sign.update();
					} else {
						entities.forEach(each -> {
							if (each.getScoreboardTags().contains(SCOREBOARD_TAG + AdvancementManager.ACHIEVEMENT_CLOUDLAND)) {
								each.remove();
							}
						});
						Sign sign = (Sign) signE.getState();
						sign.setLine(1, ChatColor.WHITE + "消失的雲島");
						sign.setLine(2, UNOBTAINED);
						sign.update();
					}
				} catch (Exception er) {
					er.printStackTrace();
				}
			}, 0, 20);
		}, 10);
	}
	
	@EventHandler
	public void onRegionChange(RegionsChangedEvent event) {
		Player player = event.getPlayer();
		
		Set<String> current = event.getCurrentRegionsNames();
		Set<String> previous = event.getPreviousRegionsNames();
		
		if (current.contains(WG_REGION) && !previous.contains(WG_REGION)) {
			player.setPlayerTime(18000, false);
		} else if (!current.contains(WG_REGION) && previous.contains(WG_REGION)) {
			player.resetPlayerTime();
		}
	}

}
