/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.setup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.riotfamily.pages.dao.PageDefinition;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.pages.setup.config.SiteDefinition;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageSetupBean implements InitializingBean, ApplicationContextAware {

	private List<SiteDefinition> siteDefinitions;

	private List<PageDefinition> pageDefinitions;

	private PlatformTransactionManager transactionManager;
	
	private ApplicationContext applicationContext;

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setSiteDefinitions(List<SiteDefinition> siteDefinitions) {
		this.siteDefinitions = siteDefinitions;
	}

	public void setPageDefinitions(List<PageDefinition> definitions) {
		this.pageDefinitions = definitions;
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
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				createNodes();
			}
		});
	}

	//FIXME Create nodes
	protected void createNodes() {
		/*
		if (Site.findAll().isEmpty()) {
			List<Site> sites = createSites();
			Page rootNode = Page.getRootNode();
			if (pageDefinitions != null) {
				for (PageDefinition definition : pageDefinitions) {
					definition.createNode(rootNode, sites);
				}
			}
		}
		*/
	}
	
	protected List<Site> createSites() {
		if (siteDefinitions == null || siteDefinitions.isEmpty()) {
			SiteDefinition definition = new SiteDefinition();
			definition.setLocale(Locale.ENGLISH);
			definition.setEnabled(true);
			siteDefinitions = Collections.singletonList(definition);
		}
		ArrayList<Site> result = new ArrayList<Site>();
		for (SiteDefinition definition : siteDefinitions) { 
			definition.createSites(result, null);
		}
		return result;
	}


}
