# shiro-web-jwtsession

## Introduction
A integration for storage of session in [JWT](https://jwt.io/)  support with  [Apache Shiro](http://shiro.apache.org)

## Adding and configuring 

Download the jar and add to lib directory


Add a  DefaultWebJWTSessionManager to shiro.ini

```ini
[main]

sessionManager=ink.codflow.shiro.web.jwtsession.session.mgt.DefaultWebJWTSessionManager
securityManager.sessionManager=$sessionManager

```

Well,Done!  
Server session is ready to act in a stateless way.Check your browser and you will find a Cookie whose names "JWTTOKEN".

## Format

Whole Session's information will be stored in JWT toke payload claims. Learn more with [JWT introduction](https://jwt.io/introduction/).  
JWT claims namespace:
|  Claim name | Session info         | Value Type |
|:--------:|:-----------------------|:-----------|
|jti       |Id                      |Int         |
|ht        |Host                    |String      |
|st        |Start Timestamp         |Data        |
|sp        |Stop Timestamp          |Data        |
|la        |Last Access Time        |Data        |
|to        |Timeout                 |Long        |
|ex        |Expired                 |Boolean     |
|ats       |Attributes              |+Depend on value type|  

#### +Depend on value type:  
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
   
WARNNING: Don't set securty content to atrributes without encryption or it be exposed to font-end.

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

## Optionally transfer with Http Header  

JWT token is store in Cookie named "JWTTOKEN" by default.For transfering in header with out cookie, set the sessionJwtTokenCookieEnabled to false.  
JWT token should be send back in header of http request. In addition, features like SSO need token stored data at font-end.
```ini
[main]

sessionJwtTokenCookieEnabled = false
```  