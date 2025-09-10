package org.ttpss930141011.bj.infrastructure

import kotlinx.coroutines.runBlocking
import kotlin.test.*

class DataLoaderTest {

    @Test
    fun `CachingDataLoader should load data once and cache it`() = runBlocking {
        // Arrange
        val loader = CachingDataLoader()
        var loadCount = 0
        val testData = "test_value"
        
        val mockLoader: suspend () -> String = {
            loadCount++
            testData
        }
        
        // Act - 多次調用同一個鍵
        val result1 = loader.load("test_key", mockLoader)
        val result2 = loader.load("test_key", mockLoader)
        val result3 = loader.load("test_key", mockLoader)
        
        // Assert
        assertEquals(testData, result1)
        assertEquals(testData, result2)
        assertEquals(testData, result3)
        assertEquals(1, loadCount, "數據應該只載入一次")
    }
    
    @Test
    fun `CachingDataLoader should reload after invalidation`() = runBlocking {
        // Arrange
        val loader = CachingDataLoader()
        var loadCount = 0
        val testData = "test_value"
        
        val mockLoader: suspend () -> String = {
            loadCount++
            "${testData}_$loadCount"
        }
        
        // Act
        val result1 = loader.load("test_key", mockLoader)
        
        // 使緩存失效
        loader.invalidate("test_key")
        
        val result2 = loader.load("test_key", mockLoader)
        
        // Assert
        assertEquals("test_value_1", result1)
        assertEquals("test_value_2", result2)
        assertEquals(2, loadCount, "失效後應該重新載入")
    }
    
    @Test
    fun `CachingDataLoader should handle different keys separately`() = runBlocking {
        // Arrange
        val loader = CachingDataLoader()
        var loadCount = 0
        
        val mockLoader: suspend () -> String = {
            loadCount++
            "value_$loadCount"
        }
        
        // Act
        val result1 = loader.load("key1", mockLoader)
        val result2 = loader.load("key2", mockLoader) 
        val result3 = loader.load("key1", mockLoader) // 應該使用緩存
        
        // Assert
        assertEquals("value_1", result1)
        assertEquals("value_2", result2)
        assertEquals("value_1", result3) // 使用緩存的值
        assertEquals(2, loadCount, "不同鍵應該分別載入")
    }
    
    @Test
    fun `CachingDataLoader should clear all cache`() = runBlocking {
        // Arrange
        val loader = CachingDataLoader()
        var loadCount = 0
        
        val mockLoader: suspend () -> String = {
            loadCount++
            "value_$loadCount"
        }
        
        // Act
        val result1 = loader.load("key1", mockLoader)
        val result2 = loader.load("key2", mockLoader)
        
        loader.clearAll()
        
        val result3 = loader.load("key1", mockLoader)
        val result4 = loader.load("key2", mockLoader)
        
        // Assert
        assertEquals("value_1", result1)
        assertEquals("value_2", result2)
        assertEquals("value_3", result3) // 重新載入
        assertEquals("value_4", result4) // 重新載入
        assertEquals(4, loadCount, "清除後應該重新載入所有鍵")
    }
    
    @Test
    fun `TestDataLoader should use mock data when available`() = runBlocking {
        // Arrange
        val loader = TestDataLoader()
        val mockData = "mocked_value"
        val realData = "real_value"
        
        loader.setMockData("test_key", mockData)
        
        val realLoader: suspend () -> String = { realData }
        
        // Act
        val result = loader.load("test_key", realLoader)
        
        // Assert
        assertEquals(mockData, result, "應該返回模擬數據")
        assertEquals(1, loader.getLoadCount("test_key"), "應該記錄載入次數")
    }
    
    @Test
    fun `TestDataLoader should use real loader when no mock data`() = runBlocking {
        // Arrange
        val loader = TestDataLoader()
        val realData = "real_value"
        
        val realLoader: suspend () -> String = { realData }
        
        // Act
        val result = loader.load("test_key", realLoader)
        
        // Assert
        assertEquals(realData, result, "沒有模擬數據時應該調用真實載入器")
        assertEquals(1, loader.getLoadCount("test_key"), "應該記錄載入次數")
    }
    
    @Test
    fun `TestDataLoader should track load counts separately`() = runBlocking {
        // Arrange
        val loader = TestDataLoader()
        
        val mockLoader: suspend () -> String = { "value" }
        
        // Act
        loader.load("key1", mockLoader)
        loader.load("key1", mockLoader)
        loader.load("key2", mockLoader)
        
        // Assert
        assertEquals(2, loader.getLoadCount("key1"))
        assertEquals(1, loader.getLoadCount("key2"))
        assertEquals(0, loader.getLoadCount("key3"))
    }
    
    @Test
    fun `TestDataLoader should handle null mock data correctly`() = runBlocking {
        // Arrange
        val loader = TestDataLoader()
        
        loader.setMockData("test_key", null)
        
        val realLoader: suspend () -> String? = { "real_value" }
        
        // Act
        val result = loader.load("test_key", realLoader)
        
        // Assert
        assertNull(result, "應該返回模擬的 null 值")
    }
}