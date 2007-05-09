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
package org.riotfamily.common.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

/**
 * Provides utility methods to invoke commands via Runtime.exec().
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public final class CommandUtils {

	private static final Log log = LogFactory.getLog(CommandUtils.class);

	private CommandUtils() {
	}

	public static String exec(String executable) throws IOException {
		return exec(new String[] {executable});
	}

	public static String exec(String executable, String arg) throws IOException {
		return exec(new String[] {executable, arg});
	}

	public static String exec(List commandLine) throws IOException {
		return exec(StringUtils.toStringArray(commandLine));
	}

	public static String exec(String[] commandLine) throws IOException {
		Process proc = Runtime.getRuntime().exec(commandLine);
		try {
			int exitStatus = proc.waitFor();
			if (exitStatus == 0) {
				return capture(proc.getInputStream());
			}
			else {
				String msg = capture(proc.getErrorStream());
				log.error("Error invoking command '"
						+ StringUtils.arrayToDelimitedString(commandLine, " ")
						+ "':\n" + msg);

				throw new IOException(msg);
			}
		}
		catch (InterruptedException e) {
			throw new IOException();
		}
	}

	private static String capture(InputStream in) {
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				if (buffer.length() > 0) {
					buffer.append('\n');
				}
				buffer.append(line);
			}
			in.close();
		}
		catch (Exception e) {
		}
		return buffer.toString();
	}
}
