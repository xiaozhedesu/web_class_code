# 服务器租赁信息管理系统

## 简介

这是一个web实训课的练习代码，使用ai（[hermes agent](https://hermes-agent.org/zh/) + deepseek-v4 pro）完成原型开发，使用 [apifox](https://apifox.com/?utm_source=bing&utm_medium=sem&utm_campaign=%E9%AB%98%E8%BD%AC%E5%8C%96%E8%AF%8D-%E4%BA%A7%E5%93%81&utm_content=Apifox&utm_term=apifox&search_term=apifox&msclkid=b4b4649e02fc161018010e8b71085508) 工具进行后端接口测试，同时给我提供学习素材。

## 技术栈

前端：Vue3 + TypeScript + Vite + Element Plus + Vue Router + Axios
后端：java17 + Spring Boot 4.0.6 + Spring Security + JPA + JWT (jjwt)
数据库：PostgreSQL 16

## 运行

首先clone本项目，然后按照以下步骤执行：

### 数据库

先在机器上部署好PostgreSQL服务，然后创建cloud_server数据库，然后运行项目根目录下的`init.sql`初始化三张表和账号数据。

```bash
psql -U <username> -d cloud_server -f init.sql
```

### 后端

使用idea打开项目后，通过maven获取依赖后，在运行配置中添加配置项保证项目正常运行。

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `DB_USERNAME` | 数据库用户名 | `root` |
| `DB_PASSWORD` | 数据库密码 | `postgres` |
| `JWT_SECRET` | JWT 签名密钥 | **必填**，用 `openssl rand -base64 64 \| tr -d '\n'` 生成 |

### 前端

```bash
cd frontend/cloud-server-manager
npm install
npm run dev
```
