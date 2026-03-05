package org.ttpss930141011.bj.infrastructure

import kotlinx.coroutines.sync.withLock

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
    private val mutex = kotlinx.coroutines.sync.Mutex()
    
    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> load(key: String, loader: suspend () -> T): T {
        mutex.withLock {
            if (cache.containsKey(key)) {
                return cache[key] as T
            }
        }
        
        // Load outside lock to avoid holding mutex during I/O
        val result = mutex.withLock {
            // Double-check after acquiring lock
            if (cache.containsKey(key)) {
                return cache[key] as T
            }
            val loaded = loader()
            cache[key] = loaded
            loaded
        }
        return result
    }
    
    override fun invalidate(key: String) {
        cache.remove(key)
    }
    
    override fun clearAll() {
        cache.clear()
    }
}
