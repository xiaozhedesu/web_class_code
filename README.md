# 服务器租赁信息管理系统

## 简介

这是一个web实训课的练习代码，使用ai（[hermes agent](https://hermes-agent.org/zh/) + deepseek-v4
pro）完成原型开发，使用 [apifox](https://apifox.com/?utm_source=bing&utm_medium=sem&utm_campaign=%E9%AB%98%E8%BD%AC%E5%8C%96%E8%AF%8D-%E4%BA%A7%E5%93%81&utm_content=Apifox&utm_term=apifox&search_term=apifox&msclkid=b4b4649e02fc161018010e8b71085508)
工具进行后端接口测试，同时给我提供学习素材。

## 技术栈

前端：Vue3 + TypeScript + Vite + Element Plus + Vue Router + Axios
后端：java17 + Spring Boot 4.0.6 + Spring Security + JPA + JWT (jjwt)
数据库：PostgreSQL 16
接口测试: Apifox
服务部署: docker compose

## 开发环境运行

> 快速体验项目可以直接跳到[生产环境部署](#生产环境部署)

首先clone本项目：

```bash
git clone https://github.com/xiaozhedesu/web_class_code.git
```

然后按照以下顺序在自己的机器上部署服务：

### 数据库

先在机器上部署好PostgreSQL服务，然后创建cloud_server数据库，然后运行项目根目录下的`init.sql`初始化三张表和账号数据。

```bash
psql -U <username> -d cloud_server -f init.sql
```

### 后端

使用idea打开项目后，通过maven获取依赖后运行`CloudServerManagerApplication`。

可以通过环境变量配置数据库的各种信息，请保证信息与PostgreSQL配置一致。

| 变量          | 说明        | 默认值         |
|---------------|-------------|----------------|
| `DB_HOST`     | 数据库位置  | `localhost`    |
| `DB_PORT`     | 数据库端口  | `5432`         |
| `DB_NAME`     | 数据库名字  | `cloud_server` |
| `DB_USERNAME` | 数据库用户名| `root`         |
| `DB_PASSWORD` | 数据库密码  | `postgres`     |

### 前端

```bash
cd frontend/cloud-server-manager
npm install
npm run dev
```

## 生产环境部署

本项目已经配置了docker-compose.yaml，只要有支持的容器服务就可以通过指令一键部署。本项目使用的测试环境是Docker Desktop，下列引导会使用docker进行。

### 确认环境

首先需要确认机器上有docker compose，执行下面的命令：

```bash
docker compose version
# 或
docker-compose version
```

出现以下类似输出即说明服务可用：

```text
Docker Compose version v2.39.2-desktop.1
```

### 部署服务

可以在cmd或linux bash中使用以下指令：

```bash
git clone https://github.com/xiaozhedesu/web_class_code.git && cd ./web_class_code && docker compose up -d --build
```

步骤：

- 将源代码从github上clong到本地的工作目录
- 进入项目根目录
- 执行docker compose的构建命令

### 访问系统

部署完成后浏览器访问：<http://localhost>

测试账号：

| 用户名 |  密码  |   角色   |
|--------|--------|----------|
| admin  | 123456 |  管理员  |
| user1  | 123456 | 普通用户 |

### 停止服务

```bash
docker compose stop
```

### 启动服务

```bash
docker compose up -d
```

### 删除服务

```bash
docker compose down
```
