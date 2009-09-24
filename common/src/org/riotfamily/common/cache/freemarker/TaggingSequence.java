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
package org.riotfamily.common.cache.freemarker;

import java.util.Collection;
import java.util.List;

import org.riotfamily.cachius.CacheContext;
import org.riotfamily.common.util.Generics;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;

/**
 * SimpleSequence subclass that tags cache items with a list of configured tags
 * whenever the size of the sequence is accessed.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class TaggingSequence extends SimpleSequence {

	private List<String> tags = Generics.newArrayList();
	
	public TaggingSequence(Collection<?> collection, ObjectWrapper wrapper) {
		super(collection, wrapper);
	}
	
	public void addTag(String tag) {
		tags.add(tag);
	}

	@Override
	public int size() {
		for (String tag : tags) {
			CacheContext.tag(tag);
		}
		return super.size();
	}
	
}
