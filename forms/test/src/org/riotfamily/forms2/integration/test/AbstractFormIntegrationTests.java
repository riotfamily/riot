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
package org.riotfamily.forms2.integration.test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jitr.Jitr;
import org.jitr.annotation.BaseUri;
import org.jitr.annotation.JitrConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.runner.RunWith;
import org.riotfamily.forms2.integration.model.TestBean;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(Jitr.class)
@JitrConfiguration(warPath="forms/test/webapp")
public abstract class AbstractFormIntegrationTests {

	private WebClient webClient = new WebClient();
	
	@BaseUri
	private String baseUri;
	
	private HtmlPage page;
	
	@Before
	public void request() throws Exception {
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		page = webClient.getPage(baseUri + getControllerMapping());
	}
	
	protected abstract String getControllerMapping();

	@Test
	public void textField() throws Exception {
		select("//div[@id='s1']/input[@type='text']").type("Hello world");
		assertAfterSave("text", is("Hello world"));
	}
	
	@Test
	public void radioButtonGroup() throws Exception {
		select("//input[@type='radio'][@value='1']").click();
		assertAfterSave("radio", is("bar"));
		select("//input[@type='radio'][@value='0']").click();
		assertAfterSave("radio", is("foo"));
	}
	
	@Test
	public void listEditor() throws Exception {
		select("//button[@class='add']").click();
		assertAfterSave("list", notNullValue());
		
		select("//li//input[@type='text']").type("aaa");
		assertAfterSave("list", hasItem("aaa"));
		
		select("//button[@class='remove']").click();
		assertAfterSave("list", not(hasItem("aaa")));
	}
	
	@Test
	public void nested() throws Exception {
		select("//div[contains(@class,'NestedForm')]//input[@type='text']").type("Hello nested");
		assertAfterSave("nested", new TypeSafeMatcher<TestBean>() {
			public void describeTo(Description d) {
				d.appendText("Hello nested");
			}
			@Override
			public boolean matchesSafely(TestBean testBean) {
				return testBean.getText().equals("Hello nested");
			}
		});
	}
	
	private HtmlElement select(String path) {
		List<?> nodes = page.getByXPath(path);
		assertEquals(path, 1, nodes.size());
		return (HtmlElement) nodes.get(0);
	}
	
	
	@SuppressWarnings("unchecked")
	private <T> void assertAfterSave(String field, Matcher<?> matcher) throws Exception {
		save();
		assertThat(getFieldValue(field), (Matcher) matcher);
	}

	protected abstract Object getFieldValue(String field);

	private void save() throws Exception {
		select("//div[contains(@class,'SubmitButton')]/button").click();
	}

}
