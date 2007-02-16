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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.revolt;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class EvolutionInstructions extends RuntimeException {

	private String instructions;
	
	private boolean printed;
	
	public EvolutionInstructions(String instructions) {
		super("The database schema is not up-to-date");
		this.instructions = instructions;
	}

	public void printStackTrace(PrintStream s) {
		if (!printed) {
			s.print(instructions);
			printed = true;
		}
		else {
			s.print("\t... see above");
		}
	}
	
	public void printStackTrace(PrintWriter s) {
		if (!printed) {
			s.print(instructions);
			printed = true;
		}
		else {
			s.print("\t... see above");
		}
	}

}
