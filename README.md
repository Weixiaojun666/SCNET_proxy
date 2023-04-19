WeiSCNET_proxy
===============
> 运行环境要求Java20+ [构建基于Java20 其余版本未测试]

## 简介

~~~
用于SC2联机版的反向代理程序
~~~

## 功能
~~~
提供用户,IP,区域封禁功能
~~~

~~~
提供服务器查询信息缓存功能
~~~

~~~
限制单IP和单账号多次登录
~~~

~~~
提供服务器白名单功能
~~~

~~~
提供基本(伪)跨服功能
~~~

## 云服务
>[云服务]由WeiServers提供 构建玩家小数据平台

~~~
统计玩家进服记录
~~~

~~~
统计某区域攻击情况
~~~

~~~
服务器信息收集以及判活
~~~

## 运行

~~~
将example_setting.json重命名为setting.json
~~~

~~~
设置控制台编码为chcp 65001
~~~

~~~
java -server -Xmx8196M -Xms1024M -jar SCNET_proxy.jar
~~~

## 备注
>在windos下彩色日志需要插件支持
[彩色插件](https://github.com/adoxa/ansicon/)

