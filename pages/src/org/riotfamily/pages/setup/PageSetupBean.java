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

import java.util.Iterator;
import java.util.List;

import org.riotfamily.pages.PageNode;
import org.riotfamily.pages.Site;
import org.riotfamily.pages.dao.PageDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageSetupBean implements InitializingBean {

	private String siteName = null;

	private List definitions;

	private PageDao pageDao;

	private PlatformTransactionManager transactionManager;

	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setDefinitions(List definitions) {
		this.definitions = definitions;
	}

	public void afterPropertiesSet() throws Exception {
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				createNodes();
			}
		});
	}

	protected void createNodes() {
		if (pageDao.listSites().isEmpty()) {
			Site site = pageDao.getSite(siteName);
			PageNode rootNode = pageDao.findRootNode(site);
			Iterator it = definitions.iterator();
			while (it.hasNext()) {
				PageDefinition definition = (PageDefinition) it.next();
				PageNode childNode = definition.createNode(site, pageDao.getLocales());
				rootNode.addChildNode(childNode);
			}
			pageDao.updateNode(rootNode);
		}
	}


}
