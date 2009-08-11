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
package org.riotfamily.components.index;

import org.riotfamily.components.model.Content;

public abstract class AbstractContentIndexer implements ContentIndexer {

	public void contentCreated(Content content) throws Exception {
		createIndex(content);
	}

	public void contentDeleted(Content content) throws Exception {
		deleteIndex(content);
	}

	public void contentModified(Content content) throws Exception {
		deleteIndex(content);
		createIndex(content);
	}

	protected abstract void createIndex(Content content) throws Exception;

	protected abstract void deleteIndex(Content content) throws Exception;

}
