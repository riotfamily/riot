package org.riotfamily.dbmsgsrc.support;

import java.text.MessageFormat;
import java.util.Locale;

import org.riotfamily.dbmsgsrc.dao.DbMessageSourceDao;
import org.riotfamily.dbmsgsrc.model.Message;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.website.cache.CacheTagUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class DbMessageSource extends AbstractMessageSource {

	public static final String DEFAULT_BUNDLE = "default";

	private DbMessageSourceDao dao;
	
	private TransactionTemplate transactionTemplate;
	
	private String bundle = DEFAULT_BUNDLE;
	
	public DbMessageSource(DbMessageSourceDao dao, PlatformTransactionManager tx) {
		this.dao = dao;
		this.transactionTemplate = new TransactionTemplate(tx); 
	}
	
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	MessageBundleEntry getEntry(final String code, final String defaultMessage) {
		return (MessageBundleEntry) transactionTemplate.execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus status) {
				MessageBundleEntry entry = dao.findEntry(bundle, code);
				if (entry == null) {
					entry = new MessageBundleEntry(bundle, code, defaultMessage);
					dao.saveEntry(entry);
				}
				return entry;
			}
		});
	}
	
	@Override
	protected MessageFormat resolveCode(String code, Locale locale, String defaultMessage) {
		CacheTagUtils.tag(Message.class);
		MessageBundleEntry entry = getEntry(code, defaultMessage);
		Message message = entry.getMessage(locale);
		if (message != null) {
			return message.getMessageFormat();
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
