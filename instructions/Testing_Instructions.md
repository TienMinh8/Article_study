# Hướng Dẫn Kiểm Thử

## 🎯 Mục Tiêu
Thiết lập một hệ thống kiểm thử toàn diện để đảm bảo chất lượng và độ tin cậy của ứng dụng.

## 🧪 Unit Tests

### 1. Test ViewModel
```kotlin
@RunWith(MockitoJUnitRunner::class)
class NewsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    @Mock
    private lateinit var newsRepository: NewsRepository
    
    @Mock
    private lateinit var aiRepository: AiRepository
    
    @Mock
    private lateinit var dataStoreManager: DataStoreManager
    
    private lateinit var viewModel: NewsViewModel
    
    @Before
    fun setup() {
        viewModel = NewsViewModel(
            newsRepository,
            aiRepository,
            dataStoreManager
        )
    }
    
    @Test
    fun `when loadNews is called, articles are loaded successfully`() = runTest {
        // Given
        val articles = listOf(
            Article(
                source = Source("1", "Test Source"),
                author = "Test Author",
                title = "Test Title",
                description = "Test Description",
                url = "https://test.com",
                urlToImage = null,
                publishedAt = "2024-03-10T10:00:00Z",
                content = "Test Content"
            )
        )
        
        whenever(newsRepository.getTopHeadlines(any(), any()))
            .thenReturn(Result.success(articles))
            
        // When
        viewModel.loadNews()
        
        // Then
        verify(newsRepository).getTopHeadlines(null, 1)
        assertEquals(articles, viewModel.articles.value)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(null, viewModel.error.value)
    }
    
    @Test
    fun `when loadNews fails, error is shown`() = runTest {
        // Given
        val errorMessage = "Network error"
        whenever(newsRepository.getTopHeadlines(any(), any()))
            .thenReturn(Result.failure(Exception(errorMessage)))
            
        // When
        viewModel.loadNews()
        
        // Then
        verify(newsRepository).getTopHeadlines(null, 1)
        assertEquals(emptyList<Article>(), viewModel.articles.value)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(errorMessage, viewModel.error.value)
    }
}
```

### 2. Test Repository
```kotlin
@RunWith(MockitoJUnitRunner::class)
class NewsRepositoryTest {
    @Mock
    private lateinit var newsApiService: NewsApiService
    
    @Mock
    private lateinit var articleDao: ArticleDao
    
    @Mock
    private lateinit var context: Context
    
    private lateinit var repository: NewsRepository
    
    @Before
    fun setup() {
        repository = NewsRepository(newsApiService, articleDao, context)
    }
    
    @Test
    fun `getTopHeadlines returns success when API call is successful`() = runTest {
        // Given
        val response = NewsResponse(
            status = "ok",
            totalResults = 1,
            articles = listOf(
                Article(
                    source = Source("1", "Test Source"),
                    author = "Test Author",
                    title = "Test Title",
                    description = "Test Description",
                    url = "https://test.com",
                    urlToImage = null,
                    publishedAt = "2024-03-10T10:00:00Z",
                    content = "Test Content"
                )
            )
        )
        
        whenever(newsApiService.getTopHeadlines(
            country = "vn",
            category = null,
            page = 1,
            apiKey = any()
        )).thenReturn(response)
        
        // When
        val result = repository.getTopHeadlines()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(response.articles, result.getOrNull())
    }
    
    @Test
    fun `getTopHeadlines returns failure when API call fails`() = runTest {
        // Given
        val errorMessage = "Network error"
        whenever(newsApiService.getTopHeadlines(
            country = "vn",
            category = null,
            page = 1,
            apiKey = any()
        )).thenThrow(RuntimeException(errorMessage))
        
        // When
        val result = repository.getTopHeadlines()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }
}
```

### 3. Test DAO
```kotlin
@RunWith(AndroidJUnit4::class)
class ArticleDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var articleDao: ArticleDao
    
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        articleDao = database.articleDao()
    }
    
    @After
    fun closeDb() {
        database.close()
    }
    
    @Test
    fun insertAndReadArticle() = runTest {
        // Given
        val article = ArticleEntity(
            url = "https://test.com",
            sourceId = "1",
            sourceName = "Test Source",
            author = "Test Author",
            title = "Test Title",
            description = "Test Description",
            urlToImage = null,
            publishedAt = "2024-03-10T10:00:00Z",
            content = "Test Content",
            category = "general"
        )
        
        // When
        articleDao.insertArticles(listOf(article))
        
        // Then
        val loaded = articleDao.getArticlesByCategory("general").first()
        assertEquals(1, loaded.size)
        assertEquals(article, loaded[0])
    }
    
    @Test
    fun deleteOldArticles() = runTest {
        // Given
        val oldArticle = ArticleEntity(
            url = "https://old.com",
            sourceId = "1",
            sourceName = "Test Source",
            author = "Test Author",
            title = "Old Title",
            description = "Old Description",
            urlToImage = null,
            publishedAt = "2024-03-09T10:00:00Z",
            content = "Old Content",
            category = "general",
            lastUpdated = System.currentTimeMillis() - 48 * 60 * 60 * 1000 // 2 days old
        )
        
        val newArticle = ArticleEntity(
            url = "https://new.com",
            sourceId = "2",
            sourceName = "Test Source",
            author = "Test Author",
            title = "New Title",
            description = "New Description",
            urlToImage = null,
            publishedAt = "2024-03-10T10:00:00Z",
            content = "New Content",
            category = "general",
            lastUpdated = System.currentTimeMillis()
        )
        
        articleDao.insertArticles(listOf(oldArticle, newArticle))
        
        // When
        val oneDayAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        articleDao.deleteOldArticles(oneDayAgo)
        
        // Then
        val remaining = articleDao.getArticlesByCategory("general").first()
        assertEquals(1, remaining.size)
        assertEquals(newArticle, remaining[0])
    }
}
```

## 🔄 Integration Tests

### 1. Repository Integration Test
```kotlin
@RunWith(AndroidJUnit4::class)
class NewsRepositoryIntegrationTest {
    private lateinit var repository: NewsRepository
    private lateinit var database: AppDatabase
    private lateinit var mockWebServer: MockWebServer
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        
        // Setup MockWebServer
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        // Setup Retrofit with MockWebServer
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()
            
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            
        val newsApiService = retrofit.create(NewsApiService::class.java)
        
        // Setup Room Database
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        
        // Create repository
        repository = NewsRepository(
            newsApiService,
            database.articleDao(),
            context
        )
    }
    
    @After
    fun tearDown() {
        database.close()
        mockWebServer.shutdown()
    }
    
    @Test
    fun `refreshTopHeadlines updates database with new articles`() = runTest {
        // Given
        val response = """
            {
                "status": "ok",
                "totalResults": 1,
                "articles": [
                    {
                        "source": {"id": "1", "name": "Test Source"},
                        "author": "Test Author",
                        "title": "Test Title",
                        "description": "Test Description",
                        "url": "https://test.com",
                        "urlToImage": null,
                        "publishedAt": "2024-03-10T10:00:00Z",
                        "content": "Test Content"
                    }
                ]
            }
        """.trimIndent()
        
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(response)
        )
        
        // When
        repository.refreshTopHeadlines()
        
        // Then
        val articles = database.articleDao()
            .getArticlesByCategory(null)
            .first()
            
        assertEquals(1, articles.size)
        assertEquals("Test Title", articles[0].title)
        assertEquals("https://test.com", articles[0].url)
    }
}
```

### 2. End-to-End Flow Test
```kotlin
@RunWith(AndroidJUnit4::class)
class NewsFlowTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    private lateinit var repository: NewsRepository
    private lateinit var viewModel: NewsViewModel
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val component = DaggerTestAppComponent.factory().create(context)
        repository = component.newsRepository()
        viewModel = component.newsViewModel()
    }
    
    @Test
    fun newsListToDetailFlow() {
        // Start at news list
        composeTestRule
            .onNodeWithTag("news_list")
            .assertIsDisplayed()
        
        // Click first article
        composeTestRule
            .onNodeWithTag("article_item_0")
            .performClick()
        
        // Verify detail screen
        composeTestRule
            .onNodeWithTag("article_detail")
            .assertIsDisplayed()
        
        // Verify share button
        composeTestRule
            .onNodeWithTag("share_button")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
}
```

## 🤖 UI Tests

### 1. Compose UI Tests
```kotlin
@RunWith(AndroidJUnit4::class)
class NewsScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    
    @Test
    fun newsListDisplaysCorrectly() {
        // Given
        val articles = listOf(
            Article(
                source = Source("1", "Test Source"),
                author = "Test Author",
                title = "Test Title",
                description = "Test Description",
                url = "https://test.com",
                urlToImage = null,
                publishedAt = "2024-03-10T10:00:00Z",
                content = "Test Content"
            )
        )
        
        // When
        composeTestRule.setContent {
            NewsTheme {
                NewsList(
                    articles = articles,
                    onArticleClick = {},
                    onShareClick = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithText("Test Title")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("Test Description")
            .assertIsDisplayed()
    }
    
    @Test
    fun loadingStateDisplaysCorrectly() {
        // When
        composeTestRule.setContent {
            NewsTheme {
                NewsList(
                    articles = emptyList(),
                    isLoading = true,
                    onArticleClick = {},
                    onShareClick = {}
                )
            }
        }
        
        // Then
        composeTestRule
            .onNodeWithTag("loading_indicator")
            .assertIsDisplayed()
    }
}
```

### 2. Screenshot Tests
```kotlin
@RunWith(AndroidJUnit4::class)
class ScreenshotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    
    private lateinit var screenshotComparator: ScreenshotComparator
    
    @Before
    fun setup() {
        screenshotComparator = ScreenshotComparator(
            composeTestRule.activity
        )
    }
    
    @Test
    fun newsListMatchesGoldenImage() {
        // Given
        val articles = listOf(/* test articles */)
        
        // When
        composeTestRule.setContent {
            NewsTheme {
                NewsList(
                    articles = articles,
                    onArticleClick = {},
                    onShareClick = {}
                )
            }
        }
        
        // Then
        screenshotComparator.assertScreenshotMatches(
            "news_list",
            composeTestRule.onRoot()
        )
    }
}
```

## 🔍 Performance Tests

### 1. Database Performance
```kotlin
@RunWith(AndroidJUnit4::class)
class DatabasePerformanceTest {
    private lateinit var database: AppDatabase
    private lateinit var articleDao: ArticleDao
    
    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
        articleDao = database.articleDao()
    }
    
    @Test
    fun insertLargeDatasetPerformance() = runTest {
        // Given
        val articles = List(1000) { index ->
            ArticleEntity(
                url = "https://test.com/$index",
                sourceId = "1",
                sourceName = "Test Source",
                author = "Test Author",
                title = "Test Title $index",
                description = "Test Description",
                urlToImage = null,
                publishedAt = "2024-03-10T10:00:00Z",
                content = "Test Content",
                category = "general"
            )
        }
        
        // When
        val startTime = System.nanoTime()
        articleDao.insertArticles(articles)
        val endTime = System.nanoTime()
        
        // Then
        val duration = (endTime - startTime) / 1_000_000 // Convert to milliseconds
        assertTrue("Insert took too long: $duration ms", duration < 1000)
    }
}
```

### 2. Network Performance
```kotlin
@RunWith(AndroidJUnit4::class)
class NetworkPerformanceTest {
    private lateinit var repository: NewsRepository
    private lateinit var mockWebServer: MockWebServer
    
    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        
        // Setup repository with MockWebServer
    }
    
    @Test
    fun apiResponseTimeTest() = runTest {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(/* sample response */)
                .setBodyDelay(100, TimeUnit.MILLISECONDS)
        )
        
        // When
        val startTime = System.nanoTime()
        repository.getTopHeadlines()
        val endTime = System.nanoTime()
        
        // Then
        val duration = (endTime - startTime) / 1_000_000
        assertTrue("API call took too long: $duration ms", duration < 500)
    }
}
```

## ✅ Kiểm Tra Hoàn Thành

- [ ] Unit tests cho ViewModel, Repository và DAO
- [ ] Integration tests cho luồng dữ liệu hoàn chỉnh
- [ ] UI tests cho các màn hình chính
- [ ] Performance tests cho database và network
- [ ] Screenshot tests cho UI consistency

## 📌 Bước Tiếp Theo

Sau khi hoàn thành các test case, tiếp tục với [Security_Instructions.md](Security_Instructions.md) để triển khai các biện pháp bảo mật cho ứng dụng. 