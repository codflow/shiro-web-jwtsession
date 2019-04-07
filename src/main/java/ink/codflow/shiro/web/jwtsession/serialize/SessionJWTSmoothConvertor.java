package ink.codflow.shiro.web.jwtsession.serialize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;

import org.apache.shiro.session.mgt.SimpleSession;
import ink.codflow.shiro.web.jwtsession.mgt.JWTSession;
import ink.codflow.shiro.web.jwtsession.mgt.UnSafeRawList;

public class SessionJWTSmoothConvertor extends SessionJWTConvertor {
    public SessionJWTSmoothConvertor() {
    }
    
    public SessionJWTSmoothConvertor(ObjStrSerializer<Object> serializer, Algorithm algorithm) {
        super(serializer, algorithm);
    }

    public Object recoverValueObject(List<Object> valueList) {
        byte typeMask = ((Integer) valueList.get(ATTRIBUTE_MASK_POSITION)).byteValue();
        return recoverValueObject(valueList, typeMask);
    }

    public Object recoverKeyObject(List<Object> valueList) {
        byte typeMask = ((Integer) valueList.get(ATTRIBUTE_MASK_POSITION)).byteValue();
        return recoverKeyObject(valueList, typeMask);
    }
    
    @Override
    protected List<Object> solveValueObject(Object value, byte typeMask) {
        String valueStr;
        List<Object>  valueList = new ArrayList<Object>();
        if ((typeMask |= (isSimpleType(value) << 3)) > 0) {
            valueList.add(value);
        } else {
            valueStr = serializer.obj2StrSerialize(value);
            valueList.add(valueStr);
        }
        valueList.add(typeMask);
        return valueList;
    }
    @Override
    protected Map<Object, Object> getSessionAttributes(SimpleSession session) {
        Map<Object, Object> atrributes = ((JWTSession)session).getAttributes();
       
        return atrributes;
    }
    
    public SimpleSession tokenStr2Session(String tokenStr, SimpleSession session) {
        ((JWTSession)session).setConvertor(this);
        return super.tokenStr2Session( tokenStr, session);
    }
    
    @Override
    protected SimpleSession solveAttributesClaim(SimpleSession session, Claim value) {
        Map<String, Object> valueMap = value.asMap();
        
        for (String atrrbuteKey : valueMap.keySet()) {
            
            @SuppressWarnings("unchecked")//get pair 
            UnSafeRawList rawList = new UnSafeRawList( (Collection<? extends Object>) valueMap.get(atrrbuteKey)) ;
            byte typeMask = ((Integer) rawList.get(ATTRIBUTE_MASK_POSITION)).byteValue();
            Object keyObject = recoverKeyObject(atrrbuteKey, typeMask);
            ((JWTSession)session).setAttributeRaw(keyObject, rawList);
        }
        return session;
    }
    
    
}
