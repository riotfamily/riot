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
package org.riotfamily.dbmsgsrc.model;

import java.text.MessageFormat;
import java.util.Locale;

import javax.persistence.CascadeType;
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
import org.riotfamily.common.web.cache.TagCacheItems;
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
	
	public Message(MessageBundleEntry entry, Locale locale, String text) {
		this.entry = entry;
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

	@ManyToOne(cascade=CascadeType.ALL)
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
	
	public String format(Object[] args, boolean escapeSingleQuotes) {
		if (args != null) {
			MessageFormat messageFormat = getMessageFormat(escapeSingleQuotes);
			if (messageFormat != null) {
				synchronized (messageFormat) {
					return messageFormat.format(args);
				}
			}
		}
		return getText();
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
