package ink.codflow.shiro.jwtsession.io;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import ink.codflow.shiro.web.jwtsession.io.JdkObjStrSerializerWapper;
import ink.codflow.shiro.web.jwtsession.io.ObjStrSerializer;

public class JdkObjStrSerializerTest {
    String str;

    @Test
    public void testObj2StrSerialize() {
        ObjStrSerializer serializer = new JdkObjStrSerializerWapper();
        String string = serializer.obj2StrSerialize(new Man());
        this.str = string;
        
    }

    @Test
    public void testStr2ObjDeserialize() {

        ObjStrSerializer serializer = new JdkObjStrSerializerWapper();
        String string = serializer.obj2StrSerialize(new Man());
        Object result = serializer.str2ObjDeserialize(str);
        
        assertTrue(result.equals(new Man()));
    }
}
