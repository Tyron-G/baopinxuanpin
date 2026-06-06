# 爆品选品雷达

基于「爆品选品」PRD 与产品背景实现的全栈应用，覆盖 **数据准备 → 信号雷达 → 洞察发现 → 爆品机会 → 竞品监控** 完整产品路径，并支持品牌约束驱动的个性化推荐。

## 技术栈

| 层 | 选型 |
|---|------|
| 前端 | Vue 3 + Vite + TypeScript + Element Plus + ECharts 5 |
| 后端 | Spring Boot 3 + MyBatis-Plus |
| 数据库 | H2 内嵌数据库（零配置，自动初始化内置样例数据） |

## 快速启动

> 详细录屏分镜与截图清单见 [演示交付说明](docs/演示交付说明_20260604.md)

**环境**：JDK 17+、Maven 3.8+、Node.js 18+

### 推荐：一键启动本地开发环境

```powershell
pwsh ./scripts/start-dev.ps1
```

脚本会先启动后端并等待 `/actuator/health` 返回 `UP`，再启动前端，避免前端首屏请求时后端尚未就绪。<!-- 2026-06-05 -->

### 1. 启动后端（8088）

```powershell
cd backend
mvn spring-boot:run
```

| 地址 | 说明 |
|------|------|
| http://localhost:8088 | API 服务 |
| http://localhost:8088/swagger-ui.html | **Swagger 接口文档**（推荐验收时打开） |
| http://localhost:8088/api-docs | OpenAPI JSON |
| http://localhost:8088/h2-console | H2 控制台（可选） |
| http://localhost:8088/actuator/health | 健康检查 |

H2 连接：`jdbc:h2:mem:selection`，用户 `sa`，密码为空。

### 2. 启动前端（5173）

```powershell
cd frontend
npm install
npm run dev
```

浏览器打开 **http://localhost:5173**（`/api` 已代理到 8088）。

### 3. 推荐体验路径（录屏用）

1. **数据准备** — 填写品牌约束，点击「生成洞察」
2. **信号雷达** — 查看今日强信号，可一键进入机会页
3. **洞察发现** — 三维度图表 + 结论 + 洞察卡片
4. **爆品机会** — 深度报告 + 导出 Markdown
5. **竞品监控** — 添加对标店铺

## 功能模块

| 模块 | 路径 | 能力 |
|------|------|------|
| 数据准备 | `/data-prep` | 品牌初始化三步表单，生成品牌上下文 |
| 信号雷达 | `/radar` | 搜索飙升 / 社媒异常 / 差评痛点 / 机会评分 |
| 洞察发现 | `/insight` | 趋势 / 竞争 / 供需图表 + TOP3 结论 + 洞察卡片 |
| 爆品机会 | `/opportunity/:cardId` | 散点图、舆情象限、人群场景、利润/供应链报告 |
| 竞品监控 | `/competitor` | 手动添加竞品店铺并追踪信号 |

## 品牌约束引擎

提交品牌后，系统会按以下规则个性化结果：

- **排除品类** — 过滤对应赛道与信号
- **预算带** — 超预算卡片降权并提示风险
- **目标品类** — 匹配卡片置顶并高亮

## 内置类目样例

| 品类 | 竞争格局 | 决策结论 |
|------|---------|---------|
| 宠物智能用品 | 分散，需求红利期 | 推荐立项 |
| 便携式咖啡器具 | 腰部竞争 | 建议观望 |
| 家用清洁机器人 | 头部集中 | 建议放弃 |

## 测试

```bash
cd backend && mvn test    # 含 BrandContextEngine 单测 + API 流程集成测试
cd frontend && npm run build
```

前端 E2E 如需直跑，请保持后端 `8088` 已启动，再执行 `cd frontend && npm run test:e2e:ci`。该套件当前通过 Vite `dev server` 提供 `/api` 代理，不能直接依赖 `vite preview`。<!-- 2026-06-04 -->

如需单独确认 E2E 运行环境，可先执行 `cd frontend && npm run test:e2e:preflight`。它会检查本地 `8088` 接口可达，以及 `4173` 所需的 CORS 预检是否通过。<!-- 2026-06-04 -->

如需切换 E2E 前端端口，可通过环境变量覆盖：例如 `E2E_PORT=4273 npm run test:e2e:ci`。当前 Playwright、预检脚本与后端 CORS 已按本机开发端口联动，不再只依赖固定的 `4173/5173`。<!-- 2026-06-04 -->

## 文档

- [演示交付说明](docs/演示交付说明_20260604.md)（**启动 / 录屏脚本 / 截图清单**）
- [贴近生产化改造说明](docs/贴近生产化改造说明_20260604.md)（市场数据仓储、时间轴落库、ApiResult、品牌校验）
- [术语与口径说明](docs/术语与口径说明_20260604.md)（沟通与文档用词 SSOT）
- [PRD 与实现对齐说明](docs/PRD与实现对齐说明_20260604.md)
- [开发交付说明](docs/开发交付说明_20260603.md)
- [后端架构优化说明](docs/后端架构优化说明_20260604.md)
- [会话交接说明](docs/会话交接说明_20260603.md)
- [任务计划](.planning/a1b2c3d4/task_plan.md)

## 项目结构

```
baopinxuanpin/
├── frontend/src/
│   ├── views/          # 五页主流程
│   ├── components/     # 图表、结论、报告、路径条
│   ├── composables/    # brandId 上下文
│   └── api/
├── backend/src/main/java/com/oneaix/selection/
│   ├── content/        # 类目剧本、信号模板、洞察叙事
│   ├── service/        # Insight / BrandContext / Radar / Report ...
│   └── controller/
├── 01 产品背景/       # Why / Who / 路径
├── 02 产品PRD/        # 三模块需求原文 + README_20260604
└── docs/
```
