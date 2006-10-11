package org.riotfamily.pages.page;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.controller.HttpErrorController;
import org.riotfamily.common.web.controller.RedirectController;
import org.riotfamily.pages.member.MemberBinder;
import org.riotfamily.pages.member.MemberBinderAware;
import org.riotfamily.pages.member.support.NullMemberBinder;
import org.riotfamily.pages.page.support.FolderController;
import org.riotfamily.pages.page.support.PageMappingEvent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.mvc.Controller;

public class PageMap implements InitializingBean, ApplicationContextAware, 
		ServletContextAware, ApplicationListener, MemberBinderAware {

	private Log log = LogFactory.getLog(PageMap.class);
	
	private static final String SERVLET_CONTEXT_ATTR = PageMap.class.getName();
	
	private static final String DEFAULT_CONTROLLER = "default";

	private static final Controller PAGE_HAS_GONE = 
			new HttpErrorController(HttpServletResponse.SC_GONE);
	
	private PageDao pageDao;
	
	private PlatformTransactionManager transactionManager;
	
	private MemberBinder memberBinder = new NullMemberBinder();
	
	private HashMap pageAndControllerMap;
	
	private TransientPage rootPage;
		
	private ApplicationContext applicationContext;
	
	private ApplicationEventMulticaster eventMulticaster;
	
	private PageMapPostProcessor postProcessor; 
	
	private long lastModified;
	
	private String defaultControllerName = DEFAULT_CONTROLLER;

	public static PageMap getInstance(ServletContext servletContext) {
		return (PageMap) servletContext.getAttribute(SERVLET_CONTEXT_ATTR);
	}

	public PageMap(PageDao pageDao, PlatformTransactionManager transactionManager) {
		this.pageDao = pageDao;
		this.transactionManager = transactionManager;
	}
	
	public void setServletContext(ServletContext servletContext) {
		servletContext.setAttribute(SERVLET_CONTEXT_ATTR, this);
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setEventMulticaster(ApplicationEventMulticaster eventMulticaster) {
		this.eventMulticaster = eventMulticaster;
	}

	public void setPostProcessor(PageMapPostProcessor postProcessor) {
		this.postProcessor = postProcessor;
	}

	public void setMemberBinder(MemberBinder memberBinder) {
		this.memberBinder = memberBinder;
	}

	public void afterPropertiesSet() throws Exception {
		if (eventMulticaster != null) {
			eventMulticaster.addApplicationListener(this);	
		}
		else {
			log.warn("No ApplicationEventMulticaster has been set.");
		}
		initMappings();
	}
	
	/**
	 * Called upon startup and when an ApplicationEvent is received ...
	 */
	public synchronized void initMappings() {
		lastModified = System.currentTimeMillis();
		pageAndControllerMap = new HashMap();
		
		new TransactionTemplate(transactionManager).execute(
			new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {
					rootPage = new TransientPage();
					rootPage.setPath("/");
					rootPage.setFolder(true);
					rootPage.setPublished(true);
					rootPage.setChildPages(pageDao.listRootPages());
					registerPage(rootPage);
					
					Collection aliases = pageDao.listAliases();
					registerAliases(aliases);
					
					if (postProcessor != null) {
						postProcessor.process(PageMap.this);
					}
					return null;
				}
			}
		);
	}
	
	protected void registerPages(Collection pages) {
		if (pages == null) {
			return;
		}
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			registerPage(page);
		}
	}
	
	protected void registerAliases(Collection aliases) {
		if (aliases == null) {
			return;
		}
		Iterator it = aliases.iterator();
		while (it.hasNext()) {
			PageAlias alias = (PageAlias) it.next();
			String path = alias.getPath();
			Page page = alias.getPage();
			if (page != null) {
				Controller rc = new RedirectController(page.getPath(), true, true);
				registerPage(path, page, rc);
			}
			else {
				registerPage(path, page, PAGE_HAS_GONE);
			}
		}
	}
	
	protected void registerPage(Page page) {
		String path = page.getPath();
		registerPage(path, page, getController(page));
		registerPages(page.getChildPages());
	}
	
	protected PageAndController registerPage(String path, Page page, 
			Controller controller) {
		
		PageAndController pc = getPageAndController(path);
		if (pc == null) {
			pc = new PageAndController(page, controller);
			pageAndControllerMap.put(path, pc);
		}
		return pc;
	}
	
	public void importPage(Page page) {
		String path = page.getPath();
		PageAndController pc = null;
		while (path != null) {
			String parentPath = getParentPath(path);
			if (parentPath == null) {
				break;
			}
			pc = getPageAndController(parentPath);
			if (pc != null) {
				break;
			}
			path = parentPath;
		}
		
		if (pc != null) {
			Page parent = pc.getPage();
			parent.addChildPage(page);
		}
		registerPage(page);
	}
	
	public Page removePage(String path) {
		PageAndController pc = getPageAndController(path);
		if (pc != null) {
			//TODO pc.setController(PAGE_HAS_GONE); ... and all childPages
			return pc.getPage();
		}
		return null;
	}
	
	private String getParentPath(String path) {
		int i = path.lastIndexOf('/');
		if (i > 0) {
			return path.substring(0, i);
		}
		return null;
	}
		
	protected Controller getController(Page page) {
		if (page == null) {
			return PAGE_HAS_GONE;
		}
		if (page.isFolder()) {
			return new FolderController(page, memberBinder);
		}
		String controllerName = page.getControllerName();
		if (controllerName == null) {
			controllerName = defaultControllerName;
		}
		return (Controller) applicationContext.getBean(
				controllerName, Controller.class);
	}
	
	public PageAndController getPageAndController(String path) {
		if (path.length() > 1 && path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return (PageAndController) pageAndControllerMap.get(path);
	}
	
	public Page getPage(String path) {
		PageAndController pc = getPageAndController(path);
		return pc != null ? pc.getPage() : null;
	}
	
	public Page getPageOrAncestor(String path) {
		Page page = null;
		while (page == null && path != null) {
			page = getPage(path);
			path = getParentPath(path);
		}
		return page;
	}
	
	public Collection getRootPages() {
		return rootPage.getChildPages();
	}
	
	public void onApplicationEvent(ApplicationEvent event) {
		if (event == PageMappingEvent.MAPPINGS_MODIFIED) {
			log.info("Mappings modified. Reloading ...");
			initMappings();
		}
	}

	public long getLastModified() {
		return this.lastModified;
	}
	
}
