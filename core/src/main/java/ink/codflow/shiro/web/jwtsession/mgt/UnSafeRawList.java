package ink.codflow.shiro.web.jwtsession.mgt;

import java.util.ArrayList;
import java.util.Collection;

/**
 * UnSafeRawList
 * Wrap the Raw JWT string list and prevent unsafe operation
 * 
 * format: 
 *          list[@see SessionJWTConvertor#ATTRIBUTE_VALUE_POSITION] :the serialized and encoded value string
 *          list[@see SessionJWTConvertor#ATTRIBUTE_MASK_POSITION] :the type mask of key-value pair
 */
public class UnSafeRawList  extends ArrayList<Object>{
    /**
     * 
     */
    private static final long serialVersionUID = -4614493259385607626L;

    public UnSafeRawList(Collection<? extends Object> c) {
        super(c);
    }
}