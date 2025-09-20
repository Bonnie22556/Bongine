package org.bon26.game

import org.bon26.engine.*
import java.awt.Color

fun main() {
    val engine = Engine()
    engine.initialize(800, 600, "Simple Platformer")

    // Создаем игрока
    val playerWidth = 40
    val playerHeight = 40
    val playerHitbox = RectHitbox(0, 400, playerWidth, playerHeight)
    val playerId = 1
    val player = engine.createObject(playerId, playerHitbox, "player")
    val playerGraphicId = engine.Graphics.drawRect(
        player.x, player.y, playerWidth, playerHeight,
        Color.BLUE, true, Color.BLUE
    )

    // Создаем платформы
    val platforms = listOf(
        RectHitbox(-1600, 550, 3200, 50), // земля
        RectHitbox(200, 450, 400, 20),
        RectHitbox(300, 350, 200, 20),
        RectHitbox(500, 250, 200, 20),
        RectHitbox(700, 150, 200, 20)
    )

    val platformGraphicIds = mutableListOf<Int>()
    platforms.forEachIndexed { index, hitbox ->
        val platformId = index + 10
        engine.createObject(platformId, hitbox, "platform")
        platformGraphicIds.add(
            engine.Graphics.drawRect(
                hitbox.x, hitbox.y, hitbox.width, hitbox.height,
                Color.GREEN, true, Color.GREEN
            )
        )
    }

    // Параметры игрока
    var velocityX = 0.0
    var velocityY = 0.0
    val gravity = 0.05
    val moveSpeed = 1.0
    val jumpForce = -5.0
    var isOnGround = false

    // Настройка камеры - КЛЮЧЕВЫЕ ИЗМЕНЕНИЯ
    engine.Camera.setCameraTarget(player)
    engine.Camera.zoom = 1.5f
    engine.Camera.cameraSmoothness = 0.25f
    engine.Camera.isTargetinCenter = true
    engine.Camera.isCameraSmooth = true

    // Установите границы камеры, учитывая размер экрана
    val screenWidth = 800
    val screenHeight = 600
    engine.Camera.setCameraBounds(
        -screenWidth/2,
        0,
        screenWidth*2 + screenWidth,
        screenHeight
    )

    // Запускаем игровой цикл
    engine.GameLoop { deltaTime ->
        // Обработка ввода
        velocityX = 0.0
        if (engine.Input.isKeyPressed(Key.LEFT)) {
            velocityX = -moveSpeed
        } else if (engine.Input.isKeyPressed(Key.RIGHT)) {
            velocityX = moveSpeed
        }

        if (engine.Input.isKeyPressed(Key.SPACE) && isOnGround) {
            velocityY = jumpForce
            isOnGround = false
        }

        // Применяем гравитацию
        velocityY += gravity

        // Предварительное обновление позиции
        var newX = player.x + velocityX.toInt()
        var newY = player.y + velocityY.toInt()

        // Ограничиваем по горизонтали в пределах уровня
        newX = newX.coerceIn(0, 1600 - playerWidth)

        // Обновляем хитбокс для проверки коллизий
        engine.gameObjectManager.updateHitboxPosition(playerId, newX, newY)

        // Проверяем коллизии
        isOnGround = false
        val intersectionsWithPlayer = engine.getIntersections(playerId)

        for (obj in intersectionsWithPlayer) {
            if (obj.tag == "platform") {
                val platform = obj.hitbox as RectHitbox
                val playerBottom = player.y + playerHeight
                val platformTop = platform.y
                val platformBottom = platform.y + platform.height

                // Проверяем, находится ли игрок над платформой
                if (playerBottom > platformTop && player.y < platformTop && velocityY > 0) {
                    isOnGround = true
                    velocityY = 0.0
                    newY = platformTop - playerHeight
                }
            }
        }

        // Финальное обновление позиции + обновляем графическое представление
        engine.gameObjectManager.updateHitboxPosition(playerId, newX, newY)
        engine.Graphics.updateElementPosition(playerGraphicId, player.x, player.y)

        // Обновляем позиции платформ
        platforms.forEachIndexed { index, hitbox ->
            engine.Graphics.updateElementPosition(platformGraphicIds[index], hitbox.x, hitbox.y)
        }

        // Отрисовка
    }

    engine.cleanup()
}