package com.loohp.nar.Listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import com.loohp.nar.NarMapPlugin;
import com.loohp.nar.Data.NSound;
import com.loohp.nar.Story.ItemCollection;
import com.loohp.nar.Story.SpecialItems;
import com.loohp.nar.Utils.AdvancementManager;
import com.loohp.nar.Utils.BoundingBoxUtils;
import com.loohp.nar.Utils.ChatUtils;
import com.loohp.nar.Utils.ClipboardUtils;
import com.loohp.nar.Utils.NBTUtils;
import com.loohp.nar.Utils.WaitUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Events implements Listener {
	
	public static Map<UUID, Block> beforeJump = new HashMap<>();
	public static Set<UUID> inTerminal = new HashSet<>();
	public static BoundingBox areaTerminal = BoundingBox.of(new Location(NarMapPlugin.plugin.mainWorld, -740, 255, -967), new Location(NarMapPlugin.plugin.mainWorld, -1067, -70, -1032));
	public static BoundingBox naBuildingTerminal = BoundingBox.of(new Location(NarMapPlugin.plugin.mainWorld, -1002, 80, -1014), new Location(NarMapPlugin.plugin.mainWorld, -994, 67, -1008));
	
	public static Location cloudLever = new Location(NarMapPlugin.plugin.mainWorld, 68, 75, -128);
	
	public static Block spawnBlock = NarMapPlugin.plugin.mainWorld.getBlockAt(-4, 9, 17);
	
	@EventHandler
	public void onSLP(ServerListPingEvent event) {
		if (!NarMapPlugin.plugin.disableCustomSLP) {
			event.setMotd(ChatColor.YELLOW + "Nana的旅程");
		}
    }
	
	@EventHandler
	public void onResoucePackStatus(PlayerResourcePackStatusEvent event) {
		Player player = event.getPlayer();
		if (event.getStatus().equals(Status.SUCCESSFULLY_LOADED)) {
			Location location = player.getLocation().clone();
			player.teleport(new Location(player.getWorld(), 0, 1000, 0));
			Bukkit.getScheduler().runTaskLater(NarMapPlugin.plugin, () -> player.teleport(location), 2);
		} else if (event.getStatus().equals(Status.FAILED_DOWNLOAD)) {
			Bukkit.getScheduler().runTaskLater(NarMapPlugin.plugin, () -> NarMapPlugin.sendResourcePack(player), 2);
		}
	}
	
	@EventHandler
	public void onBlockFall(EntityChangeBlockEvent event) {
		Block block = event.getBlock();
		World world = NarMapPlugin.plugin.mainWorld;
		Location location = block.getLocation();
		if (block.getWorld().equals(world)) {
			if (naBuildingTerminal.contains(location.toVector())) {
			    if (event.getEntityType().equals(EntityType.FALLING_BLOCK) && event.getTo().equals(Material.AIR)){
			        event.setCancelled(true);
			        block.getState().update(false, false);
			        return;
			    }
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (NarMapPlugin.plugin.disableDamage && NarMapPlugin.plugin.mainCharater.isPresent() && NarMapPlugin.plugin.mainCharater.get().equals(event.getEntity())) {
			event.setDamage(0);
		}
	}
	
	@EventHandler
	public void onItemDespawn(ItemDespawnEvent event) {
		Item item = event.getEntity();
		if (item.getScoreboardTags().contains(ItemCollection.SCOREBOARD_TAG)) {
			event.setCancelled(true);
		}
	}
	
	public static Map<UUID, ThrownItemData> thrownItems = new HashMap<>();
	
	public static class ThrownItemData {
		private Item item;
		private ItemStack itemstack;
		public ThrownItemData(Item item, ItemStack itemstack) {
			this.item = item;
			this.itemstack = itemstack;
		}
		public Item getItem() {
			return item;
		}
		public ItemStack getItemstack() {
			return itemstack;
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onThrowItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode().equals(GameMode.ADVENTURE) || player.getGameMode().equals(GameMode.SURVIVAL)) {
			UUID key = UUID.randomUUID();
			ItemStack itemstack = event.getItemDrop().getItemStack().clone();
			thrownItems.put(key, new ThrownItemData(event.getItemDrop(), itemstack));
			TextComponent message = new TextComponent(ChatColor.AQUA + "[點擊取回掉落物品]");
			BaseComponent[] hoverEventComponents = new BaseComponent[] {new TextComponent(NBTUtils.getNBTCompound(itemstack).toString())};
			message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents));
			message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nar retrieveitem " + key));
			player.spigot().sendMessage(message);
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		if (damager instanceof Projectile) {
			Projectile projectile = (Projectile) damager;
			if (projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
				damager = (Entity) projectile.getShooter();
			}
		}
		if (event.getEntity() instanceof ArmorStand) {
			ArmorStand armorstand = (ArmorStand) event.getEntity();
			if (armorstand.isInvulnerable()) {
				if (damager instanceof Player) {
					Player player = (Player) damager;
					if (player.getGameMode().equals(GameMode.CREATIVE)) {
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void itemFrameProtection(HangingBreakEvent event) {
		if (!event.getCause().equals(RemoveCause.DEFAULT)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		Player player = event.getPlayer();
		if (item != null && item.getType().equals(Material.MILK_BUCKET) && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("珍珠奶茶")) {
			if (!NarMapPlugin.plugin.advan.checkAchievement(player, AdvancementManager.ACHIEVEMENT_BUBBLETEA)) {
				NarMapPlugin.plugin.advan.sendAchievement(player, AdvancementManager.ACHIEVEMENT_BUBBLETEA);
			}
			if (player.getGameMode().equals(GameMode.ADVENTURE) || player.getGameMode().equals(GameMode.SURVIVAL)) {
				Bukkit.getScheduler().runTaskLater(NarMapPlugin.plugin, () -> {
					if (player.getEquipment().getItemInMainHand().getType().equals(Material.BUCKET)) {
						player.getInventory().setItem(player.getInventory().getHeldItemSlot(), null);
					} else if (player.getEquipment().getItemInOffHand().getType().equals(Material.BUCKET)) {
						player.getInventory().setItem(40, null);
					}
				}, 1);
			}
		}
	}
	
	@EventHandler
	public void onPreLogin(AsyncPlayerPreLoginEvent event) {
		/*
		if (event.getUniqueId().equals(UUID.fromString("1112981e-9aaa-4ff9-b7d1-c8fd0747cf4a")) || event.getName().equalsIgnoreCase("NARLIAR")) {
			event.disallow(Result.KICK_OTHER, "io.netty.channel.AbstractChannel$AnnotatedConnectException: Connection refused: no further information: ");
		}
		*/
		if (!NarMapPlugin.WAIT_SWITCH.get()) {
			event.disallow(Result.KICK_OTHER, "Please Wait!");
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.getName().equalsIgnoreCase(NarMapPlugin.plugin.getConfig().getString("MainCharacter"))) {
			NarMapPlugin.plugin.mainCharater = Optional.of(player);
		}
		Bukkit.getScheduler().runTaskLater(NarMapPlugin.plugin, () -> {
			NarMapPlugin.sendResourcePack(player);
		}, 10);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (NarMapPlugin.plugin.mainCharater.isPresent() && NarMapPlugin.plugin.mainCharater.get().equals(player)) {
			NarMapPlugin.plugin.mainCharater = Optional.empty();
		}
		
		if (!player.hasPlayedBefore()) {
			Bukkit.getScheduler().runTaskLater(NarMapPlugin.plugin, () -> {
				player.teleport(NarMapPlugin.plugin.menuSpawn);
			}, 10);
		}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		
		if (player.getBoundingBox().overlaps(areaTerminal)) {
			if (!inTerminal.contains(player.getUniqueId())) {
				inTerminal.add(player.getUniqueId());
				player.setPlayerTime(18000, false);
			}
		} else {
			if (inTerminal.remove(player.getUniqueId())) {
				player.resetPlayerTime();
			}
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Block standing = event.getFrom().clone().add(0, -0.1, 0).getBlock();
		if (standing.getBoundingBox().getWidthX() == 1 && standing.getBoundingBox().getWidthZ() == 1 && standing.getBoundingBox().getHeight() > 0) {
			beforeJump.put(player.getUniqueId(), standing);
		}
		if (event.getTo().getY() < -60) {
			Block block = beforeJump.get(player.getUniqueId());
			if (block == null) {
				block = spawnBlock;
			}
			player.setFallDistance(0);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, 10));
			int ran = ThreadLocalRandom.current().nextInt(10);
			String message;
			if (ran == 1) {
				message = player.getName() + " was The Imposter.";
				NarMapPlugin.plugin.advan.sendAchievement(player, AdvancementManager.ACHIEVEMENT_IMPOSTER);
			} else {
				message = player.getName() + " was not The Imposter.";
			}
			CompletableFuture<Void> future = ChatUtils.sendLetterByLetterTitle(player, "", message, 2, 20, 15, NSound.SFX_BEEP);
			BoundingBox box = block.getBoundingBox();
			Location loc = box.getCenter().toLocation(block.getWorld()).add(0, box.getHeight() / 2, 0);
			loc.setYaw(player.getLocation().getYaw());
			loc.setPitch(player.getLocation().getPitch());
			player.teleport(loc);
			Bukkit.getScheduler().runTaskAsynchronously(NarMapPlugin.plugin, () -> { 
				try {
					future.get();
					WaitUtils.waitTicks(25);
					Bukkit.getScheduler().runTask(NarMapPlugin.plugin, () -> player.removePotionEffect(PotionEffectType.SLOW));
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			});
		}
		
		if (player.getBoundingBox().overlaps(areaTerminal)) {
			if (!inTerminal.contains(player.getUniqueId())) {
				inTerminal.add(player.getUniqueId());
				player.setPlayerTime(18000, false);
			}
		} else {
			if (inTerminal.remove(player.getUniqueId())) {
				player.resetPlayerTime();
			}
		}
		
		if (NarMapPlugin.plugin.mainCharater.isPresent() && NarMapPlugin.plugin.mainCharater.get().equals(event.getPlayer())) {
			Location from = event.getFrom();
			Location to = event.getTo();
			if (NarMapPlugin.plugin.disableMovement) {
				if (from.getX() != to.getX() || from.getY() < to.getY() || from.getZ() != to.getZ()) {
					event.setCancelled(true);
					return;
				}
			}
			if (NarMapPlugin.plugin.disableRotation) {
				if (from.getYaw() != to.getYaw() || from.getPitch() != to.getPitch()) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	public static Map<Block, Consumer<Player>> buttons = null;
	
	public static void loadButtons() {
		World world = NarMapPlugin.plugin.mainWorld;
		buttons = new HashMap<>();
		buttons.put(new Location(world, -921, 69, -1001).getBlock(), (player) -> {
			
		});
		buttons.put(new Location(world, -921, 69, -999).getBlock(), (player) -> {
			
		});
		buttons.put(new Location(world, -934, 69, -1001).getBlock(), (player) -> {
			
		});
		buttons.put(new Location(world, -934, 69, -999).getBlock(), (player) -> {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
			Bukkit.getScheduler().runTaskLater(NarMapPlugin.plugin, () -> {
				player.teleport(new Location(world, 4187.5, 24, 14181.5, 90, 0));
				if (NarMapPlugin.plugin.mainCharater.isPresent() && NarMapPlugin.plugin.mainCharater.get().equals(player)) {
					NarMapPlugin.plugin.advan.sendProgression(player, AdvancementManager.PROG_NANA_CLASSROOM);
				}
			}, 20);
		});
		buttons.put(new Location(world, -947, 69, -1001).getBlock(), (player) -> {
			
		});
		buttons.put(new Location(world, -947, 69, -999).getBlock(), (player) -> {
			
		});
		buttons.put(new Location(world, -960, 69, -1001).getBlock(), (player) -> {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
			Bukkit.getScheduler().runTaskLater(NarMapPlugin.plugin, () -> {
				player.teleport(new Location(world, -2006, 64, -1998, 176, 0));
				if (NarMapPlugin.plugin.mainCharater.isPresent() && NarMapPlugin.plugin.mainCharater.get().equals(player)) {
					NarMapPlugin.plugin.advan.sendProgression(player, AdvancementManager.PROG_NANA_OLDBASE);
				}
			}, 20);
		});
		buttons.put(new Location(world, -960, 69, -999).getBlock(), (player) -> {
			
		});
		buttons.put(new Location(world, -973, 69, -1001).getBlock(), (player) -> {
			ItemStack item = NarMapPlugin.plugin.specialitems.get(SpecialItems.SK_SWORD);
			Item entity = player.getWorld().dropItem(player.getEyeLocation(), item);
			entity.setVelocity(new Vector(0, 0, 0));
		});
		buttons.put(new Location(world, -973, 69, -999).getBlock(), (player) -> {
			
		});
		buttons.put(new Location(world, -986, 69, -1001).getBlock(), (player) -> {
			player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
			Bukkit.getScheduler().runTaskLater(NarMapPlugin.plugin, () -> {
				player.teleport(new Location(world, 2957, 63, 1240.5, -90, 0));
				if (NarMapPlugin.plugin.mainCharater.isPresent() && NarMapPlugin.plugin.mainCharater.get().equals(player)) {
					NarMapPlugin.plugin.advan.sendProgression(player, AdvancementManager.PROG_NANA_MUSHROOM);
				}
			}, 20);
		});
		buttons.put(new Location(world, -986, 69, -999).getBlock(), (player) -> {
			Block button = new Location(world, -986, 69, -999).getBlock();
			Consumer<Player> code = buttons.remove(button);
			Block top = button.getRelative(BlockFace.UP);
			Location o = top.getLocation();
			Directional directional = (Directional) button.getBlockData();
			Block block = button.getRelative(directional.getFacing().getOppositeFace());
			Bukkit.getScheduler().runTaskAsynchronously(NarMapPlugin.plugin, () -> {
				try {
					List<Location> blockLoc = BoundingBoxUtils.getHollowCube(world, block.getBoundingBox().expand(0.0625), 0.1);
					Random random = ThreadLocalRandom.current();
					BoundingBox box = ClipboardUtils.getBuildingRegion("assignmentsigns", o).expand(1);
					int taskId = Bukkit.getScheduler().runTaskTimer(NarMapPlugin.plugin, () -> {
						for (Location loc : blockLoc) {
							if (random.nextInt(100) < 5) {
								world.spawnParticle(Particle.REDSTONE, loc, 1, new DustOptions(Color.fromRGB(random.nextInt(128) + 128, random.nextInt(128) + 128, 0), 1));
							}
						}
						for (Location loc : BoundingBoxUtils.getRandomLocation(world, box, random, 10)) {
							world.spawnParticle(Particle.REDSTONE, loc, 1, new DustOptions(Color.fromRGB(random.nextInt(128) + 128, random.nextInt(128) + 128, 0), 1));
						}
					}, 0, 5).getTaskId();
					TimeUnit.SECONDS.sleep(1);
					ClipboardUtils.placeBuilding("assignmentsigns", o, true);
					TimeUnit.SECONDS.sleep(10);
					Bukkit.getScheduler().cancelTask(taskId);
					ClipboardUtils.clearBuilding("assignmentsigns", o);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				buttons.put(button, code);
			});
		});
		buttons.put(new Location(world, -999, 69, -1001).getBlock(), (player) -> {
			Block button = new Location(world, -999, 69, -1001).getBlock();
			Consumer<Player> code = buttons.remove(button);
			Block top = button.getRelative(BlockFace.UP);
			Location o = top.getLocation();
			Directional directional = (Directional) button.getBlockData();
			Block block = button.getRelative(directional.getFacing().getOppositeFace());
			Bukkit.getScheduler().runTaskAsynchronously(NarMapPlugin.plugin, () -> {
				try {
					List<Location> blockLoc = BoundingBoxUtils.getHollowCube(world, block.getBoundingBox().expand(0.0625), 0.1);
					Random random = ThreadLocalRandom.current();
					int taskId = Bukkit.getScheduler().runTaskTimer(NarMapPlugin.plugin, () -> {
						for (Location loc : blockLoc) {
							if (random.nextInt(100) < 5) {
								world.spawnParticle(Particle.REDSTONE, loc, 1, new DustOptions(Color.fromRGB(0, random.nextInt(256), 255), 1));
							}
						}
						for (Location loc : BoundingBoxUtils.getRandomLocation(world, naBuildingTerminal, random, 60)) {
							world.spawnParticle(Particle.REDSTONE, loc, 1, new DustOptions(Color.fromRGB(255, 255, random.nextInt(256)), 1));
						}
					}, 0, 5).getTaskId();
					TimeUnit.SECONDS.sleep(1);
					for (int i = 1; i < 13; i++) {
						ClipboardUtils.placeBuilding("nabuilding" + i, o, true);
						TimeUnit.MILLISECONDS.sleep(300);
					}
					TimeUnit.SECONDS.sleep(10);
					Bukkit.getScheduler().cancelTask(taskId);
					for (int i = 1; i < 13; i++) {
						ClipboardUtils.clearBuilding("nabuilding" + i, o);
						TimeUnit.MILLISECONDS.sleep(300);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				buttons.put(button, code);
			});
		});
		buttons.put(new Location(world, -999, 69, -999).getBlock(), (player) -> {
			ItemStack item = NarMapPlugin.plugin.specialitems.get(SpecialItems.BUBBLE_TEA);
			Item entity = player.getWorld().dropItem(player.getEyeLocation(), item);
			entity.setVelocity(new Vector(0, 0, 0));
		});
	}
	
	@EventHandler
	public void onInteraction(PlayerInteractEvent event) {
		if (NarMapPlugin.plugin.disableInteraction) {
			event.setCancelled(true);
			return;
		}
		
		if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			if (event.getClickedBlock().getType().toString().contains("DOOR")) {
				NarMapPlugin.plugin.knockedDoor = Optional.of(event.getClickedBlock());
				return;
			}
		}
		
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (event.getClickedBlock().getType().equals(Material.LEVER)) {
				if (event.getClickedBlock().equals(cloudLever.getBlock())) {
					Player player = event.getPlayer();
					if (!NarMapPlugin.plugin.advan.checkAchievement(player, AdvancementManager.ACHIEVEMENT_CLOUDLAND)) {
						NarMapPlugin.plugin.advan.sendAchievement(player, AdvancementManager.ACHIEVEMENT_CLOUDLAND);
					}
				}
			}
		}
		
		Player player = event.getPlayer();
		if (event.getItem() != null && NarMapPlugin.plugin.specialitems.isUnplaceable(event.getItem())) {
			event.setCancelled(true);
			return;
		}
		
		if (buttons == null) {
			loadButtons();
		}
		
		Block block = event.getClickedBlock();
		if (block == null || !block.getType().equals(Material.STONE_BUTTON)) {
			return;
		}
		Location location = block.getLocation();
		
		Consumer<Player> function = buttons.get(location.getBlock());
		if (function != null) {
			function.accept(player);
		}
	}
	
	private int sleepTask = -1;
	
	@EventHandler
	public void onSleep(PlayerBedEnterEvent event) {
		Player player = event.getPlayer();
		if (NarMapPlugin.plugin.disableBeds || NarMapPlugin.plugin.disableInteraction) {
			event.setCancelled(true);
		}
		if (!NarMapPlugin.plugin.mainCharater.isPresent()) {
			return;
		}
		if (!player.equals(NarMapPlugin.plugin.mainCharater.get())) {
			return;
		}
		Bukkit.getScheduler().runTaskLater(NarMapPlugin.plugin, () -> {
			if (player.isSleeping()) {
				NarMapPlugin.plugin.sleptTicks.set(0);
				sleepTask = Bukkit.getScheduler().runTaskTimer(NarMapPlugin.plugin, () -> NarMapPlugin.plugin.sleptTicks.incrementAndGet(), 0, 1).getTaskId();
			}
		}, 1);
	}
	
	@EventHandler
	public void onExitSleep(PlayerBedLeaveEvent event) {
		Player player = event.getPlayer();
		if (!NarMapPlugin.plugin.mainCharater.isPresent()) {
			return;
		}
		if (!player.equals(NarMapPlugin.plugin.mainCharater.get())) {
			return;
		}
		if (sleepTask >= 0) {
			Bukkit.getScheduler().cancelTask(sleepTask);
			sleepTask = -1;
		}
	}

}
