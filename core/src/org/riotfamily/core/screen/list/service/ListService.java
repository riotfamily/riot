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
package org.riotfamily.core.screen.list.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.riotfamily.common.ui.RenderingService;
import org.riotfamily.common.web.mvc.mapping.HandlerUrlResolver;
import org.riotfamily.core.runtime.RiotRuntime;
import org.riotfamily.core.screen.ScreenRepository;
import org.riotfamily.core.screen.list.ColumnConfig;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.dto.CommandButton;
import org.riotfamily.core.screen.list.dto.ListItem;
import org.riotfamily.core.screen.list.dto.ListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Service to interact with lists and trees via JavaScript. In order to reduce
 * the complexity of this class, calls are delegated to stateful handler 
 * classes which implement the different service aspects.
 *    
 * @author Felix Gnass [fgnass at neteye dot de]
 */
@RemoteProxy
public class ListService {

	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Autowired
	private ScreenRepository screenRepository;
		
	@Autowired
	private HandlerUrlResolver handlerUrlResolver;
	
	@Autowired
	private RiotRuntime riotRuntime;
	
	@Autowired
	private MessageSource messageSource;

	@Autowired
	private RenderingService renderingService;

		
	public PlatformTransactionManager getTransactionManager() {
		return transactionManager;
	}

	public HandlerUrlResolver getHandlerUrlResolver() {
		return handlerUrlResolver;
	}
	
	public ScreenRepository getScreenRepository() {
		return screenRepository;
	}
			
	public String getResourcePath() {
		return riotRuntime.getResourcePath();
	}

	@RemoteMethod
	public ListModel getModel(String key, String expandedId, 
			HttpServletRequest request) {
		
		return new ListModelBuilder(this, key, request).buildModel(expandedId);
	}
	
	@RemoteMethod
	public List<ListItem> getChildren(String key, String parentId,
			HttpServletRequest request) {
		
		return new ListItemLoader(this, key, request).getChildren(parentId);
	}
	
	@RemoteMethod
	public ListModel gotoPage(String key, int page,
			HttpServletRequest request) {
		
		return new ListModelBuilder(this, key, request)
				.gotoPage(page).buildModel();
	}
	
	@RemoteMethod
	public ListModel sort(String key, String property, 
			HttpServletRequest request) {
		
		return new ListModelBuilder(this, key, request)
				.sort(property).buildModel();
	}
		
	@RemoteMethod
	public List<CommandButton> getFormCommands(String key, ListItem item, 
			HttpServletRequest request) {

		return new CommandContextHandler(this, key, request).createFormButtons(item);
	}

	@RemoteMethod
	public List<String> getEnabledCommands(String key, List<ListItem> items, 
			HttpServletRequest request,	HttpServletResponse response) {

		return new ChooserCommandHandler(this, key, request)
				.getEnabledCommands(items);
	}
	
	@RemoteMethod
	public CommandResult execCommand(String key,
			String commandId, List<ListItem> items, 
			HttpServletRequest request,	HttpServletResponse response) 
			throws Exception {

		return new ChooserCommandHandler(this, key, request)
				.execCommand(commandId, items);
	}
		
	@RemoteMethod
	public String renderScreenlets(String key,
			HttpServletRequest request,	HttpServletResponse response) {
		
		return new ScreenletRenderer(this, key, request).renderAll();
	}

	MessageSource getMessageSource() {
		return messageSource;
	}

	public String render(ColumnConfig col, Object value,
			TypeDescriptor typeDescriptor) {
		
		if (col.getRenderer() != null) {
			return col.getRenderer().render(value, typeDescriptor);
		}
		return renderingService.render(value, typeDescriptor);
	}

}
