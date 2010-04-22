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
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RoundRobinDiskStoreTest {

	private File baseDir;
	
	@Before
    public void setUp() {
		baseDir = new File(System.getProperty("java.io.tmpdir"), "test");
    }
		
	@Test
	public void testDirCreation() throws IOException {
		RoundRobinDiskStore store = new RoundRobinDiskStore(baseDir, 4);
		store.setMaxFilesPerDir(4);
		LinkedList<String> paths = new LinkedList<String>();
		for (int i = 0; i < 10; i++) {
			String path = store.getFile().getPath();
			String basePath = store.getBaseDir().getPath();
			Assert.assertTrue(path.startsWith(basePath));
			paths.add(path.substring(basePath.length()));
		}
		Assert.assertEquals("/000/000/000", paths.getFirst());
		Assert.assertEquals("/000/002/001", paths.getLast());
	}
}
