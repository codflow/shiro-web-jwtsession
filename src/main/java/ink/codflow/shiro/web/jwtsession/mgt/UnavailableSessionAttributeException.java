package ink.codflow.shiro.web.jwtsession.mgt;

import org.apache.shiro.ShiroException;

public class UnavailableSessionAttributeException  extends ShiroException{

    /**
     * 
     */
    private static final long serialVersionUID = -8599482538455044068L;

    public UnavailableSessionAttributeException() {
        super();
    }
    
    public UnavailableSessionAttributeException(String message) {
        super(message);
    }
    
    public UnavailableSessionAttributeException(Throwable cause) {
        super(cause);
    }

    public UnavailableSessionAttributeException(String message, Throwable cause) {
        super(message, cause);
    }
}
