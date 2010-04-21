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
package org.riotfamily.forms2.integration.model;

import java.util.Date;
import java.util.List;

import org.riotfamily.common.util.FormatUtils;

public class TestBean {

	private String text;
	
	private String radio;
	
	private String select;
	
	private Date date;
	
	private String tinymce;
	
	private List<String> list;
	
	private byte[] file;
	
	private TestBean nested;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getRadio() {
		return radio;
	}

	public void setRadio(String radio) {
		this.radio = radio;
	}
	
	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTinymce() {
		return tinymce;
	}

	public void setTinymce(String tinymce) {
		this.tinymce = tinymce;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public TestBean getNested() {
		return nested;
	}

	public void setNested(TestBean nested) {
		this.nested = nested;
	}
	
	@Override
	public String toString() {
		return FormatUtils.toJSON(this);
	}

}
