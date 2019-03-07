package ink.codflow.shiro.web.jwtsession.mgt.eis;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.shiro.session.mgt.SimpleSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ink.codflow.shiro.web.jwtsession.io.JdkObjStrSerializerWapper;
import ink.codflow.shiro.web.jwtsession.io.ObjStrSerializer;

public class SessionJwtConvertor {

    private static final String ID_KEY = "jti";
    private static final String START_TIMESTAMP_KEY = "st";
    private static final String STOP_TIMESTAMP_KEY = "sp";
    private static final String LAST_ACCESS_TIME_KEY = "la";
    private static final String TIMEOUT_KEY = "to";
    private static final String EXPIRED_KEY = "ex";
    private static final String HOST_KEY = "ht";
    private static final String ATTRIBUTES_KEY = "ats";

    private static final byte ATTRIBUTE_MASK_POSITION = 1;
    private static final byte ATTRIBUTE_VALUE_POSITION = 0;

    private static final byte ATTRIBUTES_KEY_RECOVER_MASK = 7;
    // private static final byte ATTRIBUTES_VALUE_RECOVER_MASK = 56;

    private String salt = "7dh&uP";
    private Algorithm algorithm;
    private ObjStrSerializer serializer;
    private JWTVerifier verifier;
    private static final Logger log = LoggerFactory.getLogger(SessionJwtConvertor.class);

    public SessionJwtConvertor() throws IllegalArgumentException, UnsupportedEncodingException {
        String JWT_SECRET = "mdefaultsecret";
        String secret_Key = salt + JWT_SECRET;
        this.algorithm = Algorithm.HMAC256(secret_Key);
        this.serializer = new JdkObjStrSerializerWapper();
        String msg = "Encode with default algorithm :" + algorithm.getName() + "and key:" + JWT_SECRET
                + "may cause security risks!";
        log.warn(msg);
    }

    public SessionJwtConvertor(ObjStrSerializer serializer, Algorithm algorithm) {
        this.serializer = serializer;
        this.algorithm = algorithm;
    }

    public String session2TokenStr(Serializable id, SimpleSession session) {
        return session2TokenStr(session);
    }

    public String session2TokenStr(SimpleSession session) {

        Builder jwtBuilder = JWT.create();
        solveSessionState(jwtBuilder, session);
        solveSessionAttributes(jwtBuilder, session);

        String token = jwtBuilder.sign(algorithm);
        return token;
    }

    public SimpleSession tokenStr2Session(String tokenStr, SimpleSession session) {
        if (session == null || tokenStr == null) {
            return null;
        }
        if (tokenStr.isEmpty()) {
            return session;
        }
        try {
            DecodedJWT jwt = createJWT(tokenStr);
            // DecodedJWT jwt = JWT.decode(tokenStr);
            Map<String, Claim> claims = jwt.getClaims();

            for (String keyStr : claims.keySet()) {
                Map<String, Object> valueMap = claims.get(keyStr).asMap();
                if (valueMap == null) {
                    Claim value = claims.get(keyStr);
                    if (ID_KEY.equals(keyStr)) {
                        session.setId(value.asString());
                        continue;
                    }
                    if (START_TIMESTAMP_KEY.equals(keyStr)) {
                        session.setStartTimestamp(value.asDate());
                        continue;
                    }

                    if (STOP_TIMESTAMP_KEY.equals(keyStr)) {
                        session.setStopTimestamp(value.asDate());
                        continue;
                    }
                    if (LAST_ACCESS_TIME_KEY.equals(keyStr)) {
                        session.setLastAccessTime(value.asDate());
                        continue;
                    }
                    if (TIMEOUT_KEY.equals(keyStr)) {
                        session.setTimeout(value.asLong());
                        continue;
                    }
                    if (HOST_KEY.equals(keyStr)) {
                        session.setHost(value.asString());
                        continue;
                    }
                    if (EXPIRED_KEY.equals(keyStr)) {
                        session.setExpired(value.asBoolean());
                        continue;
                    }
                }

                byte typeMask;
                int valueType;
                int keyType;
                Object valueRaw;
                Object valueObject;
                Object keyObject = null;

                for (String atrrbuteKey : valueMap.keySet()) {
                    @SuppressWarnings("unchecked")
                    List<Object> list = (List<Object>) valueMap.get(atrrbuteKey);

                    typeMask = ((Integer) list.get(ATTRIBUTE_MASK_POSITION)).byteValue();
                    valueRaw = list.get(ATTRIBUTE_VALUE_POSITION);
                    valueType = (typeMask >> 3) & ATTRIBUTES_KEY_RECOVER_MASK;
                    valueObject = resolve2Obj(valueRaw, valueType);
                    if (valueObject != null) {
                        keyType = typeMask & ATTRIBUTES_KEY_RECOVER_MASK;
                        keyObject = resolve2Obj(atrrbuteKey, keyType);
                    }
                    session.setAttribute(keyObject, valueObject);

                }

            }

        } catch (JWTDecodeException exception) {
            log.info("jwtsession rec err", exception);
        }
        return session;
    }

    private Object resolve2Obj(Object objRaw, int objType) {

        if (ValueObjectType.OBJECT_TYPE_STRING.typeMask == objType)
            return objRaw;
        if (ValueObjectType.OBJECT_TYPE_BOOLEAN.typeMask == objType)
            return  objRaw instanceof String ? Boolean.valueOf((String) objRaw):(Boolean) objRaw;
        if (ValueObjectType.OBJECT_TYPE_INTEGER.typeMask == objType)
            return objRaw instanceof String ? Integer.valueOf((String) objRaw) : (Integer) objRaw;
        if (ValueObjectType.OBJECT_TYPE_LONG.typeMask == objType)
            return  objRaw instanceof String ? Long.valueOf((String) objRaw) :(Long) (objRaw);
        if (ValueObjectType.OBJECT_TYPE_DOUBLE.typeMask == objType)
            return objRaw instanceof String ? Double.valueOf((String) objRaw) :(Double) (objRaw);
        if (ValueObjectType.OBJECT_TYPE_DATE.typeMask == objType)
            return objRaw instanceof String ? Date.parse((String) objRaw) : new Date((Long) objRaw);
        return serializer.str2ObjDeserialize((String) objRaw);

    }

    private void solveSessionAttributes(Builder jwtBuilder, SimpleSession session) {
        Map<Object, Object> atrributes = session.getAttributes();
        Map<Object, List<Object>> jwtAttributesMap = new LinkedHashMap<Object, List<Object>>();

        if (atrributes != null) {
            Set<Object> attrKeySet = atrributes.keySet();
            List<Object> valueList;
            // Record the types of key and value
            byte typeMask;
            Object value = null;
            String attrKeyStr = null;
            String valueStr = null;
            for (Object attrKey : attrKeySet) {
                typeMask = 0;
                valueList = new ArrayList<Object>();
                value = atrributes.get(attrKey);
                if (value == null) {
                    continue;
                }
                attrKeyStr = ((typeMask |= isSimpleType(attrKey)) > 0) ? attrKey.toString()
                        : serializer.obj2StrSerialize(attrKey);
                if (value != null) {
                    if ((typeMask |= (isSimpleType(value) << 3)) > 0) {
                        valueList.add(value);
                    } else {
                        valueStr = serializer.obj2StrSerialize(value);
                        valueList.add(valueStr);
                    }
                    valueList.add(typeMask);
                    jwtAttributesMap.put(attrKeyStr, valueList);
                }

            }
            UnCheckedPutPlainObject(jwtBuilder, ATTRIBUTES_KEY, jwtAttributesMap);

        }
    }

    private static void solveSessionState(Builder jwtBuilder, SimpleSession session) {
        if (session.getId() != null) {
            jwtBuilder.withJWTId(session.getId().toString());
        }

        if (session.getStartTimestamp() != null) {
            jwtBuilder.withClaim(START_TIMESTAMP_KEY, session.getStartTimestamp());
        }
        if (session.getStopTimestamp() != null) {
            jwtBuilder.withClaim(STOP_TIMESTAMP_KEY, session.getStopTimestamp());
        }
        if (session.getLastAccessTime() != null) {
            jwtBuilder.withClaim(LAST_ACCESS_TIME_KEY, session.getLastAccessTime());
        }
        if (session.getTimeout() > 0) {
            jwtBuilder.withClaim(TIMEOUT_KEY, session.getTimeout());
        }
        if (session.isExpired()) {
            jwtBuilder.withClaim(EXPIRED_KEY, session.isExpired());
        }
        if (session.getHost() != null) {
            jwtBuilder.withClaim(HOST_KEY, session.getHost());
        }
    }

    private static int isSimpleType(Object object) {

        if (object instanceof String) {
            return ValueObjectType.OBJECT_TYPE_STRING.typeMask;
        }
        if (object instanceof Boolean) {
            return ValueObjectType.OBJECT_TYPE_BOOLEAN.typeMask;
        }
        if (object instanceof Integer) {
            return ValueObjectType.OBJECT_TYPE_INTEGER.typeMask;
        }
        if (object instanceof Long) {
            return ValueObjectType.OBJECT_TYPE_LONG.typeMask;
        }
        if (object instanceof Double) {
            return ValueObjectType.OBJECT_TYPE_DOUBLE.typeMask;
        }
        if (object instanceof Date) {
            return ValueObjectType.OBJECT_TYPE_DATE.typeMask;
        }
        return ValueObjectType.OBJECT_TYPE_OBJECT.typeMask;

    }

    private static void UnCheckedPutPlainObject(Builder builder, String key, Object value) {
        Class<Builder> clazz = Builder.class;
        Method method;
        try {
            method = clazz.getDeclaredMethod("addClaim", String.class, Object.class);
            method.setAccessible(true);
            method.invoke(builder, key, value);

        } catch (Exception e) {
            String msg = "JWT reflect method fail";
            log.error(msg);
            e.printStackTrace();
        }
    }

    public DecodedJWT createJWT(String tokenStr) {
        if (this.verifier == null) {
            this.verifier = JWT.require(algorithm).build();
        }
        return this.verifier.verify(tokenStr);
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public ObjStrSerializer getSerializer() {
        return serializer;
    }

    public void setSerializer(ObjStrSerializer serializer) {
        this.serializer = serializer;
    }

}
