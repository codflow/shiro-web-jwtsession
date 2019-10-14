package ink.codflow.shiro.web.jwtsession.serialize;

import java.io.Serializable;

import org.apache.shiro.session.mgt.SimpleSession;

public abstract class AbstractSessionJWTConvertor {

	protected static final String ID_KEY = "jti";
	protected static final String START_TIMESTAMP_KEY = "st";
	protected static final String STOP_TIMESTAMP_KEY = "sp";
	protected static final String LAST_ACCESS_TIME_KEY = "la";
	protected static final String TIMEOUT_KEY = "to";
	protected static final String EXPIRED_KEY = "ex";
	protected static final String HOST_KEY = "ht";
	protected static final String ATTRIBUTES_KEY = "ats";

    protected static final byte ATTRIBUTE_MASK_POSITION = 1;
    protected static final byte ATTRIBUTE_VALUE_POSITION = 0;

    protected static final byte ATTRIBUTES_KEY_RECOVER_MASK = 7;
    // private static final byte ATTRIBUTES_VALUE_RECOVER_MASK = 56;
    abstract String session2TokenStr(Serializable id, SimpleSession session);
    abstract String session2TokenStr(SimpleSession session);
    abstract SimpleSession tokenStr2Session(String tokenStr, SimpleSession session);
}
