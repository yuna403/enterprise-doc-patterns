# 企业文档系统 - 设计模式教学演示平台

> 基于 Spring Boot 的 Web 应用，综合展示 7 种设计模式在企业级文档系统中的实际应用。

## 项目概述

本项目以"企业订单文档处理"为业务场景，演示了如何在真实项目中运用常见设计模式。系统涵盖从订单创建、审批、文档生成、文件保存到支付的完整流程。

## 涉及的设计模式

| 模式 | 类型 | 应用场景 | 核心类 |
|------|------|----------|--------|
| **组合模式** (Composite) | 结构型 | 订单组合、文档树、文件系统 | `OrderComponent`, `CompositeOrder`, `SimpleOrder` |
| **责任链模式** (Chain of Responsibility) | 行为型 | 订单多级审批 | `Approver`, `ManagerApprover`, `DirectorApprover` |
| **装饰器模式** (Decorator) | 结构型 | 文档格式化输出 | `FormatDecorator`, `BoldDecorator` |
| **策略模式** (Strategy) | 行为型 | 多种保存格式、多种支付方式 | `SaveStrategy`, `PaymentStrategy` |
| **迭代器模式** (Iterator) | 行为型 | 文件系统深度优先遍历 | `FileSystemIterator` |
| **访问者模式** (Visitor) | 行为型 | 文件节点统计访问 | `Visitor`, `StatsVisitor` |
| **单例模式** (Singleton) | 创建型 | 数据库连接管理 | `DBConnection` |

## 项目结构

```
src/main/java/com/eds/
├── common/           # 公共接口
│   ├── FileSystemNode.java    # 文件系统节点接口
│   ├── Printable.java         # 打印接口
│   ├── Visitable.java         # 可访问接口
│   └── Visitor.java           # 访问者接口
├── db/               # 数据库
│   └── DBConnection.java      # 数据库连接（单例模式）
├── document/         # 文档模块
│   ├── DocumentComponent.java # 文档组件接口
│   ├── TextElement.java       # 文本元素（叶子节点）
│   ├── CompositeElement.java  # 复合元素（容器节点）
│   ├── FormatDecorator.java   # 格式装饰器抽象类
│   ├── BoldDecorator.java     # 加粗装饰器
│   ├── SaveStrategy.java      # 保存策略接口
│   └── HtmlSaveStrategy.java  # HTML保存策略
├── filesystem/       # 文件系统模块
│   ├── Directory.java         # 目录（容器节点）
│   ├── File.java              # 文件（叶子节点）
│   ├── FileSystemIterator.java# 文件系统迭代器
│   └── StatsVisitor.java      # 统计访问者
├── order/            # 订单模块
│   ├── OrderComponent.java    # 订单组件接口
│   ├── SimpleOrder.java       # 简单订单
│   ├── CompositeOrder.java    # 组合订单
│   ├── Approver.java          # 审批者抽象类
│   ├── ManagerApprover.java   # 经理审批者
│   ├── DirectorApprover.java  # 总监审批者
│   ├── PaymentStrategy.java   # 支付策略接口
│   ├── CreditCardStrategy.java# 信用卡支付
│   └── AlipayStrategy.java    # 支付宝支付
└── web/              # Web 层
    ├── WebApp.java            # Spring Boot 启动类
    └── DemoController.java    # REST API 控制器
```

## 技术栈

- **Java 8** + **Spring Boot 2.7.18**
- **Maven** 构建管理
- 前端纯 HTML/CSS/JS（无框架依赖）
- MySQL 数据库（可选）

## 快速运行

### 环境要求
- JDK 8+
- Maven 3.6+

### 启动项目

```bash
# 克隆项目
git clone https://github.com/yuna403/enterprise-doc-patterns.git

# 进入项目目录
cd enterprise-doc-patterns

# 编译运行
mvn spring-boot:run
```

启动后访问 http://localhost:8080 即可使用 Web 演示界面。

### API 接口

| 接口 | 方法 | 说明 | 对应模式 |
|------|------|------|----------|
| `/api/order/create` | POST | 创建订单 | 组合模式 |
| `/api/order/approve` | POST | 审批订单 | 责任链模式 |
| `/api/order/document` | POST | 生成文档 | 组合模式 + 装饰器模式 |
| `/api/order/save` | POST | 保存文档 | 策略模式 |
| `/api/order/traverse` | POST | 遍历文件系统 | 迭代器模式 + 访问者模式 |
| `/api/order/pay` | POST | 支付订单 | 策略模式 |
| `/api/order/full` | POST | 一键执行全部流程 | 所有模式 |

## 业务流程

```
创建订单 → 审批订单 → 生成文档 → 保存文件 → 遍历文件 → 支付订单
(组合模式)  (责任链)   (装饰器)   (策略模式)  (迭代器+访问者) (策略模式)
```

## 数据库（可选）

如需使用数据库功能，导入 `db.sql` 到 MySQL：

```sql
mysql -u root -p < db.sql
```

## 许可证

本项目仅用于教学演示目的。
