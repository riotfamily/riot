package org.riotfamily.dbmsgsrc.support;

import java.text.MessageFormat;
import java.util.Locale;

import org.riotfamily.dbmsgsrc.dao.DbMessageSourceDao;
import org.riotfamily.dbmsgsrc.model.Message;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.website.cache.CacheTagUtils;

public class DbMessageSource extends AbstractMessageSource {

	public static final String DEFAULT_BUNDLE = "default";

	private DbMessageSourceDao dao;
	
	private String bundle = DEFAULT_BUNDLE;
	
	public DbMessageSource(DbMessageSourceDao dao) {
		this.dao = dao;
	}
	
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	MessageBundleEntry getEntry(String code) {
		return dao.findEntry(bundle, code);
	}
	
	@Override
	protected MessageFormat resolveCode(String code, Locale locale, String defaultMessage) {
		CacheTagUtils.tag(Message.class);
		MessageBundleEntry entry = dao.findEntry(bundle, code);
		if (entry != null) {
			Message message = entry.getMessage(locale);
			if (message != null) {
				return message.getMessageFormat();
			}
		}
		else {
			dao.saveEntry(new MessageBundleEntry(bundle, code, defaultMessage));
		}
		return null;
	}
	
	@Override
	protected String getMessageFromParent(String code, Object[] args, Locale locale) {
		String s = super.getMessageFromParent(code, args, locale);
		if (s == null) {
			MessageBundleEntry entry = dao.findEntry(bundle, code);
			Message message = entry.getDefaultMessage();
			if (message != null) {
				MessageFormat messageFormat = message.getMessageFormat();
				if (messageFormat != null) {
					synchronized (messageFormat) {
						return messageFormat.format(args);
					}
				}
			}
		}
		return s;
	}
	
}
