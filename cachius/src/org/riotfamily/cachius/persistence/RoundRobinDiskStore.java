/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.cachius.persistence;

import java.io.File;
import java.io.IOException;

public class RoundRobinDiskStore implements DiskStore {

	private File baseDir;
	
	private int depth;
	
	private int[] index;
	
	private int maxFilesPerDir = 500;
	
	public RoundRobinDiskStore(File baseDir, int depth) {
		setBaseDir(baseDir);
		setDepth(depth);
	}
	
	private void setBaseDir(File baseDir) {
		this.baseDir = new File(baseDir, "items");
		delete(this.baseDir);
		this.baseDir.mkdirs();
	}
	
	public File getBaseDir() {
		return baseDir;
	}
	
	private void setDepth(int depth) {
		this.depth = depth;
		this.index = new int[depth];
	}
	
	private static void delete(File f) {
        if (f.isDirectory()) {
            File[] entries = f.listFiles();
            for (int i = 0; i < entries.length; i++) {
            	delete(entries[i]);
            }
        }
        f.delete();
    }
	
	public void setMaxFilesPerDir(int maxFilesPerDir) {
		this.maxFilesPerDir = maxFilesPerDir;
	}

	public synchronized File getFile() throws IOException {
		File f = null;
		while (f == null) {
			f = createFile();
		}
		return f;
	}
	
	private File createFile() throws IOException {
		File f = getFile(depth-1);
		index[depth-1]++;
		f.getParentFile().mkdirs();
		if (f.createNewFile()) {
			return f;
		}
		return null;
	}
	
	private File getFile(int level) {
		if (level == 0) {
			return baseDir;
		}
		if (index[level] >= maxFilesPerDir) {
			index[level] = 0;
			if (level > 1) {
				index[level-1]++;
			}
		}
		return new File(getFile(level-1), String.format("%03d", index[level]));
	}

}
