# shiro-web-jwtsession


[English](https://github.com/codflow/shiro-web-jwtsession/blob/master/README.md) |  中文

## 简介
[Apache Shiro](http://shiro.apache.org) Session [JWT](https://jwt.io/) 储存支持的增强整合插件


## 添加并设置

在pom.xml中添加依赖

```xml
		<dependency>
			<groupId>ink.codflow.shiro.jwtsession</groupId>
			<artifactId>shiro-web-jwtsession</artifactId>
			<version>0.2.1-alpha1</version>
		</dependency>
```

在shiro.ini 中设置 DefaultWebJWTSessionManager  

```ini
[main]

sessionManager=ink.codflow.shiro.web.jwtsession.session.mgt.DefaultWebJWTSessionManager
securityManager.sessionManager=$sessionManager

```

就这样完成了! 
会话session已经在服务器端实现了无状态化.查看浏览器,就会发先有个名为"JWTTOKEN"的cookie. 

如果你的旧项目是权限验证时基于Shiro的，这**大概是最便捷** 将项目从服务器端session保持（在服务器内存或独立数据库中）迁移到无状态服务器(会话session储存在)的方式.

## 格式

默认所有的Session信息都会储存在JWT的payload claims中。了解更多[JWT 介绍](https://jwt.io/introduction/) 。  
JWT claims 命名:  

|  Claim 名称 | Session信息         | 值类型 |  
|--------|-----------------------|-----------|  
|jti       |Id                      |Int         |  
|ht        |Host                    |String      |    
|st        |Start Timestamp         |Data        |  
|sp        |Stop Timestamp          |Data        |  
|la        |Last Access Time        |Data        |  
|to        |Timeout                 |Long        |  
|ex        |Expired                 |Boolean     | 
|ats       |Attributes              |**基于值类型*|  

#### *基于值类型:  
JSON 常用类型(String,Int,Data,Long,Boolean), 将以json常用的形式储存.
在ats里储存着session的attribute信息。
```js
 {"String" :["stringcontent",9]}
```
* "String" 是attribute的key
* "stringcontext" 是attribute的值  
* 9 是键值对的类型代码

其他类型及自定义类必须实现[java.io.Serializable](https://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html) 接口。为在Http协议中传输，序列化结果都经过Base64转码。

 
### 例子:
#### 包含常规信息的Session(没有Attributes)
 token令牌字符串:
```txt
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdCI6MTU1MTkyMzYyNiwibGEiOjE1NTE5MjM2MjYsInRvIjoxNjAwMCwiaHQiOiJibG9nLmNvZGZvdy5pbmsiLCJqdGkiOiI3OWNlYWQ4Ni00MDdiLTExZTktYjIxMC1kNjYzYmQ4NzNkOTMifQ.iSEbJWhVPUnXlcnALixt8t8CLpYceiNjTLKpRbv5YHE
```
用Base64解码后的PAYLOAD  
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

用Base64解码后的HEADER   
它包含了token处理信息
```js
{
  "typ": "JWT",
  "alg": "HS256"
}
```
#### 有Attributes的Session 

首先我们创建一个实现 [java.io.Serializable](https://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html)的类： "Man".

```java
import java.io.Serializable;
public class Man  implements Serializable {
    private static final long serialVersionUID = 1L;
    int age =31;
    String name ="John";
}
```
然后将信息保存到session的attribute里:

```java
session.setAttribute("obj", new Man());
session.setAttribute("number", 123);
session.setAttribute("string", "codflow");
session.setAttribute("date", new Date());
```
Token令牌字符串:

```txt
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdCI6MTU1MTkyNzUxNCwiYXRzIjp7ImRhdGUiOlsxNTUxOTI3NTE0MzM0LDQ5XSwibnVtYmVyIjpbMTIzLDI1XSwic3RyaW5nIjpbImNvZGZsb3ciLDldLCJvYmoiOlsick8wQUJYTnlBRFJwYm1zdVkyOWtabXh2ZHk1emFHbHlieTUzWldJdWFuZDBjMlZ6YzJsdmJpNXpaWE56YVc5dUxtMW5kQzVsYVhNdVRXRnVBQUFBQUFBQUFBRUNBQUpKQUFOaFoyVk1BQVJ1WVcxbGRBQVNUR3BoZG1FdmJHRnVaeTlUZEhKcGJtYzdlSEFBQUFBZmRBQUVTbTlvYmc9PSIsLTddfSwibGEiOjE1NTE5Mjc1MTQsInRvIjoxODAwMDAwfQ.W7-Y1x9SbQQeuwXqc0EUdFdaN8friv7Z456KZ0JkWZs
```


用Base64解码后的PAYLOAD
"ats" Tree:
* Data 类型转换为unix时间戳
* Object 被序列化成 String
* String 和 数字类型 以Json常规形式储存 
   
  警告 : 不要将秘密信息没有加密地储存在atrributes里，否则它们将直接暴露给前端

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

# 进阶设置

## 设置 JWT 密钥 

(重要!)务必设置你自己的加密密钥.JWT将以HMAC256算法及设置的 **JWT_SecretKey**密钥对数据进行签名和验签
```ini

[main]

sessionDAO=ink.codflow.shiro.web.jwtsession.mgt.eis.JWTSessionDAO
sessionDAO.JWT_SecretKey=abcd1234

```

## 设置过期时间
如果默认session保存在Cookie里，则将同步设置Cookie过期时间
```ini
[main]

sessionManager.globalSessionTimeout = 18000

```

## 设置Session Convertor
提供了两个转换器: 

* SessionJWTConvertor :  
  饿汉模式转换器。在解码反序列化直接还原整个Session.

* SessionJWTSmoothConvertor：
  懒汉模式转换器。在取出时反序列化Session Attribute.

```ini
[main]

#sessionDAO=ink.codflow.shiro.web.jwtsession.mgt.eis.JWTSessionDAO


#sessionJWTConvertor = ink.codflow.shiro.web.jwtsession.serialize.SessionJWTConvertor
sessionJWTConvertor = ink.codflow.shiro.web.jwtsession.serialize.SessionJWTSmoothConvertor

sessionDAO.convertor = $sessionJWTConvertor 

```

## 部分缓存
缓存Session的信息以便在服务器端主动管理Session的过期和失效。
建议只缓存Session的id及时间信息.

```ini
[main]

sessionDAO=ink.codflow.shiro.web.jwtsession.mgt.eis.PartialCacheJWTSessionDAO
sessionManager.sessionDAO=$sessionDAO

cacheManager = org.apache.shiro.cache.MemoryConstrainedCacheManager
securityManager.cacheManager = $cacheManager

#默认为False
#只缓存sessionid和时间戳
#True.
#以上基础上增加缓存session attributes    
#sessionDAO.cacheSessionIdOnly=ture

```
在使用无过期功能的缓存时,建议启用 sessionValidationScheduler。
```ini
[main]
#仅限单实例使用
#集群或分布式系统,请使用quartz 
sessionValidationScheduler=org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler
#
sessionValidationScheduler.interval = 3600000
sessionValidationScheduler.sessionManager=$sessionManager
sessionManager.sessionValidationSchedulerEnabled=true
sessionManager.sessionValidationScheduler=$sessionValidationScheduler

```

### 优化的 Redis Session 缓存

 待定
 

## 在Http Header中传输  

JWT token 默认保存在名称为"JWTTOKEN" 的cookie内.若要仅要用Http Header传输, 可将 sessionJwtTokenCookieEnabled 设置为 false。  
JWT token令牌会在http response中名为"JWTTOKEN"的header内传输。需要实现单点登录的话请将内容保持并在http request中名为"JWTTOKEN"的header中回传。

```ini
[main]

sessionJwtTokenCookieEnabled = false

```  



