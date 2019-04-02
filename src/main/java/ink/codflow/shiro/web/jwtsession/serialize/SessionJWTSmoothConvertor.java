package ink.codflow.shiro.web.jwtsession.serialize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;

import org.apache.shiro.session.mgt.SimpleSession;
import ink.codflow.shiro.web.jwtsession.mgt.JWTSession;
import ink.codflow.shiro.web.jwtsession.mgt.UnSafeRawList;

public class SessionJWTSmoothConvertor extends SessionJWTConvertor {

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
        Map<Object, Object> atrributes = ((JWTSession)session).getRecoveredAttributes();
       
        return atrributes;
    }
    
    @Override
    protected SimpleSession solveAttributesClaim(SimpleSession session, Claim value) {
        Map<String, Object> valueMap = value.asMap();
        
        for (String atrrbuteKey : valueMap.keySet()) {
            
            UnSafeRawList rawList = (UnSafeRawList) valueMap.get(atrrbuteKey);
            byte typeMask = ((Integer) rawList.get(ATTRIBUTE_MASK_POSITION)).byteValue();
            Object keyObject = recoverKeyObject(atrrbuteKey, typeMask);
            session.setAttribute(keyObject, rawList);
        }
        return session;
    }
}
