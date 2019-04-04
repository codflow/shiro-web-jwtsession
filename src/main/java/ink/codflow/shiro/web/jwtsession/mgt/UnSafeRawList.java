package ink.codflow.shiro.web.jwtsession.mgt;

import java.util.List;

/**
 * UnSafeRawList
 * Wrap the Raw JWT string list and prevent unsafe operation
 * 
 * format: 
 *          list[@see SessionJWTConvertor#ATTRIBUTE_VALUE_POSITION] :the serialized and encoded value string
 *          list[@see SessionJWTConvertor#ATTRIBUTE_MASK_POSITION] :the type mask of key-value pair
 */
public interface UnSafeRawList  extends List<Object>{
    
}