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
package org.riotfamily.components.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.MapKey;
import org.riotfamily.components.model.wrapper.ValueWrapper;
import org.riotfamily.components.model.wrapper.ValueWrapperService;

@Entity
@Table(name="riot_contents")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="content_type",
    discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue("Content")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="components")
public class Content {

	private Long id;
	
	private int version;

	private Map<String, ValueWrapper<?>> wrappers;

	public Content() {
	}

	public Content createCopy() {
		Content copy = new Content();
		copyValues(copy);
		return copy;
	}
	
	protected final void copyValues(Content dest) {
		for (Entry<String, ValueWrapper<?>> entry : getWrappers().entrySet()) {
			ValueWrapper<?> copy = entry.getValue().deepCopy();
			dest.getWrappers().put(entry.getKey(), copy);
		}
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@Version
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public ValueWrapper<?> getWrapper(String key) {
		return getWrappers().get(key);
	}
	
	public void setWrapper(String key, ValueWrapper<?> wrapper) {
		getWrappers().put(key, wrapper);
	}
	
	public Object getValue(String key) {
		if (wrappers == null) {
			return null;
		}
		ValueWrapper<?> wrapper = getWrapper(key);
		return wrapper != null ? wrapper.getValue() : null;
	}
	
	@SuppressWarnings("unchecked")
	public void setValue(String key, Object value) {
		if (value == null) {
			getWrappers().remove(key);
		}
		else if (value instanceof ValueWrapper) {
			setWrapper(key, (ValueWrapper<?>) value);
		}
		else {
			ValueWrapper<Object> wrapper = (ValueWrapper<Object>) getWrapper(key);
			setWrapper(key, ValueWrapperService.createOrUpdate(wrapper, value));
		}
	}

	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="content")
	@MapKey(columns={@Column(name="property")})
	@Cascade(CascadeType.ALL)
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="components")
	public Map<String, ValueWrapper<?>> getWrappers() {
		if (wrappers == null) {
			wrappers = new HashMap<String, ValueWrapper<?>>();
		}
		return wrappers;
	}

	public void setWrappers(Map<String, ValueWrapper<?>> wrappers) {
		this.wrappers = wrappers;
	}
	
	public Map<String, Object> unwrap() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		if (wrappers != null) {
			for (Entry<String, ValueWrapper<?>> entry : wrappers.entrySet()) {
				ValueWrapper<?> wrapper = entry.getValue();
				if (wrapper != null) {
					result.put(entry.getKey(), wrapper.unwrap());
				}
				else {
					result.put(entry.getKey(), null);
				}
			}
		}
		return result;
	}
	
	public void wrap(Map<String, ?> values) {
		if (values != null) {
			for (Entry<String, ?> entry : values.entrySet()) {
				setValue(entry.getKey(), entry.getValue());
			}
			Iterator<String> it = getWrappers().keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				if (!values.containsKey(key)) {
					it.remove();
				}
			}
		}
		else {
			getWrappers().clear();
		}
	}
	
	/**
	 * Returns a Collection of Strings that should be used to tag the
	 * CacheItem containing the rendered Content.
	 */
	@Transient
	public Collection<String> getCacheTags() {
		HashSet<String> result = new HashSet<String>();
		for (ValueWrapper<?> wrapper : wrappers.values()) {
			if (wrapper != null) {
				Collection<String> tags = wrapper.getCacheTags();
				if (tags != null) {
					result.addAll(tags);
				}
			}
		}
		return result;
	}
	
}
