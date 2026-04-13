# Spring Boot 開發常見問題解答

## 🤔 常見問題

### 1. 什麼是 Spring Boot？

**Q: Spring Boot 和 Spring 框架的區別是什麼？**

A: Spring Framework 是一個大型框架，設定複雜。Spring Boot 對 Spring 進行了簡化：
- 自動設定（Auto Configuration）
- 內嵌應用伺服器
- 開箱即用的依賴管理
- 簡化的部署方式

### 2. 專案結構相關

**Q: 為什麼要分層（Controller → Service → Repository）？**

A: 分層的好處：
- **職責單一** - 每層只做自己的事
- **易於測試** - 可以分別測試每一層
- **程式碼復用** - Service 可以被多個 Controller 復用
- **易於維護** - 修改一層不影響其他層
- **易於擴展** - 新增功能只需在相應層新增

**Q: DTO 的具體作用是什麼？**

A: DTO 有幾個重要作用：

```java
// Entity - 資料庫映射，包含所有欄位包括敏感資訊
@Entity
public class User {
    private Long id;
    private String username;
    private String password;  // 不應該傳回給用戶端
    private String email;
    private Long roleId;
}

// DTO - 只包含需要傳回的欄位
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    // password 和 roleId 被排除
}
```

優勢：
1. 隱藏內部實作細節
2. 減少傳輸資料量
3. 新增校驗註解
4. 不會暴露資料庫結構

### 3. 註解相關

**Q: @Autowired 是如何工作的？**

A: @Autowired 是依賴注入的實作：

```java
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired  // Spring 會自動注入 TaskRepository Bean
    private TaskRepository repository;
}
```

工作流程：
1. Spring 啟動時掃描所有類別
2. 找到 @Repository 標記的 TaskRepository
3. 建立 TaskRepository 的 Bean 實體
4. 找到 @Autowired 註解
5. 自動注入相應的 Bean

**Q: @Transactional 有什麼作用？**

A: @Transactional 管理資料庫交易：

```java
@Transactional  // 方法執行時自動開啟交易
public void updateTask(Task task) {
    taskRepository.save(task);
    // 如果拋出例外，會自動回滾
    // 否則自動提交
}
```

重要屬性：
- `readOnly = true` - 唯讀交易（資料庫最佳化）
- `rollbackFor = Exception.class` - 指定回滾例外
- `propagation = REQUIRED` - 交易傳播方式

### 4. 資料庫相關

**Q: JPA 是什麼？和 Hibernate 是什麼關係？**

A: 
- **JPA** (Java Persistence API) - Java 資料持久化標準
- **Hibernate** - 是 JPA 的一個實作
- Spring Data JPA - 在 Hibernate 之上再做了一層封裝

```
Spring Data JPA
       ↑
   Hibernate
       ↑
      JPA
       ↑
   JDBC (\u8cc7\u6599\u5eab\u9a45\u52d5\u7a0b\u5f0f)
```

**Q: 如何自定義 Repository 查詢？**

A: Spring Data JPA 支援方法命名約定：

```java
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // 按狀態查詢
    List<Task> findByStatus(TaskStatus status);
    
    // 按優先級查詢
    List<Task> findByPriority(Integer priority);
    
    // 模糊查詢
    List<Task> findByTitleContaining(String title);
    
    // 多條件查詢
    List<Task> findByStatusAndPriority(TaskStatus status, Integer priority);
    
    // 自定義 JPQL 查詢
    @Query("SELECT t FROM Task t WHERE t.status = :status")
    List<Task> findByStatusCustom(@Param("status") TaskStatus status);
    
    // 自定義 SQL 查詢
    @Query(value = "SELECT * FROM tasks WHERE status = ?", nativeQuery = true)
    List<Task> findByStatusSQL(String status);
}
```

### 5. 驗證相關

**Q: 如何在 Controller 中進行資料驗證？**

A: 使用 Jakarta Validation 註解：

```java
@Data
public class CreateTaskRequest {
    @NotBlank(message = "標題不能為空")
    private String title;
    
    @NotNull(message = "優先級不能為空")
    private Integer priority;
    
    @Min(value = 1, message = "優先級最小是1")
    @Max(value = 10, message = "優先級最大是10")
    private Integer priority;
    
    @Email(message = "郵箱格式不正確")
    private String email;
    
    @Size(min = 10, max = 100, message = "描述長度10-100")
    private String description;
}

// 在 Controller 中使用 @Valid
@PostMapping
public ResponseEntity<TaskDTO> createTask(
    @Valid @RequestBody CreateTaskRequest request) {
    // 如果驗證失敗，Spring 會自動傳回 400 錯誤
    return ResponseEntity.ok(taskService.createTask(request));
}
```

### 6. 例外處理

**Q: 應該在哪一層處理例外？**

A: 不同例外應該在不同層處理：

```java
// Repository 層 - 捕捉資料庫例外
@Repository
public class TaskRepository {
    public Task getById(Long id) {
        try {
            return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("任務不存在"));
        } catch (DataAccessException e) {
            throw new DatabaseException("資料庫錯誤", e);
        }
    }
}

// Service 層 - 業務邏輯例外
@Service
public class TaskServiceImpl {
    public void deleteTask(Long id) {
        Task task = taskRepository.getById(id);
        if (task.getStatus() == TaskStatus.RUNNING) {
            throw new BusinessException("進行中的任務不能刪除");
        }
        taskRepository.delete(task);
    }
}

// Controller 層 - 全域例外處理
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(404)
            .body(new ErrorResponse(404, e.getMessage()));
    }
}
```

### 7. API 設計

**Q: RESTful API 的命名規範是什麼？**

A: 
```
取得所有任務      GET    /api/v1/tasks
取得單一任務      GET    /api/v1/tasks/{id}
建立任務         POST   /api/v1/tasks
更新任務         PUT    /api/v1/tasks/{id}
部分更新         PATCH  /api/v1/tasks/{id}
刪除任務         DELETE /api/v1/tasks/{id}

按狀態查詢       GET    /api/v1/tasks?status=PENDING
分頁查詢         GET    /api/v1/tasks?page=1&size=10
```

原則：
- 使用名詞而不是動詞
- 使用小寫 URL
- 使用 - 分隔單詞（task-items 不是 taskItems）
- 使用 HTTP 動詞表示操作
- 傳回適當的 HTTP 狀態碼

**Q: 應該傳回什麼樣的回應格式？**

A: 統一的回應格式：

```java
// 成功回應
{
    "code": 200,
    "message": "成功",
    "data": {
        "id": 1,
        "title": "任務",
        "status": "PENDING"
    }
}

// 錯誤回應
{
    "code": 400,
    "message": "請求參數錯誤",
    "errors": [
        {
            "field": "title",
            "message": "標題不能為空"
        }
    ]
}
```

### 8. 測試相關

**Q: 如何進行單元測試？**

A: 
```java
@SpringBootTest
class TaskServiceTest {
    @Autowired
    private TaskService taskService;
    
    @MockBean  // 模擬 Repository
    private TaskRepository repository;
    
    @BeforeEach
    void setUp() {
        // 初始化測試資料
    }
    
    @Test
    void testCreateTask() {
        // Arrange - 準備
        CreateTaskRequest request = new CreateTaskRequest("標題", 1);
        
        // Act - 執行
        TaskDTO result = taskService.createTask(request);
        
        // Assert - 斷言
        assertNotNull(result.getId());
        assertEquals("標題", result.getTitle());
    }
}
```

### 9. 部署相關

**Q: Spring Boot 應用如何部署？**

A:
```bash
# 打包為 JAR
mvn clean package

# 執行 JAR
java -jar task-system.jar

# 指定埠
java -jar task-system.jar --server.port=9090

# 指定設定檔案
java -jar task-system.jar --spring.config.location=./application-prod.yml
```

### 10. 效能最佳化

**Q: 常見的最佳化手段有哪些？**

A:
1. **資料庫查詢最佳化**
   - 新增索引
   - 避免 N+1 查詢
   - 使用投影查詢只取需要的欄位

2. **快取**
   - 使用 @Cacheable 註解
   - 整合 Redis

3. **非同步處理**
   - 使用 @Async 進行非同步操作
   - 使用訊息佇列

4. **分頁**
   - 對大資料集使用分頁查詢

---

## 🔗 相關資源

- [Spring Boot 官方檔案](https://spring.io/projects/spring-boot)
- [Spring Data JPA 檔案](https://spring.io/projects/spring-data-jpa)
- [RESTful API 設計指南](https://restfulapi.net/)

---

**如有其他問題，歡迎補充！** 💬
