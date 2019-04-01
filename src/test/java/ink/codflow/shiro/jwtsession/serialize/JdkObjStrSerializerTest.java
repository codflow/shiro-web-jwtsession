package ink.codflow.shiro.jwtsession.serialize;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ink.codflow.shiro.web.jwtsession.serialize.ObjStrSerializer;
import ink.codflow.shiro.web.jwtsession.serialize.ObjStrUrlSafeSerializer;

public class JdkObjStrSerializerTest {
    String str;

    @Test
    public void testObj2StrSerialize() {
        ObjStrSerializer serializer = new ObjStrUrlSafeSerializer();
        String string = serializer.obj2StrSerialize(new Man());
        this.str = string;
        
    }

    @Test
    public void testStr2ObjDeserialize() {

        ObjStrSerializer serializer = new ObjStrUrlSafeSerializer();
        String string = serializer.obj2StrSerialize(new Man());
        Object result = serializer.str2ObjDeserialize(str);
        
        assertTrue(result.equals(new Man()));
    }
}
