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
package org.riotfamily.components.index;

import static org.hibernate.EntityMode.POJO;

import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Filter;
import org.hibernate.metadata.ClassMetadata;
import org.riotfamily.common.util.Generics;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * ContentIndexer that allows the lookup of ContentContainerOwners.
 * <p>
 * If a Content has a ContainerContainerOwner of the type <code>Foo</code>,
 * the indexer will look for a mapped class called <code>FooContentIndex</code>,
 * that is a subclass of {@link ContentIndex}.
 * </p>
 * @see ContentIndex
 */
public class HibernateContentIndexer extends HibernateDaoSupport 
		implements ContentIndexer {

	private Logger log = LoggerFactory.getLogger(HibernateContentIndexer.class);
	
	private Map<String, ClassMetadata> metaDataMap = Generics.newHashMap();
	
	public void contentCreated(Content content) {
		Object index = createIndex(content);
		if (index != null) {
			getSession().save(index);
		}
	}

	public void contentDeleted(Content content) {
		Object index = loadIndexByContent(content);
		if (index != null) {
			getSession().delete(index);
		}
	}

	public void contentModified(Content content) {
		contentDeleted(content);
		contentCreated(content);
	}
	
	private Object createIndex(Content content) {
		ContentContainer container = content.getContainer();
		if (container != null) {
			Object owner = container.getOwner();
			if (owner != null) {
				String ownerClassName = Hibernate.getClass(owner).getName();
				ClassMetadata meta = getIndexClassMetadata(ownerClassName);
				if (meta != null) {
					ContentIndex index = (ContentIndex) meta.instantiate(content.getId(), POJO);
					String ownerProperty = StringUtils.uncapitalize(StringUtils.unqualify(ownerClassName));
					meta.setPropertyValue(index, ownerProperty, owner, POJO);
					index.setContent(content);					
					return index;
				}
			}
		}
		return null;
	}
		
	private ClassMetadata getIndexClassMetadata(String ownerClassName) {
		if (metaDataMap.containsKey(ownerClassName)) {
			return metaDataMap.get(ownerClassName);
		}
		String indexClassName = ownerClassName  + "ContentIndex";
		ClassMetadata meta = getSessionFactory().getClassMetadata(indexClassName);
		if (meta != null) {
			Class<?> indexClass = meta.getMappedClass(POJO);
			if (ContentIndex.class.isAssignableFrom(indexClass)) {
				Filter filter = indexClass.getAnnotation(Filter.class);
				Assert.isTrue(filter != null && filter.name().equals("contentIndex"),
						String.format("%s must be annotated with @Filter(name=\"contentIndex\")",
						indexClass.getName()));
			}
			else {
				meta = null;
				log.warn("Class {} matches the naming convention for content " +
						"indexes but does not extend ContentIndex.", indexClass);
			}
		}
		metaDataMap.put(ownerClassName, meta);
		return meta;
	}

	private Object loadIndexByContent(Content content) {
		ContentContainer container = content.getContainer();
		if (container != null) {
			Object owner = container.getOwner();
			if (owner != null) {
				String ownerClassName = Hibernate.getClass(owner).getName();
				ClassMetadata meta = getIndexClassMetadata(ownerClassName);
				if (meta != null) {
					return getSession().load(meta.getEntityName(), content.getId());
				}
			}
		}
		return null;
	}	
	
}
