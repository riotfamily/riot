package org.riotfamily.pages.setup;

import java.util.Locale;

import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Site;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class SiteFactoryBean implements FactoryBean, InitializingBean, ApplicationContextAware {
	
	private Locale locale;
	
	private PageDao pageDao;

	private PlatformTransactionManager transactionManager;
	
	private ApplicationContext applicationContext;
	
	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public void afterPropertiesSet() throws Exception {
		
		if (transactionManager == null) {
			transactionManager = (PlatformTransactionManager) 
					BeanFactoryUtils.beanOfTypeIncludingAncestors(
					applicationContext, PlatformTransactionManager.class);
		}
		if (pageDao == null) {
			pageDao = (PageDao)	BeanFactoryUtils.beanOfTypeIncludingAncestors(
					applicationContext, PageDao.class);
		}		
	}
			
	public void setLocale(Locale locale) {
		//this.locale = StringUtils.parseLocaleString(locale);
		this.locale = locale;		
	}

	public Class getObjectType() {		
		return Site.class;
	}
	
	public boolean isSingleton() {	
		return true;
	}		
	
	public Object getObject() throws Exception {
		
		return new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
			
			public Object doInTransaction(TransactionStatus status) {
				return pageDao.findSite(locale);
			}
		
		});		
	}

}
