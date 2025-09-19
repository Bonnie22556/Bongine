package org.bon26.game

import org.bon26.engine.Engine
import org.bon26.engine.Noise
import org.bon26.engine.NoiseType
import org.bon26.engine.RectHitbox
import java.awt.Color
import java.awt.event.KeyEvent
import kotlin.math.floor
import javax.swing.SwingUtilities

fun main() {
    val engine = Engine()
    engine.initialize(800, 600, "GaymUWU")

    // создание и настройка игрока (высокий Z-индекс)
    val playerWidth = 40
    val playerHeight = 40
    val playerHitbox = RectHitbox(0, 400, playerWidth, playerHeight)
    val playerId = 1
    val player = engine.createObject(playerId, playerHitbox, "player")
    val playerGraphicId = engine.Graphics.drawRect(
        player.x, player.y, playerWidth, playerHeight,
        Color.BLUE, true, Color.BLUE,
        strokeWidth = 1f,
        zIndex = 10 // Игрок имеет высокий Z-индекс
    )

    // Настройка камеры
    engine.Camera.setCameraTarget(player)
    engine.Camera.zoom = 0.5f
    engine.Camera.cameraSmoothness = 0.25f
    engine.Camera.isTargetinCenter = true
    engine.Camera.isCameraSmooth = true

    val noise = Noise(0, NoiseType.SIMPLEX)
    val worldSizeX = 500
    val worldSizeY = 500
    val scale = 0.01

    // Конфигурация чанков
    val chunkSize = 16
    val renderDistance = 2
    val loadedChunks = mutableSetOf<Pair<Int, Int>>()
    val tileElements = mutableMapOf<Pair<Int, Int>, MutableList<Int>>()

    fun getChunkCoordinates(x: Int, y: Int): Pair<Int, Int> {
        return Pair(floor(x / (chunkSize * 20.0)).toInt(), floor(y / (chunkSize * 20.0)).toInt())
    }

    fun loadChunk(chunkX: Int, chunkY: Int) {
        if (loadedChunks.contains(Pair(chunkX, chunkY))) return

        val elements = mutableListOf<Int>()
        for (x in 0 until chunkSize) {
            for (y in 0 until chunkSize) {
                val worldX = chunkX * chunkSize + x
                val worldY = chunkY * chunkSize + y

                if (worldX > worldSizeX || worldY > worldSizeY) continue

                val scaledX = worldX * scale
                val scaledY = worldY * scale

                val noiseValue = (noise.fBm(scaledX, scaledY, octaves = 4, lacunarity = 2.0, gain = 0.5) + 1.0) / 2.0

                val tileGraphicID = engine.Graphics.drawRect(
                    worldX * 20, worldY * 20, 20, 20,
                    Color(noiseValue.toFloat(), noiseValue.toFloat(), noiseValue.toFloat()),
                    true,
                    Color(noiseValue.toFloat(), noiseValue.toFloat(), noiseValue.toFloat()),
                    strokeWidth = 1f,
                    zIndex = 0
                )
                elements.add(tileGraphicID)
            }
        }
        tileElements[Pair(chunkX, chunkY)] = elements
        loadedChunks.add(Pair(chunkX, chunkY))
    }

    fun unloadChunk(chunkX: Int, chunkY: Int) {
        val chunkKey = Pair(chunkX, chunkY)
        if (!loadedChunks.contains(chunkKey)) return

        // Удаляем элементы в потоке EDT для безопасности
        SwingUtilities.invokeLater {
            tileElements[chunkKey]?.forEach { engine.Graphics.removeElement(it) }
        }

        tileElements.remove(chunkKey)
        loadedChunks.remove(chunkKey)
    }

    fun updateChunks() {
        val playerChunk = getChunkCoordinates(player.x, player.y)
        val chunksToKeep = mutableSetOf<Pair<Int, Int>>()

        for (x in -renderDistance..renderDistance) {
            for (y in -renderDistance..renderDistance) {
                val chunkX = playerChunk.first + x
                val chunkY = playerChunk.second + y
                chunksToKeep.add(Pair(chunkX, chunkY))
                loadChunk(chunkX, chunkY)
            }
        }

        val chunksToRemove = loadedChunks.filter { !chunksToKeep.contains(it) }
        chunksToRemove.forEach { (x, y) -> unloadChunk(x, y) }
    }

    // Первоначальная загрузка чанков
    updateChunks()

    engine.GameLoop { deltaTime ->
        val oldPosX = player.x
        val oldPosY = player.y
        var newPosX = oldPosX
        var newPosY = oldPosY

        // Обработка ввода
        if (engine.Input.isKeyPressed(KeyEvent.VK_LEFT)) newPosX -= 1
        else if (engine.Input.isKeyPressed(KeyEvent.VK_RIGHT)) newPosX += 1
        if (engine.Input.isKeyPressed(KeyEvent.VK_DOWN)) newPosY += 1
        else if (engine.Input.isKeyPressed(KeyEvent.VK_UP)) newPosY -= 1

        // Изменение Z-индекса игрока (пример)
        if (engine.Input.isKeyJustPressed(KeyEvent.VK_Z)) {
            val currentZIndex = engine.Graphics.getElementZIndex(playerGraphicId) ?: 10
            engine.Graphics.updateElementZIndex(playerGraphicId, currentZIndex + 1)
        } else if (engine.Input.isKeyJustPressed(KeyEvent.VK_X)) {
            val currentZIndex = engine.Graphics.getElementZIndex(playerGraphicId) ?: 10
            engine.Graphics.updateElementZIndex(playerGraphicId, currentZIndex - 1)
        }

        if (engine.Input.isKeyJustPressed(KeyEvent.VK_PAGE_DOWN)) engine.Camera.zoom += 0.1f
        else if (engine.Input.isKeyJustPressed(KeyEvent.VK_PAGE_UP)) engine.Camera.zoom -= 0.1f

        engine.gameObjectManager.updateHitboxPosition(playerId, newPosX, oldPosY)
        val intersectionsX = engine.getIntersections(playerId)
        for (obj in intersectionsX) {
            if (obj.tag == "solid") {
                newPosX = oldPosX
                break
            }
        }

        engine.gameObjectManager.updateHitboxPosition(playerId, oldPosX, newPosY)
        val intersectionsY = engine.getIntersections(playerId)
        for (obj in intersectionsY) {
            if (obj.tag == "solid") {
                newPosY = oldPosY
                break
            }
        }

        // Финальное обновление позиции + обновляем графическое представление
        engine.gameObjectManager.updateHitboxPosition(playerId, newPosX, newPosY)
        engine.Graphics.updateElementPosition(playerGraphicId, player.x, player.y)

        // Обновление чанков
        updateChunks()
    }

    engine.cleanup()
}