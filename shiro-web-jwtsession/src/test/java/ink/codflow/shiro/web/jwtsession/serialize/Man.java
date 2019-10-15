package ink.codflow.shiro.web.jwtsession.serialize;


import java.io.Serializable;


public class Man  implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -701342133789778467L;
    public int old =22;
    public String name ="hen";
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj ==null && obj instanceof Man) {
            return false;
        }
        Man obj0 = (Man)obj;
        boolean eq =  (this.old == obj0.old) &&(this.name.equals(obj0.name)) ;
        
        return eq ;
    }
    
    @Override
    public int hashCode() {
        int result = old;
        result = 31* result + name.hashCode();
        return result;
    }
}
