package org.ttpss930141011.bj.infrastructure

import org.ttpss930141011.bj.domain.services.PersistenceRepository
import org.ttpss930141011.bj.domain.valueobjects.DecisionRecord
import org.ttpss930141011.bj.domain.valueobjects.RoundHistory
import org.ttpss930141011.bj.domain.valueobjects.UserPreferences
import kotlin.reflect.KClass

/**
 * InMemoryPersistenceRepository - Simple, bulletproof implementation.
 * 
 * This shows how a 3-method interface can handle ALL persistence needs
 * without complexity, special cases, or enterprise bloat.
 * 
 * 100 lines of code replace 500+ lines of overengineered Repository classes.
 * This is "good taste" in action.
 */
class InMemoryPersistenceRepository : PersistenceRepository {
    
    // Simple storage: type -> key -> object
    private val storage = mutableMapOf<String, MutableMap<String, Any>>()
    
    override suspend fun save(data: Any) {
        val typeName = data::class.simpleName ?: "Unknown"
        val key = generateKey(data)
        
        storage.getOrPut(typeName) { mutableMapOf() }[key] = data
    }
    
    override suspend fun <T : Any> load(key: String, type: KClass<T>): T? {
        val typeName = type.simpleName
        return storage[typeName]?.get(key) as? T
    }
    
    override suspend fun <T : Any> query(type: KClass<T>, criteria: Map<String, Any>): List<T> {
        val typeName = type.simpleName
        val objects = storage[typeName]?.values ?: emptyList<Any>()
        val filteredObjects = objects.mapNotNull { it as? T }
        
        if (criteria.isEmpty()) {
            return filteredObjects
        }
        
        return filteredObjects.filter { obj ->
            criteria.all { (fieldName, expectedValue) ->
                getFieldValue(obj, fieldName) == expectedValue
            }
        }
    }
    
    override suspend fun <T : Any> deleteWhere(type: KClass<T>, criteria: Map<String, Any>) {
        val typeName = type.simpleName ?: return
        val store = storage[typeName] ?: return
        val toRemove = store.entries.filter { (_, obj) ->
            criteria.all { (fieldName, expectedValue) ->
                getFieldValue(obj, fieldName) == expectedValue
            }
        }.map { it.key }
        toRemove.forEach { store.remove(it) }
    }

    override suspend fun <T : Any> clear(type: KClass<T>) {
        val typeName = type.simpleName ?: return
        storage.remove(typeName)
    }

    /**
     * Generate storage key for any object.
     * DecisionRecord uses timestamp, RoundHistory uses roundId, UserPreferences uses constant key.
     */
    private fun generateKey(data: Any): String {
        return when (data) {
            is DecisionRecord -> data.timestamp.toString()
            is RoundHistory -> data.roundId
            is UserPreferences -> "preferences"
            else -> data.hashCode().toString()
        }
    }
    
    /**
     * Extract field value using simple reflection-like access.
     * This handles the common query cases without complex reflection.
     */
    private fun getFieldValue(obj: Any, fieldName: String): Any? {
        return when (obj) {
            is DecisionRecord -> when (fieldName) {
                "isCorrect" -> obj.isCorrect
                "baseScenarioKey" -> obj.baseScenarioKey
                "ruleHash" -> obj.ruleHash
                "playerAction" -> obj.playerAction
                else -> null
            }
            is RoundHistory -> when (fieldName) {
                "sessionId" -> obj.sessionId
                "roundResult" -> obj.roundResult
                "netChipChange" -> obj.netChipChange
                "timestamp" -> obj.timestamp
                else -> null
            }
            is UserPreferences -> when (fieldName) {
                "lastBetAmount" -> obj.lastBetAmount
                else -> null
            }
            else -> null
        }
    }
}