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
 *   alf
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.beans.module;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author alf
 * @since 6.4
 */
public class ModularFactoryBeansUtils {
	private static final Log log =
		LogFactory.getLog(ModularFactoryBeansUtils.class);
	
	private static int getIndex(Vector result, FactoryBeanModule module,
		String moduleName, Map modules) {
		
		int index = 0;
		String[] processAfter = module.getProcessAfter();
		if (processAfter != null) {
			for (int i=0; i<processAfter.length; i++) {
				int j = result.indexOf(modules.get(processAfter[i])) + 1;
				if (j > index) {
					index = j;
				}
			}
		}
		
		for (int i = 0; i<index; i++) {
			FactoryBeanModule m = (FactoryBeanModule) result.get(i);
			if (m.getProcessAfter() != null) {
				if (Arrays.asList(m.getProcessAfter()).contains(moduleName)) {
					throw new IllegalStateException("Circular process-after chain. Aborted.");
				}
			}
		}
		
		return index;
	}
	
	public static Collection getFactoryBeanModules(
		ApplicationContext applicationContext) {
		
		Map modules = applicationContext.getBeansOfType(
				FactoryBeanModule.class, false, false);
		Vector result = new Vector();
		
		Iterator i = modules.keySet().iterator();
		while (i.hasNext()) {
			String moduleName = (String) i.next();
			FactoryBeanModule module = (FactoryBeanModule) modules.get(moduleName);
			int index = getIndex(result, module, moduleName, modules);
			result.insertElementAt(module, index);
			
			if (log.isDebugEnabled()) {
				log.debug("Inserting module " + moduleName +
					" at position " + index);
			}
		}
		
		return result;
	}
}
