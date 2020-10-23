package com.loohp.nar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.loohp.nar.Listeners.Events;
import com.loohp.nar.Story.Cosmetics;
import com.loohp.nar.Story.Ending;
import com.loohp.nar.Story.ItemCollection;
import com.loohp.nar.Story.MailBox;
import com.loohp.nar.Story.MainSequence;
import com.loohp.nar.Story.Minesweeper;
import com.loohp.nar.Story.MusicManager;
import com.loohp.nar.Story.SpecialItems;
import com.loohp.nar.Story.StartMenu;
import com.loohp.nar.Story.StartMenu.Option;
import com.loohp.nar.Story.TerminalItems;
import com.loohp.nar.Utils.AdvancementManager;
import com.loohp.nar.Utils.HashUtils;
import com.loohp.nar.Utils.SignMenuFactory;

import net.md_5.bungee.api.ChatColor;

public class NarMapPlugin extends JavaPlugin {
	
	public static final String RESOURCE_PACK_URL = "http://files.loohpjames.com/narmap/NarMapPack.zip";
	public static byte[] RESOURCE_PACK_HASH;
	public static final AtomicBoolean WAIT_SWITCH = new AtomicBoolean(false);
	
	private DatagramSocket socket;
	
	public static NarMapPlugin plugin;
	public static ProtocolManager protocolmanager;
	
	public Cosmetics cosmetics;
	public World mainWorld;
	
	public Location menuSpawn;
	public Location blackBox;
	public Location resourcePackTest;
	
	public AdvancementManager advan;
	public SpecialItems specialitems;
	public SignMenuFactory signMenuFactory;
	public WorldGuardRegionManager wgrm;
	
	public Minesweeper minesweeper;
	
	public Optional<Player> mainCharater = Optional.empty();
	public AtomicBoolean gameOngoing = new AtomicBoolean(false);
	
	public Optional<Integer> chosenOption = Optional.empty();
	public AtomicBoolean playerToggle = new AtomicBoolean(false);
	public Optional<Block> knockedDoor = Optional.empty();
	
	public boolean terminalStage = false;
	
	public boolean disableCustomSLP = false;
	
	public boolean disableMovement = false;
	public boolean disableRotation = false;
	public boolean disableInteraction = false;
	public boolean disableBeds = true;
	public boolean disableDamage = true;
	
	public double chatDelayMultiplier = 1;
	
	public AtomicInteger sleptTicks = new AtomicInteger(0);
	
	@Override
	public void onEnable() {
		plugin = this;
		protocolmanager = ProtocolLibrary.getProtocolManager();
		getConfig().options().copyDefaults(true);
		saveConfig();
		reloadConfig();
		
		try {
			ReadableByteChannel rbc = Channels.newChannel(new URL(RESOURCE_PACK_URL).openStream());
			File zip = new File(getDataFolder(), "temp_pack.zip");
			FileOutputStream fos = new FileOutputStream(zip);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	        fos.close();
	        
	        RESOURCE_PACK_HASH = HashUtils.createSha1(zip);
	        zip.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		chatDelayMultiplier = getConfig().getDouble("ChatDelayMultiplier", 1);
		
		mainWorld = Bukkit.getWorlds().get(0);
		blackBox = new Location(mainWorld, 0.5, 1, 0.5);
		resourcePackTest = new Location(mainWorld, 998.5, 23.5, 988.5, 90, 21);
		menuSpawn = new Location(mainWorld, -3.5, 10, 17.5, 0, 0);
		
		cosmetics = new Cosmetics(this);
		specialitems = new SpecialItems(this);
		advan = new AdvancementManager(this);
		wgrm = new WorldGuardRegionManager(this);
		signMenuFactory = new SignMenuFactory(this);
		
		minesweeper = new Minesweeper(this);
		
		getCommand("nar").setExecutor(new Commands());
		getServer().getPluginManager().registerEvents(new Events(), this);
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "NarMapPlugin is up and running!");
		
		TerminalItems.detect();
		ItemCollection._init_();
		MailBox.check();
		StartMenu.startup();
		MusicManager.setup();
		Ending.startup();
		//blockSLP();
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getName().equalsIgnoreCase(NarMapPlugin.plugin.getConfig().getString("MainCharacter"))) {
				NarMapPlugin.plugin.mainCharater = Optional.of(player);
			}
		}
		
		Bukkit.getScheduler().runTaskLater(this, () -> {
			WAIT_SWITCH.set(true);
		}, 20);
		
		Bukkit.getScheduler().runTaskTimer(this, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.setFoodLevel(18);
			}
		}, 0, 20);
		
		try {
			socket = new DatagramSocket();
			byte[] buf = ("[MOTD]" + ChatColor.YELLOW + "Nana的旅程[/MOTD][AD][/AD]").getBytes(StandardCharsets.UTF_8);
			Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
				try {
					socket.send(new DatagramPacket(buf, buf.length, Inet4Address.getByName("224.0.2.60"), 4445));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}, 0, 40);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisable() {
		cosmetics.safeShutdown();
		getServer().getScheduler().cancelTasks(this);
		socket.close();
	}
	
	public synchronized CompletableFuture<Boolean> startStage(Option stage) {
		CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
		if (gameOngoing.get()) {
			future.complete(false);
		} else {
			gameOngoing.set(true);
			Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
				try {
					if (!stage.equals(Option.START_GAME) && !stage.equals(Option.STAGE_BEGIN) && !stage.equals(Option.STAGE_NIGHTMARE) && !stage.equals(Option.STAGE_REVISIT)) {
						gameOngoing.set(false);
						future.complete(false);
					} else {
						future.complete(true);
						gameOngoing.set(true);
						if (stage.equals(Option.START_GAME) || stage.equals(Option.STAGE_BEGIN)) {
							MainSequence.stageBeginning().get();
						}
						if (stage.equals(Option.START_GAME) || stage.equals(Option.STAGE_BEGIN) || stage.equals(Option.STAGE_NIGHTMARE)) {
							MainSequence.stageNightmareVillage().get();
						}
						if (stage.equals(Option.START_GAME) || stage.equals(Option.STAGE_BEGIN) || stage.equals(Option.STAGE_NIGHTMARE) || stage.equals(Option.STAGE_REVISIT)) {
							MainSequence.stageNightmareTerminal().get();
						}
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				gameOngoing.set(false);
			});
		}
		return future;
	}
	
	public static void sendResourcePack(Player player) {
		player.setResourcePack(NarMapPlugin.RESOURCE_PACK_URL, NarMapPlugin.RESOURCE_PACK_HASH);
	}
	
	public void blockSLP() {
		protocolmanager.addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Status.Client.START) {
			@Override
		    public void onPacketReceiving(PacketEvent event) {
		        event.setCancelled(true);
			}
		});
	}

}
