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


/**
 * Exception that can be thrown by a RiotDao to indicate that a property 
 * contains an invalid value. This allows the DAO layer to perform validation
 * checks upon save or update operations. 
 * 
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public class InvalidPropertyValueException extends RiotDaoException {

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
