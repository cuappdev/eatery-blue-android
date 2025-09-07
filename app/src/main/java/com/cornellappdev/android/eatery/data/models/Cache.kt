package com.cornellappdev.android.eatery.data.models

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/**
 * A cache is essentially just a convenience data structure that abstracts the idea of working with
 * keys and values, and getting the latest value for a given key.
 * Caches can be used for anything, but they are most commonly used for optimistic updates.
 */
class Cache<K, V> {
    private val _cache = MutableStateFlow<Map<K, V>>(emptyMap())

    /**
     * Puts a value at a given key index.
     * If the key is already in the map, then the value will be updated.
     */
    fun put(key: K, value: V) {
        _cache.update {
            it + (key to value)
        }
    }

    /**
     * Removes a value from the cache based on the key.
     * If the key does not exist in the map, then this function will do nothing.
     */
    fun remove(key: K) {
        _cache.update {
            it - key
        }
    }

    /**
     * Returns a flow containing the latest value for a given key.
     * The flow will have a null value if the key is not present in the cache.
     */
    fun get(key: K): Flow<V?> = _cache.map { it[key] }
}