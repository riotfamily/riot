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
