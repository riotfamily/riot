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
 *   Carsten Woelk [cwoelk at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.dao;


/**
 * Exception that can be thrown by a RiotDao to indicate that a property 
 * contains an invalid value. This allows the DAO layer to perform validation
 * checks upon save or update operations. 
 * 
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public class InvalidPropertyValueException extends RioDaoException {

	private String field;

	public InvalidPropertyValueException(String field, String code, String msg) {
		this(field, code, new Object[] {}, msg);
	}

	public InvalidPropertyValueException(String field, String code, Object[] arguments, String msg) {
		this(field, code, arguments, msg, null);
	}

	public InvalidPropertyValueException(String field, String code,
			Object[] arguments, String msg, Throwable cause) {

		super(code, arguments, msg, cause);
		this.field = field;
	}

	public String getField() {
		return this.field;
	}

}
