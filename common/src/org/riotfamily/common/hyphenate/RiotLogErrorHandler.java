/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.hyphenate;


import net.davidashen.text.Hyphenator;
import net.davidashen.util.ErrorHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RiotLogErrorHandler implements ErrorHandler {

	private String fileName;
	
	private Logger log;
	
	public RiotLogErrorHandler(String fileName) {
		this.fileName = fileName + ": "; 
		this.log = LoggerFactory.getLogger(Hyphenator.class);
	}
	
	public RiotLogErrorHandler(Logger log) {
		this.log = log;
	}
	
	public boolean isDebugged(String guard) {
		return false;
	}
	
	public void debug(String guard, String s) {
	}

	public void error(String s) {
		log.warn(fileName + s);
	}

	public void exception(String s, Exception e) {
		log.warn(fileName + s);
	}

	public void info(String s) {
		log.debug(fileName + s);
	}

	public void warning(String s) {
		log.debug(fileName + s);
	}

}
