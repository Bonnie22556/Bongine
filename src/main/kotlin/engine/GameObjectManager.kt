package org.bon26.engine

// *********************************
//
// СДЕЛАНО С ПОМОЩЬЮ ИСКУСТВЕННОГО ИНТЕЛЕКТА
//
// *********************************

class Object(val elementId: Int, var hitbox: Hitbox) {
    // Дополнительные свойства объекта, если нужно
    var tag: String = ""
    var isEnabled: Boolean = true

    // Получение координат из хитбокса
    val x: Int
        get() = when (hitbox) {
            is RectHitbox -> (hitbox as RectHitbox).x
            else -> 0
        }

    val y: Int
        get() = when (hitbox) {
            is RectHitbox -> (hitbox as RectHitbox).y
            else -> 0
        }

    // Проверяет, содержит ли хитбокс точку
    fun containsPoint(x: Int, y: Int): Boolean {
        return hitbox.contains(x, y)
    }

    // Проверяет пересечение с другим GameObject
    fun intersects(other: Object): Boolean {
        return hitbox.intersects(other.hitbox)
    }
}

sealed class Hitbox {
    abstract fun contains(pointX: Int, pointY: Int): Boolean
    abstract fun intersects(other: Hitbox): Boolean
}

class RectHitbox(var x: Int, var y: Int, val width: Int, val height: Int) : Hitbox() {
    override fun contains(pointX: Int, pointY: Int): Boolean {
        return pointX >= x && pointX <= x + width && pointY >= y && pointY <= y + height
    }

    override fun intersects(other: Hitbox): Boolean {
        return when (other) {
            is RectHitbox -> {
                x < other.x + other.width && x + width > other.x &&
                        y < other.y + other.height && y + height > other.y
            }
            else -> false
        }
    }
}


class GameObjectManager {
    private val gameObjects = mutableMapOf<Int, Object>()

    fun addGameObject(elementId: Int, hitbox: Hitbox, tag: String = ""): Object {
        val obj = Object(elementId, hitbox)
        obj.tag = tag
        gameObjects[elementId] = obj
        return obj
    }

    fun removeGameObject(elementId: Int) {
        gameObjects.remove(elementId)
    }

    fun getGameObject(elementId: Int): Object? {
        return gameObjects[elementId]
    }

    fun getObjectAtPoint(x: Int, y: Int): Object? {
        return gameObjects.values.firstOrNull { it.isEnabled && it.containsPoint(x, y) }
    }

    fun getIntersections(elementId: Int): List<Object> {
        val current = gameObjects[elementId] ?: return emptyList()
        return gameObjects.values.filter { it != current && it.isEnabled && current.intersects(it) }
    }

    fun updateHitboxPosition(elementId: Int, x: Int, y: Int) {
        val obj = gameObjects[elementId] ?: return
        when (val hitbox = obj.hitbox) {
            is RectHitbox -> obj.hitbox = RectHitbox(x, y, hitbox.width, hitbox.height)
            // Добавь другие типы хитбоксов по мере необходимости
        }
    }
}
