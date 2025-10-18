# [WeiSCNET_proxy](https://github.com/Weixiaojun666/SCNET_proxy)

![](https://img.shields.io/github/license/Weixiaojun666/SCNET_proxy.svg)
![](https://img.shields.io/badge/Java-%E2%89%A523-red.svg)
![](https://img.shields.io/github/repo-size/Weixiaojun666/SCNET_proxy.svg)
![](https://img.shields.io/github/downloads/Weixiaojun666/SCNET_proxy/total.svg)

## 简介

这是一个用于 SC2 联机版的反向代理程序。

基于 [port-forward](https://github.com/Weixiaojun666/port-forward) 使用 Netty 技术

于2025年9月开启重构

## 运行环境要求

* Java23+

## 运行

- 设置控制台编码为 UTF8-8。

```bash
chcp 65001
```

- 运行以下命令(根据实际情况调整内存大小)：

```bash
java -server -Xmx8196M -Xms1024M -jar SCNET_proxy-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 备注

* 在 Windows 下彩色日志需要插件支持。使用以下插件[ANS ICON](https://github.com/adoxa/ansicon/) 来获取彩色日志。

