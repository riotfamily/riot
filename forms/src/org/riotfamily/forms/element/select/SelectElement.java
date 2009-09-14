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
package org.riotfamily.forms.element.select;

import org.riotfamily.forms.Editor;


/**
 * Interface to be implemented by elements that provide options the user
 * can choose from.
 */
public interface SelectElement extends Editor {
		
	public String getParamName();
	
	public void renderOption(OptionItem option);

	public boolean isSelected(OptionItem option);
	
	public Object getOptions();
	
	public void setOptions(Object model);
	
	public int getOptionIndex(OptionItem option);

	public void reset();

}