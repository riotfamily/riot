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
package org.riotfamily.forms.integration.test;

import javax.annotation.Resource;

import org.jitr.Jitr;
import org.jitr.annotation.BaseUri;
import org.jitr.annotation.JitrConfiguration;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.RunWith;

@RunWith(Jitr.class)
@JitrConfiguration(warPath="forms/test/webapp")
public class InteractiveFormIntegrationTest {

	private static boolean interactive = false;
	
	@BaseUri
	private String baseUri;
	
	@Resource
	private Object shutdown;
	
	@Test
	public void interactive() throws Exception {
		if (interactive) {
			Runtime.getRuntime().exec("open " + baseUri + "form/bean");
			synchronized (shutdown) {
				shutdown.wait();
			}
		}
	}
	
	public static void main(String[] args) {
		interactive = true;
		new JUnitCore().run(Request.method(InteractiveFormIntegrationTest.class, "interactive").getRunner());
	}
}
