package ink.codflow.shiro.web.jwtsession.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.util.RequestPairSource;
import org.apache.shiro.web.util.WebUtils;

import ink.codflow.shiro.web.jwtsession.mgt.eis.JWTSourceAdaptor;

public class ThreadDataUtil {

    public final static String JWTDATA_THREAD_SOURCE = "JWTDATA_SOURCE";

    public static void setDataSourceToThread(SessionKey source) {
        if (WebUtils.isHttp(source)) {
            setDataSourceToThread((RequestPairSource) source);
        }
    }

    public static void setDataSourceToThread(Object context) {
        if (WebUtils.isHttp(context)) {
            setDataSourceToThread((RequestPairSource) context);
        }
    }

    public static void setDataSourceToThread(RequestPairSource source) {
        if (WebUtils.isHttp(source)) {
            JWTSourceAdaptor wapperredSource = new JWTSourceAdaptor(source);
            ThreadContext.put(JWTDATA_THREAD_SOURCE, wapperredSource);
        }
    }

    public static JWTSourceAdaptor getDataSourceFromThread() {
        RequestPairSource source = (RequestPairSource) ThreadContext.get(JWTDATA_THREAD_SOURCE);
        if (source ==null && WebUtils.isHttp(SecurityUtils.getSubject())) {
            HttpServletRequest request = WebUtils.getHttpRequest(SecurityUtils.getSubject());
            HttpServletResponse response = WebUtils.getHttpResponse(SecurityUtils.getSubject());
            JWTSourceAdaptor aNewSource = new JWTSourceAdaptor(request, response);
            setDataSourceToThread(aNewSource);
            return aNewSource;
        }
        
        return (JWTSourceAdaptor)source;
    }
    public static boolean isRequestSourceEmpty() {

        RequestPairSource source = (RequestPairSource) ThreadContext.get(JWTDATA_THREAD_SOURCE);
        if (source !=null) {
            return WebUtils.getHttpRequest(source).getRequestURI()!=null?false:true;
        }
        return true;
    }


    
    
}
