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
package org.riotfamily.forms.factory;

import java.util.List;

import org.riotfamily.forms.ElementFactory;


/**
 * Interface to be implemented by element factories that create 
 * {@link org.riotfamily.forms.ContainerElement container elements}.
 */
public interface ContainerElementFactory {

	public void addChildFactory(ElementFactory factory);
	
	public List<ElementFactory> getChildFactories();
	
}
