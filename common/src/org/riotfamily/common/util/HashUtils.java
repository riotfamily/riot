package org.riotfamily.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class that simplifies the generation of MD5 fingerprints. 
 */
public final class HashUtils {

	private static final String MD5 = "MD5";
	
	private static MessageDigest md5Digest;
	
	private HashUtils() {
	}
	
	/**
	 * Hashes a String using the Md5 algorithm and returns the result as a
	 * String of hexadecimal numbers. This method is synchronized to avoid
	 * excessive MessageDigest object creation. If calling this method becomes a
	 * bottleneck in your code, you may wish to maintain a pool of MessageDigest
	 * objects instead of using this method.
	 * 
	 * @param data the String to compute the hash of.
	 * @return a hashed version of the passed-in String
	 */
	public static synchronized String md5(String data) {
		if (md5Digest == null) {
			try {
				md5Digest = MessageDigest.getInstance(MD5);
			}
			catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}
		md5Digest.update(data.getBytes());
		return toHex(md5Digest.digest());
	}
	
	/**
	 * Converts an array of bytes into a String representing each byte as an
	 * unsigned hex number.
	 * 
	 * @param buffer array of bytes to convert
	 * @return generated hex string
	 */
	public static String toHex(byte[] buffer) {
		StringBuffer sb = new StringBuffer();
		String s = null;
		for (int i = 0; i < buffer.length; i++) {
			s = Integer.toHexString((int) buffer[i] & 0xff);
			if (s.length() < 2) {
				sb.append('0');
			}
			sb.append(s);
		}
		return sb.toString();
	}

}
