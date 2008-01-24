package org.riotfamily.search.site;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

	private static Log log = LogFactory.getLog(SiteIdentifier.class);
	
	private PlatformTransactionManager transactionManager;
	
	private PageDao pageDao;
	
	private String contextPath;
	
	private List sites;

	
	public SiteIdentifier(PlatformTransactionManager transactionManager, PageDao pageDao) {
		this.transactionManager = transactionManager;
		this.pageDao = pageDao;
	}
	
	public void updateSiteList() {
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus ts) {
				sites = pageDao.listSites();
			}
		});
	}
		
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
		Iterator it = sites.iterator();
		while (it.hasNext()) {
			Site site = (Site) it.next();
			if (site.matches(hostName, path)) {
				return site;
			}
		}
		return null;
	}
	
	public boolean isSiteHost(String hostName) {
		Iterator it = sites.iterator();
		while (it.hasNext()) {
			Site site = (Site) it.next();
			if (site.getHostName() != null && site.getHostName().equals(hostName)) {
				return true;
			}
		}
		return false;
	}
}
