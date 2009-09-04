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
package org.riotfamily.cachius.http.support;

import java.io.Serializable;

import javax.servlet.http.Cookie;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class SerializableCookie implements Serializable {

	private static final long serialVersionUID = 7012664038620916590L;

	private String name;

    private String value;
    
    private String domain;

    private String path;
    
    private int maxAge;
    
    private boolean secure;
    
    private int version;
    
    private String comment;

    public SerializableCookie(Cookie cookie) {
        name = cookie.getName();
        value = cookie.getValue();
        domain = cookie.getDomain();
        path = cookie.getPath();
        maxAge = cookie.getMaxAge();
        secure = cookie.getSecure();
        version = cookie.getVersion();
        comment = cookie.getComment();
    }

    public Cookie create() {
        Cookie cookie = new Cookie(name, value);
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
}
