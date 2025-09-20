package org.bon26.game

import org.bon26.engine.Engine
import org.bon26.engine.Key
import org.bon26.engine.MouseButton
import org.bon26.engine.RectHitbox
import java.awt.Color
import java.awt.event.KeyEvent

fun main() {
    val engine = Engine()
    engine.initialize(800, 600, "Particles Test and really big name wtf lol huh is it have limit or not blah blah blahaj Россия - священная наша держава, Россия - любимая наша страна. Могучая воля, великая слава - Твое достоянье на все времена!")


    val playerHitbox = RectHitbox(0, 0, 1, 1)
    val player = engine.createObject(1, playerHitbox, "player")
    val playerGraphicId = engine.Graphics.drawRect(
        player.x, player.y, 1, 1,
        Color.BLACK, false,
        zIndex = 1 // Игрок имеет высокий Z-индекс
    )

    // Настройка камеры
    engine.Camera.setCameraTarget(player)
    engine.Camera.zoom = 0.5f
    engine.Camera.cameraSmoothness = 0.25f
    engine.Camera.isTargetinCenter = true
    engine.Camera.isCameraSmooth = true

    engine.Graphics.drawRect(
        400, 300, 100, 100,
        Color.BLACK, true,
        zIndex = 1 // Игрок имеет высокий Z-индекс
    )

    fun createExplosion(x: Float, y: Float) {
        engine.Particles.emit(
            x = x,
            y = y,
            count = 10,
            minSize = 4,
            maxSize = 50,
            minLifetime = 0.8,
            maxLifetime = 2.5,
            minVelocityX = 800f,
            maxVelocityX = 1000f,
            minVelocityY = -250f,
            maxVelocityY = 250f,
            color = Color(255, 69, 0, 255), // оранжево-красный
            zIndex = 1000
        )
    }

    engine.GameLoop { deltaTime ->

        if (engine.Input.isMouseButtonHoldInterval(MouseButton.LEFT, 0.05)) {
            val mousePos = engine.Camera.screenToWorld(engine.Input.mouseX, engine.Input.mouseY)
            createExplosion(mousePos.x, mousePos.y)
        }

        if (engine.Input.isKeyJustPressed(Key.PAGE_DOWN)) engine.Camera.zoom += 0.1f
        else if (engine.Input.isKeyJustPressed(Key.PAGE_UP)) engine.Camera.zoom -= 0.1f

        engine.gameObjectManager.updateHitboxPosition(1, engine.Input.mouseX, engine.Input.mouseY)
        engine.Graphics.updateElementPosition(playerGraphicId, engine.Input.mouseX, engine.Input.mouseY)

        println("${engine.Input.getMouseButtonHoldTime(MouseButton.LEFT)}")
    }
}