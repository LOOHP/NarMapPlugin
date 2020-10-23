package com.loohp.nar.Utils;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.loohp.nar.NarMapPlugin;

public class SyncUtils {
	
	public static CompletableFuture<Void> run(Runnable task) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		Bukkit.getScheduler().runTask(NarMapPlugin.plugin, () -> {
			task.run();
			future.complete(null);
		});
		return future;
	}
	
	public static <T> CompletableFuture<T> run(Supplier<T> task) {
		CompletableFuture<T> future = new CompletableFuture<>();
		Bukkit.getScheduler().runTask(NarMapPlugin.plugin, () -> {
			future.complete(task.get());
		});
		return future;
	}
	
	public static CompletableFuture<Void> waitUntilTrue(Callable<Boolean> callable) {
		CompletableFuture<Void> future = new CompletableFuture<>();
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					if (callable.call()) {
						future.complete(null);
						this.cancel();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.runTaskTimer(NarMapPlugin.plugin, 0, 1);
		return future;
	}

}
