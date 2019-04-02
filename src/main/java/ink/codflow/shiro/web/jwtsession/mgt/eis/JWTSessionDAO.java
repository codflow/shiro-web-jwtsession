package ink.codflow.shiro.web.jwtsession.mgt.eis;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ink.codflow.shiro.web.jwtsession.mgt.JWTSessionFactory;
import ink.codflow.shiro.web.jwtsession.serialize.ObjStrSerializer;
import ink.codflow.shiro.web.jwtsession.serialize.ObjStrUrlSafeSerializer;
import ink.codflow.shiro.web.jwtsession.serialize.SessionJWTConvertor;
import ink.codflow.shiro.web.jwtsession.serialize.SessionJWTSmoothConvertor;
import ink.codflow.shiro.web.jwtsession.util.ThreadDataUtil;

public class JWTSessionDAO extends AbstractSessionDAO {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(JWTSessionDAO.class);
    
    private static final String DRFAULTSECRETKEY = "defaultsecret";
    private SessionFactory sessionFactory;
    private ObjStrSerializer<Object> serializer;
    private SessionJWTConvertor convertor;
    
    
    private String JWT_SecretKey ;
    private String salt = "d@gs3";
    private Algorithm algorithm;

    
    public Algorithm getAlgorithm() {
        if (algorithm != null) {
            return algorithm;
        }
        try {
            return Algorithm.HMAC256(getJWT_SecretKey() + getSalt());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String msg = "JWT Algorithm  Error JWT_SecretKey: " + getJWT_SecretKey();
        throw new AlgorithmMismatchException(msg);
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public JWTSessionDAO() {
        this.sessionFactory = new JWTSessionFactory();

    }

    public JWTSessionDAO(boolean isSessionJwtTokenCookieEnabled)
            throws IllegalArgumentException, UnsupportedEncodingException {
        this.sessionFactory = new JWTSessionFactory();
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        doUpdate(session);
    }

    protected void doUpdate(Session session) {
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
        doDelete(session);
    }

    protected void doDelete(Session session) {
        if (session == null) {
            throw new NullPointerException("session argument cannot be null.");
        }
        Serializable id = session.getId();
        if (id != null) {
            remove(session);
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
        return read((SimpleSession) sessionFactory.createSession(null));
    }

    protected Session storeSession(Serializable id, Session session) {
        if (id == null) {
            throw new NullPointerException("id argument cannot be null.");
        }
        if (!(session instanceof SimpleSession)) {
            throw new UnknownSessionException("not a SimpleSession");
        }
        store(id, (SimpleSession) session);
        return session;
    }

    private SimpleSession read(SimpleSession plainSession) {
        String tokenStr = ThreadDataUtil.getDataSourceFromThread().readData();
        return getConvertorLazy().tokenStr2Session(tokenStr, plainSession);
    }

    @SuppressWarnings("unused")
    private SimpleSession readSessionLazyAttribute(SimpleSession plainSession) {
        String tokenStr = ThreadDataUtil.getDataSourceFromThread().readData();
        return getConvertorLazy().tokenStr2Session(tokenStr, plainSession);
    }

    private void store(Serializable id, SimpleSession session) {
        store(session);
    }

    private void store(SimpleSession session) {
        if (session instanceof SimpleSession) {
            String tokenStr = getConvertorLazy().session2TokenStr(session);
            ThreadDataUtil.getDataSourceFromThread().storeData(tokenStr);
        }
    }

    private void remove(Session session) {
        ThreadDataUtil.getDataSourceFromThread().deleteData();
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public ObjStrSerializer<Object> getSerializer() {
        if (serializer == null) {
            this.serializer = new ObjStrUrlSafeSerializer();
        }
        return serializer;
    }

    public void setSerializer(ObjStrSerializer<Object> serializer) {
        this.serializer = serializer;
    }

    public String getJWT_SecretKey() {
        if (JWT_SecretKey ==null) {
            JWT_SecretKey = DRFAULTSECRETKEY;
            String msg = "Encode with default algorithm :" + algorithm.getName() + "and key:" + DRFAULTSECRETKEY
                    + "may cause security risks!";
            log.warn(msg);
        }
        return JWT_SecretKey;
    }

    public void setJWT_SecretKey(String jWT_SecretKey) {
        JWT_SecretKey = jWT_SecretKey;
    }

    public SessionJWTConvertor getConvertorLazy() {
        if (convertor != null) {
            return convertor;
        }
        return new SessionJWTSmoothConvertor(getSerializer(), getAlgorithm());
    }

    public void setConvertor(SessionJWTConvertor convertor) {

        this.convertor = convertor;
    }
}
