package ink.codflow.shiro.web.jwtsession.mgt;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.mgt.SimpleSession;

import ink.codflow.shiro.web.jwtsession.serialize.SessionJWTSmoothConvertor;


public class JWTSession extends SimpleSession implements Cloneable {
    /**
     * 
     */
    private static final long serialVersionUID = -3429411958810682305L;
    private transient Map<Object, UnSafeRawList> attributesRawMap;
    private transient SessionJWTSmoothConvertor convertor;

    public JWTSession(String host) {
        super(host);
    }

    public JWTSession() {
        super();
    }

    private void recoverRaw() {
        for (Entry<Object, UnSafeRawList> attributePair : attributesRawMap.entrySet()) {
            UnSafeRawList valueList = attributePair.getValue();
            Object valueObject = convertor.recoverValueObject(valueList);
            setRealAttribute(attributePair.getKey(), valueObject);
            attributesRawMap.remove(attributePair.getKey());
        }
    }

    public Map<Object, UnSafeRawList> getAttributesRawMap() {
        
        if (this.attributesRawMap == null) {
            this.attributesRawMap = new HashMap<Object, UnSafeRawList>();
        }
        return attributesRawMap;
    }

    public void setAttributesRawMap(Map<Object, UnSafeRawList> attributesRawMap) {
        this.attributesRawMap = attributesRawMap;
    }

    public void setAttributeRaw(Object key, UnSafeRawList value) {
        getAttributesRawMap().put(key, value);
    }

    public UnSafeRawList getAttributeRaw(Object key) {
        return getAttributesRawMap().get(key);
    }
    protected Object removeRawAttribute(Object key) {
        Map<Object, UnSafeRawList> attributes = getAttributesRawMap();
        if (attributes == null) {
            return null;
        } else {
            return (Object)attributes.remove(key);
        }
    }

    @Override
    public Object getAttribute(Object key) {

        Object value = getRealAttribute(key);
        if (value == null && getAttributesRawMap().containsKey(key)) {
            List<Object> valueList = getAttributesRawMap().get(key);
            value = getConvertor().recoverValueObject(valueList);
        }
        return value;
    }
    
    
    @Override
    public Collection<Object> getAttributeKeys() throws InvalidSessionException {
        
        Map<Object, Object> attributes = getRealAttributes();
        Collection<Object> raw = getAttributesRawMap().keySet();
        if (attributes != null) {
            Collection<Object> c = attributes.keySet();
            raw.addAll(c);
        }
        return raw;
    }
    
    
    
    @Override
    public Map<Object, Object> getAttributes() {
        if (attributesRawMap!=null && !getAttributesRawMap().isEmpty()) {
            recoverRaw();
        }
        return getRealAttributesLazy();
    }
    public Object removeAttribute(Object key) {

        getAttribute(key);
        Map<Object, Object> attributes = getRealAttributes();
        if (attributes == null) {
            return null;
        } else {
            return attributes.remove(key);
        }
    }
    @Override
    public void setAttribute(Object key, Object value) {
        if (key instanceof Serializable && value instanceof Serializable) {
            if (attributesRawMap!=null && getAttributesRawMap().containsKey(key)) {
                getAttributesRawMap().remove(key);
            }
            setRealAttribute(key, value);
        } else {
            String msg = "In order for the Serializer to serialize this attribute, class" + key.getClass().getName()
                    + "or" + value.getClass().getName() + "must implement java.io.Serializable.";
            throw new UnavailableSessionAttributeException(msg);
        }
    }


    public void setAttributes(Map<Object, Object> attributes) {
        if (this.attributesRawMap != null) {
            this.attributesRawMap.clear();
        }
        
        super.setAttributes(attributes);  
    }


    protected Object getRealAttribute(Object key) {
        Map<Object, Object> attributes = super.getAttributes();
        if (attributes == null) {
            return null;
        }
        return attributes.get(key);
    }
    public Object removeRealAttribute(Object key) {
        
        Map<Object, Object> attributes = super.getAttributes();
        if (attributes == null) {
            return null;
        } else {
            return attributes.remove(key);
        }
    }
    protected Map<Object, Object> getRealAttributesLazy() {
        Map<Object, Object> attributes = super.getAttributes();
        if (attributes == null) {
            attributes = new HashMap<Object, Object>();
            setRealAttributes(attributes);
        }
        return attributes;
    }
    
    private void setRealAttributes(Map<Object, Object> attributes) {
       super.setAttributes(attributes);
        
    }

    protected void setRealAttribute(Object key, Object value) {
        if (value == null) {
            
            removeRealAttribute(key);
        } else {
            getRealAttributesLazy().put(key, value);
        }
    }
    

    
    
    
    public SessionJWTSmoothConvertor getConvertor() {
        return convertor;
    }

    public void setConvertor(SessionJWTSmoothConvertor convertor) {
        this.convertor = convertor;
    }

    @Override
    public JWTSession clone() {
        JWTSession cloned = null;
        try {
            cloned = (JWTSession) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return cloned;
    }

    public Map<Object, Object> getRealAttributes() {
        return super.getAttributes();
    }
}
