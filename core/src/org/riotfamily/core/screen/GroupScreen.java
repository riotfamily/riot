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
package org.riotfamily.core.screen;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mvc.mapping.HandlerUrlUtils;
import org.riotfamily.core.security.AccessController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;


public class GroupScreen extends AbstractRiotScreen implements Controller {

	private List<RiotScreen> childScreens;

	private String viewName = ResourceUtils.getPath(
			GroupScreen.class, "group.ftl");
	
	public List<RiotScreen> getChildScreens() {
		return childScreens;
	}

	public void setChildScreens(List<RiotScreen> items) {
		this.childScreens = items;
		for (RiotScreen item : items) {
			item.setParentScreen(this);
		}
	}
	
	@Override
	public String getTitle(ScreenContext context) {
		if (context.getObject() != null && getParentScreen() instanceof ListScreen) {
			return ScreenUtils.getLabel(context.getObject(), this);
		}
		return super.getTitle(context);
	}
		
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ScreenContext context = ScreenContext.get(request);
		ModelAndView mv = new ModelAndView(viewName);
		List<ScreenLink> links = Generics.newArrayList();
		for (RiotScreen screen : childScreens) {
			if (AccessController.isGranted("view", screen)) {
				GroupScreenLink link = new GroupScreenLink(screen.getTitle(context),
						HandlerUrlUtils.getContextRelativeUrl(request, screen.getId(), context),
						screen.getIcon());
				
				if (screen instanceof GroupScreen) {
					for (RiotScreen nested : screen.getChildScreens()) {
						link.addChildLink(new GroupScreenLink(nested.getTitle(context),
								HandlerUrlUtils.getContextRelativeUrl(request, nested.getId(), context),
								nested.getIcon()));
					}
					
				}
				links.add(link);
			}
		}
		mv.addObject("links", links);
		return mv;
	}
	
	public static class GroupScreenLink extends ScreenLink {

		private List<ScreenLink> childLinks;
		
		public GroupScreenLink(String title, String url, String icon) {
			super(title, url, icon, false);
		}
		
		public void addChildLink(ScreenLink link) {
			if (childLinks == null) {
				childLinks = Generics.newArrayList();
			}
			childLinks.add(link);
		}
		
		public List<ScreenLink> getChildLinks() {
			return childLinks;
		}
		
	}

}
