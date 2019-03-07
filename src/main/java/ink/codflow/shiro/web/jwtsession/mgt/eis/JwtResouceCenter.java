package ink.codflow.shiro.web.jwtsession.mgt.eis;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import com.auth0.jwt.algorithms.Algorithm;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ink.codflow.shiro.web.jwtsession.io.JdkObjStrSerializerWapper;
import ink.codflow.shiro.web.jwtsession.io.ObjStrSerializer;
import ink.codflow.shiro.web.jwtsession.util.ThreadDataUtil;

public class JwtResouceCenter {
    private ObjStrSerializer serializer;
    private SessionJwtConvertor builder;
    private String JWT_SECRET = "msecret";

    private Algorithm algorithm;

    private static final Logger log = LoggerFactory.getLogger(SessionJwtConvertor.class);

    public JwtResouceCenter() throws IllegalArgumentException, UnsupportedEncodingException {
        this.algorithm = Algorithm.HMAC256(JWT_SECRET);
        this.serializer = new JdkObjStrSerializerWapper();
        this.builder = new SessionJwtConvertor(serializer, algorithm);
        String msg = "Encode with default algorithm :"+algorithm.getName()+ "and key:"+ JWT_SECRET + "may cause security risks!";
        log.warn(msg); 
    }

    public SimpleSession read(SimpleSession plainSession) {
        String tokenStr = ThreadDataUtil.getDataSourceFromThread().readData();
        return builder.tokenStr2Session(tokenStr, plainSession);
    }

    public void store(Serializable id, SimpleSession session) {
        store(session);
    }

    public void store(SimpleSession session) {
        if (session instanceof SimpleSession) {
            String tokenStr = builder.session2TokenStr(session);
            ThreadDataUtil.getDataSourceFromThread().storeData(tokenStr);
        }
    }
    
    public void delete(Session session) {
        ThreadDataUtil.getDataSourceFromThread().deleteData();

    }

    public  ObjStrSerializer getSerializer() {
        return serializer;
    }

    public  void setSerializer(ObjStrSerializer serializer) {
        this.serializer = serializer;
    }
}
