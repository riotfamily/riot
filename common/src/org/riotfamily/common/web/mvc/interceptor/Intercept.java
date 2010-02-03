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
package org.riotfamily.common.web.mvc.interceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Intercept {

	/**
	 * Whether direct (top-level) requests should be intercepted. 
	 */
	boolean request() default true;

	/**
	 * Whether requests forwarded by a RequestDispatcher should be intercepted.
	 * If set to <code>false</code>, the interceptor is skipped in case of a
	 * forwarded request. Note that even if set to <code>true</code> (default),
	 * the interceptor can still be skipped depending on the {@link #once()}
	 * setting. 
	 */
	boolean forward() default true;
	
	/**
	 * Whether requests included by a RequestDispatcher should be intercepted.
	 * If set to <code>false</code>, the interceptor is skipped in case of an
	 * include request. Note that even if set to <code>true</code> (default),
	 * the interceptor can still be skipped depending on the {@link #once()}
	 * setting.
	 */
	boolean include() default true;
	
	/**
	 * Whether the interceptor should be invoked only once per request. 
	 * The default is <code>true</code>, hence requests dispatched by a
	 * RequestDispatcher will only be intercepted if the interceptor was not
	 * already invoked for the originating request.
	 */
	boolean once() default true;

}
