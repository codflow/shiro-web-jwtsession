package ink.codflow.shiro.web.jwtsession.serialize;

public interface ObjStrSerializer<T> {
       public T str2ObjDeserialize(String string);
       public String obj2StrSerialize(T obj);
}
