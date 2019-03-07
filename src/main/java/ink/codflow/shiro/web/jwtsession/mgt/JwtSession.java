package ink.codflow.shiro.web.jwtsession.mgt;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.mgt.SimpleSession;

import com.auth0.jwt.interfaces.Claim;

public class JwtSession extends SimpleSession {
    /**
     * 
     */
    private static final long serialVersionUID = -3429411958810682305L;
    private transient Map<String, Claim> attributesJsonMap;

    public JwtSession(String host) {
        super(host);
    }

    public JwtSession() {
        super();
    }

    public Map<String, Claim> getAttributesJsonMap() {
        return attributesJsonMap;
    }

    public void setAttributesJsonMap(Map<String, Claim> attributesJsonMap) {
        this.attributesJsonMap = attributesJsonMap;
    }

    @Override
    public Object getAttribute(Object key) {
        return super.getAttribute(key);
    }

    @Override
    public Collection<Object> getAttributeKeys() throws InvalidSessionException {
        return super.getAttributeKeys();
    }

    @Override
    public Map<Object, Object> getAttributes() {

        return super.getAttributes();
    }

    public void setAttribute(Serializable key, Serializable value) {

        super.setAttribute(key, value);
    }

    @Override
    public void setAttribute(Object key, Object value) {
        if (key instanceof Serializable && value instanceof Serializable) {
            super.setAttribute(key, value);
        } else {
            String msg = "In order for the Serializer to serialize this attribute, class" + key.getClass().getName()
                    + "or" + value.getClass().getName() + "must implement java.io.Serializable.";
            throw new UnavailableSessionAttributeException(msg);
        }
    }
}
