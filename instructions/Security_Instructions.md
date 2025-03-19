# Hướng Dẫn Bảo Mật

## 🎯 Mục Tiêu
Triển khai các biện pháp bảo mật toàn diện để bảo vệ dữ liệu người dùng và đảm bảo tính an toàn của ứng dụng.

## 🔐 Mã Hóa Dữ Liệu

### 1. Mã Hóa Database
```kotlin
// SecurityModule.kt
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    @Provides
    @Singleton
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
            
        return EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    @Provides
    @Singleton
    fun provideEncryptedDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
            
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "encrypted_news_db"
        )
        .openHelperFactory(
            EncryptedDatabaseOpenHelper.factory(masterKey)
        )
        .build()
    }
}
```

### 2. Mã Hóa API Keys
```kotlin
// ApiKeyManager.kt
class ApiKeyManager @Inject constructor(
    private val encryptedPrefs: SharedPreferences,
    private val encryptionManager: EncryptionManager
) {
    fun storeApiKey(keyId: String, apiKey: String) {
        val encryptedKey = encryptionManager.encrypt(apiKey)
        encryptedPrefs.edit().putString(keyId, encryptedKey).apply()
    }
    
    fun getApiKey(keyId: String): String? {
        val encryptedKey = encryptedPrefs.getString(keyId, null)
        return encryptedKey?.let { encryptionManager.decrypt(it) }
    }
}

// EncryptionManager.kt
class EncryptionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }
    
    private fun getOrCreateKey(keyAlias: String): SecretKey {
        return if (keyStore.containsAlias(keyAlias)) {
            (keyStore.getEntry(keyAlias, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
            
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
            
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }
    
    fun encrypt(data: String): String {
        val key = getOrCreateKey("api_key_encryption_key")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        val combined = ByteArray(cipher.iv.size + encryptedBytes.size)
        System.arraycopy(cipher.iv, 0, combined, 0, cipher.iv.size)
        System.arraycopy(encryptedBytes, 0, combined, cipher.iv.size, encryptedBytes.size)
        
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }
    
    fun decrypt(encryptedData: String): String {
        val decoded = Base64.decode(encryptedData, Base64.DEFAULT)
        val key = getOrCreateKey("api_key_encryption_key")
        
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val ivSize = 12
        val iv = decoded.copyOfRange(0, ivSize)
        val encrypted = decoded.copyOfRange(ivSize, decoded.size)
        
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        return String(cipher.doFinal(encrypted))
    }
}
```

## 🔒 Bảo Mật Network

### 1. Certificate Pinning
```kotlin
// NetworkSecurityConfig.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkSecurityModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient {
        val certificatePinner = CertificatePinner.Builder()
            .add("api.example.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
            .add("api.example.com", "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB=")
            .build()
            
        return OkHttpClient.Builder()
            .certificatePinner(certificatePinner)
            .addInterceptor(AuthInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .build()
    }
}

// AuthInterceptor.kt
class AuthInterceptor @Inject constructor(
    private val apiKeyManager: ApiKeyManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val apiKey = apiKeyManager.getApiKey("API_KEY")
        
        val newRequest = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("X-Device-ID", getDeviceId())
            .build()
            
        return chain.proceed(newRequest)
    }
}
```

### 2. Bảo Vệ API Endpoints
```kotlin
// NetworkSecurityInterceptor.kt
class NetworkSecurityInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        
        // Kiểm tra URL hợp lệ
        if (!isValidUrl(request.url.toString())) {
            throw SecurityException("Invalid URL detected")
        }
        
        // Thêm security headers
        val secureRequest = request.newBuilder()
            .addHeader("X-Content-Type-Options", "nosniff")
            .addHeader("X-Frame-Options", "DENY")
            .addHeader("X-XSS-Protection", "1; mode=block")
            .build()
            
        return chain.proceed(secureRequest)
    }
    
    private fun isValidUrl(url: String): Boolean {
        return url.startsWith("https://") && 
               ALLOWED_DOMAINS.any { url.contains(it) }
    }
    
    companion object {
        private val ALLOWED_DOMAINS = listOf(
            "api.example.com",
            "cdn.example.com"
        )
    }
}
```

## 🛡️ Bảo Mật Ứng Dụng

### 1. Root Detection
```kotlin
// SecurityManager.kt
class SecurityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun isDeviceSecure(): Boolean {
        return !isDeviceRooted() && 
               !isRunningInEmulator() &&
               !isDebuggable()
    }
    
    private fun isDeviceRooted(): Boolean {
        val buildTags = Build.TAGS
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true
        }
        
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        
        return paths.any { File(it).exists() }
    }
    
    private fun isRunningInEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
               Build.FINGERPRINT.startsWith("generic") ||
               Build.FINGERPRINT.startsWith("unknown") ||
               Build.HARDWARE.contains("goldfish") ||
               Build.HARDWARE.contains("ranchu") ||
               Build.MODEL.contains("google_sdk") ||
               Build.MODEL.contains("Emulator") ||
               Build.MODEL.contains("Android SDK built for x86") ||
               Build.MANUFACTURER.contains("Genymotion") ||
               Build.PRODUCT.contains("sdk_gphone") ||
               Build.PRODUCT.contains("google_sdk") ||
               Build.PRODUCT.contains("sdk") ||
               Build.PRODUCT.contains("sdk_x86") ||
               Build.PRODUCT.contains("vbox86p")
    }
    
    private fun isDebuggable(): Boolean {
        return (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
}
```

### 2. Anti-Tampering
```kotlin
// IntegrityChecker.kt
class IntegrityChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun verifyAppIntegrity(): Boolean {
        return verifySignature() && 
               verifyInstallerStore() &&
               verifyPackageName()
    }
    
    private fun verifySignature(): Boolean {
        try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                )
            } else {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNATURES
                )
            }
            
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo.apkContentsSigners
            } else {
                packageInfo.signatures
            }
            
            signatures.forEach { signature ->
                val signatureBytes = signature.toByteArray()
                val md = MessageDigest.getInstance("SHA-256")
                val currentSignature = Base64.encodeToString(
                    md.digest(signatureBytes),
                    Base64.DEFAULT
                )
                
                if (currentSignature != RELEASE_SIGNATURE) {
                    return false
                }
            }
            
            return true
        } catch (e: Exception) {
            return false
        }
    }
    
    private fun verifyInstallerStore(): Boolean {
        val validInstallers = listOf(
            "com.android.vending",
            "com.google.android.feedback"
        )
        
        val installer = context.packageManager.getInstallerPackageName(context.packageName)
        return validInstallers.contains(installer)
    }
    
    private fun verifyPackageName(): Boolean {
        return context.packageName == "com.example.newsapp"
    }
    
    companion object {
        private const val RELEASE_SIGNATURE = "your_app_release_signature_here"
    }
}
```

## 🔑 Quản Lý Phiên Đăng Nhập

### 1. Token Management
```kotlin
// TokenManager.kt
class TokenManager @Inject constructor(
    private val encryptedPrefs: SharedPreferences,
    private val encryptionManager: EncryptionManager
) {
    fun storeTokens(accessToken: String, refreshToken: String) {
        encryptedPrefs.edit()
            .putString("access_token", encryptionManager.encrypt(accessToken))
            .putString("refresh_token", encryptionManager.encrypt(refreshToken))
            .putLong("token_expiry", System.currentTimeMillis() + TOKEN_VALIDITY)
            .apply()
    }
    
    fun getAccessToken(): String? {
        val encryptedToken = encryptedPrefs.getString("access_token", null)
        return encryptedToken?.let { encryptionManager.decrypt(it) }
    }
    
    fun getRefreshToken(): String? {
        val encryptedToken = encryptedPrefs.getString("refresh_token", null)
        return encryptedToken?.let { encryptionManager.decrypt(it) }
    }
    
    fun isTokenExpired(): Boolean {
        val expiry = encryptedPrefs.getLong("token_expiry", 0)
        return System.currentTimeMillis() >= expiry
    }
    
    fun clearTokens() {
        encryptedPrefs.edit()
            .remove("access_token")
            .remove("refresh_token")
            .remove("token_expiry")
            .apply()
    }
    
    companion object {
        private const val TOKEN_VALIDITY = 24 * 60 * 60 * 1000L // 24 hours
    }
}
```

### 2. Session Management
```kotlin
// SessionManager.kt
class SessionManager @Inject constructor(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository
) {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    init {
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        val accessToken = tokenManager.getAccessToken()
        _isLoggedIn.value = accessToken != null && !tokenManager.isTokenExpired()
    }
    
    suspend fun refreshTokenIfNeeded() {
        if (tokenManager.isTokenExpired()) {
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken != null) {
                try {
                    val newTokens = authRepository.refreshToken(refreshToken)
                    tokenManager.storeTokens(
                        newTokens.accessToken,
                        newTokens.refreshToken
                    )
                } catch (e: Exception) {
                    logout()
                }
            } else {
                logout()
            }
        }
    }
    
    fun logout() {
        tokenManager.clearTokens()
        _isLoggedIn.value = false
    }
}
```

## 📱 Bảo Mật UI

### 1. Screen Security
```kotlin
// SecureActivity.kt
abstract class SecureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = 
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
        }
        
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }
    
    override fun onPause() {
        super.onPause()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }
    
    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            clearSensitiveData()
        }
    }
    
    protected abstract fun clearSensitiveData()
}
```

### 2. Input Validation
```kotlin
// InputValidator.kt
object InputValidator {
    private val EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
        "@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
    )
    
    private val PASSWORD_PATTERN = Pattern.compile(
        "^" +
        "(?=.*[0-9])" +         // ít nhất 1 chữ số
        "(?=.*[a-z])" +         // ít nhất 1 chữ thường
        "(?=.*[A-Z])" +         // ít nhất 1 chữ hoa
        "(?=.*[!@#$%^&*()_+])" + // ít nhất 1 ký tự đặc biệt
        "(?=\\S+$)" +           // không chứa khoảng trắng
        ".{8,}" +               // ít nhất 8 ký tự
        "$"
    )
    
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && EMAIL_PATTERN.matcher(email).matches()
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.isNotEmpty() && PASSWORD_PATTERN.matcher(password).matches()
    }
    
    fun sanitizeInput(input: String): String {
        return input.replace(Regex("[<>\"']"), "")
    }
    
    fun validateSearchQuery(query: String): String {
        return query
            .take(100) // Giới hạn độ dài
            .replace(Regex("[^\\w\\s-]"), "") // Chỉ cho phép chữ, số, khoảng trắng và dấu gạch ngang
            .trim()
    }
}
```

## ✅ Kiểm Tra Hoàn Thành

- [ ] Mã hóa database và shared preferences
- [ ] Certificate pinning cho network requests
- [ ] Root detection và anti-tampering
- [ ] Token và session management
- [ ] Input validation và sanitization
- [ ] Screen security cho sensitive data

## 📌 Bước Tiếp Theo

Sau khi hoàn thành các biện pháp bảo mật, tiếp tục với [Performance_Instructions.md](Performance_Instructions.md) để tối ưu hiệu năng ứng dụng. 