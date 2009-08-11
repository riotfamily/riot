package org.riotfamily.components.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.riotfamily.common.hibernate.ActiveRecordBeanSupport;
import org.riotfamily.common.util.Generics;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

@Entity
@Table(name="riot_contents")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name="content_type",
    discriminatorType=DiscriminatorType.STRING
)
@DiscriminatorValue("Content")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="components")
public class Content extends ActiveRecordBeanSupport 
		implements Map<String, Object> {

	private ContentMapMarshaller marshaller;
	
	private int version;

	private ContentMap map;
	
	private String xml;
	
	private boolean dirty;
		
	private Map<String, ContentPart> parts = Generics.newHashMap();

	private Set<Object> references = Generics.newHashSet();
	
	public Content() {
	}
	
	public Content(Content other) {
		setXml(other.getXml());
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

	public Content createCopy() {
		return new Content(this);
	}
		
	@Type(type="text")
	public String getXml() {
		if (xml == null || dirty) {
			xml = marshaller.marshal(getMap());
			dirty = false;
		}
		return xml;
	}

	public void setXml(String xml) {
		parts.clear();
		map = marshaller.unmarshal(this, xml);
	}
	
	@Transient
	public Set<Object> getReferences() {
		return references;
	}

	/**
	 * ContentParts invoke this method on their owner when they are modified.
	 */
	void dirty() {
		dirty = true;
	}
	
	@Transient
	ContentMap getMap() {
		if (map == null) {
			map = new ContentMap(this);
		}
		return map;
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
	
	String nextPartId() {
		return version + "_" + parts.size();
	}

	String getPublicId(ContentPart part) {
		return getId() + "_" + part.getPartId();
	}
	
	void registerPart(ContentPart part) {
		String id = part.getPartId();
		Assert.notNull(id);
		Assert.isTrue(!parts.containsKey(id));
		parts.put(id, part); 
	}
	
	private ContentPart getPart(String id) {
		int i = id.indexOf('_');
		String partId = id.substring(i + 1);
		ContentPart part = parts.get(partId);
		Assert.notNull(part, "ContentPart " + partId 
				+ " does not exist in Content " + getId());
		
		return part;
	}

	// -----------------------------------------------------------------------
	
	public static Content load(Long id) {
		return load(Content.class, id);
	}
	
	public static ContentPart loadPart(String id) {
		Content content = loadByPartId(id);
		return content.getPart(id);
	}

	public static Content loadByPartId(String id) {
		int i = id.indexOf('_');
		Content content = Content.load(Long.valueOf(id.substring(0, i)));
		Assert.notNull(content, "Could not load content for part: " + id);
		return content;
	}

}
