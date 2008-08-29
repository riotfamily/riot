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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.io;

import java.io.File;
import java.util.Iterator;

import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.log.RiotLog;

/**
 * Iterator that iterates recursively over all files in a directory. Calling
 * {@link #next()} does not return nested directories, instead regular files
 * contained in these directories are returned. Note that calling 
 * {@link #remove()} will also delete all empty parent directories up to the
 * base directory specified in the constructor.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class RecursiveFileIterator implements Iterator<File> {

	private RiotLog log = RiotLog.get(RecursiveFileIterator.class);
	
	private File dir;
	
	private File[] files;
	
	private RecursiveFileIterator nested;
	
	private File next;
	
	private File prev;
	
	int i = 0;
	
	public RecursiveFileIterator(File file) {
		if (file.isDirectory()) {
			dir = file;
			files = file.listFiles();
			next = getNextFile();
		}
		else {
			next = file;
		}
	}
	
	public boolean hasNext() {
		return next != null;
	}

	private File getNextFile() {
		if (dir != null) {
			if (nested != null && nested.hasNext()) {
				return nested.next();
			}
			while (i < files.length) {
				nested = new RecursiveFileIterator(files[i++]);
				if (nested.hasNext()) {
					return nested.next();
				}
			}
		}
		return null;
	}
	
	public File next() {
		prev = next;
		next = getNextFile();
		return prev;
	}

	public void remove() {
		log.debug("Deleting file " + prev.getAbsolutePath());
		prev.delete();
		File dir = prev.getParentFile();
		while (!this.dir.equals(dir) && dir.list().length == 0) {
			log.debug("Deleting empty dir: " + dir.getAbsolutePath());
			dir.delete();
			dir = dir.getParentFile();
		}
	}
	
}
