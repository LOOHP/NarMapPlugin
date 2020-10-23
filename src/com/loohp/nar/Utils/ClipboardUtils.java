package com.loohp.nar.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.BoundingBox;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockState;

public class ClipboardUtils {
	
	public static void placeBuilding(String schematicFile, Location location, boolean ignoreAir) {
		if (!Bukkit.isPrimaryThread()) {
			try {
				SyncUtils.run(() -> placeBuilding(schematicFile, location, ignoreAir)).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			return;
		}
		File file = new File(Bukkit.getWorldContainer() + "/plugins/WorldEdit/schematics", schematicFile.endsWith(".schem") ? schematicFile : schematicFile + ".schem");
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		try {
			ClipboardReader reader = format.getReader(new FileInputStream(file));
		    Clipboard clipboard = reader.read();
		    reader.close();
		   
			EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(location.getWorld())).build();
		    Operation operation = new ClipboardHolder(clipboard)
				.createPaste(editSession)
			    .to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
			    .ignoreAirBlocks(ignoreAir)
			    .copyBiomes(true)
			    .copyEntities(true)
			    .build();
			Operations.complete(operation);
			editSession.close();
		} catch (WorldEditException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void clearBuilding(String schematicFile, Location location) {
		if (!Bukkit.isPrimaryThread()) {
			try {
				SyncUtils.run(() -> clearBuilding(schematicFile, location)).get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			return;
		}
		File file = new File(Bukkit.getWorldContainer() + "/plugins/WorldEdit/schematics", schematicFile.endsWith(".schem") ? schematicFile : schematicFile + ".schem");
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		try {
			ClipboardReader reader = format.getReader(new FileInputStream(file));
		    Clipboard clipboard = reader.read();
		    reader.close();
		    
		    Iterator<BlockVector3> itr = clipboard.getRegion().iterator();
		    BlockState air = BukkitAdapter.adapt(Bukkit.createBlockData(Material.AIR));
		    while (itr.hasNext()) {
		    	BlockVector3 blockpos = itr.next();
		    	clipboard.setBlock(blockpos, air);
		    }

			EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(location.getWorld())).build();
		    Operation operation = new ClipboardHolder(clipboard)
				.createPaste(editSession)
			    .to(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))
			    .build();
			Operations.complete(operation);
			editSession.close();
		} catch (WorldEditException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static BoundingBox getBuildingRegion(String schematicFile, Location location) {
		if (!Bukkit.isPrimaryThread()) {
			CompletableFuture<BoundingBox> future = new CompletableFuture<BoundingBox>();
			try {
				SyncUtils.run(() -> future.complete(getBuildingRegion(schematicFile, location)));
				return future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			return null;
		}
		File file = new File(Bukkit.getWorldContainer() + "/plugins/WorldEdit/schematics", schematicFile.endsWith(".schem") ? schematicFile : schematicFile + ".schem");
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		try {
			ClipboardReader reader = format.getReader(new FileInputStream(file));
		    Clipboard clipboard = reader.read();
		    reader.close();
		    
		    Region region = clipboard.getRegion();
		    
		    return BoundingBox.of(BukkitAdapter.adapt(location.getWorld(), region.getMaximumPoint()).getBlock(), BukkitAdapter.adapt(location.getWorld(), region.getMinimumPoint()).getBlock());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
