package org.riotfamily.dbmsgsrc;

import java.text.MessageFormat;
import java.util.Locale;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.riot.hibernate.support.HibernateHelper;
import org.springframework.context.support.AbstractMessageSource;

public class HibernateMessageSource extends AbstractMessageSource {

	private HibernateHelper hibernate;
	
	public HibernateMessageSource(SessionFactory sessionFactory) {
		this.hibernate = new HibernateHelper(sessionFactory, "messages");
	}
	
	protected MessageFormat resolveCode(String code, Locale locale) {
		MessageBundleEntry entry = getEntry(code);
		if (entry != null) {
			String msg = entry.getMessage(locale);
			return createMessageFormat(msg, locale);
		}
		return null;
	}
	
	protected MessageBundleEntry getEntry(String code) {
		return (MessageBundleEntry) hibernate.createCacheableCriteria(
				MessageBundleEntry.class).add(Restrictions.eq("code", code))
				.uniqueResult();
	}
}
