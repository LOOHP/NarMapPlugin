package com.loohp.nar.Utils;

import org.bukkit.Chunk;
import org.bukkit.World;

public class ChunkUtils {
	
	public static void setChunksAroundLoaded(Chunk chunk, boolean loaded) {
		World world = chunk.getWorld();
		int chunkX = chunk.getX();
		int chunkZ = chunk.getZ();
		
		for (int x = chunkX - 1; x < chunkX + 2; x++) {
			for (int z = chunkZ - 1; z < chunkZ + 2; z++) {
				world.setChunkForceLoaded(x, z, loaded);
			}
		}
	}

}
