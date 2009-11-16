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

public class Dimension {

	private final int width;
	
	private final int height;
	
	public Dimension() {
		this(0, 0);
	}

	public Dimension(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Dimension expand(Dimension d) {
		return new Dimension(
				Math.max(this.width, d.width),
				Math.max(this.height, d.height)
		);
	}
	
	public Dimension times(int x, int y) {
		return new Dimension(this.width * x, this.height * y);
	}
	
	public Dimension add(Dimension d) {
		return new Dimension(
				this.width + d.width,
				this.height + d.height
		);
	}
	
	public Dimension addHeight(Dimension d) {
		return new Dimension(
				Math.max(this.width, d.width),
				this.height + d.height
		);
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getCss() {
		return String.format("width:%dpx;height:%dpx", width, height);
	}

}
