# [WeiSCNET_proxy](https://github.com/Weixiaojun666/SCNET_proxy)

![](https://img.shields.io/github/license/Weixiaojun666/SCNET_proxy.svg)
![](https://img.shields.io/badge/Java-%E2%89%A520-red.svg)
![](https://img.shields.io/github/repo-size/Weixiaojun666/SCNET_proxy.svg)
![](https://img.shields.io/github/downloads/Weixiaojun666/SCNET_proxy/total.svg)

## 简介

这是一个用于 SC2 联机版的反向代理程序。

## 运行环境要求

* Java20+

## 功能

* 提供基本的反向代理功能
* 提供用户、IP 和区域封禁功能
* 提供服务器查询信息缓存功能
* 限制单 IP 和单账号多次登录
* 提供基本（伪）跨服功能

## 本地防护

* 限制海外用户登录
* 限制非白名单用户登录

## 云服务

* 统计玩家进服记录
* 服务器信息收集以及判活
* 限制未绑定玩家登录

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

## 配置文件

```yaml
# 主配置
# 以下配置仅供参考使用,请勿直接复制粘贴
# 运行一遍程序后配置文件会自动生成
# 配置文件为标准JSON，不支持注释
# 请严格区分大小写
# 请使用 UTF-8 编码保存
{
  "basicConfig": {
    # 最大链接数
    "maxConnections": 100,
    #玩家超时时间
    "playerTimeout": 120,
    # 缓存时间
    "cacheTime": 300,
    # Http超时时间
    "httpTimeout": 30,
    # Http超时重试次数
    "httpRetryCount": 3,
    # 区域封禁等级
    "regionBlockLevel": 2
  },
  "localProtection": {
    # 允许海外用户登录
    "denyOverseasLogin": true,
    # 只允许白名单用户登录
    "whitelistOnlyLogin": true
  },
  "cloudProtection": {
    # Your API Key
    "appKey": "your-app-key",
    # Your API Secret
    "appSecret": "your-app-secret"
  },
  "aggregation": {
    # 聚合服务器地址
    "port": 25565,
    # 默认进入的服务器ID
    "defaultServer": 100
  },

  "serverList": [
    {
      # 服务器ID [请保证唯一]
      "id": 100,
      # 服务器名称
      "name": "测试服",
      # 服务器地址
      "address": 127.0.0.1,
      # 服务器端口
      "port": 25565,
      # 转发后端口
      "proxyPort": 25566,
    }
  ]
}
```

## 其他

* 有bug欢迎反馈，同时招收工具人 且欢迎社区吸收此项目
