package ink.codflow.shiro.web.jwtsession.serialize;

import java.io.Serializable;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.io.DefaultSerializer;
import org.apache.shiro.io.SerializationException;

public class ObjStrUrlSafeSerializer implements ObjStrSerializer<Object> {

    @SuppressWarnings("rawtypes")
    ObjBytesSerializer serializer ;
    
    public ObjStrUrlSafeSerializer() {
        serializer = new JDKObjBytesSerializer();
    }
    
    public ObjStrUrlSafeSerializer(ObjBytesSerializer<Serializable> serializer) {
        this.serializer  = serializer;
    }
    
    
    @Override
    public Object str2ObjDeserialize(String str) {
        byte[] bs = Base64.decode(str);
        return serializer.bytes2ObjSerialize(bs);
    }

    @Override
    public String obj2StrSerialize(Object obj) {
        if (obj instanceof Serializable) {
            @SuppressWarnings("unchecked")
            byte[] bs = serializer.obj2bytesSerialize((Serializable) obj);
            return Base64.encodeToString(bs);
        }
        String msg = "Unable to serialize object [" + obj + "].  " +
                "In order for the DefaultSerializer to serialize this attribute, the [" + obj.getClass().getName() + "] " +
                "class must implement java.io.Serializable.";
        throw new SerializationException(msg);

    }

    /*
     * @Override public Object str2ObjDeserialize(String objectBase64Str) { String
     * objStr = Base64.decodeToString(objectBase64Str); Object valueObject = null;
     * ByteArrayInputStream input = new ByteArrayInputStream(objStr.getBytes()); try
     * { ObjectInputStream objectIn = new ObjectInputStream(input); valueObject=
     * objectIn.readObject(); objectIn.close(); input.close(); } catch (Exception e)
     * { e.printStackTrace(); } return valueObject; }
     * 
     * @Override public String obj2StrSerialize(Object obj) { ByteArrayOutputStream
     * output = new ByteArrayOutputStream(); ObjectOutputStream objectOut; try {
     * objectOut = new ObjectOutputStream(output); objectOut.writeObject(obj);
     * objectOut.close(); output.close(); } catch (IOException e) {
     * 
     * e.printStackTrace(); } // Prevent special character String objectBase64Str =
     * Base64.encodeToString(output.toByteArray());
     * 
     * return objectBase64Str;
     * 
     * }
     * 
     */
}
