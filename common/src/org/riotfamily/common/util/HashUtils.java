/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class that simplifies the generation of MD5 and SHA-1 hashes. 
 */
public final class HashUtils {

	public static final String MD5 = "MD5";
	
	public static final String SHA1 = "SHA-1";
	
	private HashUtils() {
	}
	
	/** 
	 * Hashes a String using the MD5 algorithm and returns the result as a
	 * String of hexadecimal numbers.
	 */
	public static String md5(String data) {
		return hash(data, MD5);
	}
	
	/** 
	 * Hashes a byte array using the MD5 algorithm and returns the result as a
	 * String of hexadecimal numbers.
	 */
	public static String md5(byte[] data) {
		return hash(data, MD5);
	}
	
	/** 
	 * Hashes an InputStream using the MD5 algorithm and returns the result as 
	 * a String of hexadecimal numbers.
	 */
	public static String md5(InputStream in) throws IOException {
		return hash(in, MD5);
	}
	
	/** 
	 * Hashes a String using the SHA-1 algorithm and returns the result as a
	 * String of hexadecimal numbers.
	 */
	public static String sha1(String data) {
		return hash(data, SHA1);
	}
	
	/** 
	 * Hashes a byte array using the SHA-1 algorithm and returns the result as 
	 * a String of hexadecimal numbers.
	 */
	public static String sha1(byte[] data) {
		return hash(data, SHA1);
	}
	
	/**
	 * Hashes a String using the specified algorithm and returns the result as 
	 * a String of hexadecimal numbers.
	 * 
	 * @param data the String to compute the hash of
	 * @return the computed hash
	 */
	public static String hash(String data, String algorithm) {
		return hash(data.getBytes(), algorithm);
	}
	
	/**
	 * Hashes a byte array using the specified algorithm and returns the 
	 * result as a String of hexadecimal numbers.
	 * 
	 * @param data the bytes to compute the hash of
	 * @return the computed hash
	 */
	public static String hash(byte[] data, String algorithm) {
		MessageDigest digest = createDigest(algorithm);
		digest.update(data);
		return toHex(digest.digest());
	}
	
	/**
	 * Hashes data read from an InputStream using the specified algorithm 
	 * and returns the result as a String of hexadecimal numbers.
	 * 
	 * @param in the stream to read the data from
	 * @return the computed hash
	 */
	public static String hash(InputStream in, String algorithm)
			throws IOException {
		
		MessageDigest digest = createDigest(algorithm);
		byte[] buffer = new byte[8192];
		int i = 0;
		while( (i = in.read(buffer)) > 0) {
			digest.update(buffer, 0, i);
		}
		in.close();
		return toHex(digest.digest());
	}
	
	/**
	 * Creates a MessageDigest for the given algorithm. 
	 * NoSuchAlgorithmExceptions are caught and re-thrown as RuntimeExceptions.
	 */
	private static MessageDigest createDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Converts an array of bytes into a String representing each byte as an
	 * unsigned hex number.
	 * 
	 * @param buffer array of bytes to convert
	 * @return generated hex string
	 */
	private static String toHex(byte[] buffer) {
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
