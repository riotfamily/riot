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
package org.riotfamily.common.util;

import static org.junit.Assert.*;
import static org.riotfamily.common.util.FormatUtils.*;

import org.junit.Test;

public class FormatUtilsTests {

	@Test
	public void testCamelToTitleCase() {
		assertEquals("Camel Case", camelToTitleCase("camelCase"));
		assertEquals("Camel CASE", camelToTitleCase("camelCASE"));
		assertEquals("Cam 31 Case", camelToTitleCase("cam31Case"));
	}
	
	@Test
	public void testCamelToXmlCase() {
		assertEquals("camel-case", camelToXmlCase("CamelCase"));
		assertEquals("camel-case", camelToXmlCase("camelCASE"));	
	}
	
	@Test
	public void testXmlToCamelCase() {
		assertEquals("fooBar", xmlToCamelCase("foo-bar"));
		assertEquals("FooBAR", xmlToCamelCase("Foo-bAR"));
	}
	
	@Test
	public void testXmlToTitleCase() {
		assertEquals("Foo Bar", xmlToTitleCase("foo-bar"));
		assertEquals("Foo Bar", xmlToTitleCase("fooBar"));
	}
	
	@Test
	public void testPropertyToTitleCase() {
		assertEquals("Foo Bar", propertyToTitleCase("foo.bar"));
		assertEquals("Foo Bar Bar", propertyToTitleCase("foo.barBar"));
	}

	@Test
	public void testFileNameToTitleCase() {
		assertEquals("Foo Bar", fileNameToTitleCase("foo.bar"));
		assertEquals("Foo Foo Bar", fileNameToTitleCase("foo-foo_bar"));
		assertEquals("Foo Bar Bar", fileNameToTitleCase("foo.barBar"));
	}
	
	@Test
	public void testToPropertyName() {
		assertEquals("fooBar", toPropertyName("foo.bar"));
		assertEquals("fooBar", toPropertyName("Foo.Bar"));
		assertEquals("fooBar", toPropertyName("FooBar"));
		//TODO assertEquals("fooFooBar", toPropertyName("foo: foo bar"));
		assertEquals("fooFooBarBar", toPropertyName("foo-Foo_bar.bar"));
	}

}
