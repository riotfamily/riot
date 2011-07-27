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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.hibernate.criterion.Restrictions;
import org.riotfamily.common.hibernate.ActiveRecordBeanSupport;
import org.riotfamily.common.util.Generics;
import org.springframework.util.StringUtils;

@Entity
@Table(name="riot_dbmsgsrc_entries")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="messages")
public class MessageBundleEntry extends ActiveRecordBeanSupport {

	public static final Locale C_LOCALE = new Locale("c");
	
	private String bundle;
	
	private String code;
	
	private String comment;
	
	private Map<Locale, Message> messages;

	
	public MessageBundleEntry() {
	}
	
	public MessageBundleEntry(String bundle, String code, String defaultMessage) {
		this.bundle = bundle;
		this.code = code;
		if (StringUtils.hasText(defaultMessage)) {
			setDefaultText(defaultMessage);
		}
	}
	
	@NaturalId
	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	@NaturalId
	@Column(nullable=false)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Type(type="text")
	@Column(name="`comment`")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Transient
	public String getDefaultText() {
		Message message = getDefaultMessage();
		return message != null ? message.getText() : null;
	}
	
	@Transient
	public void setDefaultText(String text) {
		Message message = getDefaultMessage();
		if (message == null) {
			setDefaultMessage(new Message(this, C_LOCALE, text));
		}
		else {
			message.setText(text);
		}
	}
	
	@Transient
	public Message getDefaultMessage() {
		if (messages == null) {
			return null;
		}
		return messages.get(C_LOCALE);
	}

	public void setDefaultMessage(Message defaultMessage) {
		if (messages == null) {
			messages = Generics.newHashMap();
		}
		messages.put(C_LOCALE, defaultMessage);
	}

	@OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="entry_id")
	@MapKeyColumn(name="locale")
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="messages")
	public Map<Locale, Message> getMessages() {
		return messages;
	}

	public void setMessages(Map<Locale, Message> messages) {
		this.messages = messages;
	}
		
	public void addTranslation(Locale locale) {
		addTranslation(locale, getDefaultText());		
	}
	
	public void addTranslation(Locale locale, String text) {
		Message msg = new Message();
		msg.setText(text);
		if (messages == null) {
			messages = Generics.newHashMap();
		}
		messages.put(locale, msg);
	}
	
	@Transient
	public boolean isTranslated() {
		if (messages == null) {
			return false;
		}
		return messages.size() > 1;
	}
	
	@Transient
	public String getText(Locale locale) {
		if (messages != null) {
			Message message = messages.get(locale);
			if (message != null) {
				return message.getText();
			}
		}
		return null;
	}

	// ------------------------------------------------------------------------
	// Active record methods
	// ------------------------------------------------------------------------

	public static MessageBundleEntry loadByBundleAndCode(String bundle, String code) {
		return (MessageBundleEntry) getSession().createCriteria(MessageBundleEntry.class)
			.setCacheable(true)
			.setCacheRegion("messages")
			.add(Restrictions.naturalId()
				.set("bundle", bundle)
				.set("code", code))
				.uniqueResult();
	}
	
	public static MessageBundleEntry load(Long id) {
		return load(MessageBundleEntry.class, id);
	}

	@SuppressWarnings("unchecked")
	public static void removeEmptyEntries(String bundle) {
		List<MessageBundleEntry> entries = getSession().createCriteria(MessageBundleEntry.class)
			.setCacheable(true)
			.setCacheRegion("messages")
			.add(Restrictions.sizeLe("messages", 1))
			.add(Restrictions.naturalId()
				.set("bundle", bundle))
				.list();
		
		for (MessageBundleEntry entry : entries) {
			entry.delete();
		}

	}

}
