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
package org.riotfamily.media.setup.config;

import org.riotfamily.common.beans.namespace.GenericNamespaceHandlerSupport;
import org.riotfamily.media.setup.RiotFileFactoryBean;
import org.riotfamily.media.setup.RiotImageFactoryBean;
import org.riotfamily.media.setup.RiotSwfFactoryBean;
import org.riotfamily.media.setup.RiotVideoFactoryBean;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class MediaNamespaceHandler extends GenericNamespaceHandlerSupport {

	public void init() {
		register("file", RiotFileFactoryBean.class);
		register("image", RiotImageFactoryBean.class);
		register("swf", RiotSwfFactoryBean.class);
		register("video", RiotVideoFactoryBean.class);
	}
	
}
