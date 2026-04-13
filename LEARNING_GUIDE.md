# Spring Boot 系統設計學習指南

## 📚 專案概覽

本專案是一個完整的 **Spring Boot 任務管理系統**，展示了企業級 Java 應用的標準架構和最佳實踐。

### 核心特性

✅ **分層架構** - 清晰的 Controller → Service → Repository 三層設計  
✅ **RESTful API** - 符合 REST 標準的 API 設計  
✅ **ORM 映射** - Spring Data JPA + Hibernate 資料持久化  
✅ **資料驗證** - 使用 Jakarta Validation 進行輸入驗證  
✅ **交易管理** - @Transactional 註解管理交易  
✅ **例外處理** - 統一的例外處理機制  
✅ **單元測試** - 完整的測試覆蓋  

---

## 🏗️ 架構設計詳解

### 1. 分層架構

```
┌─────────────────────────────────────┐
│      Controller (控制器層)          │  ← HTTP 請求入口
│  TaskController                      │  ← 路由和參數綁定
└──────────────────┬──────────────────┘
                   │
┌──────────────────▼──────────────────┐
│      Service (業務邏輯層)           │  ← 核心業務邏輯
│  TaskServiceImpl                      │  ← 交易管理
└──────────────────┬──────────────────┘
                   │
┌──────────────────▼──────────────────┐
│     Repository (資料訪問層)         │  ← 資料庫操作
│  TaskRepository                      │  ← ORM 框架
└─────────────────────────────────────┘
```

### 2. 核心套件結構

```
src/main/java/com/tasksystem/
│
├── controller/              # Web 層 - 接收 HTTP 請求
│   └── TaskController      # 處理任務相關的 REST 端點
│
├── service/                # 業務邏輯層
│   ├── TaskService        # 介面定義
│   └── impl/
│       └── TaskServiceImpl # 介面實現
│
├── repository/            # 資料訪問層
│   └── TaskRepository     # JPA 資料訪問物件
│
├── entity/                # 資料模型
│   ├── Task              # 任務實體
│   └── TaskStatus        # 任務狀態列舉
│
├── dto/                   # 資料傳輸物件
│   ├── TaskDTO           # 回應物件
│   └── CreateTaskRequest # 請求物件
│
├── config/                # 設定類
│   └── WebConfig         # Web 設定
│
└── TaskSystemApplication # 應用啟動類
```

---

## 🔍 核心概念詳解

### 1. Entity（實體類）

**作用**: 映射資料庫表格結構

```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    
    // JPA 會將這個類自動映射到資料庫表格
}
```

### 2. Repository（倉庫）

**作用**: 封裝資料庫操作，遵循 DAO 模式

```java
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // JpaRepository 提供了基本的 CRUD 操作
    // 可以自定義查詢方法
    List<Task> findByStatus(TaskStatus status);
}
```

### 3. Service（服務層）

**作用**: 實作業務邏輯，管理交易

```java
@Service
@Transactional  // 交易管理
public class TaskServiceImpl implements TaskService {
    
    @Autowired
    private TaskRepository repository;
    
    @Override
    public TaskDTO createTask(CreateTaskRequest request) {
        // 1. 驗證邏輯
        // 2. 業務處理
        // 3. 呼叫 Repository 儲存
        // 4. 傳回結果
    }
}
```

### 4. Controller（控制器）

**作用**: 處理 HTTP 請求，路由和參數綁定

```java
@RestController
@RequestMapping("/v1/tasks")
public class TaskController {
    
    @Autowired
    private TaskService service;
    
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody CreateTaskRequest request) {
        // 1. 接收請求參數
        // 2. 呼叫 Service 業務邏輯
        // 3. 傳回 HTTP 回應
    }
}
```

### 5. DTO（資料傳輸物件）

**作用**: 在不同層之間傳輸資料，隔離內部實作

```java
@Data
public class TaskDTO {
    // 只包含需要傳回給用戶端的欄位
    // 不包含資料庫實作細節
    private Long id;
    private String title;
    private TaskStatus status;
}
```

---

## 💡 關鍵 Spring Boot 註解

| 註解 | 作用 |
|-----|------|
| `@SpringBootApplication` | 標記為 Spring Boot 應用入口 |
| `@RestController` | 聲明 REST 控制器 |
| `@Service` | 聲明服務類 |
| `@Repository` | 聲明資料訪問類 |
| `@Entity` | JPA 實體類 |
| `@Table` | 指定表格名稱 |
| `@Column` | 指定欄位屬性 |
| `@Autowired` | 自動裝配依賴 |
| `@Transactional` | 交易管理 |
| `@PostMapping/@GetMapping/@PutMapping/@DeleteMapping` | HTTP 方法映射 |
| `@RequestBody/@PathVariable/@RequestParam` | 參數綁定 |
| `@Valid` | 資料驗證 |

---

## 🔄 請求處理流程

```
請求到達
   ↓
Controller 接收請求
   ↓
@RequestBody 參數綁定 & @Valid 驗證
   ↓
呼叫 Service 業務邏輯
   ↓
Service 呼叫 Repository 操作資料庫
   ↓
Repository 執行 SQL 陳述式
   ↓
資料傳回給 Service
   ↓
Service 進行業務處理
   ↓
Controller 傳回 HTTP 回應
   ↓
Client 接收回應
```

---

## 🚀 常見操作示例

### 查詢所有任務

```java
// Controller
@GetMapping
public ResponseEntity<List<TaskDTO>> getAllTasks() {
    return ResponseEntity.ok(taskService.getAllTasks());
}

// Service
@Override
@Transactional(readOnly = true)
public List<TaskDTO> getAllTasks() {
    return taskRepository.findAll()
        .stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
}

// Repository
// JpaRepository 已包含 findAll() 方法
```

### 建立新任務

```java
// Controller
@PostMapping
public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody CreateTaskRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(taskService.createTask(request));
}

// Service
@Override
@Transactional
public TaskDTO createTask(CreateTaskRequest request) {
    Task task = Task.builder()
        .title(request.getTitle())
        .priority(request.getPriority())
        .status(TaskStatus.TODO)
        .build();
    
    Task saved = taskRepository.save(task);
    return convertToDTO(saved);
}
```

---

## 📋 學習檢查清單

- [ ] 瞭解 Spring Boot 三層架構
- [ ] 瞭解依賴注入（DI）和控制反轉（IoC）
- [ ] 瞭解 JPA 和 Hibernate ORM 映射
- [ ] 瞭解 REST API 設計原則
- [ ] 瞭解 @Transactional 交易管理
- [ ] 瞭解 DTO 的作用
- [ ] 能夠自己實作一個新的 Entity 和 CRUD 操作
- [ ] 瞭解單元測試的意義
- [ ] 瞭解例外處理機制

---

## 🛠️ 擴展方向

### 短期（初級）
- [ ] 新增分頁功能
- [ ] 實作全域例外處理
- [ ] 新增更多查詢方法

### 中期（中級）
- [ ] 新增安全認証（Spring Security）
- [ ] 實作快取機制
- [ ] 新增日誌稽核

### 長期（高級）
- [ ] 新增微服務設計
- [ ] 實作 MQ 非同步處理
- [ ] 新增分散式交易

---

## 📚 推薦閱讀

- Spring Boot Official Documentation
- Spring Data JPA Reference Guide
- RESTful API Design Best Practices
- Clean Architecture by Robert C. Martin

---

## ⚡ 快速指令

```bash
# 建置專案
mvn clean install

# 執行應用
mvn spring-boot:run

# 執行測試
mvn test

# 查看依賴
mvn dependency:tree

# 清理建置
mvn clean
```

---

**開始學習吧！祝你學習順利！** 🎉
