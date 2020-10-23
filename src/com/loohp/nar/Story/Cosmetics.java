package com.loohp.nar.Story;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import com.denizenscript.denizen.npc.traits.SneakingTrait;
import com.loohp.nar.NarMapPlugin;
import com.loohp.nar.Data.NSound;
import com.loohp.nar.Utils.AdvancementManager;
import com.loohp.nar.Utils.ChatUtils;
import com.loohp.nar.Utils.ChunkUtils;
import com.loohp.nar.Utils.NBTUtils;
import com.loohp.nar.Utils.RayTrace;
import com.mojang.authlib.properties.Property;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.md_5.bungee.api.ChatColor;

public class Cosmetics implements Listener {
	
	private NarMapPlugin plugin;
	
	private NPC activeNPC = null;
	private Set<Integer> glowing = new HashSet<>();
	
	private Set<Location> closetDoors = new HashSet<>();
	private Location closet;
	protected final Map<UUID, ClosetPlayerData> openedCloset = new ConcurrentHashMap<>();
	private ItemStack leaveClosetItem;
	
	private BoundingBox closetBox;
	private BoundingBox saturnBox;
	
	private Map<NPC, NPC> mapping = new HashMap<>();
	private NPC mirrorPlayer;
	private String usingTexture = null;
	private AtomicBoolean closetFlag = new AtomicBoolean(false);
	
	public Cosmetics(NarMapPlugin plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		closetDoors.add(new Location(plugin.mainWorld, 17, 75, -83));
		closetDoors.add(new Location(plugin.mainWorld, 17, 76, -83));
		closetDoors.add(new Location(plugin.mainWorld, 16, 75, -83));
		closetDoors.add(new Location(plugin.mainWorld, 16, 76, -83));
		
		closet = new Location(plugin.mainWorld, 998.5, 14.5, 988.5, 90, 0);
		
		ChunkUtils.setChunksAroundLoaded(closet.getChunk(), true);
		
		leaveClosetItem = new ItemStack(Material.BARRIER);
		ItemMeta meta = leaveClosetItem.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "關閉衣櫃");
		leaveClosetItem.setItemMeta(meta);
		leaveClosetItem = NBTUtils.set(leaveClosetItem, 1, "leaveClosetItem");
		
		clothesSelector();
		saturnIndicator();
		Bukkit.getScheduler().runTaskLater(plugin, () -> closetMirror(), 100);
		
		saturnBox = BoundingBox.of(new Location(plugin.mainWorld, 14.99, 71.35, -87.00), new Location(plugin.mainWorld, 16.02, 71.00, -86.84));
		closetBox = BoundingBox.of(plugin.mainWorld.getBlockAt(1002, 13, 1001), plugin.mainWorld.getBlockAt(990, 21, 975));
	}
	
	private void setClosetNPC() {
		mapping.put(CitizensAPI.getNPCRegistry().getById(3), CitizensAPI.getNPCRegistry().getById(32));
		mapping.put(CitizensAPI.getNPCRegistry().getById(7), CitizensAPI.getNPCRegistry().getById(31));
		mapping.put(CitizensAPI.getNPCRegistry().getById(8), CitizensAPI.getNPCRegistry().getById(30));
		mapping.put(CitizensAPI.getNPCRegistry().getById(9), CitizensAPI.getNPCRegistry().getById(29));
		mapping.put(CitizensAPI.getNPCRegistry().getById(10), CitizensAPI.getNPCRegistry().getById(28));
		mapping.put(CitizensAPI.getNPCRegistry().getById(11), CitizensAPI.getNPCRegistry().getById(27));
		mapping.put(CitizensAPI.getNPCRegistry().getById(12), CitizensAPI.getNPCRegistry().getById(26));
		
		mirrorPlayer = CitizensAPI.getNPCRegistry().getById(33);
	}
	
	@SuppressWarnings("deprecation")
	private void closetMirror() {
		setClosetNPC();
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			if (Bukkit.getOnlinePlayers().stream().noneMatch(each -> each.getWorld().equals(plugin.mainWorld) && closetBox.contains(each.getBoundingBox()))) {
				return;
			}
			if (closetFlag.get()) {
				return;
			}
			Bukkit.getScheduler().runTask(plugin, () -> {
				try {
					for (Entry<NPC, NPC> entry : mapping.entrySet()) {
						NPC npc = entry.getKey();
						NPC npc_mirror = entry.getValue();
						if (!npc_mirror.isSpawned()) {
							npc_mirror.spawn(npc_mirror.getStoredLocation());
						}
						Location mirror = npc_mirror.getEntity().getLocation();
						if (!npc.isSpawned()) {
							npc_mirror.spawn(npc_mirror.getStoredLocation());
						}
						Location real = npc.getEntity().getLocation();
						mirror.setYaw(270 + (90 - real.getYaw()));
						mirror.setPitch(real.getPitch());
						npc_mirror.teleport(mirror, TeleportCause.PLUGIN);
						npc_mirror.data().set(NPC.GLOWING_METADATA, npc.data().get(NPC.GLOWING_METADATA));
					}
					if (!plugin.mainCharater.isPresent()) {
						return;
					}
					Player player = plugin.mainCharater.get();
					if (player.getWorld().equals(plugin.mainWorld) && closetBox.contains(player.getBoundingBox())) {
						Property property = ((CraftPlayer) player).getProfile().getProperties().get("textures").iterator().next();
						String name = property.getName();
						String texture = property.getValue();
						String signature = property.getSignature();
						if (usingTexture == null || !texture.equals(usingTexture)) {
							mirrorPlayer.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, name);
							mirrorPlayer.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, texture);
							mirrorPlayer.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, signature);
							usingTexture = texture;
						    if (mirrorPlayer.isSpawned()) {
						    	Location loc = mirrorPlayer.getStoredLocation();
						    	mirrorPlayer.despawn();
						    	mirrorPlayer.spawn(loc);
						    }
						}
						Equipment equipment = mirrorPlayer.getTraitNullable(Equipment.class);
						for (EquipmentSlot slot : EquipmentSlot.values()) {
							Equipment.EquipmentSlot cSlot;
							switch (slot) {
							case CHEST:
								cSlot = Equipment.EquipmentSlot.CHESTPLATE;
								break;
							case FEET:
								cSlot = Equipment.EquipmentSlot.BOOTS;
								break;
							case HAND:
								cSlot = Equipment.EquipmentSlot.OFF_HAND;
								break;
							case HEAD:
								cSlot = Equipment.EquipmentSlot.HELMET;
								break;
							case LEGS:
								cSlot = Equipment.EquipmentSlot.LEGGINGS;
								break;
							case OFF_HAND:
							default:
								cSlot = Equipment.EquipmentSlot.HAND;
								break;
							}
							ItemStack item_mirror = equipment.get(cSlot);
							ItemStack item = player.getEquipment().getItem(slot);
							if (item_mirror != item || (item_mirror != null && !item_mirror.isSimilar(item))) {
								equipment.set(cSlot, item == null ? null : item.clone());
							}
						}
						boolean isSneaking = player.isSneaking();
						SneakingTrait sneakingTrait = mirrorPlayer.getTraitNullable(SneakingTrait.class);
						if (sneakingTrait == null) {
							mirrorPlayer.addTrait(SneakingTrait.class);
						}
						if (isSneaking != sneakingTrait.isSneaking()) {
							if (isSneaking) {
								sneakingTrait.sneak();
							} else {
								sneakingTrait.stand();
							}
						}
						Location mirror = mirrorPlayer.getEntity().getLocation();
						Location real = player.getLocation();
						mirror.setYaw(270 + (90 - real.getYaw()));
						mirror.setPitch(real.getPitch());
						double x_offset = real.getX() - 990.5;
						mirror.setX(990.5 - x_offset);
						mirror.setY(real.getY());
						mirror.setZ(real.getZ());
						mirrorPlayer.teleport(mirror, TeleportCause.PLUGIN);
					}
				} catch (Exception e) {
					e.printStackTrace();
					closetFlag.set(true);
					setClosetNPC();
					closetFlag.set(false);
				}
			});
		}, 100, 1);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event) {
		if (plugin.disableInteraction) {
			return;
		}
		
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		
		if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (player.getEquipment().getItemInMainHand() != null && NBTUtils.contains(player.getEquipment().getItemInMainHand(), "leaveClosetItem")) {
				event.setCancelled(true);
				ClosetPlayerData data = openedCloset.remove(player.getUniqueId());
				if (data != null) {
					player.teleport(data.getLocation());
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
				}
				player.getInventory().clear();
				Inventory inv = data.getInventory();
				for (int i = 0; i < player.getInventory().getSize(); i++) {
					if (inv.getItem(i) != null) {
						player.getInventory().setItem(i, inv.getItem(i).clone());
					}
				}
				player.resetPlayerTime();
				return;
			}
		}
			
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Location location = block.getLocation();
			if (closetDoors.contains(location)) {
				Bukkit.getScheduler().runTaskLater(plugin, () -> {
					if (block.getBlockData() instanceof Openable) {
						Openable openable = (Openable) block.getBlockData();
						openable.setOpen(false);
						block.setBlockData(openable);
						block.getWorld().playSound(block.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 3, 1);
					}
				}, 40);
				Inventory inv = Bukkit.createInventory(null, 54);
				for (int i = 0; i < player.getInventory().getSize(); i++) {
					if (player.getInventory().getItem(i) != null) {
						inv.setItem(i, player.getInventory().getItem(i).clone());
					}
				}
				player.getInventory().clear();
				openedCloset.put(player.getUniqueId(), new ClosetPlayerData(player.getLocation().clone(), inv));
				player.getInventory().setItem(4, leaveClosetItem.clone());
				player.teleport(closet);
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
				player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_OPEN, 3, 1);
				player.setPlayerTime(18000, false);
			}
		}
	}
	
	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();

		Entity entity = event.getRightClicked();
		if (entity instanceof ItemFrame) {
			ItemFrame frame = (ItemFrame) entity;
			if (frame.getItem() != null) {
				ItemStack item = frame.getItem();
				if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
					if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).equalsIgnoreCase("智能衣櫃")) {
						if (!player.getGameMode().equals(GameMode.CREATIVE) && !player.getGameMode().equals(GameMode.SPECTATOR)) {
							event.setCancelled(true);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 今天穿什麼好呢？");
							return;
						}
					}
				}
			}
		}

		if (entity instanceof Painting) {
			RayTrace rayTrace = new RayTrace(player.getEyeLocation().toVector(), player.getLocation().getDirection());
			if (rayTrace.intersects(saturnBox, 5, 0.05) && !plugin.advan.checkAchievement(player, AdvancementManager.ACHIEVEMENT_SATURN)) {
				plugin.signMenuFactory
	            .newMenu(new ArrayList<>(Arrays.asList("", "&2&l^^^^^^^^^^^", "&5&l請輸入", "&2&l=============")))
	            .response((p, lines) -> {
	                if (p.equals(player) && lines[0].replace(" ", "").equalsIgnoreCase("saturn")) {
	                	player.sendTitle(ChatColor.GREEN + "正確!", "", 10, 40, 15);
	                	player.playSound(player.getLocation(), NSound.SFX_ACCEPTED, 5, 1);
	                	plugin.advan.sendAchievement(player, AdvancementManager.ACHIEVEMENT_SATURN);
	                    return true;
	                }
	                ChatUtils.sendLetterByLetterTitle(player, ChatColor.RED + "" + ChatColor.MAGIC + "!!" + ChatColor.RED + "錯誤" + ChatColor.MAGIC + "!!", "", 2, 40, 15, NSound.SFX_BEEP);
	                return false; // failure. becaues reopenIfFail was called, menu will reopen when closed.
	            })
	            .open(player);
			}
		}
	}
	
	private void saturnIndicator() {
		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			if (!plugin.mainCharater.isPresent()) {
				return;
			}
			Player player = plugin.mainCharater.get();
			if (!plugin.advan.checkAchievement(player, AdvancementManager.ACHIEVEMENT_SATURN)) {
				RayTrace rayTrace = new RayTrace(player.getEyeLocation().toVector(), player.getLocation().getDirection());
				Vector pos = rayTrace.positionOfIntersection(saturnBox, 5, 0.05);
				if (pos != null) {
					player.getWorld().spawnParticle(Particle.REDSTONE, pos.toLocation(player.getWorld()), 10, new DustOptions(Color.YELLOW, 1));
				}
			}
		}, 1, 3);
	}
	
	@EventHandler
	public void onAnimation(PlayerAnimationEvent event) {
		if (event.getAnimationType().equals(PlayerAnimationType.ARM_SWING)) {
			Player player = event.getPlayer();
			Entity entity = RayTrace.getLookingEntity(player, 7, EntityType.PLAYER);
			if (entity != null) {
				NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
				if (npc != null) {
					if (npc.getName().startsWith("narskin")) {
						if (!npc.getName().substring(7).equals("clear")) {
							Bukkit.dispatchCommand(player, "skin set " + npc.getName());
						} else {
							Bukkit.dispatchCommand(player, "skin clear");
						}
					}
				}
			}
		}
	}
	
	private void clothesSelector() {
		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			if (!plugin.mainCharater.isPresent()) {
				return;
			}
			Player player = plugin.mainCharater.get();
			if (player != null) {
				Entity entity = RayTrace.getLookingEntity(player, 7, EntityType.PLAYER);
				if (entity != null) {
					NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
					if (npc != null) {
						if (npc.getName().startsWith("narskin")) {
							if (activeNPC == null || !activeNPC.equals(npc)) {
								player.playSound(player.getLocation(), NSound.SFX_SELECT, 5, 1);
							}
							npc.data().set(NPC.GLOWING_METADATA, true);
							player.sendTitle("", ChatColor.GREEN + "左鍵更換", 0, 7, 10);
							activeNPC = npc;
							glowing.add(npc.getId());
						} else {
							activeNPC = null;
						}
					} else {
						activeNPC = null;
					}
				} else {
					activeNPC = null;
				}
			}
			Iterator<Integer> itr = glowing.iterator();
			while (itr.hasNext()) {
				Integer npcId = itr.next();
				if (activeNPC == null || activeNPC.getId() != npcId) {
					NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
					if (npc.getEntity() != null) {
						npc.data().set(NPC.GLOWING_METADATA, false);
					}
					itr.remove();
				}
			}
		}, 0, 3);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		ClosetPlayerData data = openedCloset.remove(player.getUniqueId());
		if (data != null) {
			player.teleport(data.getLocation());
			
			player.getInventory().clear();
			Inventory inv = data.getInventory();
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				if (inv.getItem(i) != null) {
					player.getInventory().setItem(i, inv.getItem(i).clone());
				}
			}
			player.resetPlayerTime();
		}
	}
	
	public void safeShutdown() {
		for (Entry<UUID, ClosetPlayerData> entry : openedCloset.entrySet()) {
			Player player = Bukkit.getPlayer(entry.getKey());
			if (player != null) {
				ClosetPlayerData data = entry.getValue();
				player.getInventory().clear();
				Inventory inv = data.getInventory();
				for (int i = 0; i < player.getInventory().getSize(); i++) {
					if (inv.getItem(i) != null) {
						player.getInventory().setItem(i, inv.getItem(i).clone());
					}
				}
				player.resetPlayerTime();
			}
		}
	}
	
	public static class ClosetPlayerData {
		
		private final Location location;
		private final Inventory inv;
		
		public ClosetPlayerData(Location location, Inventory inv) {
			this.location = location;
			this.inv = inv;
		}

		public Location getLocation() {
			return location;
		}

		public Inventory getInventory() {
			return inv;
		}
	}

}
