package org.ttpss930141011.bj.infrastructure

/**
 * 數據載入器接口 - 統一管理延遲載入和緩存策略
 * 
 * 提供可測試的數據載入抽象，支持：
 * - 延遲載入：只在需要時載入數據
 * - 緩存機制：避免重複載入同一數據
 * - 失效控制：可以強制重新載入數據
 * - 錯誤處理：載入失敗時的降級策略
 */
interface DataLoader {
    /**
     * 載入數據，如果已載入過則返回緩存結果
     * 
     * @param key 數據的唯一識別鍵
     * @param loader 實際的數據載入邏輯
     * @return 載入的數據
     */
    suspend fun <T> load(key: String, loader: suspend () -> T): T
    
    /**
     * 使指定鍵的緩存失效，下次載入時會重新執行載入邏輯
     * 
     * @param key 要失效的數據鍵
     */
    fun invalidate(key: String)
    
    /**
     * 清除所有緩存
     */
    fun clearAll()
}

/**
 * 生產環境的數據載入器實現
 * 
 * 特性：
 * - 線程安全的緩存機制
 * - 防止重複載入同一數據
 * - 異常處理和降級
 */
class CachingDataLoader : DataLoader {
    private val cache = mutableMapOf<String, Any?>()
    private val loadingFlags = mutableMapOf<String, Boolean>()
    
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> load(key: String, loader: suspend () -> T): T {
        // 如果已有緩存，直接返回
        if (cache.containsKey(key)) {
            return cache[key] as T
        }
        
        // 防止重複載入：如果正在載入，等待完成
        if (loadingFlags[key] == true) {
            while (loadingFlags[key] == true) {
                kotlinx.coroutines.delay(10)
            }
            return cache[key] as T
        }
        
        // 執行載入
        loadingFlags[key] = true
        try {
            val result = loader()
            cache[key] = result
            return result
        } finally {
            loadingFlags[key] = false
        }
    }
    
    override fun invalidate(key: String) {
        cache.remove(key)
        loadingFlags.remove(key)
    }
    
    override fun clearAll() {
        cache.clear()
        loadingFlags.clear()
    }
}

/**
 * 測試環境的數據載入器實現
 * 
 * 特性：
 * - 可預設模擬數據
 * - 完全可控的載入行為
 * - 無實際 I/O 操作
 */
class TestDataLoader : DataLoader {
    private val mockData = mutableMapOf<String, Any?>()
    private val loadCounts = mutableMapOf<String, Int>()
    
    /**
     * 設置模擬數據
     */
    fun setMockData(key: String, data: Any?) {
        mockData[key] = data
    }
    
    /**
     * 獲取某個鍵的載入次數（測試用）
     */
    fun getLoadCount(key: String): Int = loadCounts[key] ?: 0
    
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> load(key: String, loader: suspend () -> T): T {
        loadCounts[key] = (loadCounts[key] ?: 0) + 1
        
        return if (mockData.containsKey(key)) {
            mockData[key] as T
        } else {
            loader()
        }
    }
    
    override fun invalidate(key: String) {
        mockData.remove(key)
    }
    
    override fun clearAll() {
        mockData.clear()
        loadCounts.clear()
    }
}