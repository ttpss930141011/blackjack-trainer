package org.ttpss930141011.bj.infrastructure

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
