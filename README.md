# Task System - Spring Boot 任務系統

這是一個基於 Spring Boot 3.2+ 的任務管理系統，用於學習和演示 Spring Boot 系統設計最佳實踐。

## 專案特點

- ✅ **標準分層架構** - Controller → Service → Repository
- ✅ **REST API** - 完整的 RESTful API 設計
- ✅ **資料持久化** - 使用 JPA 和 Hibernate
- ✅ **資料庫支援** - H2 (開發) 和 MySQL (生產)
- ✅ **輸入驗證** - 使用 Jakarta Validation
- ✅ **單元測試** - 完整的測試用例
- ✅ **CORS 支援** - 跨域資源共享設定
- ✅ **日誌記錄** - 完善的日誌管理

## 技術棧

| 技術 | 版本 |
|-----|------|
| Java | 17 |
| Spring Boot | 3.2.0 |
| Spring Data JPA | 3.2.0 |
| Jakarta Persistence | 3.1.0 |
| Lombok | 1.18+ |
| H2 Database | Latest |
| MySQL | 8.0.33 |

## 專案結構

```
task-system/
├── src/
│   ├── main/
│   │   ├── java/com/example/tasksystem/
│   │   │   ├── controller/          # 控制器層
│   │   │   ├── service/             # 業務邏輯層
│   │   │   │   └── impl/           # 實現類
│   │   │   ├── repository/          # 資料訪問層
│   │   │   ├── entity/              # 實體類
│   │   │   ├── dto/                 # 資料傳輸物件
│   │   │   ├── config/              # 設定類
│   │   │   └── TaskSystemApplication.java  # 應用入口
│   │   └── resources/
│   │       └── application.yml      # 應用設定
│   └── test/                        # 測試程式碼
├── pom.xml                          # Maven 設定
└── README.md
```

## 快速開始

### 前置需求

- Java 17 或更高版本
- Maven 3.6+
- （選用）MySQL 8.0+

### 1. 建置專案

```bash
mvn clean install
```

### 2. 執行應用

```bash
mvn spring-boot:run
```

應用將在 `http://localhost:8080/api` 啟動

### 3. 訪問 H2 Console（開發環境）

```
http://localhost:8080/api/h2-console
```

**JDBC URL:** `jdbc:h2:mem:taskdb`  
**Username:** `sa`  
**Password:** (留空)

## API 端點

### 建立任務

```http
POST /api/v1/tasks
Content-Type: application/json

{
  "title": "完成專案報告",
  "description": "完成Q1季度的專案總結報告",
  "priority": 1
}
```

### 取得所有任務

```http
GET /api/v1/tasks
```

### 取得單一任務

```http
GET /api/v1/tasks/{id}
```

### 更新任務

```http
PUT /api/v1/tasks/{id}
Content-Type: application/json

{
  "title": "更新的標題",
  "description": "更新的描述",
  "priority": 2
}
```

### 刪除任務

```http
DELETE /api/v1/tasks/{id}
```

### 取得指定狀態的任務

```http
GET /api/v1/tasks/status/{status}
```

**狀態可選值:** `TODO`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`

### 更新任務狀態

```http
PATCH /api/v1/tasks/{id}/status?status=IN_PROGRESS
```

## 資料庫模型

### Task 實體

| 欄位 | 類型 | 說明 |
|-----|------|------|
| id | Long | 主鍵 |
| title | String | 任務標題 |
| description | Text | 任務描述 |
| status | String | 任務狀態 |
| priority | Integer | 優先級 |
| createdAt | DateTime | 建立時間 |
| updatedAt | DateTime | 更新時間 |
| completedAt | DateTime | 完成時間 |

## 執行測試

```bash
# 執行所有測試
mvn test

# 執行特定的測試類
mvn test -Dtest=TaskServiceTest
```

## 設定檔案說明

### application.yml

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update  # 自動建立/更新表格
    show-sql: false      # 不顯示 SQL 陳述式
  datasource:
    url: jdbc:h2:mem:taskdb  # H2 記憶體資料庫
```

## 常見問題

### Q: 如何切換到 MySQL 資料庫？

A: 修改 `application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/taskdb
    username: root
    password: yourpassword
    driver-class-name: com.mysql.cj.jdbc.Driver
```

### Q: 如何新增實體類別？

A: 
1. 在 `entity` 套件中建立實體類別
2. 在 `repository` 套件中建立 Repository 介面
3. 在 `service` 套件中新增業務邏輯

## 學習路徑

1. **第一步** - 瞭解專案結構和分層架構
2. **第二步** - 學習 Spring Boot 註解和依賴注入
3. **第三步** - 學習 JPA 和資料庫操作
4. **第四步** - 學習 REST API 設計原則
5. **第五步** - 學習交易管理和例外處理
6. **第六步** - 學習單元測試和整合測試

## 擴展建議

- [ ] 新增全域例外處理器
- [ ] 實作分頁和排序功能
- [ ] 新增使用者認証和授權
- [ ] 實作快取機制
- [ ] 新增 API 檔案 (Swagger/OpenAPI)
- [ ] 實作稽核日誌
- [ ] 新增資料加密
- [ ] 實作非同步任務處理

## 授權條款

MIT License

## 聯絡方式

如有任何問題，歡迎提交 Issue 或 Pull Request。