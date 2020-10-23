package com.loohp.nar.Story;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import com.loohp.nar.NarMapPlugin;
import com.loohp.nar.Data.NSound;
import com.loohp.nar.Utils.AdvancementManager;
import com.loohp.nar.Utils.ChatUtils;
import com.loohp.nar.Utils.ClipboardUtils;
import com.loohp.nar.Utils.EntityUtils;
import com.loohp.nar.Utils.OptionUtils;
import com.loohp.nar.Utils.RayTrace;
import com.loohp.nar.Utils.SyncUtils;
import com.loohp.nar.Utils.WaitUtils;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class MainSequence {
	
	public static CompletableFuture<Void> stageBeginning() {
		CompletableFuture<Void> future = new CompletableFuture<>();
		if (!NarMapPlugin.plugin.mainCharater.isPresent()) {
			future.completeExceptionally(new RuntimeException("No main charater"));
		} else {
			Player player = NarMapPlugin.plugin.mainCharater.get();
			Bukkit.getScheduler().runTaskAsynchronously(NarMapPlugin.plugin, () -> {
				try {
					//Resource Pack Test
					SyncUtils.run(() -> player.setGameMode(GameMode.ADVENTURE)).get();
					ChatUtils.clearChat(Bukkit.getOnlinePlayers().toArray(new Player[0]));
					
					SyncUtils.run(() -> player.teleport(NarMapPlugin.plugin.blackBox)).get();
					WaitUtils.waitTicks(20);
					ChatUtils.sendLetterByLetterTitleToAllPlayers(ChatColor.GREEN + "為確保遊戲最佳體驗", "", 2, 80, 15, NSound.SFX_BEEP).get();
					WaitUtils.waitTicks(80 + 15);
					ChatUtils.sendLetterByLetterTitleToAllPlayers(ChatColor.AQUA + "請確保您已經安裝資源包", "", 2, 80, 15, NSound.SFX_BEEP).get();
					WaitUtils.waitTicks(80 + 15);
					ChatUtils.sendLetterByLetterTitleToAllPlayers(ChatColor.AQUA + "並已開啟遊戲聲音", "", 2, 80, 15, NSound.SFX_BEEP).get();
					WaitUtils.waitTicks(80 + 15);
					
					SyncUtils.run(() -> player.teleport(NarMapPlugin.plugin.resourcePackTest)).get();
					while (true) {
						ChatUtils.sendToAllPlayers(ChatColor.GREEN + "請選擇你所見方塊的顔色！");
						CompletableFuture<Integer> option = OptionUtils.sendRequest(4, 3, 60000, new TextComponent(ChatColor.BLUE + "[藍色]"), new TextComponent(ChatColor.RED + "[紅色]"),
								new TextComponent(ChatColor.YELLOW + "[黃色]"), new TextComponent(ChatColor.GREEN + "[綠色]"));
						int answer = option.get();
						
						if (answer == 0) {
							ChatUtils.sendToAllPlayers(ChatColor.GREEN + "正確！");
							player.getWorld().playSound(player.getLocation(), NSound.SFX_ACCEPTED, 10, 1);
							break;
						} else {
							ChatUtils.sendToAllPlayers(ChatColor.RED + "錯誤！請安裝資源包並重試!");
							WaitUtils.waitTicks(40);
							NarMapPlugin.sendResourcePack(player);
						}
					}
					
					WaitUtils.waitTicks(40);
					ChatUtils.clearChat(Bukkit.getOnlinePlayers().toArray(new Player[0]));
						
					World world = NarMapPlugin.plugin.mainWorld;
					//Begin
					SyncUtils.run(() -> player.teleport(NarMapPlugin.plugin.blackBox)).get();
					WaitUtils.waitTicks(20);
					
					NarMapPlugin.plugin.disableMovement = true;
					NarMapPlugin.plugin.disableRotation = true;
					NarMapPlugin.plugin.disableInteraction = true;
					
					Location item_Location = new Location(world, 22, 75, -86);
					ClipboardUtils.clearBuilding("item_collection", item_Location);
					ItemCollection.clear();
					
					ChatUtils.sendLetterByLetterTitleToAllPlayers(ChatColor.YELLOW + "這沒甚麽特別餒", "", 2, 80, 15, NSound.SFX_BEEP).get();
					WaitUtils.waitTicks(80 + 15);
					ChatUtils.sendLetterByLetterTitleToAllPlayers(ChatColor.YELLOW + "這只是又一個無聊的早晨Zzz", "", 2, 80, 15, NSound.SFX_BEEP).get();
					WaitUtils.waitTicks(80 + 15);
					SyncUtils.run(() -> world.setTime(23390)).get();
					world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
					WaitUtils.waitTicks(20);
					
					SyncUtils.run(() -> player.setGameMode(GameMode.SPECTATOR)).get();
					SyncUtils.run(() -> player.setFlySpeed(0)).get();
					SyncUtils.run(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 5))).get();
					SyncUtils.run(() -> player.teleport(new Location(world, -2909, 69, 716, -90F, -4.5F))).get();
					WaitUtils.waitTicks(5);
					SyncUtils.run(() -> world.playSound(player.getLocation(), NSound.SFX_MORNING, 20000000, 1)).get();
					WaitUtils.waitTicks(50);
					ChatUtils.sendLetterByLetterTitleToAllPlayers("太陽出來了", ChatColor.AQUA + "今天的天氣真好!", 2, 80, 15, NSound.SFX_BEEP);
					WaitUtils.waitTicks(190);
					world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
					SyncUtils.run(() -> world.setTime(0)).get();
					SyncUtils.run(() -> player.removePotionEffect(PotionEffectType.SLOW)).get();
					SyncUtils.run(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 5))).get();
					
					NarMapPlugin.plugin.disableRotation = false;
					
					SyncUtils.run(() -> player.setFlySpeed(0.08F)).get();
					SyncUtils.run(() -> player.teleport(new Location(world, 17, 75.5626, -87, 0, 0))).get();
					SyncUtils.run(() -> player.setGameMode(GameMode.ADVENTURE)).get();
					WaitUtils.waitTicks(20);
					SyncUtils.run(() -> player.removePotionEffect(PotionEffectType.BLINDNESS)).get();
					
					WaitUtils.waitTicks(40);
					
					NarMapPlugin.plugin.advan.sendProgression(player, AdvancementManager.PROG_BEGIN);
					
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 早晨~");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 今天是10月22日嗎？");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 噢 今天是有人約了我去他家嗎?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 是時候打扮預備一下!");
					
					NarMapPlugin.plugin.disableMovement = false;
					
					WaitUtils.waitTicks(20);					
					ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "打開衣櫃");
					
					NarMapPlugin.plugin.disableInteraction = false;
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return NarMapPlugin.plugin.cosmetics.openedCloset.containsKey(player.getUniqueId());
						}
					}).get();
					
					WaitUtils.waitTicks(5);
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !NarMapPlugin.plugin.cosmetics.openedCloset.containsKey(player.getUniqueId());
						}
					}).get();
					
					List<String> strList = new ArrayList<>();
					strList.add("我要去樓剛的家!");
					strList.add("我要去呂的家!");
					strList.add("我要去冰的家!");
					
					List<String> strListCopy = new ArrayList<>(strList);
					
					for (int n = 0; n < 3; n++) {
						
						WaitUtils.waitTicks(40);
						
						if (n == 1) {
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 早晨~");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							NarMapPlugin.plugin.disableMovement = true;
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 今天是10月22日嗎？");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 等一下? 爲什莫? 昨天不都是10月22日嗎？");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 不可能! 我應該是在做夢吧?");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							NarMapPlugin.plugin.disableMovement = false;
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 那 今天是有人約了我去他家嗎, 是時候出門了!");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						} else if (n == 2) {
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 呃.. 好吧.");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							NarMapPlugin.plugin.disableMovement = true;
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 是搞錯了嗎, 爲甚麽每天都會是10月22日啊...");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 怎麽辦? 怎麽辦?");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 嗯？我是要拜訪完所有人的家而且集齊所有物品嗎?");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 哇！那我要趕快出門!");
							NarMapPlugin.plugin.disableMovement = false;
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						}
						
						TextComponent[] options = new TextComponent[strList.size()];
						for (int i = 0; i < options.length; i++) {
							switch (i) {
							case 0:
								options[0] = new TextComponent(ChatColor.RED + "[" + strList.get(0) + "]");
								break;
							case 1:
								options[1] = new TextComponent(ChatColor.YELLOW + "[" + strList.get(1) + "]");
								break;
							default:
								options[i] = new TextComponent(ChatColor.GREEN + "[" + strList.get(2) + "]");
								break;
							}
						}
						
						for (Player each : Bukkit.getOnlinePlayers()) {
							each.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 200, 2);
						}
						
						int answer = OptionUtils.sendRequest(3, 0, 20000, options).get();
						
						for (Player each : Bukkit.getOnlinePlayers()) {
							each.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 200, 2);
						}
						
						ChatUtils.clearChat(Bukkit.getOnlinePlayers().toArray(new Player[0]));
						
						ChatUtils.sendToAllPlayers(ChatColor.GREEN + "<" + player.getName() + "> " + strList.get(answer));
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						
						if (strListCopy.indexOf(strList.get(answer)) == 0) {
							ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "請在樓剛家尋找樓剛!");
							
							NPC car = CitizensAPI.getNPCRegistry().getById(24);
							
							SyncUtils.waitUntilTrue(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return player.getNearbyEntities(3, 1, 3).contains(car.getEntity());
								}
							}).get();
							
							NarMapPlugin.plugin.disableMovement = true;
							
							ChatUtils.sendToAllPlayers("<LIARCAR> 欸想要你的生日禮物就得完成關卡哦");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 不是說互相都不要送生日禮物給對方嗎");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<LIARCAR> 。。。。。。");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<LIARCAR> 我也弄了你就玩吧啊哈哈哈");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							
							SyncUtils.run(() -> {
								ItemFrame a = (ItemFrame) Bukkit.getEntity(UUID.fromString("6fe95612-6e50-4ea3-a9e8-635005a58b47"));
								ItemFrame b = (ItemFrame) Bukkit.getEntity(UUID.fromString("9db3ef38-cad1-449e-bdb6-1259586ab81f"));
								ItemFrame c = (ItemFrame) Bukkit.getEntity(UUID.fromString("0b2d112c-2f14-49c1-b842-0cb38a674f14"));
								ItemFrame d = (ItemFrame) Bukkit.getEntity(UUID.fromString("ba4bed2b-5d62-4c37-ba20-20b4bfa96b52"));
								ItemFrame e = (ItemFrame) Bukkit.getEntity(UUID.fromString("871d39b6-44f3-4ce9-a192-af32fd1aa537"));
								ItemFrame f = (ItemFrame) Bukkit.getEntity(UUID.fromString("4895fabb-9c9e-4234-9782-29bdf4ba3d16"));
								ItemFrame g = (ItemFrame) Bukkit.getEntity(UUID.fromString("aa8caaf4-7c2a-4504-ad7c-dcfea4242b00"));
								
								a.setItem(null, false);
								b.setItem(null, false);
								c.setItem(null, false);
								d.setItem(null, false);
								e.setItem(null, false);
								f.setItem(null, false);
								g.setItem(null, false);
							}).get();
							
							ClipboardUtils.placeBuilding("car_challenge", new Location(world, -68, 109, -137), false);
							SyncUtils.run(() -> {
								world.getBlockAt(-95, 100, -146).setType(Material.SMOOTH_QUARTZ);
								world.getBlockAt(-95, 99, -146).setType(Material.SMOOTH_QUARTZ);
								world.getNearbyEntities(ClipboardUtils.getBuildingRegion("car_challenge", new Location(world, -68, 109, -137))).forEach(each -> {
									if (each instanceof Item) {
										each.remove();
									}
								});
							}).get();
							ItemFrame frame = SyncUtils.run(() -> (ItemFrame) Bukkit.getEntity(UUID.fromString("3d297e33-1ac3-4030-a121-7fd8b0fda057"))).get();
							SyncUtils.run(() -> {
								frame.setItem(null, false);
							}).get();
							
							SyncUtils.run(() -> {
								player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
							}).get();
							WaitUtils.waitTicks(20);
							Location o_loc = SyncUtils.run(() -> {
								Location location = player.getLocation().clone();
								player.teleport(new Location(world, -76.5, 79, -143.5, 90, 0));
								return location;
							}).get();
							WaitUtils.waitTicks(20);
							
							NarMapPlugin.plugin.disableMovement = false;
							
							ItemStack compare = NarMapPlugin.plugin.specialitems.get(SpecialItems.CHALLENGE_CARRR);
							
							SyncUtils.waitUntilTrue(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return frame.getItem() != null && frame.getItem().isSimilar(compare);
								}
							}).get();
							
							NarMapPlugin.plugin.advan.sendProgression(player, AdvancementManager.PROG_CHALLENGE_CARRR);
							
							SyncUtils.run(() -> {
								player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
							}).get();
							WaitUtils.waitTicks(20);
							SyncUtils.run(() -> player.teleport(o_loc)).get();
							
							NarMapPlugin.plugin.disableMovement = true;
							
							SyncUtils.run(() -> {
								if (!player.getInventory().containsAtLeast(compare, 1)) {
									Item item = world.dropItem(player.getEyeLocation(), NarMapPlugin.plugin.specialitems.get(SpecialItems.CHALLENGE_CARRR));
									item.setVelocity(new Vector(0, 0, 0));
								}
							}).get();
							
							ChatUtils.sendToAllPlayers("<LIARCAR> 恭喜完成智障關卡喲啊哈哈哈");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> ......................");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							
							NarMapPlugin.plugin.disableMovement = false;
							
						} else if (strListCopy.indexOf(strList.get(answer)) == 1) {
							ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "請在呂家尋找呂!!");
							
							SyncUtils.run(() -> world.getBlockAt(60, 65, -125).setType(Material.TARGET)).get();
							ClipboardUtils.placeBuilding("biggy_bats", new Location(world, 59, 79, -123), true);
							
							NPC biggy = CitizensAPI.getNPCRegistry().getById(23);
							SyncUtils.run(() -> biggy.despawn()).get();
							WaitUtils.waitTicks(10);
							SyncUtils.run(() -> biggy.spawn(new Location(world, 60.443, 79, -126.604, 90, 0))).get();					
							WaitUtils.waitTicks(10);
							SyncUtils.run(() -> biggy.teleport(new Location(world, 60.443, 79, -126.604, 90, 0), TeleportCause.PLUGIN)).get();
							
							SyncUtils.waitUntilTrue(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return player.getNearbyEntities(3, 1, 3).contains(biggy.getEntity());
								}
							}).get();
							
							NarMapPlugin.plugin.disableMovement = true;
							
							ChatUtils.sendToAllPlayers("<Biggy0527> Hai~這裡是不是很漂亮呢?");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 黑白主色，加上紫綠的配搭");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 就是我新的音樂大本營啦！");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 哇喔！感覺如何？");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 不過總是有村民在吵三小");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 創作靈感盡失");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 正好屋子對著一個籃球場");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 可以幫我打他們進籃");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 回復這裡的寧靜嗎？");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 啥？你邀請我來是要我打籃球？");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 幫幫我吧..那怕一個也好");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 箱子裡有你需要的球棒");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 拜託你了~");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers(ChatColor.ITALIC + player.getName() + " 悄悄的說: 幹嘛要打村民..這樣摔下去不會死的嗎？");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers(ChatColor.ITALIC + player.getName() + " 悄悄的說: 還有哪有人用球棒打籃球的...");
							WaitUtils.waitTicks(50);
							ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "按下按鈕開始投籃");
							
							NarMapPlugin.plugin.disableMovement = false;
							
							SyncUtils.waitUntilTrue(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									boolean hoop1 = world.getBlockAt(25, 75, -130).getType().equals(Material.LIME_WOOL);
									boolean hoop2 = world.getBlockAt(13, 75, -130).getType().equals(Material.LIME_WOOL);
									boolean hoop3 = world.getBlockAt(13, 75, -116).getType().equals(Material.LIME_WOOL);
									boolean hoop4 = world.getBlockAt(25, 75, -116).getType().equals(Material.LIME_WOOL);
									return hoop1 || hoop2 || hoop3 || hoop4;
								}
							}).get();
							
							SyncUtils.run(() -> world.getBlockAt(60, 65, -125).setType(Material.AIR)).get();
							
							player.playSound(player.getLocation(), NSound.SFX_ACCEPTED, 5, 1);
							
							ChatUtils.sendToAllPlayers("<Biggy0527> GOAL!!");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 村民真是最煩的生物啊");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 現在可以開大喇叭");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 再沒有人吵著我啦!");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 啊對了");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 這就送你吧");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<Biggy0527> 當是我答謝的禮物~");
							
							SyncUtils.run(() -> {
								ItemStack reward = NarMapPlugin.plugin.specialitems.get(SpecialItems.CHALLENGE_BIGGY);
								Item item = biggy.getEntity().getWorld().dropItem(biggy.getEntity().getLocation().add(0, 1.8, 0), reward);
								item.setVelocity(player.getLocation().toVector().subtract(biggy.getEntity().getLocation().add(0, 1.8, 0).toVector()).add(new Vector(0, 1, 0)).multiply(0.15));
								Bukkit.getScheduler().runTaskLater(NarMapPlugin.plugin, () -> {
									if (item.isValid()) {
										item.teleport(player);
									}
								}, 40);
							}).get();
							NarMapPlugin.plugin.advan.sendProgression(player, AdvancementManager.PROG_CHALLENGE_BIGGY);
							
						} else if (strListCopy.indexOf(strList.get(answer)) == 2) {
							ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "請在冰家尋找冰!");
							
							NPC henry = CitizensAPI.getNPCRegistry().getById(34);
							SyncUtils.run(() -> henry.despawn()).get();
							WaitUtils.waitTicks(10);
							SyncUtils.run(() -> henry.spawn(new Location(world, 23.5, 70, -60.5, 180, 0))).get();					
							WaitUtils.waitTicks(10);
							SyncUtils.run(() -> henry.teleport(new Location(world, 23.5, 70, -60.5, 180, 0), TeleportCause.PLUGIN)).get();
							
							SyncUtils.waitUntilTrue(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return player.getNearbyEntities(3, 1, 3).contains(henry.getEntity());
								}
							}).get();
							
							NarMapPlugin.plugin.disableMovement = true;
							
							ChatUtils.sendToAllPlayers("<henryauyong> 嗨！生日快樂！");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<henryauyong> ... 這算是預早慶祝吧");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<henryauyong> 總之！我當然有準備禮物");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<henryauyong> 不過你要先完成我準備給你的挑戰");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 什麼挑戰？");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<henryauyong> 啊對了");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<henryauyong> 你看，我家的花園裡面埋了很多地雷...");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<henryauyong> 你能幫我把它們清掉嗎？");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> ... 為什麼你家裡會有地雷啦！");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							
							Minesweeper mine = NarMapPlugin.plugin.minesweeper;
							SyncUtils.run(() -> mine.reset()).get();
							SyncUtils.run(() -> mine.reset()).get();
							
							SyncUtils.run(() -> {
								player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
							}).get();
							WaitUtils.waitTicks(20);
							Location o_loc = SyncUtils.run(() -> {
								Location location = player.getLocation().clone();
								player.teleport(new Location(world, -66.5, 92, -75, 180, 0));
								return location;
							}).get();
							WaitUtils.waitTicks(80);
							
							NarMapPlugin.plugin.disableMovement = false;
							
							SyncUtils.run(() -> {
								Item item = world.dropItem(player.getEyeLocation(), NarMapPlugin.plugin.specialitems.get(SpecialItems.MINEHOE));
								item.setVelocity(new Vector(0, 0, 0));
							}).get();
							
							ChatUtils.sendLetterByLetterTitleToAllPlayers(ChatColor.AQUA + "藍色那格是安全的", "", 2, 80, 15, NSound.SFX_BEEP).get();
							WaitUtils.waitTicks(80 + 15);
							
							SyncUtils.waitUntilTrue(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return mine.isEnd();
								}
							}).get();
							
							if (mine.isWon()) {
								SyncUtils.run(() -> {
									Item item = world.dropItem(player.getEyeLocation(), NarMapPlugin.plugin.specialitems.get(SpecialItems.MINEWON));
									item.setVelocity(new Vector(0, 0, 0));
								}).get();
								ChatUtils.sendToAllPlayers("<henryauyong> Wow 想不到你真的成功了");
								WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
								ChatUtils.sendToAllPlayers(ChatColor.GRAY + "" + ChatColor.ITALIC + "<henryauyong> 可惡，我原本以為她一定會失敗的說");
								WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
								ChatUtils.sendToAllPlayers("<" + player.getName() + "> 你剛才有說什麼嗎？");
								WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
								ChatUtils.sendToAllPlayers("<henryauyong> 既然你成功了，這是你的禮物！");
								WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							} else {
								SyncUtils.run(() -> {
									Item item = world.dropItem(player.getEyeLocation(), NarMapPlugin.plugin.specialitems.get(SpecialItems.MINELOST));
									item.setVelocity(new Vector(0, 0, 0));
								}).get();
								ChatUtils.sendToAllPlayers("<henryauyong> 噢，你失敗了啊...");
								WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
								ChatUtils.sendToAllPlayers("<" + player.getName() + "> 太難了啦！我平常又沒在玩這種東西");
								WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
								ChatUtils.sendToAllPlayers("<henryauyong> 不過沒關係，該送的禮物還是會送的！");
								WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							}
							
							SyncUtils.run(() -> {
								ItemStack reward = NarMapPlugin.plugin.specialitems.get(SpecialItems.CHALLENGE_HENRY);
								Item item = henry.getEntity().getWorld().dropItem(henry.getEntity().getLocation().add(0, 1.8, 0), reward);
								item.setVelocity(player.getLocation().toVector().subtract(henry.getEntity().getLocation().add(0, 1.8, 0).toVector()).add(new Vector(0, 1, 0)).multiply(0.15));
								Bukkit.getScheduler().runTaskLater(NarMapPlugin.plugin, () -> {
									if (item.isValid()) {
										item.teleport(player);
									}
								}, 40);
							}).get();
							
							NarMapPlugin.plugin.advan.sendProgression(player, AdvancementManager.PROG_CHALLENGE_HENRY);
							
							SyncUtils.run(() -> {
								player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
							}).get();
							WaitUtils.waitTicks(20);
							SyncUtils.run(() -> player.teleport(o_loc)).get();
						}
						
						WaitUtils.waitTicks(50);
						ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "回家吧!");
						
						if (n == 0) {
							ClipboardUtils.placeBuilding("item_collection", item_Location, true);
						}
						
						SyncUtils.waitUntilTrue(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return world.getNearbyEntities(BoundingBox.of(world.getBlockAt(24, 70, -82), world.getBlockAt(27, 72, -86))).contains(player);
							}
						}).get();
						
						SyncUtils.run(() -> world.setTime(18000)).get();
						ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "請上樓");
						
						SyncUtils.waitUntilTrue(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return world.getNearbyEntities(BoundingBox.of(world.getBlockAt(27, 75, -85), world.getBlockAt(25, 77, -82))).contains(player);
							}
						}).get();
						
						if (n == 0) {
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 嗚啊這是甚麽?");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 有人闖進我家了嗎?!");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "請試拿著其他物品");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						}
						
						if (strListCopy.indexOf(strList.get(answer)) == 0) {
							SyncUtils.waitUntilTrue(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return ItemCollection.STATUS.getRed();
								}
							}).get();
						} else if (strListCopy.indexOf(strList.get(answer)) == 1) {
							SyncUtils.waitUntilTrue(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return ItemCollection.STATUS.getPurple();
								}
							}).get();
						} else if (strListCopy.indexOf(strList.get(answer)) == 2) {
							SyncUtils.waitUntilTrue(new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									return ItemCollection.STATUS.getGreen();
								}
							}).get();
						}
						
						strList.remove(answer);
						
						if (n == 0) {
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 喔買尬! 也太累了吧!");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 該睡了啦!");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 明天就是我的生日喇!");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						} else if (n == 1) {
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 該睡了啦!");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 好吧, 明天真的就是我的生日喇.");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						} else if (n == 2) {
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 拜托! 明天一定要是10月23日啊");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 還是我忘了誰的物品嗎?");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 喔對! 林的物品!");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<" + player.getName() + "> 他應該不會忘記我的生日吧?");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						}
						
						ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "該睡了");
						NarMapPlugin.plugin.disableBeds = false;
						NarMapPlugin.plugin.sleptTicks.set(0);
						
						SyncUtils.waitUntilTrue(new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								return NarMapPlugin.plugin.sleptTicks.get() > 100;
							}
						}).get();
						
						SyncUtils.run(() -> world.setTime(0)).get();
						NarMapPlugin.plugin.disableBeds = true;
					}
					
					NarMapPlugin.plugin.disableMovement = true;
					
					SyncUtils.run(() -> world.setTime(18000)).get();
					
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 呃...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 果然還是10月22日... 咦?怎麽現在是晚上呢？");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 好可怕喔...");
					WaitUtils.waitTicks(50);
					ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "小心撞頭喔!");
					
					NarMapPlugin.plugin.disableMovement = false;
					
					ItemStack letter = NarMapPlugin.plugin.specialitems.get(SpecialItems.LOOHP_LETTER);
					SyncUtils.run(() -> {
						Inventory inv = ((Container) world.getBlockAt(32, 71, -82).getState()).getInventory();
						inv.setItem(12, letter);
						inv.setItem(14, NarMapPlugin.plugin.specialitems.get(SpecialItems.CHALLENGE_LOOHP));
					}).get();
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return world.getNearbyEntities(BoundingBox.of(world.getBlockAt(25, 73, -81), world.getBlockAt(27, 70, -84))).contains(player);
						}
					}).get();
					
					SyncUtils.run(() -> EntityUtils.faceLocation(player, new Location(world, 28.5, 71.5, -81.5))).get();
					
					WaitUtils.waitTicks(10);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 欸？郵箱裏有東西嗎？");
					WaitUtils.waitTicks(50);
					ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "檢查郵箱!");
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return player.getInventory().containsAtLeast(letter, 1);
						}
					}).get();
					
					NarMapPlugin.plugin.advan.sendProgression(player, AdvancementManager.PROG_CHALLENGE_LOOHP);
					
					TextComponent toggle = new TextComponent(ChatColor.GREEN + "閱讀完畢後點擊繼續");
					TextComponent othersToggle = toggle.duplicate();
					
					toggle.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/nar toggle"));
					toggle.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.AQUA + "點擊繼續")));
					
					othersToggle.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.AQUA + "等待主角...")));
					
					player.spigot().sendMessage(toggle);
					for (Player each : Bukkit.getOnlinePlayers()) {
						if (!each.equals(player)) {
							each.spigot().sendMessage(othersToggle);
						}
					}
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return NarMapPlugin.plugin.playerToggle.compareAndSet(true, false);
						}
					}).get();
					
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 至少也有封信跟一雙鞋...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 爲什麽我會這麽累啊...");
					WaitUtils.waitTicks(50);
					ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "請將物品放在對應方塊上");
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ItemCollection.STATUS.getBlue();
						}
					}).get();
					
					WaitUtils.waitTicks(50);
					ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "躺在床上..");
					
					NarMapPlugin.plugin.disableBeds = false;
					NarMapPlugin.plugin.sleptTicks.set(0);
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return NarMapPlugin.plugin.sleptTicks.get() > 100;
						}
					}).get();
					
					SyncUtils.run(() -> world.setTime(0)).get();
					NarMapPlugin.plugin.disableBeds = true;
					
					NarMapPlugin.plugin.advan.sendTip(player, AdvancementManager.TIP_STAGE);
					//
					future.complete(null);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			});
		}
		return future;
	}
	
	public static CompletableFuture<Void> stageNightmareVillage() {
		CompletableFuture<Void> future = new CompletableFuture<>();
		if (!NarMapPlugin.plugin.mainCharater.isPresent()) {
			future.completeExceptionally(new RuntimeException("No main charater"));
		} else {
			Player player = NarMapPlugin.plugin.mainCharater.get();
			Bukkit.getScheduler().runTaskAsynchronously(NarMapPlugin.plugin, () -> {
				try {
					World world = NarMapPlugin.plugin.mainWorld;
					//Teleport to village
					SyncUtils.run(() -> player.setGameMode(GameMode.ADVENTURE)).get();
					SyncUtils.run(() -> world.setTime(18000)).get();
					SyncUtils.run(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 5))).get();
					SyncUtils.run(() -> player.teleport(new Location(world, -3255.5, 67.5626, 561, 0, 90))).get();
					
					NarMapPlugin.plugin.disableMovement = true;
					NarMapPlugin.plugin.disableInteraction = true;
					
					SyncUtils.run(() -> {
						Block door = world.getBlockAt(-3253, 67, 564);
						Openable openable = (Openable) door.getBlockData();
						openable.setOpen(false);
						door.setBlockData(openable);
					}).get();
					
					SyncUtils.run(() -> world.getNearbyEntities(new Location(world, -3266, 74, 617), 3, 3, 3).forEach(each -> {
						if (each instanceof ItemFrame) {
							each.setGlowing(false);
						}
					})).get();
					
					Location portalloc = new Location(world, -3256, 78, 615);
					SyncUtils.run(() -> ClipboardUtils.clearBuilding("village_portal", portalloc)).get();
					
					SyncUtils.run(() -> NarMapPlugin.plugin.advan.sendProgression(player, AdvancementManager.PROG_NIGHTMARE)).get();
					SyncUtils.run(() -> world.playSound(player.getLocation(), NSound.SFX_NIGHTMARE, 200, 1)).get();
					
					//Wake up in nana 18-20 village
					WaitUtils.waitTicks(20);
					ChatUtils.sendToAllPlayers(ChatColor.ITALIC + player.getName() + " 我怎麽大半夜醒過來喇...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 60);
					ChatUtils.sendToAllPlayers(ChatColor.ITALIC + player.getName() + " 呼吸急促...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 60);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 10月23日了嗎?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 我在哪?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 你應該對這個地方很熟悉吧");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 啊! 你是誰?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 那條信息對你來說是不夠的.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 這是神馬? 說! 我已經卡在同一天太久了啦!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 我已經完成所有朋友的考驗! 爲什麽還在重複!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 你朋友的考驗?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 啊哈哈哈你真是可愛! 出來吧，你就會明白我在供三小..");
					
					NarMapPlugin.plugin.disableMovement = false;
					NarMapPlugin.plugin.disableInteraction = false;
					
					NPC oldNa = CitizensAPI.getNPCRegistry().getById(13);
					Location startLoc = new Location(world, -3246.5, 67, 558.5);
					
					SyncUtils.run(() -> oldNa.spawn(startLoc)).get();
					WaitUtils.waitTicks(10);
					SyncUtils.run(() -> oldNa.teleport(startLoc, TeleportCause.PLUGIN)).get();
					
					NPC oldLoohp = CitizensAPI.getNPCRegistry().getById(14);
					
					NPC shadownpc = CitizensAPI.getNPCRegistry().getById(15);
					SyncUtils.run(() -> shadownpc.despawn()).get();
					
					Location target = new Location(world, -3274.5, 75, 606.5);
					int taskid = Bukkit.getScheduler().runTaskTimer(NarMapPlugin.plugin, () -> {
						oldLoohp.faceLocation(oldNa.getEntity().getLocation());
					}, 0, 2).getTaskId();
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return world.getNearbyEntities(new Location(world, -3249.5, 67, 564.5), 2, 2, 2).contains(player);
						}
					}).get();
					
					ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "跟著這個女孩");
					
					SyncUtils.run(() -> oldNa.getNavigator().setTarget(target)).get();
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return world.getNearbyEntities(target, 6, 1, 6).contains(oldNa.getEntity());
						}
					}).get();
					
					SyncUtils.run(() -> oldNa.getNavigator().cancelNavigation()).get();
					Bukkit.getScheduler().cancelTask(taskid);
					
					WaitUtils.waitTicks(20);
					SyncUtils.run(() -> player.teleport(new Location(world, -3272, 75, 597))).get();
					SyncUtils.run(() -> EntityUtils.faceLocation(player, oldLoohp.getEntity().getLocation())).get();
					
					NarMapPlugin.plugin.disableMovement = true;
					NarMapPlugin.plugin.disableRotation = true;
					
					UUID a = UUID.fromString("ab2f656c-a96b-4c21-bd51-4cffbdeb1140");
					UUID b = UUID.fromString("1a483905-49b4-4201-aceb-af52a96ca515");
					UUID c = UUID.fromString("bb4cad4e-386e-45a6-89b0-c7eca7ab1f00");
					UUID d = UUID.fromString("70af9d6a-696b-41db-b8ae-c1248566b1b5");
					SyncUtils.run(() -> {
						ItemFrame item2018 = (ItemFrame) Bukkit.getEntity(a);
						ItemFrame item2019 = (ItemFrame) Bukkit.getEntity(b);	
						item2018.setItem(null, false);
						item2019.setItem(null, false);
						ItemStack itemstack2018 = ((Chest) item2018.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(0);
						ItemStack itemstack2019 = ((Chest) item2019.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(0);
						ItemFrame rod = (ItemFrame) Bukkit.getEntity(c);
						ItemFrame cake = (ItemFrame) Bukkit.getEntity(d);
						rod.setItem(itemstack2018.clone(), false);
						cake.setItem(itemstack2019.clone(), false);
					}).get();
					
					WaitUtils.waitTicks(20);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 呃哈嘍? ");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers(ChatColor.ITALIC + "沒人理會...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 50);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 你們看不見我嗎?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers(ChatColor.ITALIC + "還是沒人理會...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 50);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 哇？... ?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 50);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 等一下...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 50);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 我...死了嗎?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 300);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 我不會把這個叫做, \"死亡\"");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 別擔心, 我已經把你的靈魂給... 抽 出 來.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 從現在開始, 你在這個平行世界 已經自由了!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 這不是你一直在追求的嗎?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					
					String[] strs = new String[] {
							"不! 我根本不是這樣想的!",
							"這雖然是我想要的但我還未準備好要失去所有我本擁有的",
							"這也太棒了吧!這裏會有珍珠奶茶嗎?"};
					
					TextComponent[] options = new TextComponent[] { 
							new TextComponent(ChatColor.BLUE + "[" + strs[0] + "]"),
							new TextComponent(ChatColor.RED + "[" + strs[1] + "]"),
							new TextComponent(ChatColor.YELLOW + "[" + strs[2] + "]")
					};
					
					for (Player each : Bukkit.getOnlinePlayers()) {
						each.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 200, 2);
					}
					
					int answer = OptionUtils.sendRequest(3, 0, 20000, options).get();
					
					for (Player each : Bukkit.getOnlinePlayers()) {
						each.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 200, 2);
					}
					
					ChatUtils.clearChat(Bukkit.getOnlinePlayers().toArray(new Player[0]));
					
					ChatUtils.sendToAllPlayers(ChatColor.GREEN + "<" + player.getName() + "> " + strs[answer]);
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					
					if (answer == 2) {
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 我還沒有跟你要什麽呢!");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + player.getName() + "> 偶不能沒了他啊啊啊! \\_/");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					}
					
					Vector v = player.getLocation().clone().getDirection().multiply(-4);
					Location behindloc = player.getLocation().clone().add(v.clone());
					behindloc.setY(behindloc.getY() + 10);
					
					SyncUtils.run(() -> shadownpc.spawn(behindloc)).get();
					WaitUtils.waitTicks(10);
					SyncUtils.run(() -> shadownpc.teleport(behindloc, TeleportCause.PLUGIN)).get();
					SyncUtils.run(() -> shadownpc.faceLocation(shadownpc.getEntity().getLocation().clone().add(v.clone().multiply(2)))).get();
					
					SyncUtils.run(() -> oldNa.teleport(new Location(world, -3276.5, 75, 591.5, 180, 0), TeleportCause.PLUGIN)).get();
					SyncUtils.run(() -> oldLoohp.faceLocation(oldNa.getEntity().getLocation().clone().add(0, -1.8, 0))).get();
					
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 好吧，不過...");
					NarMapPlugin.plugin.disableRotation = false;
					WaitUtils.waitTicks(10);
					ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "請轉過頭來.");
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							Entity entity = RayTrace.getLookingEntity(player, 7, EntityType.PLAYER);
							return entity == null ? false : entity.equals(shadownpc.getEntity());
						}
					}).get();
					
					SyncUtils.run(() -> EntityUtils.faceLocation(player, shadownpc.getEntity().getLocation())).get();
					NarMapPlugin.plugin.disableRotation = true;
					
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 這是你現在屬於的地方, 而我終於可以脫離這個苦難惹!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 拜拜你條尾!");
					WaitUtils.waitTicks(20);
					SyncUtils.run(() -> shadownpc.teleport(shadownpc.getEntity().getLocation().add(500, 100, 0), TeleportCause.PLUGIN));
					WaitUtils.waitTicks(100);
					ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "你現在可以動了");
					
					SyncUtils.run(() -> world.getNearbyEntities(new Location(world, -3266, 74, 617), 3, 3, 3).forEach(each -> {
						if (each instanceof ItemFrame) {
							each.setGlowing(true);
						}
					})).get();
					
					NarMapPlugin.plugin.disableRotation = false;
					NarMapPlugin.plugin.disableMovement = false;
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							ItemFrame item2018 = (ItemFrame) Bukkit.getEntity(a);
							ItemFrame item2019 = (ItemFrame) Bukkit.getEntity(b);	
							ItemStack itemstack2018 = ((Chest) item2018.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(0);
							ItemStack itemstack2019 = ((Chest) item2019.getLocation().add(0, -3, 0).getBlock().getState()).getInventory().getItem(0);
							return (item2018.getItem() != null && itemstack2018.isSimilar(item2018.getItem())) && (item2019.getItem() != null && itemstack2019.isSimilar(item2019.getItem()));
						}
					}).get();
					
					SyncUtils.run(() -> world.getNearbyEntities(new Location(world, -3266, 74, 617), 3, 3, 3).forEach(each -> {
						if (each instanceof ItemFrame) {
							each.setGlowing(false);
						}
					})).get();
					
					NarMapPlugin.plugin.advan.sendProgression(player, AdvancementManager.PROG_NANA_VILLAGE);
					
					SyncUtils.run(() -> ClipboardUtils.placeBuilding("village_portal", portalloc, true)).get();
					portalloc.getWorld().playSound(portalloc, Sound.BLOCK_END_PORTAL_SPAWN, 200, 1);
					
					BoundingBox portal = BoundingBox.of(portalloc.getWorld().getBlockAt(-3256, 76, 616), portalloc.getWorld().getBlockAt(-3256, 74, 617));
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							Collection<Entity> players = portalloc.getWorld().getNearbyEntities(portal, each -> each instanceof Player);
							for (Entity entity : players) {
								entity.teleport(new Location(world, -907.5, 68, -999.5, 90, 0));
								if (entity.equals(player)) {
									return true;
								}
							}
							return false;
						}
					}).get();
					
					NarMapPlugin.plugin.advan.sendTip(player, AdvancementManager.TIP_STAGE);
					//
					future.complete(null);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			});
		}
		return future;
	}
	
	public static CompletableFuture<Void> stageNightmareTerminal() {
		CompletableFuture<Void> future = new CompletableFuture<>();
		if (!NarMapPlugin.plugin.mainCharater.isPresent()) {
			future.completeExceptionally(new RuntimeException("No main charater"));
		} else {
			Player player = NarMapPlugin.plugin.mainCharater.get();
			Bukkit.getScheduler().runTaskAsynchronously(NarMapPlugin.plugin, () -> {
				try {
					World world = NarMapPlugin.plugin.mainWorld;
					//Teleport to terminal
					SyncUtils.run(() -> {
						player.setGameMode(GameMode.SPECTATOR);
						player.setFlySpeed(0);
					}).get();
					SyncUtils.run(() -> world.setTime(18000)).get();
					//SyncUtils.run(() -> player.teleport(new Location(world, -907.5, 68, -999.5, 90, 0))).get();
					SyncUtils.run(() -> player.teleport(new Location(world, -908.817, 68.824, -995.230, 130.2F, 10.7F))).get();
					
					NPC shadownpc = CitizensAPI.getNPCRegistry().getById(15);
					SyncUtils.run(() -> shadownpc.despawn()).get();
					
					NarMapPlugin.plugin.disableInteraction = true;
					NarMapPlugin.plugin.disableMovement = true;
					NarMapPlugin.plugin.disableRotation = true;
					
					TerminalItems.resetProgressionItems();
					ClipboardUtils.clearBuilding("terminal_bars", new Location(world, -1016, 68, -999));
					
					NPC cutsceneNa = CitizensAPI.getNPCRegistry().getById(16);
					SyncUtils.run(() -> cutsceneNa.despawn()).get();
					WaitUtils.waitTicks(10);
					SyncUtils.run(() -> cutsceneNa.spawn(new Location(world, -907.5, 68, -999.5, 90, 0))).get();					
					WaitUtils.waitTicks(10);
					SyncUtils.run(() -> cutsceneNa.teleport(new Location(world, -907.5, 68, -999.5, 90, 0), TeleportCause.PLUGIN)).get();
					SyncUtils.run(() -> cutsceneNa.getNavigator().setTarget(new Location(world, -1018.5, 68, -999.5))).get();
					
					NarMapPlugin.plugin.advan.sendProgression(player, AdvancementManager.PROG_TERMINAL);
					
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 呃...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 這是哪裏啊？");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 你適應力很強的..");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 當然，當然，畢竟我也是。");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 總之，這就是你的家了! 你真正屬於的地方.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					
					String[] strs = new String[] {
							"我聽你在胡說！我才不屬於這裏呢！我屬於出面的世界!",
							"這裏除了虛空,什麽都沒有!你要我怎麽生存啊?",
							"這裏與世無爭, 也沒我想像中的差啊~"};
					
					TextComponent[] options = new TextComponent[] {
							new TextComponent(ChatColor.BLUE + "[" + strs[0] + "]"),
							new TextComponent(ChatColor.RED + "[" + strs[1] + "]"),
							new TextComponent(ChatColor.YELLOW + "[" + strs[2] + "]")
					};
					
					for (Player each : Bukkit.getOnlinePlayers()) {
						each.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 200, 2);
					}
					
					int answer = OptionUtils.sendRequest(3, 0, 15000, options).get();
					
					for (Player each : Bukkit.getOnlinePlayers()) {
						each.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 200, 2);
					}
					
					SyncUtils.run(() -> {
						player.setFlySpeed(0.08F);
						player.setGameMode(GameMode.ADVENTURE);
						player.teleport(cutsceneNa.getEntity().getLocation());
						cutsceneNa.getNavigator().cancelNavigation();
						cutsceneNa.teleport(cutsceneNa.getEntity().getLocation().add(500, 100, 0), TeleportCause.PLUGIN);
						cutsceneNa.despawn();
					}).get();
					
					NarMapPlugin.plugin.disableRotation = false;
					
					ChatUtils.clearChat(Bukkit.getOnlinePlayers().toArray(new Player[0]));
					
					ChatUtils.sendToAllPlayers(ChatColor.GREEN + "<" + player.getName() + "> " + strs[answer]);
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					
					if (answer == 2) {
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 你看!也沒這麽差嘛!");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 全個世界從現在開始也是屬於你的喇!");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + player.getName() + "> 等一下... 那,...我來之前是誰在這個世界啊?");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 呃呃!..");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 40);
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 呃!..");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 40);
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 呃啊啊啊啊!..");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 40);
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 這裏一直都是屬於你的");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + player.getName() + "> 什麽?我從來沒來過這裏啊!你這是甚麽意思?");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 你現在只是靈魂在看喲.");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers(ChatColor.ITALIC + "" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + ChatColor.ITALIC + "悄悄的提醒你：千萬千萬不要碰這些按鈕.");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 50);
					} else {
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 你沒得選!");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 所有事情都消失了，你所做的，所影響你的，都不會再出現了!");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 你已經不在是那個你，你已經從你的身體徹底的登出了..");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 你所剩下的，都將屬於我的了!");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + player.getName() + "> 這絕對不可能！你你你...怎麽能取代我所做的所有事情！");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 我不能改變你的過去，但從今以後，我會跟上.");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 然後，把他改寫成我的故事.");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 反正呢~ 我還有事要做，再見嘍..");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					}
					
					NarMapPlugin.plugin.disableMovement = false;
					NarMapPlugin.plugin.disableInteraction = false;
					
					ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "你現在可以動了");
					WaitUtils.waitTicks(20);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 所以...這是什麽? 虛空之按鈕?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 欸？門廳的最後還有東西誒.");
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return TerminalItems.getCompletedCount() >= 6;
						}						
					}).get();
					
					ItemStack mem_clock = NarMapPlugin.plugin.specialitems.get(SpecialItems.MEMORY_CLOCK);
					SyncUtils.run(() -> {
						Item clock = world.dropItem(new Location(world, -1022.5, 80, -999.5), mem_clock.clone());
						clock.setVelocity(new Vector(0, -1, 0));
					}).get();
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return player.getInventory().containsAtLeast(mem_clock, 1);
						}
					}).get();
					
					ClipboardUtils.placeBuilding("terminal_bars", new Location(world, -1016, 68, -999), true);
					SyncUtils.run(() -> shadownpc.spawn(new Location(world, -1013, 68, -999.5, -90, 0))).get();
					WaitUtils.waitTicks(10);
					SyncUtils.run(() -> shadownpc.teleport(new Location(world, -1013, 68, -999.5, -90, 0), TeleportCause.PLUGIN)).get();
					
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 爲什麽?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 告訴我爲什麽！");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 你就不能離我遠點嗎?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 我連你是誰都不知道...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 而且你連看著我講話也沒有誒...你知道這樣很沒禮貌嗎?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 我... 我...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 我不想! 這樣可以了吧?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 我是不會回頭的! 我畜心積累那麽久只是爲了這個...不行!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 欸你究竟在講什麽啊。我完全聽不懂啊！");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.MAGIC + player.getName() + ChatColor.RESET + "> 安靜! 把那個時鐘交給我，那麽沒有東西會被污染.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 我有個鬼主意! 如果我這樣...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 50);
					ChatUtils.sendToAllPlayers(ChatColor.ITALIC + player.getName() + " 隨機按下時鐘的其中一個按鈕...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					SyncUtils.run(() -> {
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 150, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 150, 0));
					}).get();
					WaitUtils.waitTicks(40);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 發生什麽事?");
					WaitUtils.waitTicks(80);
					SyncUtils.run(() -> player.teleport(new Location(world, 2536.5, 64, 3875.5))).get();
					
					NarMapPlugin.plugin.advan.sendProgression(player, AdvancementManager.PROG_NANA_NEWBASE);
					
					NarMapPlugin.plugin.disableMovement = true;
					NarMapPlugin.plugin.disableInteraction = true;
					
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 我在...我家嗎?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 這裏不就我蓋的嗎...?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 所以我現在要幹嘛? 我猜我可以到處逛逛吧...");
					WaitUtils.waitTicks(50);
					ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "嘗試周圍找找來尋找呂吧 (他在室内喔)");
					
					NPC recueloohp = CitizensAPI.getNPCRegistry().getById(20);
					SyncUtils.run(() -> recueloohp.despawn()).get();
					NPC shadownar = CitizensAPI.getNPCRegistry().getById(21);
					SyncUtils.run(() -> shadownar.despawn()).get();
					
					NarMapPlugin.plugin.disableMovement = false;
					NarMapPlugin.plugin.disableInteraction = false;
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return world.getNearbyEntities(new Location(world, 2504.5, 76.5, 3829.5), 3, 3, 3).contains(player);
						}
					}).get();
					
					ChatUtils.sendToAllPlayers("<Biggy0527> 啊!! 你剛剛說你要什麽?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					
					strs = new String[] {
							"誰把你鎖在這裏了?",
							"你在這裏幹嘛?",
							"爲什麽我家會變成這樣?",
							"我想吃壽司!"};
					
					options = new TextComponent[] {
							new TextComponent(ChatColor.BLUE + "[" + strs[0] + "]"),
							new TextComponent(ChatColor.RED + "[" + strs[1] + "]"),
							new TextComponent(ChatColor.YELLOW + "[" + strs[2] + "]"),
							new TextComponent(ChatColor.GREEN + "[" + strs[3] + "]")
					};
					
					for (Player each : Bukkit.getOnlinePlayers()) {
						each.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 200, 2);
					}
					
					answer = OptionUtils.sendRequest(4, 0, 20000, options).get();
					
					for (Player each : Bukkit.getOnlinePlayers()) {
						each.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 200, 2);
					}
					
					ChatUtils.clearChat(Bukkit.getOnlinePlayers().toArray(new Player[0]));
					
					ChatUtils.sendToAllPlayers(ChatColor.GREEN + "<" + player.getName() + "> " + strs[answer]);
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					
					if (answer == 0) {
						ChatUtils.sendToAllPlayers("<Biggy0527> 額... 你啊! 你好久以前做的啊!");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + player.getName() + "> 什麽? 我沒有啊!");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<Biggy0527> 額... 你有啊! 你幾個月前做的啊!");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					} else if (answer == 1) {
						ChatUtils.sendToAllPlayers("<Biggy0527> 你把我鎖在這的啊！我才不會想留在這裏呢!");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + player.getName() + "> 我發誓我才剛剛來到這個詭異的地方.");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					} else if (answer == 2) {
						ChatUtils.sendToAllPlayers("<Biggy0527> 什麽意思?這裏一直都這樣啊，有什麽不一樣嗎?");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<Biggy0527> 你自己蓋的啊...");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + player.getName() + "> 對...也不對！這裏不完全跟我蓋的一樣啊");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					} else {
						ChatUtils.sendToAllPlayers("<Biggy0527> 你五分鐘前才剛剛離開呢...");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<Biggy0527> 你不是說肚子痛嗎？");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						ChatUtils.sendToAllPlayers("<" + player.getName() + "> 我沒有...");
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					}
					
					SyncUtils.run(() -> recueloohp.spawn(new Location(world, 2495.5, 76, 3829.5))).get();
					WaitUtils.waitTicks(10);
					SyncUtils.run(() -> recueloohp.teleport(new Location(world, 2495.5, 76, 3829.5), TeleportCause.PLUGIN)).get();
					AtomicBoolean look = new AtomicBoolean(true);
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							recueloohp.faceLocation(player.getEyeLocation().clone().add(0, -1.8, 0));
							return !look.get();
						}
					});
					
					ChatUtils.sendToAllPlayers("<LOOHP> Lui我在這... 等我一下啦...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 我以爲 " + player.getName() + " 剛走呢... 哇賽!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 50);
					ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "轉身...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 蛤? 誰? 我嗎? 什麽啦...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 呃... 是我問題還是... " + player.getName() + " 看起來有些不同?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 呃...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 這是我第一次看你...呃... 這樣穿搭？");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 感覺你變好了.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + ">怎麽說?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> Emm 例如，您沒有立即嘗試殺我");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 蛤?我怎麽會想殺了你呀?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 自從你在洞窟裏找到那個魔法時間記憶時鐘.你就...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 想要...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 50);
					ChatUtils.sendToAllPlayers("<LOOHP> 改變... 事情.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 可是我從來沒有在洞窟裏找到任何時鐘啊.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 我在一座橋的中央找到這個時鐘.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 我隨便亂按了幾顆按鈕然後就帶我來到這裏惹.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 噢! 是不是跟電影裏的情節那樣...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 你是從平行時空的未來來的!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 然後你找到了這個時鐘!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers(ChatColor.ITALIC + "林自言自語: 我也滿好奇我在你的未來是怎麽樣的一個人...?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 噢, 難怪她把我鎖住了這麽久!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 所以她是嘗試著改變未來.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 那你應該可以用這個時鐘帶你回去然後...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 告訴她... 她根本不需要那個時鐘! ..她的未來一切安好!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					
					look.set(false);
					
					SyncUtils.run(() -> shadownar.spawn(new Location(world, 2492.2, 74, 3833.7))).get();
					WaitUtils.waitTicks(10);
					SyncUtils.run(() -> shadownar.teleport(new Location(world, 2492.2, 74, 3833.7), TeleportCause.PLUGIN)).get();
					SyncUtils.run(() -> recueloohp.faceLocation(shadownar.getEntity().getLocation().clone().add(0, -1.8, 0))).get();
					SyncUtils.run(() -> shadownar.faceLocation(recueloohp.getEntity().getLocation().clone().add(0, -1.8, 0))).get();
					
					ChatUtils.sendToAllPlayers("<" + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.RESET + "> 走屁走啊...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.RESET + "> 只有我才有方法，才知道怎麽樣把事情...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.RESET + "> ...回到正軌，回到我想要的結果.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.RESET + "> 你絕對不能夠破壞這個計劃!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.RESET + "> 準備好接受被回憶時鐘的力量吞噬嘛?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 噢不... 我們留太長了啦...情況不太好呢...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> " + player.getName() + ",全村的希望就在你手上!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 我可能不會相信這個在我的時空的 \"你\" , 但我選擇相信你.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 去吧... 去吧!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 等一下，讓我把最後的話説完.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<LOOHP> 嗯?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 你在另一個平行時空裏並沒有這麽不一樣.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					
					ChatUtils.sendToAllPlayers(ChatColor.ITALIC + player.getName() + " 在時鐘上隨機按下按鈕...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					SyncUtils.run(() -> {
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 0));
					}).get();
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 50);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 請帶我回到可以修理這個的時候..!");
					WaitUtils.waitTicks(50);
					SyncUtils.run(() -> player.teleport(new Location(world, 2159, 38, -523))).get();
					ChatUtils.sendToAllPlayers("林 被 " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.RESET + " (基於劇情需要) 而被擊斃..");
					for (Player each : Bukkit.getOnlinePlayers()) {
						each.playSound(each.getLocation(), Sound.ENTITY_PLAYER_DEATH, 5, 1);
					}
					
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 哇哦... 這是多久之前哪...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 求神拜佛...我還記住方向");
					WaitUtils.waitTicks(50);
					
					NarMapPlugin.plugin.advan.sendProgression(player, AdvancementManager.PROG_NANA_S3BASE);
					
					ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "嘗試周圍找找來尋找娜吧 (她在地面喔)");
					
					NPC miningNar = CitizensAPI.getNPCRegistry().getById(22);
					SyncUtils.run(() -> miningNar.spawn(new Location(world, 2110.5, 63, -483.5))).get();
					WaitUtils.waitTicks(10);
					SyncUtils.run(() -> miningNar.teleport(new Location(world, 2110.5, 63, -483.5), TeleportCause.PLUGIN)).get();
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return miningNar.getEntity().getNearbyEntities(5, 5, 5).contains(player);
						}
					}).get();
					
					look.set(true);
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							miningNar.faceLocation(player.getEyeLocation().clone().add(0, -1.8, 0));
							return !look.get();
						}
					});
					
					ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 你好! 很高興認識你喔!哇哦我們長得很像餒!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 欸？我們連名字也差不多的耶,太可愛了吧! XD");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 你在幹嘛啊? :P");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 我聽到這裏有一個傳説中的物品能帶我到未來看看餒!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 和我一起吧!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					
					look.set(false);
					
					SyncUtils.run(() -> miningNar.getNavigator().setTarget(new Location(world, 2110, 11, -381))).get();
					
					List<String> strList = new ArrayList<>();
					strList.add("未來也不是這麽酷嘛, 對吧?");
					strList.add("你不再找它我就請你喝珍珠奶茶.");
					strList.add("你知道嗎，沒有時鐘的加速，你就能永保青春美麗!");
					strList.add("你要知道, 事情本就應這樣發生啊...");
					
					List<String> strListCopy = new ArrayList<>(strList);
					
					while (true) {
						options = new TextComponent[strList.size()];
						for (int i = 0; i < options.length; i++) {
							switch (i) {
							case 0:
								options[0] = new TextComponent(ChatColor.BLUE + "[" + strList.get(0) + "]");
								break;
							case 1:
								options[1] = new TextComponent(ChatColor.RED + "[" + strList.get(1) + "]");
								break;
							case 2:
								options[2] = new TextComponent(ChatColor.YELLOW + "[" + strList.get(2) + "]");
								break;
							default:
								options[i] = new TextComponent(ChatColor.GREEN + "[" + strList.get(3) + "]");
								break;
							}
						}
						
						for (Player each : Bukkit.getOnlinePlayers()) {
							each.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 200, 2);
						}
						
						answer = OptionUtils.sendRequest(4, 0, 10000, options).get();
						
						for (Player each : Bukkit.getOnlinePlayers()) {
							each.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 200, 2);
						}
						
						ChatUtils.clearChat(Bukkit.getOnlinePlayers().toArray(new Player[0]));
						
						ChatUtils.sendToAllPlayers(ChatColor.GREEN + "<" + player.getName() + "> " + strList.get(answer));
						WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
						
						if (strListCopy.indexOf(strList.get(answer)) == 0) {
							ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 對吧, 可能真的沒這麽酷");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 但是我還是想一看究竟餒");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 那樣我就可以改造自己變得更酷炫了啦哦吼!");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							strList.remove(answer);
						} else if (strListCopy.indexOf(strList.get(answer)) == 1) {
							ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 哎喲不錯哦!");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 可是我今天已經喝了一杯");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 不想再多喝一杯:/有點膩");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							strList.remove(answer);
						} else if (strListCopy.indexOf(strList.get(answer)) == 2) {
							ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 噢... 呃... 這樣啊...");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 是這樣的... 我呢... 呃... 應該不應該再繼續找那個時鐘惹");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 還是! 我直接再時間回流，我就能變得更加更加美哦呵呵呵呵呵~");
							WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
							ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 我需要辣個時鐘啊啊啊啊!!!");
							strList.remove(answer);
						} else {
							break;
						}
					}
					
					SyncUtils.run(() -> miningNar.getNavigator().cancelNavigation()).get();
					
					look.set(true);
					
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							miningNar.faceLocation(player.getEyeLocation().clone().add(0, -1.8, 0));
							return !look.get();
						}
					});
					
					ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 什麽意思?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 我知道你有選擇困難，我也知道未來是奪莫的不可預測");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 我已經經歷過你所經歷的,還有更多，多到你不能想象的");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 怎麽可能? 沒有人會真的...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 噓.. 我是你啦,未來的你. 我知道!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 我不信！告訴我我自己才知道的東西.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> " + ChatColor.MAGIC + "...哎呀我想不到什麽東西說.也太無聊了吧,甘安捏~.");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 那.. 我應該做什麽?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 你不需要特意做任何事, 無論怎樣, 都會是最好的選擇, 而且會逐漸成爲最好的你喲!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 答應我. 答應你的禮物 你未來的好朋友們.答應你自己!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 嗯... 嗯... 嗯...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 好啦. 好啦. 我答應!");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<nanannaarrrrrrr> 但你現在要去哪裏啊?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 我也不知道呢!但應該是你以後會去到的!還有，我保證,我絕對不會讓你失望的!");
					WaitUtils.waitTicks(100);
					
					SyncUtils.run(() -> {
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 250, 0));
						player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 250, 0));
					}).get();
					WaitUtils.waitTicks(100);
					SyncUtils.run(() -> player.teleport(new Location(world, 17, 75.5626, -87, 0, 0))).get();
					
					Location item_Location = new Location(world, 22, 75, -86);
					ClipboardUtils.placeBuilding("item_collection", item_Location, true);
					
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 現在是10月23號了嗎?");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 噢.. 過了這麽久, 我還差一樣物品...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers("<" + player.getName() + "> 但我不知道在哪裏啊...");
					WaitUtils.waitTicks(NarMapPlugin.plugin.chatDelayMultiplier * 100);
					ChatUtils.sendToAllPlayers(ChatColor.YELLOW + "" + ChatColor.ITALIC + "完成收藏 (❁活在當下❁)");
					
					Location lastSlot = new Location(world, 24.5, 76, -86.5);
					SyncUtils.waitUntilTrue(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return player.getLocation().distanceSquared(lastSlot) <= 0.3;
						}
					}).get();
					
					NarMapPlugin.plugin.advan.sendTip(player, AdvancementManager.TIP_STAGE);
					
					SyncUtils.run(() -> {
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 150, 0));
					}).get();
					
					SyncUtils.run(() -> player.teleport(NarMapPlugin.plugin.blackBox)).get();
					WaitUtils.waitTicks(20);
					
					ChatUtils.sendLetterByLetterTitleToAllPlayers(ChatColor.YELLOW + "不管過去發生了什麼", "", 2, 50, 15, NSound.SFX_BEEP).get();
					WaitUtils.waitTicks(80 + 15);
					ChatUtils.sendLetterByLetterTitleToAllPlayers("有什麼能比現在重要", "", 2, 50, 15, NSound.SFX_BEEP).get();
					WaitUtils.waitTicks(80 + 15);
					ChatUtils.sendTitleToAllPlayers("今天的主角", "那個我們捧在手心裏呵護的小女孩!", 15, 80, 15);
					WaitUtils.waitTicks(15 + 80 + 15);
					WaitUtils.waitTicks(40);
					ChatUtils.sendTitleToAllPlayers(ChatColor.YELLOW + "Happy " + ChatColor.GOLD + "Birthday!", ChatColor.YELLOW + player.getName(), 15, 80, 15);
					WaitUtils.waitTicks(15 + 80 + 15);
					WaitUtils.waitTicks(40);
					
					/*
					SyncUtils.run(() -> {
						for (Player each : Bukkit.getOnlinePlayers()) {
							PacketPlayOutGameStateChange packet = new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.e, 1);
							((CraftPlayer) each).getHandle().playerConnection.sendPacket(packet);
						}
					}).get();
					*/
					
					NarMapPlugin.plugin.advan.sendProgression(player, AdvancementManager.PROG_COMPLETE);
					SyncUtils.run(() -> player.teleport(new Location(world, 10002.5, 100, 10002.5, 180, 0))).get();
					SyncUtils.run(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0))).get();
					
					//
					future.complete(null);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			});
		}
		return future;
	}

}
