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
package org.riotfamily.components.model.wrapper;

import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class NumberWrapper extends ValueWrapper {

	private BigDecimal number;

	public void setValue(Object value) {
		if (value instanceof BigDecimal) {
			this.number = (BigDecimal) value;
		}
		else if (value instanceof BigInteger) {
			this.number = new BigDecimal((BigInteger) value);
		}
		else if (value instanceof Integer) {
			this.number = new BigDecimal(((Integer) value).intValue());
		}
		else if (value instanceof Short) {
			this.number = new BigDecimal(((Short) value).intValue());
		}
		else if (value instanceof Long) {
			this.number = new BigDecimal(((Long) value).longValue());
		}
		else if (value instanceof Double) {
			this.number = new BigDecimal(((Double) value).doubleValue());
		}
		else if (value instanceof Float) {
			this.number = new BigDecimal(((Float) value).doubleValue());
		}
	}

	public Object getValue() {
		return number;
	}
	
	public ValueWrapper deepCopy() {
		NumberWrapper copy = new NumberWrapper();
		copy.wrap(number);
		return copy;
	}
}
