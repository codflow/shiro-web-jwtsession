package ink.codflow.shiro.web.jwtsession.mgt.eis;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.web.util.RequestPairSource;
public class JWTSourceAdaptor implements RequestPairSource {

    private ServletRequest request;
    private ServletResponse response;
    private JWTHttpDataHandler handler;

    public JWTSourceAdaptor(RequestPairSource source) {
        this.request = source.getServletRequest();
        this.response = source.getServletResponse();
        this.handler = new JWTHttpDataHandler(this);
    }

    public JWTSourceAdaptor(ServletRequest request, ServletResponse response) {
        this.request = request;
        this.response = response;
        this.handler = new JWTHttpDataHandler(this);
    }
    @Override
    public ServletRequest getServletRequest() {
        return request;
    }
    @Override
    public ServletResponse getServletResponse() {
        return response;
    }
    public String readData() {
        return handler.readData();
    }
    public boolean storeData(String str) {
        return handler.storeData(str);
    }
    
    public boolean deleteData() {
        return handler.deleteData();
    }
    
    public boolean isRequestSourceEmpty() {
        return handler.isRequestSourceEmpty();
    }
    
    public boolean isSessionJwtTokenCookieEnabled() {
        return handler.isSessionJwtTokenCookieEnabled();
    }
    public void setSessionJwtTokenCookieEnabled(boolean sessionJwtTokenCookieEnabled,long globalSessionTimeout ) {
        handler.setSessionJwtTokenCookieEnabled(sessionJwtTokenCookieEnabled);
        handler.setglobalSessionTimeout(globalSessionTimeout);
    }
    
    
}
