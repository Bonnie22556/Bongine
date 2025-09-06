package org.bon26.engine

class Engine {
    val Graphics = SimpleGraphics()
    val Input = Input()
    val Audio = AudioEngine()
    private var isRunning = false
    private var lastUpdateTime = System.nanoTime()
    internal val gameObjectManager = GameObjectManager()

    // Инициализация
    fun initialize(width: Int, height: Int, title: String = "Unnamed") {
        Graphics.createWindow(width, height, title)
        Input.setupInput(Graphics.getCanvasComponent())
    }

    // Методы для работы с объектами и хитбоксами
    fun createObject(elementId: Int, hitbox: Hitbox, tag: String = ""): Object {
        return gameObjectManager.addGameObject(elementId, hitbox, tag)
    }

    fun getObjectAtPoint(x: Int, y: Int): Object? {
        return gameObjectManager.getObjectAtPoint(x, y)
    }

    fun getIntersections(elementId: Int): List<Object> {
        return gameObjectManager.getIntersections(elementId)
    }

    // Обновление (вызывать в игровом цикле)
    fun update() {
        Graphics.update()
    }

    // Очистка ресурсов
    fun cleanup() {
        Audio.cleanup()
    }

    fun GameLoop(updateCallback: (Double) -> Unit) {
        isRunning = true
        while (isRunning) {
            val currentTime = System.nanoTime()
            val deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0
            lastUpdateTime = currentTime

            updateCallback(deltaTime)
            update()
            Input.update()

            try {
                Thread.sleep(2)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

}