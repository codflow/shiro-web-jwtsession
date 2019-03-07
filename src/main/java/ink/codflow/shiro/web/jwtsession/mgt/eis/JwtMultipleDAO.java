package ink.codflow.shiro.web.jwtsession.mgt.eis;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ink.codflow.shiro.web.jwtsession.mgt.JwtSessionFactory;

public class JwtMultipleDAO extends AbstractSessionDAO {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(JwtMultipleDAO.class);
    private JwtResouceCenter center;
    private SessionFactory sessionFactory;

    public JwtMultipleDAO() throws IllegalArgumentException, UnsupportedEncodingException {
        this.sessionFactory = new JwtSessionFactory();
        this.center = new JwtResouceCenter();
    }

    public JwtMultipleDAO(boolean isSessionJwtTokenCookieEnabled)
            throws IllegalArgumentException, UnsupportedEncodingException {
        this.sessionFactory = new JwtSessionFactory();
        this.center = new JwtResouceCenter();
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        storeSession(session.getId(), session);
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void delete(Session session) {
        if (session == null) {
            throw new NullPointerException("session argument cannot be null.");
        }
        Serializable id = session.getId();
        if (id != null) {
            center.delete(session);
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {

        Collection<Session> sessions = new ArrayList<Session>();
        Serializable sessionId = "jwttoken";
        sessions.add(doReadSession(sessionId));
        return sessions;
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        storeSession(sessionId, session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        
        return center.read((SimpleSession) sessionFactory.createSession(null));
    }

    protected Session storeSession(Serializable id, Session session) {
        if (id == null) {
            throw new NullPointerException("id argument cannot be null.");
        }
        if (!(session instanceof SimpleSession)) {
            throw new UnknownSessionException("not a SimpleSession");
        }
        center.store(id, (SimpleSession) session);
        return session;
    }



}
