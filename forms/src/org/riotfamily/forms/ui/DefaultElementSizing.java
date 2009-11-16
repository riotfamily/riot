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
package org.riotfamily.forms.ui;

public class DefaultElementSizing implements ElementSizing {

	public Dimension getLabelSize() {
		return new Dimension(150, 40);
	}
	
	public Dimension getButtonSize() {
		return new Dimension(150, 18);
	}

	public Dimension getCheckboxSize() {
		return new Dimension(150, 20);
	}

	public Dimension getFilePreviewSize() {
		return new Dimension(300, 32);
	}

	public Dimension getListItemPadding() {
		return new Dimension(20, 20);
	}

	public Dimension getMapItemPadding() {
		return new Dimension(20, 20);
	}

	public Dimension getRadioButtonSize() {
		return new Dimension(150, 20);
	}

	public Dimension getSelectBoxSize(int i) {
		return new Dimension(100, 18 * i);
	}

	public Dimension getTextareaSize(Integer rows, Integer cols, String text) {
		return new Dimension(350, (rows != null ? rows : 2) * 23 + 10);
	}
	
	public Dimension getTextSize(String text) {
		return new Dimension(300, 50);
	}

	public Dimension getTextfieldSize() {
		return new Dimension(350, 23);
	}

	public Dimension getVideoPreviewSize() {
		return new Dimension(150, 150);
	}

}
