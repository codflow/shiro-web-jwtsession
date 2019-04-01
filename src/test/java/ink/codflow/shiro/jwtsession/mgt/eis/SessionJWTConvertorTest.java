package ink.codflow.shiro.jwtsession.mgt.eis;

import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.junit.Test;

import ink.codflow.shiro.jwtsession.serialize.Man;
import ink.codflow.shiro.web.jwtsession.mgt.JWTSession;
import ink.codflow.shiro.web.jwtsession.mgt.JWTSessionFactory;
import ink.codflow.shiro.web.jwtsession.serialize.SessionJWTConvertor;

public class SessionJWTConvertorTest {

  @Test
  public void test() throws IllegalArgumentException, UnsupportedEncodingException {
    SessionJWTConvertor convertor = new SessionJWTConvertor();
    JWTSessionFactory factory = new JWTSessionFactory();
    JWTSession session = (JWTSession) factory.createSession();
        
        session.setAttribute("string", "sdddd");
        session.setAttribute("obj", new Man());
        session.setAttribute("boolean", true);
        session.setAttribute("int", 5);
        session.setAttribute("data", new Date());
        session.setAttribute( new Man(), "obj");
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