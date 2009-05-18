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
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.schema;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.common.hibernate.AbstractSetupBean;

public class SystemPageSync extends AbstractSetupBean {

	private SitemapSchema sitemapSchema;
	
	public SystemPageSync(SessionFactory sessionFactory,
			SitemapSchema sitemapSchema) {
		
		super(sessionFactory);
		this.sitemapSchema = sitemapSchema;
	}

	@Override
	protected void setup(Session session) throws Exception {
		sitemapSchema.syncSystemPages();
	}
	
}
