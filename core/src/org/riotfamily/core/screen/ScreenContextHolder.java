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
package org.riotfamily.core.screen;

import org.riotfamily.common.util.Generics;

public final class ScreenContextHolder {

	private static ThreadLocal<ScreenContext> threadLocal = Generics.newThreadLocal();
	
	private ScreenContextHolder() {
	}
	
	public static void set(ScreenContext context) {
		threadLocal.set(context);
	}
	
	public static ScreenContext get() {
		return threadLocal.get();
	}
	
	public static void remove() {
		threadLocal.remove();
	}
	
}
