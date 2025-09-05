package org.bon26.engine

// *********************************
//
// СДЕЛАНО С ПОМОЩЬЮ ИСКУСТВЕННОГО ИНТЕЛЕКТА
//
// *********************************

import java.awt.event.*
import javax.swing.JComponent

class Input {
    // Текущие состояния
    private val keysPressed = BooleanArray(256)
    private val mouseButtons = BooleanArray(3) // 0=left, 1=middle, 2=right

    // Предыдущие состояния (для определения изменений)
    private val prevKeysPressed = BooleanArray(256)
    private val prevMouseButtons = BooleanArray(3)

    // Позиция мыши
    var mouseX = 0
    var mouseY = 0

    fun setupInput(component: JComponent) {
        // Обработка мыши
        component.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {}

            override fun mousePressed(e: MouseEvent) {
                when (e.button) {
                    MouseEvent.BUTTON1 -> mouseButtons[0] = true
                    MouseEvent.BUTTON2 -> mouseButtons[1] = true
                    MouseEvent.BUTTON3 -> mouseButtons[2] = true
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                when (e.button) {
                    MouseEvent.BUTTON1 -> mouseButtons[0] = false
                    MouseEvent.BUTTON2 -> mouseButtons[1] = false
                    MouseEvent.BUTTON3 -> mouseButtons[2] = false
                }
            }
        })

        // Отслеживание движения мыши
        component.addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                mouseX = e.x
                mouseY = e.y
            }

            override fun mouseDragged(e: MouseEvent) {
                mouseX = e.x
                mouseY = e.y
            }
        })

        // Обработка клавиатуры
        component.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode < keysPressed.size) {
                    keysPressed[e.keyCode] = true
                }
            }

            override fun keyReleased(e: KeyEvent) {
                if (e.keyCode < keysPressed.size) {
                    keysPressed[e.keyCode] = false
                }
            }
        })

        // Компонент должен быть focusable для получения событий клавиатуры
        component.isFocusable = true
        component.requestFocusInWindow()
    }

    // Обновление состояний (вызывать в начале каждого кадра)
    fun update() {
        // Сохраняем текущие состояния как предыдущие
        System.arraycopy(keysPressed, 0, prevKeysPressed, 0, keysPressed.size)
        System.arraycopy(mouseButtons, 0, prevMouseButtons, 0, mouseButtons.size)
    }

    // Функции для проверки состояния ввода
    fun isKeyPressed(keyCode: Int): Boolean {
        return keyCode < keysPressed.size && keysPressed[keyCode]
    }

    fun isKeyJustPressed(keyCode: Int): Boolean {
        return keyCode < keysPressed.size && keysPressed[keyCode] && !prevKeysPressed[keyCode]
    }

    fun isKeyJustReleased(keyCode: Int): Boolean {
        return keyCode < keysPressed.size && !keysPressed[keyCode] && prevKeysPressed[keyCode]
    }

    fun isMouseButtonPressed(button: Int): Boolean {
        return button in 0..2 && mouseButtons[button]
    }

    fun isMouseButtonJustPressed(button: Int): Boolean {
        return button in 0..2 && mouseButtons[button] && !prevMouseButtons[button]
    }

    fun isMouseButtonJustReleased(button: Int): Boolean {
        return button in 0..2 && !mouseButtons[button] && prevMouseButtons[button]
    }
}
