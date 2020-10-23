package com.loohp.nar.Story;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.loohp.nar.NarMapPlugin;
import com.loohp.nar.Data.NSound;
import com.loohp.nar.Utils.AdvancementManager;
import com.loohp.nar.Utils.RayTrace;
import com.loohp.nar.Utils.RayTrace.TraceResult;

import net.md_5.bungee.api.ChatColor;
import ru.beykerykt.lightapi.LightAPI;
import ru.beykerykt.lightapi.LightType;
import ru.beykerykt.lightapi.chunks.ChunkInfo;

public class StartMenu implements Listener {
	
	public static final double CHAR_HEIGHT = 0.12;
	public static final double CHAR_WIDTH = 0.1;
	
	public static final String LOCKED = "尚未解鎖";
	
	public static World world = NarMapPlugin.plugin.mainWorld;
	public static BoundingBox area = BoundingBox.of(new Location(world, -14, 20, 7).getBlock(), new Location(world, 6, 0, 27).getBlock());
	public static Set<UUID> players = new HashSet<>();
	public static BiMap<Option, BoundingBox> optionsBox = HashBiMap.create();
	public static Map<UUID, Option> selectedOption = new HashMap<>();
	
	public static Map<Option, Boolean> waypointStatus = new HashMap<>();
	public static Map<Option, Boolean> stageStatus = new HashMap<>();
	
	public enum Option {
		START_GAME("開始遊戲", UUID.fromString("c1dff76f-d22a-42cf-9d1c-7877f0015cb2"), 0),
		
		WAYPOINT_STREET("南瓜路", UUID.fromString("44b5ac15-a36a-422b-9b32-2c9bfa1c1272"), 0),
		WAYPOINT_VILLAGE("娜的村莊", UUID.fromString("de9b7236-e019-45bc-9192-c14dacc578f2"), 0),
		WAYPOINT_CLASSROOM("教室", UUID.fromString("5c2fb76a-90d1-40b8-965f-9237c08b9b9d"), 0),
		WAYPOINT_OLDBASE("娜2017年末的家園", UUID.fromString("e5252012-5764-4741-ba89-ed054d1f787a"), 0),
		WAYPOINT_MUSHROOM("被遺忘的蘑菇地", UUID.fromString("535da78c-9c76-4fa1-ba7c-d1da95f6c322"), 0),
		WAYPOINT_NEWBASE("幽靈小鎮", UUID.fromString("46d32d99-d005-41fb-96fc-7251d6b44f12"), 0),
		WAYPOINT_S3BASE("娜2017年夏季的家園", UUID.fromString("c0fe7b00-3157-4a9f-b27f-3de83488e555"), 0),
		WAYPOINT_TERMINAL("終端空間", UUID.fromString("55c6c265-54cf-44b0-b199-e0a15613ca91"), 0),
		WAYPOINT_ENDING("片尾", UUID.fromString("86e9aaa9-e90e-4148-a9f4-27c977e4a075"), 0),
		
		STAGE_BEGIN("朋友們的挑戰", UUID.fromString("7d21becd-f78e-4fe3-9916-9c630ec48b32"), 1),
		STAGE_NIGHTMARE("娜的惡夢", UUID.fromString("f07afd4d-cf19-4d68-9cd2-6ef7570a1244"), 1),
		STAGE_REVISIT("未來與過去", UUID.fromString("aaede025-d312-4a9e-ae32-e16b3be00ec7"), 1);
		
		private String title;
		private UUID uuid;
		private int axis;
		
		Option(String title, UUID uuid, int axis) {
			this.title = title;
			this.uuid = uuid;
			this.axis = axis;
		}
		
		public String getTitle() {
			return title;
		}
		
		public ArmorStand getEntity() {
			return (ArmorStand) Bukkit.getEntity(uuid);
		}
		
		public UUID getUniqueId() {
			return uuid;
		}

		public int getAxis() {
			return axis;
		}
	}
	
	public static void startup() {
		Bukkit.getPluginManager().registerEvents(new StartMenu(), NarMapPlugin.plugin);
		
		world.setChunkForceLoaded(0, 0, true);
		world.setChunkForceLoaded(0, 1, true);
		world.setChunkForceLoaded(-1, 1, true);
		world.setChunkForceLoaded(-1, 0, true);
		
		for (Option option : Option.values()) {
			Location loc = option.getEntity().getLocation().add(0, 0.4, 0);
			BoundingBox box = BoundingBox.of(loc, loc).expand(CHAR_WIDTH * (option.getAxis() == 0 ? option.getTitle().length() : 1), CHAR_HEIGHT, CHAR_WIDTH * (option.getAxis() == 1 ? option.getTitle().length() : 1));
			optionsBox.put(option, box);
			if (option.name().startsWith("WAYPOINT")) {
				waypointStatus.put(option, false);
			} else if (option.name().startsWith("STAGE")) {
				stageStatus.put(option, false);
			}
		}
		
		Bukkit.getScheduler().runTaskTimer(NarMapPlugin.plugin, () -> {
			Collection<Entity> playersInMenu = world.getNearbyEntities(area, each -> each instanceof Player);
			if (playersInMenu.isEmpty()) {
				for (Option option : Option.values()) {
					option.getEntity().setCustomNameVisible(false);
				}
			} else {
				for (Option option : Option.values()) {
					option.getEntity().setCustomNameVisible(true);
				}
			}
			List<Player> otherPlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
			otherPlayers.removeAll(playersInMenu);
			for (Entity entity : playersInMenu) {
				Player player = (Player) entity;
				BoundingBox[] boxes = optionsBox.values().toArray(new BoundingBox[0]);
				TraceResult looking = RayTrace.getFirstIntersectedBoundingBoxAndLocationLookingAt((LivingEntity) player, 20, boxes);
				int index = looking.getIndex();
				if (index >= 0) {
					Option option = optionsBox.inverse().get(boxes[index]);
					if (option != null) {
						Option select = selectedOption.get(player.getUniqueId());
						if (select == null || (select != null && !option.equals(select))) {
							selectedOption.put(player.getUniqueId(), option);
							world.playSound(option.getEntity().getLocation(), NSound.SFX_SELECT, 3, 1);
						}
						world.spawnParticle(Particle.REDSTONE, looking.getLocation(), 10, new DustOptions(Color.YELLOW, 1));
					} else {
						selectedOption.remove(player.getUniqueId());
					}
				} else {
					selectedOption.remove(player.getUniqueId());
				}
			}
			for (Player player: otherPlayers) {
				selectedOption.remove(player.getUniqueId());
			}
			
			Collection<Option> selected = selectedOption.values();
			List<Option> unselected = new ArrayList<>(Arrays.asList(Option.values()));
			unselected.removeAll(selected);
			for (Option option : selected) {
				if (waypointStatus.getOrDefault(option, true) && stageStatus.getOrDefault(option, true)) {
					option.getEntity().setCustomName(ChatColor.YELLOW + "" + ChatColor.BOLD + option.getTitle());
				} else {
					option.getEntity().setCustomName(ChatColor.GRAY + "" + ChatColor.BOLD + LOCKED);
				}
			}
			for (Option option : unselected) {
				if (waypointStatus.getOrDefault(option, true) && stageStatus.getOrDefault(option, true)) {
					option.getEntity().setCustomName(ChatColor.WHITE + option.getTitle());
				} else {
					option.getEntity().setCustomName(ChatColor.GRAY + LOCKED);
				}
			}
			players = playersInMenu.parallelStream().map(each -> each.getUniqueId()).collect(Collectors.toSet());
		}, 0, 1);
		
		Location speedStand = new Location(world, -0.5, 11.5, 17.5);
		Bukkit.getScheduler().runTaskTimer(NarMapPlugin.plugin, () -> {
			List<Location> locs = new LinkedList<>();
			for (Option option : Option.values()) {
				Location location = option.getEntity().getLocation();
				LightAPI.createLight(location, LightType.BLOCK, 15, false);			
				locs.add(location);
			}
			LightAPI.createLight(speedStand, LightType.BLOCK, 15, false);			
			locs.add(speedStand);
			
			Set<ChunkInfo> infos = new HashSet<>();
			for (Location location : locs) {
				infos.addAll(LightAPI.collectChunks(location, LightType.BLOCK, 15));
			}
			
			for (ChunkInfo info : infos) {
				LightAPI.updateChunk(info, LightType.BLOCK);
			}
		}, 0, 100);
		
		Bukkit.getScheduler().runTaskTimer(NarMapPlugin.plugin, () -> {
			if (NarMapPlugin.plugin.mainCharater.isPresent() && Bukkit.getOnlinePlayers().contains(NarMapPlugin.plugin.mainCharater.get())) {
				Player player = NarMapPlugin.plugin.mainCharater.get();
				waypointStatus.put(Option.WAYPOINT_STREET, NarMapPlugin.plugin.advan.checkProgression(player, AdvancementManager.PROG_BEGIN));
				waypointStatus.put(Option.WAYPOINT_VILLAGE, NarMapPlugin.plugin.advan.checkProgression(player, AdvancementManager.PROG_NANA_VILLAGE));
				waypointStatus.put(Option.WAYPOINT_CLASSROOM, NarMapPlugin.plugin.advan.checkProgression(player, AdvancementManager.PROG_NANA_CLASSROOM));
				waypointStatus.put(Option.WAYPOINT_MUSHROOM, NarMapPlugin.plugin.advan.checkProgression(player, AdvancementManager.PROG_NANA_MUSHROOM));
				waypointStatus.put(Option.WAYPOINT_NEWBASE, NarMapPlugin.plugin.advan.checkProgression(player, AdvancementManager.PROG_NANA_NEWBASE));
				waypointStatus.put(Option.WAYPOINT_OLDBASE, NarMapPlugin.plugin.advan.checkProgression(player, AdvancementManager.PROG_NANA_OLDBASE));
				waypointStatus.put(Option.WAYPOINT_S3BASE, NarMapPlugin.plugin.advan.checkProgression(player, AdvancementManager.PROG_NANA_S3BASE));
				waypointStatus.put(Option.WAYPOINT_TERMINAL, NarMapPlugin.plugin.advan.checkProgression(player, AdvancementManager.PROG_TERMINAL));
				waypointStatus.put(Option.WAYPOINT_ENDING, NarMapPlugin.plugin.advan.checkProgression(player, AdvancementManager.PROG_COMPLETE));
				
				stageStatus.put(Option.STAGE_BEGIN, NarMapPlugin.plugin.advan.checkProgression(player, AdvancementManager.PROG_BEGIN));
				stageStatus.put(Option.STAGE_NIGHTMARE, NarMapPlugin.plugin.advan.checkProgression(player, AdvancementManager.PROG_NANA_VILLAGE));
				stageStatus.put(Option.STAGE_REVISIT, NarMapPlugin.plugin.advan.checkProgression(player, AdvancementManager.PROG_TERMINAL));
			} else {
				for (Entry<Option, Boolean> entry : waypointStatus.entrySet()) {
					entry.setValue(true);
				}
				for (Entry<Option, Boolean> entry : stageStatus.entrySet()) {
					entry.setValue(true);
				}
			}
		}, 0, 20);
		
		Entity speed = Bukkit.getEntity(UUID.fromString("5890a515-5fef-4406-9dbb-3b3167060c60"));
		DecimalFormat format = new DecimalFormat("0.##");
		Bukkit.getScheduler().runTaskTimer(NarMapPlugin.plugin, () -> {
			speed.setCustomName(ChatColor.AQUA + "現時遊戲對話速度設置為 " + format.format(Math.pow(NarMapPlugin.plugin.chatDelayMultiplier, -1)) + " 倍");
		}, 0, 10);
	}
	
	@EventHandler
	public void onAnimation(PlayerAnimationEvent event) {
		Player player = event.getPlayer();
		if (players.contains(player.getUniqueId())) {
			Option option = selectedOption.get(player.getUniqueId());
			if (option != null) {
				if (waypointStatus.getOrDefault(option, true) && stageStatus.getOrDefault(option, true)) {					
					switch (option) {
					case WAYPOINT_CLASSROOM:
						player.teleport(new Location(world, 4187.5, 24, 14181.5, 90, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
						break;
					case WAYPOINT_ENDING:
						player.teleport(new Location(world, 10002.5, 100, 10002.5, 180, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
						break;
					case WAYPOINT_MUSHROOM:
						player.teleport(new Location(world, 2957, 63, 1240.5, -90, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
						break;
					case WAYPOINT_NEWBASE:
						player.teleport(new Location(world, 2536.5, 64, 3875.5));
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
						break;
					case WAYPOINT_OLDBASE:
						player.teleport(new Location(world, -2006, 64, -1998, 176, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
						break;
					case WAYPOINT_S3BASE:
						player.teleport(new Location(world, 2159, 38, -523));
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
						break;
					case WAYPOINT_STREET:
						player.teleport(new Location(world, 17, 75.5626, -87, 0, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
						break;
					case WAYPOINT_TERMINAL:
						player.teleport(new Location(world, -907.5, 68.0, -999.5, 0, 90));
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
						break;
					case WAYPOINT_VILLAGE:
						player.teleport(new Location(world, -3255.5, 67.5626, 561, 0, 90));
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
						break;
					case START_GAME:
					case STAGE_BEGIN:
					case STAGE_NIGHTMARE:
					case STAGE_REVISIT:
						Bukkit.getScheduler().runTaskAsynchronously(NarMapPlugin.plugin, () -> {
							try {
								if (!NarMapPlugin.plugin.startStage(option).get()) {
									player.sendMessage(ChatColor.RED + "遊戲已在進行中");
								}
							} catch (InterruptedException | ExecutionException e) {
								e.printStackTrace();
							}
						});
						break;
					}					
					//player.sendMessage(option.getTitle());
				}
			}
		}
	}

}
