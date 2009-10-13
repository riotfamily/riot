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
package org.riotfamily.core.runtime;

public final class RiotVersion {

	private static String versionString;
	
	private RiotVersion() {
	}
	
	/**
	 * Returns the full Riot version string.
	 * @see java.lang.Package#getImplementationVersion
	 */
	public static String getVersionString() {
		if (versionString == null) {
			versionString = RiotRuntime.class.getPackage().getImplementationVersion();
		}
		return versionString;
	}
	
}
