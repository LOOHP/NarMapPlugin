package com.loohp.nar.Utils;

import java.io.File;

public class CustomFileUtils {
	
	public static void deleteFolderRecrusively(File file) {
		if (file.isDirectory()) {
			for (File each : file.listFiles()) {
				if (each.isDirectory()) {
					deleteFolderRecrusively(each);
					each.delete();
				} else {
					each.delete();
				}
			}
		} else {
			file.delete();
		}
	}

}
