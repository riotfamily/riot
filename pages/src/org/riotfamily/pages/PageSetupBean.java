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
package org.riotfamily.pages;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

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

	private Collection locales;
	
	private String[] handlerNames;
	
	private PageDao pageDao;
	
	private PlatformTransactionManager transactionManager;

	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setHandlerNames(String[] handlerNames) {
		this.handlerNames = handlerNames;
	}

	public void setLocales(Collection locales) {
		this.locales = locales;
	}
	
	public void afterPropertiesSet() throws Exception {
		if (locales == null) {
			locales = Collections.singletonList(null);
		}
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				createNodes();
			}
		});
	}
	
	protected void createNodes() {
		for (int i = 0; i < handlerNames.length; i++) {
			PageNode node = pageDao.getNodeForHandler(handlerNames[i]);
			Iterator it = locales.iterator();
			while (it.hasNext()) {
				Locale locale = (Locale) it.next();
				if (node.getPage(locale) == null) {
					node.addPage(new Page(handlerNames[i], locale));
				}
			}
			//pageDao.updateNode(node);
		}
	}
}
