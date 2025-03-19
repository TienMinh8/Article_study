# Hướng Dẫn Cấu Trúc Database

## 🎯 Mục Tiêu
Thiết kế và triển khai cấu trúc cơ sở dữ liệu hiệu quả cho ứng dụng tin tức, đảm bảo khả năng mở rộng và hiệu suất cao.

## 📊 Sơ Đồ Database

### 1. Bảng Articles
```kotlin
@Entity(
    tableName = "articles",
    indices = [
        Index(value = ["url"], unique = true),
        Index(value = ["category"]),
        Index(value = ["publishedAt"])
    ]
)
data class ArticleEntity(
    @PrimaryKey
    val url: String,
    val sourceId: String?,
    val sourceName: String,
    val author: String?,
    val title: String,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String,
    val content: String?,
    val category: String?,
    val isSaved: Boolean = false,
    val readCount: Int = 0,
    val lastReadAt: Long? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
```

### 2. Bảng User Preferences
```kotlin
@Entity(
    tableName = "user_preferences",
    indices = [Index(value = ["userId"], unique = true)]
)
data class UserPreferenceEntity(
    @PrimaryKey
    val userId: String,
    val preferredCategories: List<String>,
    val readArticles: List<String>,
    val readTime: Map<String, Long>,
    val notificationSettings: NotificationSettings,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class NotificationSettings(
    val enabled: Boolean = true,
    val frequency: NotificationFrequency = NotificationFrequency.DAILY,
    val quietHoursStart: Int? = null,
    val quietHoursEnd: Int? = null,
    val categories: List<String> = emptyList()
)

enum class NotificationFrequency {
    REALTIME, DAILY, WEEKLY, CUSTOM
}
```

### 3. Bảng Reading History
```kotlin
@Entity(
    tableName = "reading_history",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["articleUrl"]),
        Index(value = ["timestamp"])
    ]
)
data class ReadingHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val articleUrl: String,
    val readDuration: Long,
    val completionPercentage: Float,
    val timestamp: Long = System.currentTimeMillis()
)
```

### 4. Bảng Categories
```kotlin
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String?,
    val parentId: String?,
    val order: Int,
    val isActive: Boolean = true
)
```

### 5. Bảng Search History
```kotlin
@Entity(
    tableName = "search_history",
    indices = [Index(value = ["userId"])]
)
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val query: String,
    val timestamp: Long = System.currentTimeMillis(),
    val resultCount: Int,
    val selectedArticle: String?
)
```

## 🔄 Type Converters

### 1. List và Map Converters
```kotlin
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            try {
                Gson().fromJson(it, object : TypeToken<List<String>>() {}.type)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    @TypeConverter
    fun fromStringMap(value: Map<String, Long>?): String? {
        return value?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toStringMap(value: String?): Map<String, Long>? {
        return value?.let {
            try {
                Gson().fromJson(it, object : TypeToken<Map<String, Long>>() {}.type)
            } catch (e: Exception) {
                emptyMap()
            }
        }
    }

    @TypeConverter
    fun fromNotificationSettings(settings: NotificationSettings?): String? {
        return settings?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toNotificationSettings(value: String?): NotificationSettings? {
        return value?.let {
            try {
                Gson().fromJson(it, NotificationSettings::class.java)
            } catch (e: Exception) {
                NotificationSettings()
            }
        }
    }
}
```

## 📝 Data Access Objects (DAOs)

### 1. ArticleDao
```kotlin
@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles WHERE category = :category ORDER BY publishedAt DESC LIMIT :limit")
    fun getArticlesByCategory(category: String?, limit: Int = 20): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE url IN (:urls)")
    suspend fun getArticlesByUrls(urls: List<String>): List<ArticleEntity>

    @Query("""
        SELECT * FROM articles 
        WHERE category IN (:categories) 
        AND publishedAt >= :minDate 
        ORDER BY publishedAt DESC 
        LIMIT :limit
    """)
    fun getArticlesByCategories(
        categories: List<String>,
        minDate: String = getMinDate(),
        limit: Int = 20
    ): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Update
    suspend fun updateArticle(article: ArticleEntity)

    @Query("DELETE FROM articles WHERE isSaved = 0 AND lastUpdated < :timestamp")
    suspend fun deleteOldArticles(timestamp: Long)

    @Query("""
        SELECT * FROM articles 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%'
        ORDER BY publishedAt DESC
        LIMIT :limit
    """)
    suspend fun searchArticles(query: String, limit: Int = 20): List<ArticleEntity>

    @Transaction
    suspend fun updateReadStatus(url: String, userId: String) {
        val article = getArticleByUrl(url)
        article?.let {
            updateArticle(it.copy(
                readCount = it.readCount + 1,
                lastReadAt = System.currentTimeMillis()
            ))
        }
    }
}
```

### 2. UserPreferenceDao
```kotlin
@Dao
interface UserPreferenceDao {
    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    fun getUserPreferences(userId: String): Flow<UserPreferenceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePreferences(preference: UserPreferenceEntity)

    @Query("""
        UPDATE user_preferences 
        SET readArticles = :articles,
            lastUpdated = :timestamp
        WHERE userId = :userId
    """)
    suspend fun updateReadArticles(
        userId: String,
        articles: List<String>,
        timestamp: Long = System.currentTimeMillis()
    )

    @Query("""
        UPDATE user_preferences 
        SET preferredCategories = :categories,
            lastUpdated = :timestamp
        WHERE userId = :userId
    """)
    suspend fun updatePreferredCategories(
        userId: String,
        categories: List<String>,
        timestamp: Long = System.currentTimeMillis()
    )
}
```

### 3. ReadingHistoryDao
```kotlin
@Dao
interface ReadingHistoryDao {
    @Query("""
        SELECT * FROM reading_history 
        WHERE userId = :userId 
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    fun getUserReadingHistory(userId: String, limit: Int = 50): Flow<List<ReadingHistoryEntity>>

    @Insert
    suspend fun insertReadingRecord(record: ReadingHistoryEntity)

    @Query("""
        SELECT articleUrl, SUM(readDuration) as totalDuration
        FROM reading_history
        WHERE userId = :userId
        AND timestamp >= :startTime
        GROUP BY articleUrl
        ORDER BY totalDuration DESC
        LIMIT :limit
    """)
    suspend fun getMostReadArticles(
        userId: String,
        startTime: Long,
        limit: Int = 10
    ): List<ArticleReadStats>

    @Query("DELETE FROM reading_history WHERE timestamp < :timestamp")
    suspend fun deleteOldRecords(timestamp: Long)
}

data class ArticleReadStats(
    val articleUrl: String,
    val totalDuration: Long
)
```

### 4. SearchHistoryDao
```kotlin
@Dao
interface SearchHistoryDao {
    @Query("""
        SELECT * FROM search_history 
        WHERE userId = :userId 
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    fun getUserSearchHistory(userId: String, limit: Int = 20): Flow<List<SearchHistoryEntity>>

    @Insert
    suspend fun insertSearchRecord(record: SearchHistoryEntity)

    @Query("""
        SELECT query, COUNT(*) as count
        FROM search_history
        WHERE userId = :userId
        AND timestamp >= :startTime
        GROUP BY query
        ORDER BY count DESC
        LIMIT :limit
    """)
    suspend fun getPopularSearches(
        userId: String,
        startTime: Long,
        limit: Int = 10
    ): List<PopularSearch>

    @Query("DELETE FROM search_history WHERE timestamp < :timestamp")
    suspend fun deleteOldSearches(timestamp: Long)
}

data class PopularSearch(
    val query: String,
    val count: Int
)
```

## 🔧 Database Configuration

### 1. Database Module
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "news_app_db"
        )
        .addTypeConverters(Converters())
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Khởi tạo dữ liệu mặc định
                CoroutineScope(Dispatchers.IO).launch {
                    val database = AppDatabase.getInstance(context)
                    initializeDefaultData(database)
                }
            }
        })
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideArticleDao(database: AppDatabase): ArticleDao {
        return database.articleDao()
    }

    @Provides
    fun provideUserPreferenceDao(database: AppDatabase): UserPreferenceDao {
        return database.userPreferenceDao()
    }

    @Provides
    fun provideReadingHistoryDao(database: AppDatabase): ReadingHistoryDao {
        return database.readingHistoryDao()
    }

    @Provides
    fun provideSearchHistoryDao(database: AppDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }
}
```

### 2. Database Migration
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Thêm cột mới vào bảng articles
        database.execSQL("""
            ALTER TABLE articles 
            ADD COLUMN readCount INTEGER NOT NULL DEFAULT 0
        """)
        
        database.execSQL("""
            ALTER TABLE articles 
            ADD COLUMN lastReadAt INTEGER
        """)
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Tạo bảng reading_history mới
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS reading_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                userId TEXT NOT NULL,
                articleUrl TEXT NOT NULL,
                readDuration INTEGER NOT NULL,
                completionPercentage REAL NOT NULL,
                timestamp INTEGER NOT NULL
            )
        """)
        
        // Tạo indices
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_reading_history_userId 
            ON reading_history (userId)
        """)
        
        database.execSQL("""
            CREATE INDEX IF NOT EXISTS index_reading_history_articleUrl 
            ON reading_history (articleUrl)
        """)
    }
}
```

## 🔒 Bảo Mật Database

### 1. Mã Hóa Database
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    @Provides
    @Singleton
    fun provideSQLCipher(
        @ApplicationContext context: Context
    ): SQLCipher {
        val passphrase = getOrGeneratePassphrase(context)
        return SQLCipher(passphrase)
    }

    private fun getOrGeneratePassphrase(context: Context): ByteArray {
        val sharedPrefs = context.getSharedPreferences(
            "database_security",
            Context.MODE_PRIVATE
        )
        
        val existingPassphrase = sharedPrefs.getString("db_passphrase", null)
        if (existingPassphrase != null) {
            return Base64.decode(existingPassphrase, Base64.DEFAULT)
        }

        val newPassphrase = ByteArray(32).apply {
            SecureRandom().nextBytes(this)
        }
        
        sharedPrefs.edit().putString(
            "db_passphrase",
            Base64.encodeToString(newPassphrase, Base64.DEFAULT)
        ).apply()

        return newPassphrase
    }
}
```

### 2. Xóa Dữ Liệu Nhạy Cảm
```kotlin
@Dao
interface SecurityDao {
    @Query("DELETE FROM user_preferences WHERE userId = :userId")
    suspend fun deleteUserData(userId: String)

    @Query("DELETE FROM reading_history WHERE userId = :userId")
    suspend fun deleteReadingHistory(userId: String)

    @Query("DELETE FROM search_history WHERE userId = :userId")
    suspend fun deleteSearchHistory(userId: String)

    @Transaction
    suspend fun deleteAllUserData(userId: String) {
        deleteUserData(userId)
        deleteReadingHistory(userId)
        deleteSearchHistory(userId)
    }
}
```

## ✅ Kiểm Tra Hoàn Thành

- [ ] Cấu trúc bảng dữ liệu đã được định nghĩa
- [ ] Type Converters cho các kiểu dữ liệu phức tạp
- [ ] Data Access Objects (DAOs) cho mỗi bảng
- [ ] Cấu hình Database và Dependency Injection
- [ ] Migration strategies cho phiên bản mới
- [ ] Bảo mật và mã hóa database

## 📌 Bước Tiếp Theo

Sau khi hoàn thành cấu trúc database, tiếp tục với [Testing_Instructions.md](Testing_Instructions.md) để thiết lập các test case và đảm bảo tính ổn định của ứng dụng. 