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
package org.riotfamily.cachius.http.header;

import java.io.Serializable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class AbstractCookie implements Serializable {

	private String name;

    private String domain;

    private String path;
    
    private int maxAge;
    
    private boolean secure;
    
    private int version;
    
    private String comment;

    public AbstractCookie(Cookie cookie) {
        name = cookie.getName();
        domain = cookie.getDomain();
        path = cookie.getPath();
        maxAge = cookie.getMaxAge();
        secure = cookie.getSecure();
        version = cookie.getVersion();
        comment = cookie.getComment();
    }

    public void send(HttpServletRequest request, 
    		HttpServletResponse response) {
    	
    	response.addCookie(createCookie(request));
    }
    
    protected Cookie createCookie(HttpServletRequest request) {
        Cookie cookie = new Cookie(name, getValue(request));
        if (domain != null) {
            cookie.setDomain(domain);
        }
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(secure);
        cookie.setVersion(version);
        cookie.setComment(comment);
        return cookie;
    }

	protected abstract String getValue(HttpServletRequest request);

}
