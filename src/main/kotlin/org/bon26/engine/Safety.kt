package org.bon26.engine

object SafetyUtils {
    fun <T> getSafe(list: List<T>, index: Int): T? {
        return if (index >= 0 && index < list.size) list[index] else null
    }

    fun <K, V> getSafe(map: Map<K, V>, key: K): V? {
        return if (map.containsKey(key)) map[key] else null
    }

    fun removeSafe(list: MutableList<*>, index: Int): Boolean {
        return if (index >= 0 && index < list.size) {
            try {
                list.removeAt(index)
                true
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
    }
}
