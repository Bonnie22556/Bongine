package org.bon26.engine

class Engine {
    val Graphics = SimpleGraphics()
    val Input = Input()
    val Audio = AudioEngine()
    val Camera = Camera()
    private var isRunning = false
    private var lastUpdateTime = System.nanoTime()
    internal val gameObjectManager = GameObjectManager()

    // Инициализация
    fun initialize(width: Int, height: Int, title: String = "Unnamed") {
        Graphics.createWindow(width, height, title)
        Input.setupInput(Graphics.getCanvasComponent())
        Graphics.setCamera(Camera)
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

            updateCamera(deltaTime)
            Graphics.update()
            Input.update()

            try {
                Thread.sleep(2)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    private fun updateCamera(deltaTime: Double) {
        if (Camera.IsTargetinCenter == true) {
            Camera.camTarget?.let { target ->
                // Центрируем камеру на игроке (центр игрока минус половина экрана)
                val targetX = target.x + target.width / 2 - Graphics.getCanvasComponent().width / (2 * Camera.zoom)
                val targetY = target.y + target.height / 2 - Graphics.getCanvasComponent().height / (2 * Camera.zoom)

                // Плавное движение камеры
                Camera.x += ((targetX - Camera.x) * Camera.cameraSmoothness * deltaTime).toFloat()
                Camera.y += ((targetY - Camera.y) * Camera.cameraSmoothness * deltaTime).toFloat()
            }

        }
        else {
            Camera.camTarget?.let { target ->
                val targetX = target.x + target.width / 2
                val targetY = target.y + target.height / 2

                // Плавное движение камеры
                Camera.x += ((targetX - Camera.x) * Camera.cameraSmoothness * deltaTime).toFloat()
                Camera.y += ((targetY - Camera.y) * Camera.cameraSmoothness * deltaTime).toFloat()
            }
        }
        // Ограничение камеры границами
        Camera.cameraBounds?.let { bounds ->
            val zoom = Camera.zoom
            val screenWidth = Graphics.getCanvasComponent().width / zoom
            val screenHeight = Graphics.getCanvasComponent().height / zoom

            Camera.x = Camera.x.coerceIn(
                bounds.x.toFloat(),
                (bounds.x + bounds.width - screenWidth).toFloat()
            )
            Camera.y = Camera.y.coerceIn(
                bounds.y.toFloat(),
                (bounds.y + bounds.height - screenHeight).toFloat()
            )
        }
    }


}