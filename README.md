# ResumeCraft

ResumeCraft 是一个面向中文简历场景的桌面简历编辑器，采用前后端一体化打包架构，双击即用，无需安装数据库或配置环境。

## 项目特性

- **即点即用**：双击 `ResumeCraft.exe` 即可启动，自动打开浏览器，关闭后自动退出
- **零配置**：内置 H2 嵌入式数据库，无需安装 MySQL / Redis
- **模块化编辑**：基础信息、教育背景、实习经历、项目经历、专业技能、论文发表、科研经历、获奖情况、求职意向
- **实时预览**：编辑区与预览区联动，所见即所得
- **AI 优化**：支持单模块 AI 优化、逐字段 AI 润色和整份简历 AI 分析评分，可通过 UI 动态切换 AI 模型
- **简历导入**：支持 Markdown / TXT 格式拖拽导入
- **PDF 导出**：支持导出排版精美的 PDF 简历
- **跨平台**：支持 Windows 和 macOS (Apple Silicon)

## 技术栈

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| React | 18.3 | UI 框架 |
| TypeScript | 5.6 | 类型安全 |
| Vite | 6.0 | 构建工具与开发服务器 |
| Zustand | 5.0 | 状态管理 |
| Tailwind CSS | 3.4 | 样式方案 |
| React Router | 7.13 | 路由管理 |
| Axios | 1.14 | HTTP 客户端（统一拦截、Token 刷新） |
| @react-pdf/renderer | 4.1 | PDF 导出 |
| pdfjs-dist | 4.8 | PDF 渲染引擎 |
| Framer Motion | 12.x | 动画 |
| react-markdown | 10.1 | Markdown 渲染 |

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 运行时 |
| Spring Boot | 3.3.5 | 应用框架 |
| Spring Security | - | 认证授权（JWT） |
| MyBatis-Plus | 3.5.9 | ORM 与数据库访问 |
| MySQL | 8.x | 开发环境数据库 |
| H2 | - | 桌面模式嵌入式数据库 |
| Redis | 6.x+ | 开发环境缓存 |
| JJWT | 0.12.6 | JWT 令牌签发与验证 |
| Knife4j | 4.5 | API 文档（开发模式） |
| WebFlux | - | AI 流式响应（SSE） |
| Lombok | - | 代码简化 |
| jpackage (JDK 17+) | - | 桌面应用打包 |

## 快速开始

### 下载打包好的应用（推荐）

前往 [GitHub Releases](https://github.com/Zylo206/ResumeCraft/releases) 下载对应平台的压缩包：

- **Windows**：下载 `ResumeCraft-win-x64.zip`，解压后双击 `ResumeCraft\ResumeCraft.exe`
- **macOS**：敬请期待

应用启动后会自动打开浏览器，关闭浏览器标签页 30 秒后应用自动退出。

> 首次运行可能触发 Windows SmartScreen 警告，点击「仍要运行」即可。

### 从源码构建桌面应用

#### 环境要求

- JDK 17+（需包含 jpackage）
- Node.js 18+
- Maven 3.9+

#### Windows 构建

```powershell
git clone https://github.com/Zylo206/ResumeCraft.git
cd ResumeCraft

npm install
cd server && mvn dependency:resolve && cd ..

.\scripts\package-win.ps1
```

输出：`target\win-app\ResumeCraft\ResumeCraft.exe`

#### macOS 构建

```bash
git clone https://github.com/Zylo206/ResumeCraft.git
cd ResumeCraft

npm install
cd server && mvn dependency:resolve && cd ..

chmod +x scripts/package-mac.sh
./scripts/package-mac.sh
```

输出：`target/mac-app/ResumeCraft.app`

> macOS 版本必须在 Apple Silicon Mac 上构建。

## 本地开发

> 以下内容仅适用于需要修改源码的开发者。普通用户请直接下载 Release 中的打包应用。

### 环境要求

- Node.js 18+
- Java 17
- Maven 3.9+
- MySQL 8.x
- Redis 6.x+

### 1. 配置环境变量

在项目根目录创建 `.env` 文件，配置以下关键变量：

```env
# 后端
SERVER_PORT=8084
APP_ENV=development

# 数据库
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=resumecraft
MYSQL_USERNAME=root
MYSQL_PASSWORD=your_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT（开发环境有默认值，生产环境必须修改）
JWT_SECRET=your-secret-key

# AI 服务（可选，也可启动后通过 UI 配置）
AI_API_KEY=your_api_key
AI_BASE_URL=https://api.openai.com/v1
AI_MODEL=gpt-4
AI_ANALYSIS_MODEL=gpt-4
```

### 2. 启动后端

```bash
cd server
mvn spring-boot:run
```

后端启动后：

- API 地址：http://localhost:8084/api
- 健康检查：http://localhost:8084/api/health
- 接口文档：http://localhost:8084/api/doc.html

### 3. 启动前端

```bash
npm install
npm run dev
```

前端地址：http://localhost:5173

前端会通过 Vite 代理将 `/api` 请求转发到后端。

### 开发说明

- 后端启动时自动执行 `schema.sql` 建表，无需手动迁移
- `APP_ENV=development` 时自动创建测试账号：`test@example.com` / `Test123456`
- 验证码不会真正发送，直接查看后端日志获取
- 前端和后端统一从根目录 `.env` 读取配置

## 项目结构

```text
ResumeCraft/
├── src/                            # 前端源码
│   ├── api/                        # Axios 封装与接口定义
│   ├── components/
│   │   ├── modules/                # 各模块表单组件（基础信息、教育、实习、项目等）
│   │   ├── editor/                 # 编辑器组件（侧边栏、预览面板）
│   │   ├── preview/                # 简历预览组件
│   │   ├── analysis/               # AI 分析组件
│   │   ├── settings/               # 设置弹窗（AI 配置）
│   │   ├── layout/                 # 布局组件（Header 等）
│   │   ├── branding/               # 品牌标识（Logo）
│   │   ├── dashboard/              # 工作台组件
│   │   └── ui/                     # 通用 UI 组件
│   ├── pages/                      # 页面组件
│   │   ├── DashboardPage.tsx       # 工作台
│   │   ├── EditorPage.tsx          # 编辑页
│   │   ├── ChromePreviewPage.tsx   # 预览页
│   │   └── FieldOptimizePage.tsx   # 字段优化页
│   ├── store/                      # Zustand 状态管理
│   ├── utils/                      # 工具函数（PDF 导出、导入、AI 服务等）
│   └── types/                      # TypeScript 类型定义
├── server/                         # Spring Boot 后端
│   └── src/main/java/com/zylo/resumecraft/
│       ├── controller/             # REST 控制器
│       ├── service/                # 业务逻辑
│       ├── config/                 # 配置类（安全、桌面生命周期等）
│       ├── security/               # JWT 认证
│       └── entity/                 # 数据实体
├── config/                         # 字段优化提示词配置
├── scripts/                        # 打包脚本
│   ├── package-win.ps1             # Windows 打包
│   └── package-mac.sh              # macOS 打包
├── public/fonts/                   # PDF 导出字体
└── vite.config.ts                  # Vite 配置
```

## 架构说明

### 运行模式

项目支持两种运行模式：

| 模式 | 数据库 | 缓存 | context-path | 前端 API 前缀 |
|------|--------|------|--------------|---------------|
| 开发模式 | MySQL | Redis | `/api` | `/api` |
| 桌面模式 | H2 嵌入式 | 内存替代 | `/` | 无（同源） |

### 桌面模式生命周期

```
双击 exe/app
    ↓
LoadingWindow 显示启动画面
    ↓
Spring Boot 启动（H2 数据库、内存 Redis）
    ↓
LocalBrowserOpener 关闭启动画面 → 打开浏览器
    ↓
前端自动等待后端就绪 → 自动登录测试账号
    ↓
用户使用应用
    ↓
关闭浏览器标签页 → 30 秒无心跳 → 自动退出
```

### 前端请求流程

```
浏览器 → Vite 开发服务器/静态文件
    ↓
Axios 客户端（自动携带 Token、自动刷新）
    ↓
/api/* → Spring Boot 后端
    ↓
MySQL/H2 + Redis/内存 + AI 服务
```

## 主要接口

### 认证

- `POST /auth/login` - 登录
- `POST /auth/register` - 注册
- `POST /auth/refresh` - 刷新 Token
- `POST /auth/logout` - 退出登录
- `POST /auth/send-code` - 发送验证码

### 简历

- `GET /resumes` - 简历列表
- `POST /resumes` - 创建简历
- `PUT /resumes/{id}` - 更新简历
- `DELETE /resumes/{id}` - 删除简历
- `GET /resumes/{id}/modules` - 获取模块列表
- `POST /resumes/{id}/modules` - 添加模块
- `POST /resumes/{id}/modules/{mid}/update` - 更新模块
- `DELETE /resumes/{id}/modules/{mid}` - 删除模块

### AI

- `POST /resumes/{id}/modules/{mid}/ai-optimize` - 模块 AI 优化
- `POST /resumes/{id}/modules/{mid}/ai-optimize-field` - 字段 AI 优化
- `POST /resumes/{id}/analysis` - 整份简历分析
- `POST /resumes/{id}/smart-onepage/preview` - 智能一页预览

### AI 配置（桌面模式）

- `GET /ai-config` - 获取当前 AI 配置（API Key 脱敏）
- `PUT /ai-config` - 更新 AI 配置（apiKey、baseUrl、model、analysisModel）

### 桌面模式

- `GET /desktop/heartbeat` - 心跳检测
- `POST /desktop/shutdown` - 主动退出
- `GET /health` - 健康检查

> 接口路径在开发模式下需加 `/api` 前缀，桌面模式下无需前缀。

## 环境变量参考

### 桌面模式（打包应用）

桌面模式下无需配置环境变量，应用内置默认配置。AI 服务可通过界面右上角齿轮图标配置。

### 开发模式

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `SERVER_PORT` | 后端端口 | `8084` |
| `APP_ENV` | 运行环境 | `development` |
| `MYSQL_HOST` | MySQL 地址 | `localhost` |
| `MYSQL_PORT` | MySQL 端口 | `3306` |
| `MYSQL_DATABASE` | 数据库名 | `resumecraft` |
| `MYSQL_USERNAME` | 数据库用户名 | `root` |
| `MYSQL_PASSWORD` | 数据库密码 | - |
| `REDIS_HOST` | Redis 地址 | `localhost` |
| `REDIS_PORT` | Redis 端口 | `6379` |
| `JWT_SECRET` | JWT 密钥 | 开发环境有默认值 |
| `AI_API_KEY` | AI 服务密钥 | - |
| `AI_BASE_URL` | AI 服务地址 | - |
| `AI_MODEL` | AI 模型名称 | `deepseek-chat` |
| `AI_ANALYSIS_MODEL` | 简历分析模型 | `deepseek-chat` |

## 常见问题

**Q: 双击 exe 后浏览器没有自动打开？**
A: 检查 `RESUMECRAFT_OPEN_BROWSER` 环境变量是否为 `true`（默认就是）。

**Q: 应用启动后很快就自动退出了？**
A: 心跳超时机制导致。确保浏览器标签页保持打开，或增大 `RESUMECRAFT_HEARTBEAT_TIMEOUT` 值。

**Q: PDF 导出失败？**
A: 确认 `public/fonts/` 目录下存在字体文件。

**Q: AI 功能不工作？**
A: 桌面模式下点击右上角齿轮图标检查 AI 配置；开发模式下检查 `.env` 中的 `AI_API_KEY`、`AI_BASE_URL`、`AI_MODEL` 配置。

## 许可证

MIT
