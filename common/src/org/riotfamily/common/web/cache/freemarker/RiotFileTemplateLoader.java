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
package org.riotfamily.common.web.cache.freemarker;

import java.io.File;
import java.io.IOException;

import org.riotfamily.cachius.CacheContext;

import freemarker.cache.FileTemplateLoader;

/**
 * TemplateLoader that invokes {@link CachiusContext#addInvolvedFile(File)}
 * to track files involved in the generation of cached content.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class RiotFileTemplateLoader extends FileTemplateLoader {

	public RiotFileTemplateLoader(File baseDir) throws IOException {
		super(baseDir);
	}

	@Override
	public Object findTemplateSource(String name) throws IOException {
		File file = (File) super.findTemplateSource(name);
		if (file != null) {
			CacheContext.addFile(file);
		}
		return file;
	}

}
