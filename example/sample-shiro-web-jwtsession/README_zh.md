# Apache Shiro Web JWT Session Example

[English](https://github.com/codflow/sample-shiro-web-jwtsession/blob/master/README.md) | 中文 

web应用案例，示例通过修改`shiro.ini` 来配置 Shiro Web JWT Session 并提供基础登录功能。


这个例子基于 [shiro web example](https://github.com/apache/shiro/tree/1.3.x/samples/web) 
 
  仅对 `shiro.ini`进行了修改并添加了shiro-web-jwtsession依赖。   


运行例子
---------------

```
mvn jetty:run
```

浏览器浏览 `http://localhost:9080/`

或者

直接浏览示例网站
---------------

[![Deployed](https://img.shields.io/badge/deployed-Web%20JWT%20Session%20Example-blue.svg?style=popout&logo=appveyor)](http://sample-shiro-web-jwtsession.herokuapp.com)


查看其中的 "JWTTOKEN" Cookie
