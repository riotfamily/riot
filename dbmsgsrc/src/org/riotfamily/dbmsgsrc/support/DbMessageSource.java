package org.riotfamily.dbmsgsrc.support;

import java.text.MessageFormat;
import java.util.Locale;

import org.riotfamily.dbmsgsrc.dao.DbMessageSourceDao;
import org.riotfamily.dbmsgsrc.model.Message;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;

public class DbMessageSource extends AbstractMessageSource {

	private DbMessageSourceDao dao;
	
	public DbMessageSource(DbMessageSourceDao dao) {
		this.dao = dao;
	}

	@Override
	protected MessageFormat resolveCode(String code, Locale locale, String defaultMessage) {
		MessageBundleEntry entry = dao.findEntry(code);
		if (entry != null) {
			Message message = entry.getMessage(locale);
			if (message != null) {
				return message.getMessageFormat();
			}
		}
		else {
			dao.saveEntry(new MessageBundleEntry(code, defaultMessage));
		}
		return null;
	}
	
}
