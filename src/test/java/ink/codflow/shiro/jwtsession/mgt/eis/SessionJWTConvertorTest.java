package ink.codflow.shiro.jwtsession.mgt.eis;

import static org.junit.Assert.*;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.junit.Test;

import ink.codflow.shiro.jwtsession.io.Man;
import ink.codflow.shiro.web.jwtsession.mgt.JwtSession;
import ink.codflow.shiro.web.jwtsession.mgt.JwtSessionFactory;
import ink.codflow.shiro.web.jwtsession.mgt.eis.SessionJwtConvertor;

public class SessionJWTConvertorTest {

    @Test
    public void test() throws IllegalArgumentException, UnsupportedEncodingException {
        SessionJwtConvertor convertor = new SessionJwtConvertor();
        JwtSessionFactory factory = new JwtSessionFactory();
        JwtSession session = (JwtSession) factory.createSession();
        
        session.setAttribute("string", "sdddd");
        session.setAttribute("obj", new Man());
        session.setAttribute("boolean", true);
        session.setAttribute("int", 5);
        session.setAttribute("data", new Date());
        session.setAttribute( new Man(),"obj");
        session.setAttribute(true,"boolean");
        session.setAttribute( 5,"int");
        session.setAttribute(new Date(),"data");
      //  session.setId("6554-526151-55");
        String tokenStr = convertor.session2TokenStr(session);
        
        System.out.println(tokenStr);
        Session session2 = factory.createSession();
        convertor.tokenStr2Session(tokenStr, (SimpleSession) session2);
        assertTrue(session.equals(session2));
    }


}