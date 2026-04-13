# PostgreSQL 設定說明

## 📋 PostgreSQL 設定步驟

### 1. 安裝 PostgreSQL

**Windows:**
- 下載並安裝 PostgreSQL：https://www.postgresql.org/download/windows/
- 或使用 Chocolatey：`choco install postgresql`

**macOS:**
```bash
brew install postgresql
brew services start postgresql
```

**Ubuntu/Debian:**
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### 2. 建立資料庫和使用者

```sql
-- 連接到 PostgreSQL（預設以 postgres 使用者）
psql -U postgres

-- 建立資料庫
CREATE DATABASE taskdb;

-- 建立應用程式使用者
CREATE USER taskuser WITH PASSWORD 'taskpass';

-- 授權給使用者
GRANT ALL PRIVILEGES ON DATABASE taskdb TO taskuser;

-- 退出
\q
```

### 3. 測試連線

```bash
# 測試連線
psql -h localhost -U taskuser -d taskdb

# 如果成功，會看到 PostgreSQL 提示符
taskdb=>
```

### 4. 應用程式設定

在 `application.yml` 中設定：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taskdb
    username: taskuser
    password: taskpass
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.PostgreSQLDialect
```

### 5. 常見問題

**連線錯誤：**
- 確保 PostgreSQL 服務正在運行
- 檢查防火牆設定
- 驗證使用者名稱和密碼

**權限錯誤：**
- 確保使用者有足夠的資料庫權限
- 檢查 `pg_hba.conf` 檔案中的認證設定

**埠衝突：**
- PostgreSQL 預設使用 5432 埠
- 如果衝突，可以修改 PostgreSQL 設定或應用程式設定