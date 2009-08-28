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
package org.riotfamily.components.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.riotfamily.common.hibernate.ActiveRecordBeanSupport;
import org.riotfamily.common.util.Generics;
import org.riotfamily.website.cache.TagCacheItems;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Entity that stores objects of any kind in a map. The map is serialized using
 * a ContentMapMarshaller. 
 */
@Entity
@Table(name="riot_contents")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="components")
@TagCacheItems
public class Content extends ActiveRecordBeanSupport implements ContentMap {

	private ContentMapMarshaller marshaller;
	
	private int version;

	private ContentContainer container;

	private boolean dirty;
	
	private String xml;

	private boolean xmlRequiresUpdate;
	
	private transient boolean unmarshalling;
	
	private transient ContentMap map;
	
	private transient Map<String, ContentFragment> fragments;

	private transient Set<Object> references;
	
	public Content() {
	}
	
	public Content(ContentContainer container) {
		this.container = container;
	}
	
	public Content(Content other) {
		setContainer(other.getContainer());
		setXml(other.getXml());
		dirty = false;
	}
	
	@Transient
	@Required
	public void setMarshaller(ContentMapMarshaller marshaller) {
		this.marshaller = marshaller;
	}
	
	@Version
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	Content createCopy() {
		dirty = false;
		return new Content(this);
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	public ContentContainer getContainer() {
		return container;
	}

	private void setContainer(ContentContainer container) {
		this.container = container;
	}
	
	@Type(type="text")
	public String getXml() {
		if (xml == null || xmlRequiresUpdate) {
			xml = marshaller.marshal(getMap());
			xmlRequiresUpdate = false;
		}
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
		unmarshalling = true;
		if (fragments != null) {
			fragments.clear();
		}
		map = marshaller.unmarshal(this, xml);
		xmlRequiresUpdate = false;
		unmarshalling = false;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	protected void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	/**
	 * ContentFragments invoke this method when they are modified.
	 */
	void fragmentModified() {
		if (!unmarshalling) {
			xmlRequiresUpdate = true;
			setDirty(true);
		}
	}
	
	@Transient
	private ContentMap getMap() {
		if (map == null) {
			map = new ContentMapImpl(this);
		}
		return map;
	}
	
	@Transient
	public Set<Object> getReferences() {
		if (references == null) {
			references = Generics.newHashSet();
		}
		return references;
	}
	
	@Transient
	private Map<String, ContentFragment> getFragments() {
		if (fragments == null) {
			fragments = Generics.newHashMap();
		}
		return fragments;
	}
	
	// -----------------------------------------------------------------------
	// Implementation of the ContentFragment interface
	// -----------------------------------------------------------------------
	
	@Transient
	public String getCompositeId() {
		return getFragmentId();
	}

	@Transient
	public String getFragmentId() {
		return String.valueOf(getId());
	}

	@Transient
	public Content getContent() {
		return this;
	}
	
	// -----------------------------------------------------------------------
	// Implementation of the Map interface (delegate methods)
	// -----------------------------------------------------------------------
	
	public void clear() {
		getMap().clear();
	}

	public boolean containsKey(Object key) {
		return getMap().containsKey(key);
	}

	public boolean containsValue(Object value) {
		return getMap().containsValue(value);
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return getMap().entrySet();
	}

	public Object get(Object key) {
		return getMap().get(key);
	}

	@Transient
	public boolean isEmpty() {
		return getMap().isEmpty();
	}

	public Set<String> keySet() {
		return getMap().keySet();
	}

	public Object put(String key, Object value) {
		return getMap().put(key, value);
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		getMap().putAll(m);
	}

	public Object remove(Object key) {
		return getMap().remove(key);
	}

	public int size() {
		return getMap().size();
	}

	public Collection<Object> values() {
		return getMap().values();
	}
	
	// -----------------------------------------------------------------------
	
	String nextFragmentId() {
		return version + "_" + getFragments().size();
	}

	String getCompositeId(ContentFragment part) {
		return getId() + "_" + part.getFragmentId();
	}
	
	void registerfragment(ContentFragment fragment) {
		String id = fragment.getFragmentId();
		Assert.notNull(id);
		Assert.isTrue(!getFragments().containsKey(id));
		getFragments().put(id, fragment); 
	}
	
	private ContentFragment getFragment(String id) {
		int i = id.indexOf('_');
		if (i == -1) {
			return this;
		}
		String fragmentId = id.substring(i + 1);
		ContentFragment fragment = getFragments().get(fragmentId);
		Assert.notNull(fragment, "Fragment " + fragmentId 
				+ " does not exist in Content " + getId());
		
		return fragment;
	}

	// -----------------------------------------------------------------------
	
	public static Content load(Long id) {
		return load(Content.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public static<T extends ContentFragment> T loadFragment(String id) {
		Content content = loadByFragmentId(id);
		return (T) content.getFragment(id);
	}

	public static Content loadByFragmentId(String id) {
		int i = id.indexOf('_');
		String contentId = (i != -1) ? id.substring(0, i) : id;
		Content content = Content.load(Long.valueOf(contentId));
		Assert.notNull(content, "Could not load content for part: " + id);
		return content;
	}

}
