package org.riotfamily.dbmsgsrc.support;

import java.text.MessageFormat;
import java.util.Locale;

import org.riotfamily.dbmsgsrc.dao.DbMessageSourceDao;
import org.riotfamily.dbmsgsrc.model.Message;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.website.cache.CacheTagUtils;

public class DbMessageSource extends AbstractMessageSource {

	private static final String DEFAULT_BUNDLE = "default";

	private DbMessageSourceDao dao;
	
	private String bundle = DEFAULT_BUNDLE;
	
	public DbMessageSource(DbMessageSourceDao dao) {
		this.dao = dao;
	}
	
	public void setBundle(String bundle) {
		this.bundle = bundle;
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
	
}
