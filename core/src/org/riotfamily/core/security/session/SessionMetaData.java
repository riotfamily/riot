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
package org.riotfamily.core.security.session;

import java.io.Serializable;
import java.util.Date;

/**
 * Interface that provides meta data about the last and current session.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface SessionMetaData extends Serializable {

	public String getUserId();

	public String getUserName();

	public Date getLastLoginDate();

	public String getLastLoginIP();
	
}
