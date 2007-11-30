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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class that simplifies the generation of MD5 fingerprints. 
 */
public final class HashUtils {

	private static final String MD5 = "MD5";
	
	private static final String SHA1 = "SHA-1";
	
	private static MessageDigest md5Digest;
	
	private static MessageDigest sha1Digest;
	
	private HashUtils() {
	}
	
	/**
	 * Hashes a String using the MD5 algorithm and returns the result as a
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
	 * Hashes a String using the SHA-1 algorithm and returns the result as a
	 * String of hexadecimal numbers. This method is synchronized to avoid
	 * excessive MessageDigest object creation. If calling this method becomes a
	 * bottleneck in your code, you may wish to maintain a pool of MessageDigest
	 * objects instead of using this method.
	 * 
	 * @param data the String to compute the hash of.
	 * @return a hashed version of the passed-in String
	 */
	public static synchronized String sha1(String data) {
		if (sha1Digest == null) {
			try {
				sha1Digest = MessageDigest.getInstance(SHA1);
			}
			catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}
		sha1Digest.update(data.getBytes());
		return toHex(sha1Digest.digest());
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
			s = Integer.toHexString(buffer[i] & 0xff);
			if (s.length() < 2) {
				sb.append('0');
			}
			sb.append(s);
		}
		return sb.toString();
	}

}
