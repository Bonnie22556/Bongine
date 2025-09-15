package org.bon26.engine

import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.*
import java.awt.Point
import java.awt.geom.Point2D

// Главный класс библиотеки
class SimpleGraphics {
    private var frame: JFrame? = null
    private var canvas: GraphicsCanvas? = null

    // Перечисление для фильтров изображений
    enum class ImageFilter {
        NONE, ANTIALIASING
    }

    fun getCanvasComponent(): JComponent {
        return canvas ?: throw IllegalStateException("Canvas not initialized")
    }

    // Создание окна
    fun createWindow(width: Int, height: Int, title: String = "Simple Graphics") {
        frame = JFrame(title)
        frame!!.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame!!.isResizable = true

        canvas = GraphicsCanvas(width, height)
        frame!!.add(canvas)
        frame!!.pack()
        frame!!.setLocationRelativeTo(null)
        frame!!.isVisible = true
    }

    // Установка заголовка окна
    fun setWindowTitle(title: String) {
        frame?.title = title
    }

    // Установка иконки окна
    fun setWindowIcon(imagePath: String) {
        try {
            val image = ImageIO.read(File(imagePath))
            frame?.iconImage = image
        } catch (e: Exception) {
            println("Ошибка загрузки иконки: ${e.message}")
        }
    }

    // Отрисовка текста с форматированием
    fun drawRichText(text: String, x: Int, y: Int,
                     fontName: String = "Arial",
                     fontSize: Int = 12,
                     color: Color = Color.BLACK,
                     style: Int = Font.PLAIN): Int {
        return canvas?.drawRichText(text, x, y, fontName, fontSize, color, style) ?: -1
    }

    // Загрузка и отрисовка PNG изображения
    fun drawImage(imagePath: String, x: Int, y: Int,
                  filter: ImageFilter = ImageFilter.NONE,
                  scaleX: Double = 1.0, scaleY: Double = 1.0,
                  rotation: Double = 0.0): Int {
        return canvas?.drawImage(imagePath, x, y, filter, scaleX, scaleY, rotation) ?: -1
    }

    fun drawRect(x: Int, y: Int, width: Int, height: Int,
                 outlineColor: Color = Color.BLACK, isFilled: Boolean = false, fillColor: Color = outlineColor, strokeWidth: Float = 1F): Int {
        return canvas?.drawRect(x, y, width, height, outlineColor, isFilled, fillColor, strokeWidth) ?: -1
    }

    // Удаление элемента по ID
    fun removeElement(id: Int): Boolean {
        return canvas?.removeElement(id) == true
    }

    // Обновление позиции элемента
    fun updateElementPosition(id: Int, x: Int, y: Int): Boolean {
        return canvas?.updateElementPosition(id, x, y) == true
    }

    fun setCamera(camera: Camera) {
        canvas?.camera = camera
    }

    // Обновление масштаба элемента
    fun updateElementScale(id: Int, scaleX: Double, scaleY: Double): Boolean {
        return canvas?.updateElementScale(id, scaleX, scaleY) == true
    }

    // Обновление поворота элемента
    fun updateElementRotation(id: Int, rotation: Double): Boolean {
        return canvas?.updateElementRotation(id, rotation) == true
    }

    // Полное обновление элемента
    fun updateElement(id: Int, x: Int? = null, y: Int? = null,
                      scaleX: Double? = null, scaleY: Double? = null,
                      rotation: Double? = null): Boolean {
        return canvas?.updateElement(id, x, y, scaleX, scaleY, rotation) == true
    }

    // Получение всех ID элементов определенного типа
    fun getElementIds(type: ElementType): List<Int> {
        return canvas?.getElementIds(type) ?: emptyList()
    }

    // Получение информации об элементе
    fun getElementInfo(id: Int): ElementInfo? {
        return canvas?.getElementInfo(id)
    }

    // Обновление экрана
    fun update() {
        canvas?.repaint()
    }

    // Очистка экрана
    fun clear(color: Color = Color.WHITE) {
        canvas?.clearColor = color
        canvas?.repaint()
    }

    // Очистка всех элементов
    fun clearAllElements() {
        canvas?.clearAllElements()
    }

    // Ожидание закрытия окна
    fun waitForClose() {
        frame?.isVisible = true
        while (frame?.isVisible == true) {
            Thread.sleep(100)
        }
    }
}

// Типы элементов
enum class ElementType {
    TEXT, IMAGE, RECT
}

// Информация об элементе
data class ElementInfo(
    val id: Int,
    val type: ElementType,
    val x: Int,
    val y: Int,
    val scaleX: Double,
    val scaleY: Double,
    val rotation: Double,
    val path: String? = null
)
// Внутренний класс для холста
class GraphicsCanvas(width: Int, height: Int) : JPanel() {
    private val images = mutableMapOf<String, BufferedImage>()
    private val textElements = mutableMapOf<Int, TextElement>()
    private val imageElements = mutableMapOf<Int, ImageElement>()
    private val rectElements = mutableMapOf<Int, RectElement>()
    var camera: Camera? = null
    private var nextId = 0
    var clearColor: Color = Color.WHITE

    init {
        preferredSize = Dimension(width, height)
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g as Graphics2D

        // Очистка экрана
        g2d.color = clearColor
        g2d.fillRect(0, 0, width, height)

        camera?.let {
            g2d.translate((-it.x * it.zoom).toDouble(), (-it.y * it.zoom).toDouble())
            g2d.scale(it.zoom.toDouble(), it.zoom.toDouble())
        }

        // Отрисовка изображений
        for (element in imageElements.values) {
            val image = images[element.path]
            if (image != null) {
                applyRenderingHints(g2d, element.filter)

                val transform = AffineTransform()
                transform.translate(element.x.toDouble(), element.y.toDouble())
                transform.rotate(Math.toRadians(element.rotation), image.width * element.scaleX / 2, image.height * element.scaleY / 2)
                transform.scale(element.scaleX, element.scaleY)

                g2d.drawImage(image, transform, null)
            }
        }


        // Отрисовка анимаций
        for (rect in rectElements.values) {
            val originalStroke = g2d.stroke
            g2d.stroke = BasicStroke(rect.strokeWidth)

            if (rect.isFilled) {
                g2d.color = rect.fillColor
                g2d.fillRect(rect.x, rect.y, rect.width, rect.height)
                g2d.color = rect.outlineColor
                g2d.drawRect(rect.x, rect.y, rect.width, rect.height)
            }
            else {
                g2d.color = rect.outlineColor
                g2d.drawRect(rect.x, rect.y, rect.width, rect.height)
            }

            g2d.stroke = originalStroke
        }


        // Отрисовка текста
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        for (element in textElements.values) {
            g2d.color = element.color
            g2d.font = Font(element.fontName, element.style, element.fontSize)
            g2d.drawString(element.text, element.x, element.y)
        }
    }

    private fun applyRenderingHints(g2d: Graphics2D, filter: SimpleGraphics.ImageFilter) {
        when (filter) {
            SimpleGraphics.ImageFilter.NONE -> {
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
            }
            SimpleGraphics.ImageFilter.ANTIALIASING -> {
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            }
        }
    }

    fun drawRichText(text: String, x: Int, y: Int,
                     fontName: String, fontSize: Int,
                     color: Color, style: Int): Int {
        val id = nextId++
        textElements[id] = TextElement(text, x, y, fontName, fontSize, color, style, id)
        return id
    }

    fun drawImage(imagePath: String, x: Int, y: Int,
                  filter: SimpleGraphics.ImageFilter,
                  scaleX: Double, scaleY: Double,
                  rotation: Double): Int {
        val image = images[imagePath] ?: run {
            val loadedImage = loadImage(imagePath)
            images[imagePath] = loadedImage
            loadedImage
        }
        val id = nextId++
        imageElements[id] = ImageElement(imagePath, x, y, filter, scaleX, scaleY, rotation, id)
        return id
    }

    fun drawRect(x: Int, y: Int, width: Int, height: Int,
                 outlineColor: Color = Color.BLACK, isFilled: Boolean = false,
                 fillColor: Color = outlineColor, strokeWidth: Float = 1f): Int {
        val id = nextId++
        rectElements[id] = RectElement(x, y, width, height, outlineColor, isFilled, fillColor, strokeWidth, id)
        return id
    }

    fun removeElement(id: Int): Boolean {
        return when {
            textElements.containsKey(id) -> textElements.remove(id) != null
            imageElements.containsKey(id) -> imageElements.remove(id) != null
            rectElements.containsKey(id) -> rectElements.remove(id) != null
            else -> false
        }
    }

    fun updateElementPosition(id: Int, x: Int, y: Int): Boolean {
        return when {
            textElements.containsKey(id) -> {
                val element = textElements[id]!!
                textElements[id] = element.copy(x = x, y = y)
                true
            }
            imageElements.containsKey(id) -> {
                val element = imageElements[id]!!
                imageElements[id] = element.copy(x = x, y = y)
                true
            }
            rectElements.containsKey(id) -> {
                val element = rectElements[id]!!
                rectElements[id] = element.copy(x = x, y = y)
                true
            }
            else -> false
        }
    }

    fun updateElementScale(id: Int, scaleX: Double, scaleY: Double): Boolean {
        return when {
            imageElements.containsKey(id) -> {
                val element = imageElements[id]!!
                imageElements[id] = element.copy(scaleX = scaleX, scaleY = scaleY)
                true
            }
            else -> false
        }
    }

    fun updateElementRotation(id: Int, rotation: Double): Boolean {
        return when {
            imageElements.containsKey(id) -> {
                val element = imageElements[id]!!
                imageElements[id] = element.copy(rotation = rotation)
                true
            }
            else -> false
        }
    }

    fun updateElement(id: Int, x: Int? = null, y: Int? = null,
                      scaleX: Double? = null, scaleY: Double? = null,
                      rotation: Double? = null): Boolean {
        var updated = false

        if (x != null && y != null) {
            updated = updateElementPosition(id, x, y) || updated
        }

        if (scaleX != null && scaleY != null) {
            updated = updateElementScale(id, scaleX, scaleY) || updated
        }

        if (rotation != null) {
            updated = updateElementRotation(id, rotation) || updated
        }

        return updated
    }

    fun getElementIds(type: ElementType): List<Int> {
        return when (type) {
            ElementType.TEXT -> textElements.keys.toList()
            ElementType.IMAGE -> imageElements.keys.toList()
            ElementType.RECT -> rectElements.keys.toList()
        }
    }

    fun getElementInfo(id: Int): ElementInfo? {
        return when {
            textElements.containsKey(id) -> {
                val element = textElements[id]!!
                ElementInfo(id, ElementType.TEXT, element.x, element.y, 1.0, 1.0, 0.0)
            }
            imageElements.containsKey(id) -> {
                val element = imageElements[id]!!
                ElementInfo(id, ElementType.IMAGE, element.x, element.y, element.scaleX, element.scaleY, element.rotation, element.path)
            }
            else -> null
        }
    }

    fun clearAllElements() {
        textElements.clear()
        imageElements.clear()
    }

    private fun loadImage(path: String): BufferedImage {
        return try {
            ImageIO.read(File(path))
        } catch (e: Exception) {
            println("Ошибка загрузки изображения: ${e.message}")
            // Возвращаем пустое изображение в случае ошибки
            BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
        }
    }
}

class Camera {
    var x: Float = 0f
    var y: Float = 0f
    var zoom: Float = 1f
    var camTarget: Object? = null
    var cameraSmoothness = 5.0f
    var cameraBounds: RectHitbox? = null
    var IsTargetinCenter: Boolean? = null
    var IsCameraSmooth: Boolean? = null

    fun setCameraTarget(obj: Object) {
        camTarget = obj
    }
    fun setCameraBounds(x: Int, y: Int, width: Int, height: Int) {
        cameraBounds = RectHitbox(x, y, width, height)
    }
    fun setCameraPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun worldToScreen(worldX: Float, worldY: Float): Point {
        return Point(
            ((worldX - x) * zoom).toInt(),
            ((worldY - y) * zoom).toInt()
        )
    }

    fun screenToWorld(screenX: Int, screenY: Int): Point2D.Float {
        return Point2D.Float(
            screenX / zoom + x,
            screenY / zoom + y
        )
    }

    fun changeZoom(newZoom: Float, centerX: Int, centerY: Int) {
        val worldPosBefore = screenToWorld(centerX, centerY)
        zoom = newZoom
        val worldPosAfter = screenToWorld(centerX, centerY)

        x += (worldPosBefore.x - worldPosAfter.x)
        y += (worldPosBefore.y - worldPosAfter.y)
    }
}

class Animation() { /* TODO("Сделать потом да, SpriteSheet нормальный, а не это уроство") */ }

// Вспомогательные классы для хранения элементов отрисовки
data class TextElement(val text: String, val x: Int, val y: Int,
                       val fontName: String, val fontSize: Int,
                       val color: Color, val style: Int, val id: Int)

data class ImageElement(val path: String, val x: Int, val y: Int,
                        val filter: SimpleGraphics.ImageFilter,
                        val scaleX: Double, val scaleY: Double,
                        val rotation: Double, val id: Int)

data class RectElement(val x: Int, val y: Int,
                            val width: Int, val height: Int,
                            val outlineColor: Color, val isFilled: Boolean,
                            val fillColor: Color, val strokeWidth: Float, // ← Добавить
                            val id: Int)