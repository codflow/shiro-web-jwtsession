package ink.codflow.shiro.web.jwtsession.serialize;


public interface ObjBytesSerializer<T>  {
     byte[] obj2bytesSerialize(T obj);
     T bytes2ObjSerialize(byte[] bs);
}
