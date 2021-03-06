package ink.codflow.shiro.web.jwtsession.mgt.eis;

import org.apache.shiro.web.servlet.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.util.WebUtils;

public class JWTHttpDataHandler implements JWTDataHandler<String> {
    public final static String DEFAULT_JWT_SESSION_COOKIE_NAME = "JWTTOKEN";
    public final static String DEFAULT_JWT_SESSION_HEADER_NAME = "JWTTOKEN";
    private String JwtStrData;
    private JWTSourceAdaptor source;
    // JWTToken stored in Cookie as Default
    private boolean sessionJwtTokenCookieEnabled = true;
    private long globalSessionTimeout = 18000;
    
    private Cookie templatecookie;
    JWTHttpDataHandler(JWTSourceAdaptor source) {
        this.source = source;
    }

    @Override
    public String readData() {
        return JwtStrData != null ? readCurrentData() : readReferencedData();
    }

    @Override
    public boolean storeData(String str) {
        this.JwtStrData = str;
        return isSessionJwtTokenCookieEnabled() ? store2Cookie(str) : store2Header(str);
    }

    @Override
    public boolean deleteData() {
        this.JwtStrData = null;
        SimpleCookie cookie = (SimpleCookie) getTemplateSessionCookie();
        if (WebUtils.isHttp(source)) {
            HttpServletRequest request = WebUtils.getHttpRequest(source);
            HttpServletResponse response = WebUtils.getHttpResponse(source);
            cookie.removeFrom(request, response);
            return true;
        }
        return false;

    }

    public String readReferencedData() {
        return sessionJwtTokenCookieEnabled ? readReferencedCookieData() : readReferencedHeaderData();
    }

    public String readReferencedCookieData() {
        
        if (WebUtils.isHttp(source)) {
            HttpServletRequest request = WebUtils.getHttpRequest(source);
            HttpServletResponse response = WebUtils.getHttpResponse(source);
            return getTemplateSessionCookie().readValue(request, response);
        }
        return null;
        //Cookie cookie = getCookie(DEFAULT_JWT_SESSION_COOKIE_NAME);
        //return cookie != null ? cookie.getValue() : null;
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

//    protected Cookie getCookie(String key) {
//        if (WebUtils.isHttp(source)) {
//            Cookie[] cookies = WebUtils.getHttpRequest(source).getCookies();
//            if (cookies != null) {
//                for (javax.servlet.http.Cookie cookie : cookies) {
//                    if (cookie.getName().equals(key)) {
//                        return cookie;
//                    }
//                }
//            }
//        }
//        return null;
//    }

    protected boolean store2Cookie(String value) {

        SimpleCookie cookie = new SimpleCookie(getTemplateSessionCookie());
        cookie.setHttpOnly(true); // more secure, protects against XSS attacks
        
        cookie.setMaxAge((int)getGlobalSessionTimeout());
        cookie.setValue(value);
        if (WebUtils.isHttp(source)) {
            HttpServletRequest request = WebUtils.getHttpRequest(source);
            HttpServletResponse response = WebUtils.getHttpResponse(source);
            cookie.saveTo(request, response);
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

    public void setglobalSessionTimeout(long globalSessionTimeout) {
        this.globalSessionTimeout = globalSessionTimeout;
        
    }

    public long getGlobalSessionTimeout() {
        return globalSessionTimeout;
    }

    public void setTemplateSessionCookie(Cookie cookie) {
        this.templatecookie = cookie;
    }
    
    public Cookie getTemplateSessionCookie() {
        if (templatecookie == null) {
            templatecookie = new SimpleCookie(DEFAULT_JWT_SESSION_COOKIE_NAME);
        }
        
        return this.templatecookie;
    }
    
    

}
