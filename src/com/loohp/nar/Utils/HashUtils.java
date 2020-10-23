package com.loohp.nar.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class HashUtils {
	
	public static byte[] createSha1(File file) throws Exception  {
	    return createSha1(new FileInputStream(file));
	}
	
	public static byte[] createSha1(InputStream fis) throws Exception  {
	    MessageDigest digest = MessageDigest.getInstance("SHA-1");
	    int n = 0;
	    byte[] buffer = new byte[8192];
	    while (n != -1) {
	        n = fis.read(buffer);
	        if (n > 0) {
	            digest.update(buffer, 0, n);
	        }
	    }
	    return digest.digest();
	}

}
