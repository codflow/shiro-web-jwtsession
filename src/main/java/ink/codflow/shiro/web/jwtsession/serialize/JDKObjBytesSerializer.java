package ink.codflow.shiro.web.jwtsession.serialize;

import java.io.Serializable;

import org.apache.shiro.io.DefaultSerializer;
import org.apache.shiro.io.SerializationException;

public class JDKObjBytesSerializer implements ObjBytesSerializer<Object>  {

    @SuppressWarnings("rawtypes")
    DefaultSerializer serializer = new DefaultSerializer();
    
    /*
     * 
     * Wrap the JDK Serialize function
     * (non-Javadoc)
     * @see ink.codflow.shiro.web.jwtsession.serialize.ObjBytesSerializer#obj2bytesSerialize(java.lang.Object)
     */
    public byte[] obj2bytesSerialize(Object obj) {
        if (obj instanceof Serializable) {
            @SuppressWarnings("unchecked")
            byte[] bs = serializer.serialize((Serializable) obj);
            return bs;
        }
        String msg = "Unable to serialize object [" + obj + "].  " +
                "In order for the DefaultSerializer to serialize this attribute, the [" + obj.getClass().getName() + "] " +
                "class must implement java.io.Serializable.";
        throw new SerializationException(msg);

    }
    public Object bytes2ObjSerialize(byte[] bs) {
        return serializer.deserialize(bs);
    }
    
}
