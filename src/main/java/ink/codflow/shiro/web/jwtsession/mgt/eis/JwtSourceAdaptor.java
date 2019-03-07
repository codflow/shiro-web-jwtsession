package ink.codflow.shiro.web.jwtsession.mgt.eis;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.web.util.RequestPairSource;
public class JwtSourceAdaptor implements RequestPairSource {

    private ServletRequest request;
    private ServletResponse response;
    private JwtHttpDataWapper wapper;

    public JwtSourceAdaptor(RequestPairSource source) {
        this.request = source.getServletRequest();
        this.response = source.getServletResponse();
        this.wapper = new JwtHttpDataWapper(this);
    }

    public JwtSourceAdaptor(ServletRequest request, ServletResponse response) {
        this.request = request;
        this.response = response;
        this.wapper = new JwtHttpDataWapper(this);
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
