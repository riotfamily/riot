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
package org.riotfamily.forms2;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hamcrest.Matcher;
import org.jitr.Jitr;
import org.jitr.annotation.BaseUri;
import org.jitr.annotation.JitrConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

@RunWith(Jitr.class)
@JitrConfiguration(warPath="forms/test/webapp")
public class FormsIntegrationTests {

	private static boolean interactive = false;
	
	private WebClient webClient = new WebClient();
	
	@BaseUri
	private String baseUri;
	
	private HtmlPage page;
	
	@Resource
	private Map<String, Object> backingObject;
	
	@Resource
	private Object shutdown;
	
	@Before
	public void request() throws Exception {
		if (!interactive) {
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			page = webClient.getPage(baseUri + "form");
		}
	}
	
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
	public void interactive() throws Exception {
		if (interactive) {
			Runtime.getRuntime().exec("open " + baseUri + "form");
			synchronized (shutdown) {
				shutdown.wait();
			}
		}
	}
	
	private HtmlElement select(String path) {
		List<?> nodes = page.getByXPath(path);
		assertEquals(path, 1, nodes.size());
		return (HtmlElement) nodes.get(0);
	}
	
	
	@SuppressWarnings("unchecked")
	private <T> void assertAfterSave(String field, Matcher<?> matcher) throws Exception {
		save();
		assertThat(backingObject.get(field), (Matcher) matcher);
	}
	
	private void save() throws Exception {
		select("//div[contains(@class,'SubmitButton')]/button").click();
	}

	public static void main(String[] args) {
		interactive = true;
		new JUnitCore().run(Request.method(FormsIntegrationTests.class, "interactive").getRunner());
	}

}
