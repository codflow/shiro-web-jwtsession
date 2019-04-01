package ink.codflow.shiro.web.jwtsession.mgt.eis;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.CacheManagerAware;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.ValidatingSession;

import ink.codflow.shiro.web.jwtsession.mgt.JWTSession;
import ink.codflow.shiro.web.jwtsession.serialize.ObjBytesSerializer;

public class PartialCacheJWTSessionDAO extends JWTSessionDAO implements CacheManagerAware {

    private  boolean cacheSessionIdOnly = true;
    private static final String prefixT = "_ctol_";
    ObjBytesSerializer<Object> serializer;

    CacheManager cacheManager;

    Cache<Serializable, Map<Object, Object>> cache;

    private Cache<Serializable, Map<Object, Object>> activeSessions;
    private String activeSessionsCacheName = ACTIVE_SESSION_CACHE_NAME;
    public static final String ACTIVE_SESSION_CACHE_NAME = "shiro-activePartialSessionCache";

    public PartialCacheJWTSessionDAO() {

    }

    protected Cache<Serializable, Map<Object, Object>> createActiveSessionsCache() {
        Cache<Serializable, Map<Object, Object>> cache = null;
        CacheManager mgr = getCacheManager();
        if (mgr != null) {
            String name = getActiveSessionsCacheName();
            cache = mgr.getCache(name);
        }
        return cache;
    }

    private String getActiveSessionsCacheName() {
        return activeSessionsCacheName;
    }

    public void setActiveSessionsCacheName(String activeSessionsCacheName) {
        this.activeSessionsCacheName = activeSessionsCacheName;
    }

    @Override
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    private CacheManager getCacheManager() {

        return this.cacheManager;
    }

    @Override
    public Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session, sessionId);
        Session remainingSession = partiallyCacheSession((SimpleSession) session);
        storeSession(sessionId, remainingSession);
        return sessionId;
    }

    public Session readSession(Serializable sessionId) throws UnknownSessionException {

        JWTSession s = (JWTSession) super.readSession(sessionId);
        sessionId = s.getId();
        Map<Object, Object> attributesMap = getCachedSessionAttributes(sessionId);
        if (s == null || attributesMap == null) {
            return null;
        }
        if (attributesMap.size() > 1) {
            for (Entry<Object, Object> entry : attributesMap.entrySet()) {
//                if (!entry.getKey().equals(sessionId)) {
//                    s.setAttribute(entry.getKey(), entry.getValue());
//                }
                s.setAttributes(attributesMap);
            }
        }
        // s.setAttributes(attributesMap);
        return s;
    }

    protected Session partiallyCacheSession(Session session) {
        Session storeSession = (SimpleSession) session;
        if (isCacheSessionIdOnly()) {
            doCacheSessionId(session);

        } else {
            doCacheSessionAttributes(session);
            storeSession = cloneSessionWithoutAts(session);
        }

        return storeSession;
    }

    private Map<Object, Object> getCachedSessionAttributes(Serializable sessionId) {
        Map<Object, Object> cached = null;
        if (sessionId != null) {
            Cache<Serializable, Map<Object, Object>> cache = getActivePartialSessionsCacheLazy();
            if (cache != null) {
                cached = getCachedSessionAttributes(sessionId, cache);
            }
        }
        return cached;
    }

    protected Map<Object, Object> getCachedSessionAttributes(Serializable sessionId,
            Cache<Serializable, Map<Object, Object>> cache) {
        return cache.get(sessionId);
    }

    public void update(Session session) throws UnknownSessionException {

        if (session instanceof ValidatingSession) {
            if (((ValidatingSession) session).isValid() && getCachedContain(session.getId()) != null) {
                Session remainingSession = partiallyCacheSession((SimpleSession) session);
                doUpdate(remainingSession);
            } else {
                uncache(session);
                doDelete(session);
            }
        } else {
            Session remainingSession = partiallyCacheSession((SimpleSession) session);
            doUpdate(remainingSession);
        }
    }

    public void delete(Session session) {
        uncache(session);
        doDelete(session);
    }

    private void doCacheSessionId(Session session) {
        Serializable sessionId = session.getId();
        Serializable createTime = session.getStartTimestamp().getTime();
        Map<Object, Object> map = new LinkedHashMap<Object, Object>();
//        StringBuilder sb = new StringBuilder();
//        sb.append(prefixT).append(sessionId);

        Serializable mapKey = sessionId;
        map.put(mapKey, createTime);
        cache(sessionId, map);
    }

    private Session doCacheSessionAttributes(Session session) {

        Map<Object, Object> map = extractSessionAttributes(session);
        if (map ==null) {
            map = new HashMap<Object, Object>();
        }
        Serializable sessionId = session.getId();
        map.put(sessionId, session.getStartTimestamp());
        cache(sessionId, map);
        return session;
    }

    protected void cache(Serializable sessionId, Map<Object, Object> attributeMaps) {
        if (attributeMaps == null || sessionId == null) {

            return;
        }
        Cache<Serializable, Map<Object, Object>> cache = getActivePartialSessionsCacheLazy();
        if (cache == null) {
            return;
        }
        cache(sessionId, attributeMaps, cache);
    }

    private Session cloneSessionWithoutAts(Session session) {
        JWTSession session0 = (JWTSession) ((JWTSession) session).clone();
        session0.setAttributes(null);
        return session0;
    }

    private Map<Object, Object> extractSessionAttributes(Session session) {
        SimpleSession session0 = (SimpleSession) session;
        Map<Object, Object> attributeMaps = session0.getAttributes();
//        Map<Object, Object> emptyMap = new HashMap<Object, Object>(0);
//        session0.setAttributes(emptyMap);
        return attributeMaps;
    }

    private void cache(Serializable sessionId, Map<Object, Object> attributesMap,
            Cache<Serializable, Map<Object, Object>> cache) {

        cache.put(sessionId, attributesMap);

    }

    private Map<Object, Object> getCachedContain(Serializable sessionId) {

        Map<Object, Object> map = getActivePartialSessionsCacheLazy().get(sessionId);
        return map;
    }

    protected void uncache(Session session) {
        if (session == null) {
            return;
        }
        Serializable id = session.getId();
        if (id == null) {
            return;
        }
        Cache<Serializable, Map<Object, Object>> cache = getActivePartialSessionsCacheLazy();
        if (cache != null) {
            cache.remove(id);
        }
    }

    private Cache<Serializable, Map<Object, Object>> getActivePartialSessionsCacheLazy() {
        if (this.activeSessions == null) {
            this.activeSessions = createActiveSessionsCache();
        }
        return activeSessions;
    }

    public boolean isCacheSessionIdOnly() {
        return cacheSessionIdOnly;
    }

    public void setCacheSessionIdOnly(boolean cacheSessionIdOnly) {
        this.cacheSessionIdOnly = cacheSessionIdOnly;
    }

}
