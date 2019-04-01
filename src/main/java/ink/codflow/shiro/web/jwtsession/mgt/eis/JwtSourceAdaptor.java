package ink.codflow.shiro.web.jwtsession.mgt.eis;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.web.util.RequestPairSource;
public class JWTSourceAdaptor implements RequestPairSource {

    private ServletRequest request;
    private ServletResponse response;
    private JWTHttpDataWapper wapper;

    public JWTSourceAdaptor(RequestPairSource source) {
        this.request = source.getServletRequest();
        this.response = source.getServletResponse();
        this.wapper = new JWTHttpDataWapper(this);
    }

    public JWTSourceAdaptor(ServletRequest request, ServletResponse response) {
        this.request = request;
        this.response = response;
        this.wapper = new JWTHttpDataWapper(this);
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
        return wapper.readData();
    }
    public boolean storeData(String str) {
        return wapper.storeData(str);
    }
    
    public boolean deleteData() {
        return wapper.deleteData();
    }
    
    public boolean isRequestSourceEmpty() {
        return wapper.isRequestSourceEmpty();
    }
    
    public boolean isSessionJwtTokenCookieEnabled() {
        return wapper.isSessionJwtTokenCookieEnabled();
    }
    public void setSessionJwtTokenCookieEnabled(boolean sessionJwtTokenCookieEnabled) {
        wapper.setSessionJwtTokenCookieEnabled(sessionJwtTokenCookieEnabled);
    }
    
}
