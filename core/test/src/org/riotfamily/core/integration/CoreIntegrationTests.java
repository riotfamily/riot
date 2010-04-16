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
package org.riotfamily.core.integration;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.jitr.Jitr;
import org.jitr.annotation.BaseUri;
import org.jitr.annotation.JitrConfiguration;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.RunWith;

@RunWith(Jitr.class)
@JitrConfiguration(warPath="core/test/webapp")
public class CoreIntegrationTests {

	private static boolean interactive = false;
	
	@BaseUri
	private String baseUri;

	private Object shutdown = new Object();
	
	@Test
	public void interactive() throws Exception {
		if (interactive) {
			Runtime.getRuntime().exec("open " + baseUri + "riot");
			synchronized (shutdown) {
				shutdown.wait();
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		interactive = true;
		Enumeration<URL> res = CoreIntegrationTests.class.getClassLoader().getResources("META-INF/spring.handlers");
		while (res.hasMoreElements()) {
			System.out.println(res.nextElement());
		}
		new JUnitCore().run(Request.method(CoreIntegrationTests.class, "interactive").getRunner());
	}

}
