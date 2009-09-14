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
package org.riotfamily.core.dao;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataAccessException;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public class RiotDaoException extends DataAccessException
		implements MessageSourceResolvable {

	private String code;

	private Object[] arguments;

	public RiotDaoException(String code, String msg) {
		this(code, new Object[] {}, msg);
	}

	public RiotDaoException(String code, Object[] arguments, String msg) {
		this(code, arguments, msg, null);
	}

	public RiotDaoException(String code, Object[] arguments, String msg, Throwable cause) {
		super(msg, cause);
		this.code = code;
		this.arguments = arguments;
	}

	public Object[] getArguments() {
		return this.arguments;
	}

	public String getCode() {
		return this.code;
	}

	public String[] getCodes() {
		return new String[] { code };
	}

	public String getDefaultMessage() {
		return getMessage();
	}

}
