package org.riotfamily.dbmsgsrc.model;

import java.util.Locale;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.MapKey;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.riotfamily.common.util.Generics;
import org.springframework.util.StringUtils;

@Entity
@Table(name="riot_dbmsgsrc_entries")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="messages")
public class MessageBundleEntry {

	public static final Locale C_LOCALE = new Locale("c");
	
	private Long id;
	
	private String bundle;
	
	private String code;
	
	private String comment;
	
	private Map<Locale, Message> messages;

	
	public MessageBundleEntry() {
	}
	
	public MessageBundleEntry(String bundle, String code, String defaultMessage) {
		this.bundle = bundle;
		this.code = code;
		setDefaultMessage(new Message(defaultMessage));
	}

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	@NaturalId
	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	@NaturalId
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Type(type="text")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	@OneToMany
    @JoinColumn(name="entry_id")
    @MapKey(columns={@Column(name="locale")})
	@Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
	@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="messages")
	public Map<Locale, Message> getMessages() {
		return messages;
	}

	public void setMessages(Map<Locale, Message> messages) {
		this.messages = messages;
	}
	
	public Message getMessage(Locale locale) {
		Message message = messages.get(locale);
		if (message == null && StringUtils.hasLength(locale.getCountry())) {
			Locale lang = new Locale(locale.getLanguage());
			message = messages.get(lang);
		}
		if (message == null) {
			message = getDefaultMessage();
		}
		return message;
	}
	
	public void addTranslation(Locale locale) {
		Message msg = new Message();
		msg.setText(getDefaultMessage().getText());
		messages.put(locale, msg);
	}

}
