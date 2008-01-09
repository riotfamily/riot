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
package org.riotfamily.components.model;

import java.util.List;
import java.util.Map;

import org.riotfamily.media.model.RiotFile;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class BuiltInWrapperFactory implements ValueWrapperFactory {

	public ValueWrapper createWapper(Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof ValueWrapper) {
			return (ValueWrapper) value;
		}
		if (value instanceof String) {
			return new StringWrapper((String) value);
		}
		if (value instanceof Boolean) {
			return new BooleanWrapper((Boolean) value);
		}
		if (value instanceof RiotFile) {
			return new RiotFileWrapper((RiotFile) value);
		}
		if (value instanceof List) {
			return new ListWrapper((List) value);
		}
		if (value instanceof Map) {
			return new MapWrapper((Map) value);
		}
		return null;
	}
}
