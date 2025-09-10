package org.ttpss930141011.bj.domain.services

import org.ttpss930141011.bj.domain.valueobjects.*
import kotlin.reflect.KClass

/**
 * Simple persistence interface.
 * 
 * 3 methods handle all persistence needs:
 * - save(data) - Store any domain object
 * - load(key, type) - Retrieve object by key
 * - query(type, criteria) - Find objects matching criteria
 */
interface PersistenceRepository {
    suspend fun save(data: Any)
    suspend fun <T : Any> load(key: String, type: KClass<T>): T?
    suspend fun <T : Any> query(type: KClass<T>, criteria: Map<String, Any> = emptyMap()): List<T>
}

/**
 * Helper functions for reified generics
 */
suspend inline fun <reified T : Any> PersistenceRepository.load(key: String): T? = 
    load(key, T::class)

suspend inline fun <reified T : Any> PersistenceRepository.query(criteria: Map<String, Any> = emptyMap()): List<T> = 
    query(T::class, criteria)