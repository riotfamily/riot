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
package org.riotfamily.media.setup;

import java.io.IOException;

import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.processing.FileProcessor;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

/**
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 7.1
 */
public abstract class AbstractRiotFileFactoryBean extends AbstractFactoryBean {

	private Resource resource;	
	
	private FileProcessor processor;
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public void setProcessor(FileProcessor processor) {
		this.processor = processor;
	}
	
	protected Object createInstance() throws Exception {
		RiotFile file = createRiotFile(resource);
		if (processor != null) {
			processor.process(file);
		}
		return file;
	}
	
	protected abstract RiotFile createRiotFile(Resource resource) throws IOException;

}
