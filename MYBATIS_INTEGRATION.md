# MyBatis (iBatis) Integration - Vendor Data Service

## ✅ Implementation Complete

MyBatis has been successfully integrated into the vendor-data-service to replace Spring JDBC for database operations.

---

## 📦 Dependencies Added

### build.gradle.kts

```kotlin
// MyBatis (iBatis) instead of Spring JDBC
implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
```

**Removed**: `spring-boot-starter-jdbc` (replaced by MyBatis starter which includes JDBC)

---

## 🗂️ Files Created

### 1. Mapper Interface

**File**: `src/main/java/com/vendor/vendordataservice/repository/mybatis/CaseMapper.java`

```java
@Mapper
public interface CaseMapper {
    List<CaseRecord> searchCases(@Param("request") SearchRequest request);
    CaseRecord getCaseById(...);
    int countCases(@Param("request") SearchRequest request);
}
```

### 2. MyBatis XML Mapper

**File**: `src/main/resources/mybatis/mapper/CaseMapper.xml`

Features:
- ✅ Complete result maps for all DTOs
- ✅ Nested collections (charges, sentences, dockets, events, defendants)
- ✅ Dynamic SQL with conditional where clauses
- ✅ Pagination support (OFFSET/FETCH)
- ✅ Parameter binding with @Param
- ✅ Lazy loading configuration

---

## ⚙️ Configuration

### application.yml

```yaml
mybatis:
  mapper-locations: classpath:mybatis/mapper/*.xml
  type-aliases-package: com.vendor.vendordataservice.api.dto
  configuration:
    map-underscore-to-camel-case: true
    default-fetch-size: 100
    default-statement-timeout: 30
    lazy-loading-enabled: true
    aggressive-lazy-loading: false
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
```

**Key Settings**:
- **map-underscore-to-camel-case**: Automatically maps `case_number` → `caseNumber`
- **lazy-loading-enabled**: Nested collections loaded on demand
- **log-impl**: SQL logging via SLF4J

---

## 🔄 Service Layer Updates

### DefaultSearchService.java

**Before** (Spring JDBC):
```java
private final VendorCaseRepository repository;
private final MatchScoringStrategy scoringStrategy;

List<VendorCaseRow> rows = repository.search(...);
// Manual mapping and grouping
```

**After** (MyBatis):
```java
private final CaseMapper caseMapper;

List<CaseRecord> caseRecords = caseMapper.searchCases(request);
// Automatic mapping with nested objects
```

---

## 🎯 Key Features

### 1. Dynamic SQL Queries

```xml
<where>
    <if test="request.nameLast != null and request.nameLast != ''">
        AND UPPER(c.last_name) LIKE UPPER(CONCAT('%', #{request.nameLast}, '%'))
    </if>
    <if test="request.nameFirst != null and request.nameFirst != ''">
        AND UPPER(c.first_name) LIKE UPPER(CONCAT('%', #{request.nameFirst}, '%'))
    </if>
    <!-- ... more conditions ... -->
</where>
```

### 2. Nested Object Mapping

```xml
<resultMap id="CaseRecordMap" type="CaseRecord">
    <id property="caseId" column="case_id"/>
    <!-- ... case fields ... -->
    
    <collection property="charges" ofType="ChargeDto" 
                select="selectChargesByCaseId" column="case_id"/>
    <collection property="dockets" ofType="DocketDto" 
                select="selectDocketsByCaseId" column="case_id"/>
    <!-- ... more collections ... -->
</resultMap>
```

### 3. Pagination

```xml
ORDER BY c.filed_date DESC
OFFSET #{request.page} * #{request.pageSize} - #{request.pageSize} ROWS
FETCH NEXT #{request.pageSize} ROWS ONLY
```

### 4. Conditional Loading

Service layer filters collections based on include flags:

```java
if (!Boolean.TRUE.equals(request.getIncludeCharges())) {
    caseRecords.forEach(c -> c.setCharges(null));
}
```

---

## 📊 Query Examples

### Search Cases

```sql
SELECT c.case_id, c.case_number, c.filed_date, ...
FROM cases c
WHERE UPPER(c.last_name) LIKE UPPER(CONCAT('%', ?, '%'))
  AND c.date_of_birth = ?
  AND c.filed_date >= ?
  AND c.county_id IN (?, ?, ?)
ORDER BY c.filed_date DESC
OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
```

### Load Charges

```sql
SELECT charge_id, charge_sequence_number, offense_date, ...
FROM charges
WHERE case_id = ?
ORDER BY charge_sequence_number
```

### Load Sentences

```sql
SELECT sentence_sequence_number, sentence_imposed_date, ...
FROM sentences
WHERE charge_id = ?
ORDER BY sentence_sequence_number
```

---

## 🚀 Benefits of MyBatis

### vs Spring JDBC

| Feature | Spring JDBC | MyBatis |
|---------|-------------|---------|
| SQL Control | Manual | XML-based, full control |
| Object Mapping | Manual RowMapper | Automatic with ResultMap |
| Nested Objects | Manual joins | Automatic with collections |
| Dynamic SQL | String concatenation | XML tags (`<if>`, `<where>`) |
| Lazy Loading | Not supported | Built-in |
| Caching | Manual | Built-in (1st & 2nd level) |
| Type Handling | Manual | Automatic with TypeHandlers |

### Advantages

1. **Less Boilerplate**: No manual RowMapper implementations
2. **Type Safety**: Compile-time checking with mapper interfaces
3. **Maintainability**: SQL in XML files, separate from Java code
4. **Performance**: Built-in caching and lazy loading
5. **Flexibility**: Dynamic SQL with conditional logic
6. **Nested Objects**: Automatic mapping of complex hierarchies

---

## 🧪 Testing

### Unit Test Example

```java
@SpringBootTest
@MybatisTest
class CaseMapperTest {
    
    @Autowired
    private CaseMapper caseMapper;
    
    @Test
    void testSearchCases() {
        SearchRequest request = SearchRequest.builder()
            .nameLast("SMITH")
            .nameFirst("JOHN")
            .page(1)
            .pageSize(10)
            .build();
            
        List<CaseRecord> results = caseMapper.searchCases(request);
        
        assertNotNull(results);
        assertTrue(results.size() <= 10);
    }
}
```

---

## 📝 SQL Logging

Enable SQL logging in `application.yml`:

```yaml
logging:
  level:
    com.vendor.vendordataservice.repository.mybatis: DEBUG
```

Output:
```
DEBUG - ==>  Preparing: SELECT c.case_id, c.case_number FROM cases c WHERE UPPER(c.last_name) LIKE ? AND c.date_of_birth = ?
DEBUG - ==> Parameters: %SMITH%(String), 1980-01-15(LocalDate)
DEBUG - <==      Total: 5
```

---

## 🔧 Performance Tuning

### 1. Batch Operations

```xml
<insert id="batchInsert" parameterType="java.util.List">
    INSERT INTO cases (case_number, filed_date) VALUES
    <foreach collection="list" item="item" separator=",">
        (#{item.caseNumber}, #{item.filedDate})
    </foreach>
</insert>
```

### 2. Result Set Handling

```yaml
mybatis:
  configuration:
    default-fetch-size: 100  # Fetch 100 rows at a time
    default-statement-timeout: 30  # 30 second timeout
```

### 3. Lazy Loading

```yaml
mybatis:
  configuration:
    lazy-loading-enabled: true
    aggressive-lazy-loading: false  # Load only when accessed
```

---

## 🎓 MyBatis Best Practices

### 1. Use @Param for Multiple Parameters

```java
List<CaseRecord> search(
    @Param("nameLast") String nameLast,
    @Param("dob") LocalDate dob
);
```

### 2. Separate Complex Queries

```xml
<!-- Main query -->
<select id="searchCases" resultMap="CaseRecordMap">
    SELECT * FROM cases WHERE ...
</select>

<!-- Nested query -->
<select id="selectCharges" resultType="ChargeDto">
    SELECT * FROM charges WHERE case_id = #{case_id}
</select>
```

### 3. Use Dynamic SQL

```xml
<where>
    <if test="nameLast != null">
        AND last_name = #{nameLast}
    </if>
</where>
```

### 4. Handle NULL Values

```xml
<result property="middleName" column="middle_name" 
        jdbcType="VARCHAR" javaType="String"/>
```

---

## 📚 Additional Resources

- **MyBatis Documentation**: https://mybatis.org/mybatis-3/
- **Spring Boot MyBatis Starter**: https://mybatis.org/spring-boot-starter/
- **Dynamic SQL**: https://mybatis.org/mybatis-3/dynamic-sql.html
- **Result Maps**: https://mybatis.org/mybatis-3/sqlmap-xml.html#Result_Maps

---

## ✅ Migration Checklist

- [x] Add MyBatis dependency to build.gradle.kts
- [x] Remove spring-boot-starter-jdbc dependency
- [x] Create CaseMapper interface
- [x] Create CaseMapper.xml with all queries
- [x] Configure MyBatis in application.yml
- [x] Update DefaultSearchService to use CaseMapper
- [x] Test search functionality
- [ ] Add unit tests for CaseMapper
- [ ] Performance testing with large datasets
- [ ] Add caching configuration if needed

---

## 🎉 Summary

MyBatis has been successfully integrated into the vendor-data-service, providing:

✅ **Cleaner Code** - No manual RowMapper implementations  
✅ **Better Performance** - Built-in caching and lazy loading  
✅ **Easier Maintenance** - SQL in XML, separate from Java  
✅ **Type Safety** - Compile-time checking with interfaces  
✅ **Nested Objects** - Automatic mapping of complex structures  
✅ **Dynamic SQL** - Conditional queries without string concatenation  

The service is now ready for testing with MyBatis-powered database operations!
