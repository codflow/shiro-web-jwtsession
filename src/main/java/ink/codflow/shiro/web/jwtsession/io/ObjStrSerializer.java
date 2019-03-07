package ink.codflow.shiro.web.jwtsession.io;
public interface ObjStrSerializer {
    abstract Object str2ObjDeserialize(String string);
    abstract String obj2StrSerialize(Object obj);
}
