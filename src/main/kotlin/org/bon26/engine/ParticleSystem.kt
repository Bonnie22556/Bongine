package org.bon26.engine

import java.awt.Color
import kotlin.random.Random

class ParticleSystem(private val graphics: SimpleGraphics) {
    data class Particle(
        var x: Float,
        var y: Float,
        var velocityX: Float,
        var velocityY: Float,
        var lifeTime: Double,
        var currentTime: Double = 0.0,
        var sizeX: Int = 5,
        var sizeY: Int = 5,
        var color: Color = Color.ORANGE,
        var elementId: Int = -1,
        var zIndex: Int = 1000
    )

    private var particles = mutableListOf<Particle>()

    fun emit(x: Float, y: Float, count: Int = 10,
             minSize: Int = 3, maxSize: Int = 8,
             minLifetime: Double = 0.5, maxLifetime: Double = 2.0,
             minVelocityX: Float = -50f, maxVelocityX: Float = 50f,
             minVelocityY: Float = -100f, maxVelocityY: Float = -50f,
             color: Color = Color.ORANGE, zIndex: Int) {

        repeat(count) {
            val size = Random.nextInt(minSize, maxSize + 1)
            val lifetime = Random.nextDouble(minLifetime, maxLifetime)
            val velX = Random.nextFloat() * (maxVelocityX - minVelocityX) + minVelocityX
            val velY = Random.nextFloat() * (maxVelocityY - minVelocityY) + minVelocityY

            val particle = Particle(
                x, y,
                velX,
                velY,
                lifetime,
                sizeX = size,
                sizeY = size,
                color = color,
                zIndex = zIndex,

            )

            // Создаем графический элемент для частицы
            particle.elementId = graphics.drawRect(
                x.toInt(), y.toInt(),
                size, size,
                outlineColor = color,
                isFilled = true,
                fillColor = color,
                zIndex = zIndex
            )
            particles.add(particle)
        }
    }

    fun update(deltaTime: Double) {
        val iterator = particles.iterator()
        while (iterator.hasNext()) {
            val particle = iterator.next()
            particle.currentTime += deltaTime
            particle.x += particle.velocityX * deltaTime.toFloat()
            particle.y += particle.velocityY * deltaTime.toFloat()

            // Обновляем позицию
            graphics.updateElementPosition(particle.elementId, particle.x.toInt(), particle.y.toInt())

            // Обновляем прозрачность
            val alpha = (1 - particle.currentTime / particle.lifeTime).coerceIn(0.0, 1.0)
            val newColor = Color(
                particle.color.red,
                particle.color.green,
                particle.color.blue,
                (alpha * 255).toInt()
            )

            // Используем новый метод вместо пересоздания
            graphics.updateElementColor(particle.elementId, newColor)

            if (particle.currentTime >= particle.lifeTime) {
                graphics.removeElement(particle.elementId)
                iterator.remove()
            }
        }
    }

    fun clear() {
        // Удаляем все частицы и их графические элементы
        particles.forEach { particle ->
            graphics.removeElement(particle.elementId)
        }
        particles.clear()
    }
}