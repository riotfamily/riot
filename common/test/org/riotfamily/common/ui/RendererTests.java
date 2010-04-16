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
package org.riotfamily.common.ui;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;
import org.springframework.context.i18n.LocaleContextHolder;

public class RendererTests {

	@Test
	public void locale() {
		LocaleRenderer renderer = new LocaleRenderer();
		
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		assertEquals("German (Germany)", renderer.render(Locale.GERMANY));
		
		LocaleContextHolder.setLocale(Locale.GERMAN);
		assertEquals("Deutsch (Deutschland)", renderer.render(Locale.GERMANY));
	}
}
