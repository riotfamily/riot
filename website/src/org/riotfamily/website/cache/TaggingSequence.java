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
package org.riotfamily.website.cache;

import java.util.Collection;
import java.util.List;

import org.riotfamily.cachius.CachiusContext;
import org.riotfamily.common.util.Generics;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;

/**
 * SimpleSequence subclass that tags cache items with a list of configured tags
 * whenever the size of the sequence is accessed.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class TaggingSequence extends SimpleSequence {

	private List<String> tags = Generics.newArrayList();
	
	public TaggingSequence(Collection<?> collection, ObjectWrapper wrapper) {
		super(collection, wrapper);
	}
	
	public void addTag(String tag) {
		tags.add(tag);
	}

	@Override
	public int size() {
		for (String tag : tags) {
			CachiusContext.tag(tag);
		}
		return super.size();
	}
	
}
