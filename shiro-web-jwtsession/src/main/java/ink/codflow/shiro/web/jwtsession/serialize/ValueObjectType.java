package ink.codflow.shiro.web.jwtsession.serialize;

import java.util.Date;


public enum ValueObjectType {

    OBJECT_TYPE_STRING(String.class,1),OBJECT_TYPE_BOOLEAN(Boolean.class,2),
    OBJECT_TYPE_INTEGER(Integer.class,3),OBJECT_TYPE_LONG(Long.class,4),
    OBJECT_TYPE_DOUBLE(Double.class,5),OBJECT_TYPE_DATE(Date.class,6),
    OBJECT_TYPE_OBJECT(Date.class,-1);
    @SuppressWarnings("rawtypes")
    final Class clazz;
    final int typeMask;
    

    @SuppressWarnings("rawtypes")
    ValueObjectType(Class clazz,int mask){
        this.clazz = clazz;
        this.typeMask = mask;
    }


/*    ValueObjectType(Class clazz){
        this.clazz = clazz;
        this.typeMask =StaticFields.COUNTER++;
       
    }
    
    
    private static final class StaticFields {
        private static byte COUNTER = 0;
    }
    
*/

}
