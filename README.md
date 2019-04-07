# shiro-web-jwtsession

English |  [中文](https://github.com/codflow/shiro-web-jwtsession/blob/master/README_zh.md)
## Introduction
A integration for storage of session in [JWT](https://jwt.io/)  support with  [Apache Shiro](http://shiro.apache.org)


## Adding and configuring 

Add dependency to pom.xml

```xml
		<dependency>
			<groupId>ink.codflow.shiro.jwtsession</groupId>
			<artifactId>shiro-web-jwtsession</artifactId>
			<version>0.2.1-alpha1</version>
		</dependency>
```

Add a  DefaultWebJWTSessionManager to shiro.ini

```ini
[main]

sessionManager=ink.codflow.shiro.web.jwtsession.session.mgt.DefaultWebJWTSessionManager
securityManager.sessionManager=$sessionManager

```

Well,Done! 
Server session is ready to act in a stateless way.Check your browser and you will find a Cookie whose names "JWTTOKEN". 

If your old project is based on Shiro, it is **the almost convenient** way to convert  state server (sessions are stored at server side,server memory or distributed database) to a stateless server (sessions are stored at client side,signed token string in cookie or another type of storage).

## Format

Whole Session's information will be stored in JWT toke payload claims. Learn more with [JWT introduction](https://jwt.io/introduction/).  
JWT claims namespace:  

|  Claim name | Session info         | Value Type |  
|--------|-----------------------|-----------|  
|jti       |Id                      |Int         |  
|ht        |Host                    |String      |    
|st        |Start Timestamp         |Data        |  
|sp        |Stop Timestamp          |Data        |  
|la        |Last Access Time        |Data        |  
|to        |Timeout                 |Long        |  
|ex        |Expired                 |Boolean     | 
|ats       |Attributes              |**Depend on value type*|  

#### *Depend on value type:  
As normal types include in JSON (String,Int,Data,Long,Boolean), they will be encode in "json" way as following.  

```js
 {"String" :["stringcontent",9]}
```
* "String" is the attribute key
* "stringcontext" is the attribute value
* 9 is the inner type mask

Other types and User definded types will be serialized in a proper way and must implement [java.io.Serializable](https://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html) interface.To be compatible ,they be encoded by base64 before JWT encoding.

 
### For Example:
#### Session with common info   
Token string RAW:
```txt
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdCI6MTU1MTkyMzYyNiwibGEiOjE1NTE5MjM2MjYsInRvIjoxNjAwMCwiaHQiOiJibG9nLmNvZGZvdy5pbmsiLCJqdGkiOiI3OWNlYWQ4Ni00MDdiLTExZTktYjIxMC1kNjYzYmQ4NzNkOTMifQ.iSEbJWhVPUnXlcnALixt8t8CLpYceiNjTLKpRbv5YHE
```
PAYLOAD decoded by Base64 
```js
{
  "jti": "79cead86-407b-11e9-b210-d663bd873d93",
  "ht": "blog.codfow.ink",
  "st": 1551922877,
  "sp": 1551938877,
  "la": 1551922898,
  "to": 16000,
  "ex": true
}

```

HEADER decoded by Base64   
It contains jwt encoding info.
```js
{
  "typ": "JWT",
  "alg": "HS256"
}
```
#### Session with attributes

First we define a class implement [java.io.Serializable](https://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html) called "Man".

```java
import java.io.Serializable;
public class Man  implements Serializable {
    private static final long serialVersionUID = 1L;
    int age =31;
    String name ="John";
}
```
Then set attributes to session:

```java
session.setAttribute("obj", new Man());
session.setAttribute("number", 123);
session.setAttribute("string", "codflow");
session.setAttribute("date", new Date());
```
Token string RAW:

```txt
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdCI6MTU1MTkyNzUxNCwiYXRzIjp7ImRhdGUiOlsxNTUxOTI3NTE0MzM0LDQ5XSwibnVtYmVyIjpbMTIzLDI1XSwic3RyaW5nIjpbImNvZGZsb3ciLDldLCJvYmoiOlsick8wQUJYTnlBRFJwYm1zdVkyOWtabXh2ZHk1emFHbHlieTUzWldJdWFuZDBjMlZ6YzJsdmJpNXpaWE56YVc5dUxtMW5kQzVsYVhNdVRXRnVBQUFBQUFBQUFBRUNBQUpKQUFOaFoyVk1BQVJ1WVcxbGRBQVNUR3BoZG1FdmJHRnVaeTlUZEhKcGJtYzdlSEFBQUFBZmRBQUVTbTlvYmc9PSIsLTddfSwibGEiOjE1NTE5Mjc1MTQsInRvIjoxODAwMDAwfQ.W7-Y1x9SbQQeuwXqc0EUdFdaN8friv7Z456KZ0JkWZs
```


PAYLOAD decoded by Base64 
"ats" Tree:
* Data encoded to milliseconds since the "the epoch"
* Object is serialized as String
* String and Number is in the "json" way  
   
WARNNING: Don't set confidential content to atrributes without encryption or it be exposed to font-end.

```json
{
  "st": 1551927514,
  "ats": {
    "date": [
      1551927514334,
      49
    ],
    "number": [
      123,
      25
    ],
    "string": [
      "codflow",
      9
    ],
    "obj": [
      "rO0ABXNyADRpbmsuY29kZmxvdy5zaGlyby53ZWIuand0c2Vzc2lvbi5zZXNzaW9uLm1ndC5laXMuTWFuAAAAAAAAAAECAAJJAANhZ2VMAARuYW1ldAASTGphdmEvbGFuZy9TdHJpbmc7eHAAAAAfdAAESm9obg==",
      -7
    ]
  },
  "la": 1551927514,
  "to": 1800000
}
```

# Advanced Options

## Set JWT secret 

(Important!)Change the JWT secret to your own.Token is signed by HMAC256 with the **JWT_SecretKey**
```ini

[main]

sessionDAO=ink.codflow.shiro.web.jwtsession.mgt.eis.JWTSessionDAO
sessionDAO.JWT_SecretKey=abcd1234

```

## Set Session timeout
If session is stored cookie as default, the option alse change the cookie maxage
```ini
[main]

sessionManager.globalSessionTimeout = 18000

```

## Session Convertor
Two classes of convertors are provided: 

* SessionJWTConvertor :  
  Eager mode.Decode and deserialize whole session token string immediately.

* SessionJWTSmoothConvertor：
  Lazy mode.Decode and deserialize session attribute values in need.

```ini
[main]

#sessionDAO=ink.codflow.shiro.web.jwtsession.mgt.eis.JWTSessionDAO


#sessionJWTConvertor = ink.codflow.shiro.web.jwtsession.serialize.SessionJWTConvertor
sessionJWTConvertor = ink.codflow.shiro.web.jwtsession.serialize.SessionJWTSmoothConvertor

sessionDAO.convertor = $sessionJWTConvertor 

```

## Partial Cache 
Cache the info about session to management the session expiration at server side.
It is recommened that Server cache the session id only.

```ini
[main]

sessionDAO=ink.codflow.shiro.web.jwtsession.mgt.eis.PartialCacheJWTSessionDAO
sessionManager.sessionDAO=$sessionDAO

cacheManager = org.apache.shiro.cache.MemoryConstrainedCacheManager
securityManager.cacheManager = $cacheManager

#False as default.
#Cache sessionID and timestamp only.
#True.
#Cache the session attributes in addition.    
#sessionDAO.cacheSessionIdOnly=ture

```
When use a cache without expiration function,it is recommended to set sessionValidationScheduler enabled.
```ini
[main]
#For single instance server only
#For distribute system ,use quartz instead 
sessionValidationScheduler=org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler
#
sessionValidationScheduler.interval = 3600000
sessionValidationScheduler.sessionManager=$sessionManager
sessionManager.sessionValidationSchedulerEnabled=true
sessionManager.sessionValidationScheduler=$sessionValidationScheduler

```

### Optimized Redis Session Cache

TBD

## Be transfered with Http Header  

JWT token is store in Cookie named "JWTTOKEN" by default.For transfering in header with out cookie, set the sessionJwtTokenCookieEnabled to false.  
JWT token should be sent in header with the name "JWTTOKEN" in http response. In addition, it must be stored data at font-end and sent back in header with the name "JWTTOKEN" of http request.
```ini
[main]

sessionJwtTokenCookieEnabled = false

```  



