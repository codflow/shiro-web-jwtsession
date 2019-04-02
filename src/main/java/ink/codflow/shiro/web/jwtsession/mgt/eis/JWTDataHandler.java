package ink.codflow.shiro.web.jwtsession.mgt.eis;

public interface JWTDataHandler<T> {

    
    
    /**
     * Returns value from data source 
     * 
     * @return value if exists ,<code>null</code> otherwise.
     */
     T readData();
     
     /**
      * Store value from data source 
      * 
      * @return <code>true</code> if success ,<code>false</code> otherwise.
      */
     boolean storeData(T value);
     /**
      * Delete value from data source 
      * 
      * @return <code>true</code> if success ,<code>false</code> otherwise.
      */
     boolean deleteData();
}
