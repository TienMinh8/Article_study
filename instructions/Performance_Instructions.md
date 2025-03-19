# Hướng Dẫn Tối Ưu Hiệu Năng

## 🎯 Mục Tiêu
Tối ưu hiệu năng ứng dụng để đảm bảo trải nghiệm người dùng mượt mà và tiết kiệm tài nguyên hệ thống.

## 🚀 Tối Ưu UI

### 1. Layout Optimization
```kotlin
// OptimizedConstraintLayout.kt
class OptimizedConstraintLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    
    init {
        // Tắt hardware acceleration cho các view không cần thiết
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Tránh đo lại kích thước không cần thiết
        if (!isInLayout) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
    
    override fun requestLayout() {
        // Tránh gọi requestLayout() liên tục
        if (!isInLayout) {
            super.requestLayout()
        }
    }
}

// Sử dụng trong layout XML
<OptimizedConstraintLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:clipChildren="false"
    android:clipToPadding="false">
    
    <!-- Child views -->
    
</OptimizedConstraintLayout>
```

### 2. RecyclerView Optimization
```kotlin
// OptimizedRecyclerView.kt
class OptimizedRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    
    init {
        // Tối ưu bộ nhớ cache
        setItemViewCacheSize(20)
        setHasFixedSize(true)
        
        // Tắt animation mặc định
        itemAnimator = null
        
        // Tối ưu prefetch
        layoutManager?.isItemPrefetchEnabled = true
        (layoutManager as? LinearLayoutManager)?.initialPrefetchItemCount = 4
    }
    
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Tối ưu nested scrolling
        if (parent is NestedScrollView) {
            isNestedScrollingEnabled = false
        }
    }
}

// Custom ViewHolder với ViewBinding
abstract class BaseViewHolder<T : Any, VB : ViewBinding>(
    protected val binding: VB
) : RecyclerView.ViewHolder(binding.root) {
    
    private var item: T? = null
    
    open fun bind(item: T) {
        this.item = item
        bindData(item)
    }
    
    abstract fun bindData(item: T)
    
    open fun recycle() {
        // Giải phóng tài nguyên
        item = null
    }
}
```

### 3. Image Loading Optimization
```kotlin
// ImageLoadingManager.kt
object ImageLoadingManager {
    private val imageLoader by lazy {
        ImageLoader.Builder(App.context)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .crossfade(300)
            .build()
    }
    
    fun loadImage(
        imageView: ImageView,
        url: String?,
        placeholder: Int = R.drawable.placeholder,
        error: Int = R.drawable.error
    ) {
        val request = ImageRequest.Builder(imageView.context)
            .data(url)
            .target(imageView)
            .placeholder(placeholder)
            .error(error)
            .size(Size.ORIGINAL)
            .scale(Scale.FILL)
            .allowHardware(true)
            .build()
            
        imageLoader.enqueue(request)
    }
    
    fun preloadImage(context: Context, url: String?) {
        val request = ImageRequest.Builder(context)
            .data(url)
            .build()
            
        imageLoader.enqueue(request)
    }
}
```

## 🔄 Tối Ưu Network

### 1. Caching Strategy
```kotlin
// NetworkCacheInterceptor.kt
class NetworkCacheInterceptor @Inject constructor(
    @ApplicationContext private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        
        if (!isNetworkAvailable(context)) {
            request = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
        }
        
        val response = chain.proceed(request)
        
        if (isNetworkAvailable(context)) {
            val cacheControl = CacheControl.Builder()
                .maxAge(5, TimeUnit.MINUTES)
                .build()
                
            return response.newBuilder()
                .removeHeader(HEADER_PRAGMA)
                .removeHeader(HEADER_CACHE_CONTROL)
                .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                .build()
        } else {
            val cacheControl = CacheControl.Builder()
                .maxStale(7, TimeUnit.DAYS)
                .build()
                
            return response.newBuilder()
                .removeHeader(HEADER_PRAGMA)
                .removeHeader(HEADER_CACHE_CONTROL)
                .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                .build()
        }
    }
    
    companion object {
        private const val HEADER_PRAGMA = "Pragma"
        private const val HEADER_CACHE_CONTROL = "Cache-Control"
    }
}

// NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheSize = 10 * 1024 * 1024L // 10 MB
        return Cache(context.cacheDir, cacheSize)
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        networkCacheInterceptor: NetworkCacheInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addNetworkInterceptor(networkCacheInterceptor)
            .build()
    }
}
```

### 2. Request Optimization
```kotlin
// RequestOptimizer.kt
class RequestOptimizer @Inject constructor() {
    private val requestQueue = LinkedHashMap<String, Deferred<Any>>()
    private val requestScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    suspend fun <T> optimizeRequest(
        key: String,
        timeWindow: Long = 500L,
        request: suspend () -> T
    ): T {
        return withContext(Dispatchers.IO) {
            val existing = requestQueue[key]
            if (existing != null) {
                @Suppress("UNCHECKED_CAST")
                existing.await() as T
            } else {
                val deferred = requestScope.async {
                    delay(timeWindow)
                    request()
                }
                requestQueue[key] = deferred
                
                try {
                    deferred.await() as T
                } finally {
                    requestQueue.remove(key)
                }
            }
        }
    }
}

// Usage in Repository
class NewsRepository @Inject constructor(
    private val requestOptimizer: RequestOptimizer,
    private val newsApi: NewsApi
) {
    suspend fun getNews(category: String): List<News> {
        return requestOptimizer.optimizeRequest("news_$category") {
            newsApi.getNews(category)
        }
    }
}
```

## 💾 Tối Ưu Database

### 1. Query Optimization
```kotlin
// OptimizedQueries.kt
@Dao
interface NewsDao {
    // Sử dụng LIMIT và ORDER BY hiệu quả
    @Query("""
        SELECT * FROM news 
        WHERE category = :category 
        ORDER BY publishedAt DESC 
        LIMIT :limit
    """)
    fun getLatestNews(category: String, limit: Int = 20): Flow<List<News>>
    
    // Sử dụng JOIN thay vì nested queries
    @Transaction
    @Query("""
        SELECT n.*, c.name as categoryName 
        FROM news n 
        INNER JOIN categories c ON n.categoryId = c.id 
        WHERE n.isSaved = 1
    """)
    fun getSavedNewsWithCategory(): Flow<List<NewsWithCategory>>
    
    // Sử dụng Index cho các trường thường xuyên query
    @Query("""
        SELECT * FROM news 
        WHERE title LIKE '%' || :query || '%' 
        OR content LIKE '%' || :query || '%'
        ORDER BY 
            CASE 
                WHEN title LIKE :query || '%' THEN 1
                WHEN title LIKE '%' || :query || '%' THEN 2
                ELSE 3
            END,
            publishedAt DESC
    """)
    suspend fun searchNews(query: String): List<News>
}

// Database Indices
@Entity(
    tableName = "news",
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["publishedAt"]),
        Index(value = ["title"]),
        Index(value = ["isSaved"])
    ]
)
data class NewsEntity(
    @PrimaryKey val id: String,
    val categoryId: String,
    val title: String,
    val content: String,
    val publishedAt: Long,
    val isSaved: Boolean
)
```

### 2. Transaction Optimization
```kotlin
// TransactionOptimizer.kt
class TransactionOptimizer @Inject constructor(
    private val database: AppDatabase
) {
    suspend fun <T> runInTransaction(block: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            database.withTransaction {
                block()
            }
        }
    }
    
    suspend fun <T> runAsyncTransaction(
        block: suspend () -> T,
        onError: (Exception) -> Unit = {}
    ) {
        withContext(Dispatchers.IO) {
            try {
                database.withTransaction {
                    block()
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}

// Usage in Repository
class NewsRepository @Inject constructor(
    private val transactionOptimizer: TransactionOptimizer,
    private val newsDao: NewsDao
) {
    suspend fun updateNews(news: List<News>) {
        transactionOptimizer.runInTransaction {
            newsDao.deleteOldNews()
            newsDao.insertNews(news)
        }
    }
}
```

## 🧵 Tối Ưu Threading

### 1. Coroutine Optimization
```kotlin
// CoroutineOptimizer.kt
object CoroutineOptimizer {
    private val mainScope = CoroutineScope(
        SupervisorJob() + 
        Dispatchers.Main.immediate +
        CoroutineExceptionHandler { _, throwable ->
            Log.e("CoroutineOptimizer", "Error in main scope", throwable)
        }
    )
    
    private val ioScope = CoroutineScope(
        SupervisorJob() + 
        Dispatchers.IO +
        CoroutineExceptionHandler { _, throwable ->
            Log.e("CoroutineOptimizer", "Error in IO scope", throwable)
        }
    )
    
    fun launchMain(block: suspend CoroutineScope.() -> Unit): Job {
        return mainScope.launch { block() }
    }
    
    fun launchIO(block: suspend CoroutineScope.() -> Unit): Job {
        return ioScope.launch { block() }
    }
    
    suspend fun <T> withMainContext(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.Main.immediate) { block() }
    }
    
    suspend fun <T> withIOContext(block: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.IO) { block() }
    }
}

// Usage in ViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {
    
    fun refreshNews() {
        viewModelScope.launch {
            CoroutineOptimizer.withIOContext {
                repository.refreshNews()
            }
        }
    }
}
```

### 2. WorkManager Optimization
```kotlin
// OptimizedWorker.kt
class NewsRefreshWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Thực hiện công việc nền
                val result = repository.refreshNews()
                Result.success()
            } catch (e: Exception) {
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        }
    }
    
    companion object {
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
                
            val request = PeriodicWorkRequestBuilder<NewsRefreshWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES
            )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                10, TimeUnit.SECONDS
            )
            .build()
            
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "news_refresh",
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }
    }
}
```

## 📊 Performance Monitoring

### 1. Custom Performance Metrics
```kotlin
// PerformanceTracker.kt
object PerformanceTracker {
    private val metrics = mutableMapOf<String, Long>()
    
    fun startTracking(key: String) {
        metrics[key] = System.nanoTime()
    }
    
    fun stopTracking(key: String): Long {
        val startTime = metrics.remove(key) ?: return 0
        val endTime = System.nanoTime()
        val duration = (endTime - startTime) / 1_000_000 // Convert to milliseconds
        
        Log.d("Performance", "$key took $duration ms")
        return duration
    }
    
    fun trackBlock(key: String, block: () -> Unit): Long {
        startTracking(key)
        block()
        return stopTracking(key)
    }
    
    suspend fun trackSuspendBlock(key: String, block: suspend () -> Unit): Long {
        startTracking(key)
        block()
        return stopTracking(key)
    }
}

// Usage
class NewsRepository @Inject constructor() {
    suspend fun getNews(): List<News> {
        return PerformanceTracker.trackSuspendBlock("fetch_news") {
            api.getNews()
        }
    }
}
```

### 2. Memory Leak Detection
```kotlin
// MemoryLeakDetector.kt
object MemoryLeakDetector {
    private val weakReferences = mutableMapOf<String, WeakReference<Any>>()
    
    fun watch(key: String, target: Any) {
        weakReferences[key] = WeakReference(target)
    }
    
    fun checkLeaks() {
        weakReferences.forEach { (key, ref) ->
            if (ref.get() != null) {
                Log.w("MemoryLeak", "Potential memory leak detected for $key")
            }
        }
    }
}

// BaseActivity with leak detection
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            MemoryLeakDetector.watch(
                "${javaClass.simpleName}_${System.identityHashCode(this)}",
                this
            )
        }
    }
}
```

## ✅ Kiểm Tra Hoàn Thành

- [ ] Layout và RecyclerView optimization
- [ ] Image loading và caching
- [ ] Network request optimization
- [ ] Database query optimization
- [ ] Coroutine và threading optimization
- [ ] Performance monitoring implementation

## 📌 Bước Tiếp Theo

Sau khi hoàn thành tối ưu hiệu năng, tiếp tục với [Localization_Instructions.md](Localization_Instructions.md) để triển khai đa ngôn ngữ cho ứng dụng. 