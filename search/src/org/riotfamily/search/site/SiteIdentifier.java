package org.riotfamily.search.site;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Site;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Service that can be used to identify the Sites that belongs to an URL. 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class SiteIdentifier {

	private Log log = LogFactory.getLog(SiteIdentifier.class);
	
	private PlatformTransactionManager transactionManager;
	
	private PageDao pageDao;
	
	private String contextPath;
	
	private List<Site> sites;

	
	public SiteIdentifier(PlatformTransactionManager transactionManager, PageDao pageDao) {
		this.transactionManager = transactionManager;
		this.pageDao = pageDao;
	}
	
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
	public void updateSiteList() {
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus ts) {
				sites = pageDao.listSites();
				if (sites != null) {
				    Iterator<Site> i = sites.iterator();
				    while (i.hasNext()) {
				        Hibernate.initialize(i.next().getAliases());
				    }
				}
			}
		});
	}
	
	/**
	 * Returns the first Site that matches the given URL.
	 * <p>
	 * <strong>Note:</strong> If no contextPath has been  
	 * {@link #setContextPath(String) set manually}, the method will try to
	 * guess the contextPath using the following strategy: If no matching Site
	 * is found in the first pass, the leading directory name is stripped from
	 * the URL and the lookup is performed again with the modified path. If 
	 * this yields a result, {@link #setContextPath(String)} is invoked with the
	 * assumed prefix. This behavior might lead to unexpected results in certain 
	 * scenarios, for example when you have a Site that doesn't specify a
	 * hostName (only a prefix) and you invoke this method with an URL that
	 * contains this prefix as second directory name <em>before</em> you 
	 * called it with a valid (resolvable) URL. This is very unlikely to 
	 * happen. If you run into this problem nevertheless, you can set the 
	 * contextPath {@link #setContextPath(String) manually}. 
	 */	
	public Site getSiteForUrl(String url) {
		String hostName = ServletUtils.getHost(url);
		String path = ServletUtils.getPath(url);
		if (contextPath != null && contextPath.length() > 0) {
			path = path.substring(contextPath.length());
		}
		Site site = getSite(hostName, path);
		if (site == null) {
			if (contextPath == null) {
				if (path.length() > 1) {
					int i = path.indexOf('/', 1);
					if (i != -1) {
						String cp = path.substring(0, i);
						path = path.substring(i);
						site = getSite(hostName, path);
						if (site != null) {
							contextPath = cp;
							log.info("Assuming the contextPath is " + cp);
						}
					}
				}
			}
		}
		return site;
	}

	private Site getSite(String hostName, String path) {
		Iterator<Site> it = sites.iterator();
		while (it.hasNext()) {
			Site site = it.next();
			if (site.matches(hostName, path)) {
				return site;
			}
		}
		return null;
	}
	
	/**
	 * Returns whether the given hostName belongs to any of the configured
	 * Sites.
	 */
	public boolean isSiteHost(String hostName) {
		Iterator<Site> it = sites.iterator();
		while (it.hasNext()) {
			Site site = it.next();
			if (site.hostNameMatches(hostName, false)) {
				return true;
			}
		}
		return false;
	}
}
