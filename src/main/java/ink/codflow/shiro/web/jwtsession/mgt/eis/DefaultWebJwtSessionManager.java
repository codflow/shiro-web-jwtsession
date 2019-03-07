package ink.codflow.shiro.web.jwtsession.mgt.eis;

import java.io.Serializable;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.DelegatingSession;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.apache.shiro.web.util.RequestPairSource;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ink.codflow.shiro.web.jwtsession.util.ThreadDataUtil;

public class DefaultWebJwtSessionManager extends DefaultSessionManager implements WebSessionManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultWebJwtSessionManager.class);

    private boolean sessionJwtTokenCookieEnabled;
    private boolean sessionIdUrlRewritingEnabled;

    public DefaultWebJwtSessionManager() {
        try {
            super.sessionDAO = new JwtMultipleDAO();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.sessionJwtTokenCookieEnabled = true;
        this.sessionIdUrlRewritingEnabled = false;
    }

/*    @Override
    public Serializable getSessionId(SessionKey key) {
        Serializable id = super.getSessionId(key);
        if (id == null && WebUtils.isWeb(key)) {
            ServletRequest request = WebUtils.getRequest(key);
            ServletResponse response = WebUtils.getResponse(key);
            id = getSessionId(request, response);
        }
        return id;
    }
    
*/


    @Override
    protected void onStart(Session session, SessionContext context) {
        super.onStart(session, context);

    }

    @Override
    protected Session doCreateSession(SessionContext context) {

        setDataSource(context);
        return super.doCreateSession(context);
    }

    protected Session createExposedSession(Session session, SessionContext context) {
        if (!WebUtils.isWeb(context)) {
            return super.createExposedSession(session, context);
        }
        ServletRequest request = WebUtils.getRequest(context);
        ServletResponse response = WebUtils.getResponse(context);
        SessionKey key = new WebSessionKey(session.getId(), request, response);
        return new DelegatingSession(this, key);
    }

    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {

        boolean isTokenExists = false;
        setDataSource(sessionKey);
        setUrlRewriteFlag(sessionKey);
        if (WebUtils.isHttp(sessionKey)) {
            JwtSourceAdaptor source = new JwtSourceAdaptor((RequestPairSource) sessionKey);
            isTokenExists = source.readData() != null;
            isTokenExists = true;
        }
        if (StringUtils.hasText((String) sessionKey.getSessionId())) {
            isTokenExists = true;
        }

        if (isTokenExists == false) {
            log.debug("Unable to resolve session current request  [{}].  Returning null to indicate a "
                    + "session could not be found.", sessionKey);
            return null;
        }
        Session s = retrieveSessionFromDataSource(JwtHttpDataWapper.DEFAULT_JWT_SESSION_COOKIE_NAME);
        if (s == null) {
            // session ID was provided, meaning one is expected to be found, but we couldn't
            // find one:
            String msg = "Could not find session with current request";
            throw new UnknownSessionException(msg);
        }
        return s;
    }

    private void setUrlRewriteFlag(SessionKey key) {
        Serializable id = super.getSessionId(key);
        if (id == null && WebUtils.isWeb(key)) {
            ServletRequest request = WebUtils.getRequest(key);
         // always set rewrite flag - SHIRO-361
            request.setAttribute(ShiroHttpServletRequest.SESSION_ID_URL_REWRITING_ENABLED,isSessionIdUrlRewritingEnabled());
        }        
    }

    private void setDataSource(Object pairSource) {

        if (ThreadDataUtil.isRequestSourceEmpty()) {
            ThreadDataUtil.setDataSourceToThread(pairSource);
            ThreadDataUtil.getDataSourceFromThread().setSessionJwtTokenCookieEnabled(sessionJwtTokenCookieEnabled);
        }
    }

    public boolean isSessionIdUrlRewritingEnabled() {
        return sessionIdUrlRewritingEnabled;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void setSessionIdUrlRewritingEnabled(boolean sessionIdUrlRewritingEnabled) {
        this.sessionIdUrlRewritingEnabled = sessionIdUrlRewritingEnabled;
    }

    @SuppressWarnings("unused")
    private boolean isSessionJwtTokenCookieEnabled() {
        return sessionJwtTokenCookieEnabled;
    }

    @Override
    public boolean isServletContainerSessions() {
        return false;
    }
}
