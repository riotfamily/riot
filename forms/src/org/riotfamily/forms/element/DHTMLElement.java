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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element;

import org.riotfamily.forms.Element;


/**
 * Interface to be implemented by elements that need to execute a client side
 * script in order to be functional. 
 */
public interface DHTMLElement extends Element {

	/**
	 * Returns a JavaScript that is evaluated in order to initialize the 
	 * element, or <code>null</code> if no initialization is needed.
	 */
	public String getInitScript();
	
	/**
	 * Implementors may return an object path expression 
	 * (like <code>foo.bar.something</code>) that must be defined before
	 * the init script can be evaluated.
	 */
	public String getPrecondition();

}
