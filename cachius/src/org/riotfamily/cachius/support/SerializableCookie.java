/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.cachius.support;

import java.io.Serializable;

import javax.servlet.http.Cookie;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class SerializableCookie implements Serializable {

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
