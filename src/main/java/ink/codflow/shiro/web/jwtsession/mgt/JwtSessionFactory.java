package ink.codflow.shiro.web.jwtsession.mgt;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;

public class JwtSessionFactory implements SessionFactory {

    @Override
    public Session createSession(SessionContext initData) {
        if (initData != null) {
            String host = initData.getHost();
            if (host != null) {
                return new JwtSession(host);
            }
        }
        return new JwtSession();
    }
    public Session createSession() {
        return new JwtSession();
    }

}
