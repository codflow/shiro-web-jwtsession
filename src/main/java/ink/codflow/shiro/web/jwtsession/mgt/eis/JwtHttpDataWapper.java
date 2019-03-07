package ink.codflow.shiro.web.jwtsession.mgt.eis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.util.WebUtils;

public class JwtHttpDataWapper {
    public final static String DEFAULT_JWT_SESSION_COOKIE_NAME = "JwtToken";
    public final static String DEFAULT_JWT_SESSION_HEADER_NAME = "JwtToken";
    private String JwtStrData;
    private JwtSourceAdaptor source;
    // JWTToken stored in Cookie as Default
    private boolean sessionJwtTokenCookieEnabled = true;

    JwtHttpDataWapper(JwtSourceAdaptor source) {
        this.source = source;
    }

    public String readData() {
        return JwtStrData != null ? readCurrentData() : readReferencedData();
    }

    public String readReferencedData() {
        return sessionJwtTokenCookieEnabled ? readReferencedCookieData() : readReferencedHeaderData();
    }

    public String readReferencedCookieData() {
        Cookie cookie = getCookie(DEFAULT_JWT_SESSION_COOKIE_NAME);
        return cookie != null ? cookie.getValue() : null;
    }

    public boolean storeData(String str) {
        this.JwtStrData = str;
        return isSessionJwtTokenCookieEnabled() ? store2Cookie(str) : store2Header(str);
    }

    private boolean store2Header(String value) {
        if (WebUtils.isHttp(source)) {
            HttpServletResponse response = WebUtils.getHttpResponse(source);
            response.setHeader(DEFAULT_JWT_SESSION_HEADER_NAME, value);
            return true;
        }
        return false;
    }

    public boolean isRequestSourceEmpty() {
        return WebUtils.getHttpRequest(source).getRequestURI() != null ? false : true;
    }

    protected String readCurrentData() {
        return JwtStrData;
    }

    public String readReferencedHeaderData() {
        return readRequestHeader(DEFAULT_JWT_SESSION_HEADER_NAME);
    }

    public String readRequestHeader(String name) {
        if (WebUtils.isHttp(source)) {
            String header = WebUtils.getHttpRequest(source).getHeader(name);
            return header;
        }
        return null;
    }

    protected Cookie getCookie(String key) {
        if (WebUtils.isHttp(source)) {
            Cookie[] cookies = WebUtils.getHttpRequest(source).getCookies();
            if (cookies != null) {
                for (javax.servlet.http.Cookie cookie : cookies) {
                    if (cookie.getName().equals(key)) {
                        return cookie;
                    }
                }
            }
        }
        return null;
    }

    protected boolean store2Cookie(String value) {
        SimpleCookie cookie = new SimpleCookie(DEFAULT_JWT_SESSION_COOKIE_NAME);
        cookie.setHttpOnly(true); // more secure, protects against XSS attacks
        
        cookie.setValue(value);
        if (WebUtils.isHttp(source)) {
            HttpServletRequest request = WebUtils.getHttpRequest(source);
            HttpServletResponse response = WebUtils.getHttpResponse(source);
            cookie.saveTo(request, response);
            return true;
        }
        return false;
    }

    public boolean deleteData() {
        this.JwtStrData = null;
        SimpleCookie cookie = new SimpleCookie(DEFAULT_JWT_SESSION_COOKIE_NAME);

        if (WebUtils.isHttp(source)) {
            HttpServletRequest request = WebUtils.getHttpRequest(source);
            HttpServletResponse response = WebUtils.getHttpResponse(source);
            cookie.removeFrom(request, response);
            return true;
        }
        return false;

    }

    public boolean isSessionJwtTokenCookieEnabled() {
        return sessionJwtTokenCookieEnabled;
    }

    public void setSessionJwtTokenCookieEnabled(boolean sessionJwtTokenCookieEnabled) {
        this.sessionJwtTokenCookieEnabled = sessionJwtTokenCookieEnabled;
    }

}
