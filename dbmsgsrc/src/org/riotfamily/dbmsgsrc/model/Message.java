package org.riotfamily.dbmsgsrc.model;

import java.text.MessageFormat;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.website.cache.TagCacheItems;
import org.springframework.util.ObjectUtils;

@Entity
@Table(name="riot_dbmsgsrc_messages")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="messages")
@TagCacheItems
public class Message {

	private Long id;
	
	private MessageBundleEntry entry;
	
	private Locale locale;
	
	private String text;

	private MessageFormat messageFormat;
	
	public Message() {
	}
	
	public Message(Locale locale, String text) {
		this.locale = locale;
		this.text = text;
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="entry_id", insertable=false, updatable=false)
	public MessageBundleEntry getEntry() {
		return entry;
	}

	public void setEntry(MessageBundleEntry entry) {
		this.entry = entry;
	}
	
	@Column(insertable=false, updatable=false)
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Type(type="text")
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		this.messageFormat = null;
	}
	
	@Transient
	public MessageFormat getMessageFormat(boolean escapeSingleQuotes) {
		if (messageFormat == null && text != null) {
			String pattern = text;
			if (escapeSingleQuotes) {
				pattern = FormatUtils.escapeChars(pattern, "'", '\'');
			}
			messageFormat = new MessageFormat(pattern, locale);
		}
		return messageFormat;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Message) {
			Message other = (Message) obj;
			return ObjectUtils.nullSafeEquals(id, other.getId());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

}
