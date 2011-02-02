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

import java.io.PrintWriter;

import org.riotfamily.common.util.DocumentWriter;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.forms.AbstractEditorBase;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.MessageUtils;
import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.forms.event.JavaScriptEventAdapter;
import org.riotfamily.forms.request.FormRequest;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.ui.Dimension;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for elements that let the user choose a reference to
 * to another object.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class AbstractChooser extends AbstractEditorBase
		implements Editor, DHTMLElement, JavaScriptEventAdapter, 
		ResourceElement {
	
	private Object object;

	private static FormResource RESOURCE = 
			new ScriptResource("form/chooser.js", "riot.chooser", 
			Resources.RIOT_DIALOG);
	
	@Override
	public String getEventTriggerId() {		
		return getId();
	}
	
	
	@Override
	protected void renderInternal(PrintWriter writer) {
		DocumentWriter doc = new DocumentWriter(writer);
		doc.start("div").attribute("class", "chooser").body();
		
		if (object != null) {
			doc.start("span").attribute("class", "label").body();		
			renderLabel(object, writer);
			doc.end();
		}
		
		doc.startEmpty("input")
				.attribute("type", "button")
				.attribute("class", "choose")
				.attribute("disabled", !isEnabled())
				.attribute("value", FormatUtils.stripTags(getChooseLabel()))
				.end();
		
		if (!isRequired() && getValue() != null) {
			doc.start("input")
					.attribute("type", "button")
					.attribute("class", "unset")
					.attribute("disabled", !isEnabled())
					.attribute("value", FormatUtils.stripTags(getUnsetLabel()))
					.end();
		}
		doc.end();
	}
	
	public Dimension getDimension() {
		return getFormContext().getSizing().getButtonSize();
	}

	protected String getUnsetLabel() {
		return MessageUtils.getUILabel(this, "chooser", "unset", "Unset");
	}

	protected String getChooseLabel() {
		return MessageUtils.getUILabel(this, "chooser", "choose", "Choose");
	}
	
	protected abstract void renderLabel(Object object, PrintWriter writer);
	
	public int getEventTypes() {		
		return 0;
	}
	
	@Override
	public void processRequest(FormRequest request) {
		validate();
	}
	
	protected void validate() {
		if (isRequired() && object == null) {
			ErrorUtils.reject(this, "required");
		}
	}

	public void handleJavaScriptEvent(JavaScriptEvent event) {
		Object oldObject = object;
		setObjectId(event.getValue());
		getFormListener().elementChanged(this);
		fireChangeEvent(object, oldObject);
	}
	
	public FormResource getResource() {
		return RESOURCE;
	}
	
	public String getInitScript() {
		return String.format("riot.chooser.register('%s', '%s')", 
				getId(), getFormContext().getContextPath() + getChooserUrl());
	}
		
	protected abstract Object loadBean(String objectId);
	
	protected void setObjectId(String objectId) {
		log.debug("Setting objectId to: " + objectId);
		Object oldObject = object;
		if (StringUtils.hasLength(objectId)) {
			object = loadBean(objectId);
		}
		else {
			object = null;
		}
		fireChangeEvent(object, oldObject);
	}
	
	public void setValue(Object value) {
		Object oldObject = object;
		this.object = value;
		fireChangeEvent(object, oldObject);
	}

	public Object getValue() {
		return object;
	}

		
	protected abstract String getChooserUrl();
	
}
