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
package org.riotfamily.common.web.cache.annotation;

import static org.junit.Assert.*;

import java.io.Writer;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.riotfamily.cachius.CacheContext;
import org.riotfamily.cachius.CacheService;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.NestedServletException;

public class CacheAnnotationTests {
	
	private DispatcherServlet servlet;
	
	@Before
	public void initServlet() throws ServletException {
		servlet = new DispatcherServlet() {
			@Override
			protected WebApplicationContext createWebApplicationContext(WebApplicationContext parent) {
				GenericWebApplicationContext wac = new GenericWebApplicationContext();
				CacheService cacheService = new CacheService();
				CacheAnnotationHandlerAdapter handlerAdapter = new CacheAnnotationHandlerAdapter(cacheService, null);
				wac.getBeanFactory().initializeBean(handlerAdapter, "handlerAdapter");
				wac.getBeanFactory().registerSingleton("handlerAdapter", handlerAdapter);
				wac.registerBeanDefinition("controller1", new RootBeanDefinition(TimeController.class));
				wac.registerBeanDefinition("controller2", new RootBeanDefinition(CacheKeyController.class));
				wac.registerBeanDefinition("controller3", new RootBeanDefinition(CustomCacheKeyController.class));
				wac.registerBeanDefinition("controller4", new RootBeanDefinition(PathVariableController.class));
				wac.refresh();
				return wac;
			}
		};
		servlet.init(new MockServletConfig());
	}
	
		
	@Test
	public void uncached() throws Exception {
		String t1 = get("/time/uncached");
		assertTrue("Result must start with 'Uncached'", t1.startsWith("Uncached"));
		Thread.sleep(10);
		String t2 = get("/time/uncached");
		assertTrue("The two uncached results must differ", !t1.equals(t2));
	}
	
	@Test
	public void cached() throws Exception {
		String t1 = get("/time/cached");
		assertTrue("Result must start with 'Cached'", t1.startsWith("Cached"));
		Thread.sleep(10);
		String t2 = get("/time/cached");
		assertEquals(t1, t2);
	}
	
	@Test
	public void lastModified() throws Exception {
		String t1 = get("/time/interval");
		assertTrue("Result must start with 'Cached'", t1.startsWith("Interval"));
		Thread.sleep(10);
		String t2 = get("/time/interval");
		assertEquals(t1, t2);
		TimeController.lastModified = System.currentTimeMillis();
		t2 = get("/time/interval");
		assertTrue("Result must differ after changing lastModified: " + t1, !t1.equals(t2));
	}
	
	@Test
	public void key() throws Exception {
		String key = get("/key/generated", "name", "world");
		assertTrue(key.endsWith("{world;}"));
	}

	@Test(expected=NestedServletException.class)
	public void invalidArgument() throws Exception {
		get("/key/invalid");
	}
	
	@Test
	public void customKey() throws Exception {
		String key = get("/key/custom", "name", "world");
		assertTrue(key.contains("custom=world"));
	}
	
	@Test
	public void customPrefix() throws Exception {
		String key = get("/custom/cached");
		assertTrue(key, key.startsWith("custom-cached"));
	}
	
	@Test
	public void nullPrefix() throws Exception {
		String t1 = get("/custom/uncached");
		assertTrue("Result must start with 'Uncached'", t1.startsWith("Uncached"));
		Thread.sleep(10);
		String t2 = get("/custom/uncached");
		assertTrue("The two uncached results must differ", !t1.equals(t2));
	}
	
	@Test
	public void pathVariable() throws Exception {
		assertEquals("bar", get("/path/foo/bar"));
	}
	
	
	public String get(String url, String... params) throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", url);
		for (int i = 0; i < params.length; i += 2) {
			request.setParameter(params[i], params[i+1]);
		}
		MockHttpServletResponse response = new MockHttpServletResponse();
		servlet.service(request, response);
		return response.getContentAsString();
	}
	
	@Controller
	@RequestMapping("/time/*")
	public static class TimeController {

		public static long lastModified = System.currentTimeMillis();
		
		@RequestMapping
		public void uncached(Writer out) throws Exception {
			out.write("Uncached: " + String.valueOf(System.currentTimeMillis()));
		}
		
		@Cache
		@RequestMapping
		public void cached(Writer out) throws Exception {
			out.write("Cached: " + String.valueOf(System.currentTimeMillis()));
		}
		
		@Cache
		@RequestMapping
		public void interval(Writer out) throws Exception {
			out.write("Interval: " + String.valueOf(System.currentTimeMillis()));
		}
		
		public long getLastModifiedForInterval(Writer out) throws Exception {
			return lastModified;
		}
	}
	
	@Controller
	@RequestMapping("/key/*")
	public static class CacheKeyController {

		@Cache
		@RequestMapping
		public void generated(Writer out, @RequestParam String name) throws Exception {
			out.write(CacheContext.getCacheKey());
		}
		
		@Cache
		@RequestMapping
		public void invalid(Writer out, HttpServletRequest request) throws Exception {
		}
		
		@Cache
		@RequestMapping
		public void custom(Writer out, @RequestParam String name) throws Exception {
			out.write(CacheContext.getCacheKey());
		}
		
		public String getCacheKeyForCustom(Writer out, @RequestParam String name) {
			return "custom=" + name;
		}
	}
	
	@Controller
	@RequestMapping("/custom/*")
	public static class CustomCacheKeyController {

		public String getCacheKey(HttpServletRequest request, Method handlerMethod) {
			if (handlerMethod.getName().equals("uncached")) {
				return null;
			}
			return "custom-" + handlerMethod.getName();
		}

		@Cache
		@RequestMapping
		public void uncached(Writer out) throws Exception {
			out.write("Uncached: " + String.valueOf(System.currentTimeMillis()));
		}
		
		@Cache
		@RequestMapping
		public void cached(Writer out) throws Exception {
			out.write(CacheContext.getCacheKey());
		}
		
	}
	
	@Controller
	@RequestMapping("/path/*")
	public static class PathVariableController {

		@Cache
		@RequestMapping("foo/{bar}")
		public void foo(@PathVariable String bar, Writer out) throws Exception {
			out.write(bar);
		}
				
	}

}
