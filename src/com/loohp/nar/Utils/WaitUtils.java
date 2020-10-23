package com.loohp.nar.Utils;

import java.util.concurrent.TimeUnit;

public class WaitUtils {
	
	public static void waitTicks(int ticks) {
		try {
			TimeUnit.MILLISECONDS.sleep(ticks * 50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void waitTicks(double ticks) {
		waitTicks((int) Math.round(ticks));
	}

}
