package org.bon26.engine

import java.awt.event.*
import javax.swing.JComponent
import java.awt.event.KeyEvent

// Enum для кнопок мыши
enum class MouseButton(val index: Int) {
    LEFT(0),
    MIDDLE(1),
    RIGHT(2),
    BACK(3),
    FORWARD(4)
}

enum class Key(val code: Int) {
    ESCAPE(KeyEvent.VK_ESCAPE),
    ENTER(KeyEvent.VK_ENTER),
    SPACE(KeyEvent.VK_SPACE),
    TAB(KeyEvent.VK_TAB),
    SHIFT(KeyEvent.VK_SHIFT),
    CONTROL(KeyEvent.VK_CONTROL),
    ALT(KeyEvent.VK_ALT),
    BACKSPACE(KeyEvent.VK_BACK_SPACE),
    DELETE(KeyEvent.VK_DELETE),

    // Буквы
    A(KeyEvent.VK_A),
    B(KeyEvent.VK_B),
    C(KeyEvent.VK_C),
    D(KeyEvent.VK_D),
    E(KeyEvent.VK_E),
    F(KeyEvent.VK_F),
    G(KeyEvent.VK_G),
    H(KeyEvent.VK_H),
    I(KeyEvent.VK_I),
    J(KeyEvent.VK_J),
    K(KeyEvent.VK_K),
    L(KeyEvent.VK_L),
    M(KeyEvent.VK_M),
    N(KeyEvent.VK_N),
    O(KeyEvent.VK_O),
    P(KeyEvent.VK_P),
    Q(KeyEvent.VK_Q),
    R(KeyEvent.VK_R),
    S(KeyEvent.VK_S),
    T(KeyEvent.VK_T),
    U(KeyEvent.VK_U),
    V(KeyEvent.VK_V),
    W(KeyEvent.VK_W),
    X(KeyEvent.VK_X),
    Y(KeyEvent.VK_Y),
    Z(KeyEvent.VK_Z),

    // Цифры
    NUM_0(KeyEvent.VK_0),
    NUM_1(KeyEvent.VK_1),
    NUM_2(KeyEvent.VK_2),
    NUM_3(KeyEvent.VK_3),
    NUM_4(KeyEvent.VK_4),
    NUM_5(KeyEvent.VK_5),
    NUM_6(KeyEvent.VK_6),
    NUM_7(KeyEvent.VK_7),
    NUM_8(KeyEvent.VK_8),
    NUM_9(KeyEvent.VK_9),

    // Функциональные клавиши
    F1(KeyEvent.VK_F1),
    F2(KeyEvent.VK_F2),
    F3(KeyEvent.VK_F3),
    F4(KeyEvent.VK_F4),
    F5(KeyEvent.VK_F5),
    F6(KeyEvent.VK_F6),
    F7(KeyEvent.VK_F7),
    F8(KeyEvent.VK_F8),
    F9(KeyEvent.VK_F9),
    F10(KeyEvent.VK_F10),
    F11(KeyEvent.VK_F11),
    F12(KeyEvent.VK_F12),

    // Стрелки
    UP(KeyEvent.VK_UP),
    DOWN(KeyEvent.VK_DOWN),
    LEFT(KeyEvent.VK_LEFT),
    RIGHT(KeyEvent.VK_RIGHT),

    // Дополнительные клавиши
    HOME(KeyEvent.VK_HOME),
    END(KeyEvent.VK_END),
    PAGE_UP(KeyEvent.VK_PAGE_UP),
    PAGE_DOWN(KeyEvent.VK_PAGE_DOWN),
    INSERT(KeyEvent.VK_INSERT),
    CAPS_LOCK(KeyEvent.VK_CAPS_LOCK),
    NUM_LOCK(KeyEvent.VK_NUM_LOCK),
    SCROLL_LOCK(KeyEvent.VK_SCROLL_LOCK),

    // Символы
    COMMA(KeyEvent.VK_COMMA),
    PERIOD(KeyEvent.VK_PERIOD),
    SLASH(KeyEvent.VK_SLASH),
    SEMICOLON(KeyEvent.VK_SEMICOLON),
    EQUALS(KeyEvent.VK_EQUALS),
    MINUS(KeyEvent.VK_MINUS),
    PLUS(KeyEvent.VK_PLUS),
    OPEN_BRACKET(KeyEvent.VK_OPEN_BRACKET),
    CLOSE_BRACKET(KeyEvent.VK_CLOSE_BRACKET),
    BACK_SLASH(KeyEvent.VK_BACK_SLASH),
    QUOTE(KeyEvent.VK_QUOTE),
    BACK_QUOTE(KeyEvent.VK_BACK_QUOTE);

    companion object {
        private val codeMap = entries.associateBy { it.code }
        fun fromCode(code: Int): Key? = codeMap[code]
    }
}

class Input {
    // Текущие состояния
    private val keysPressed = mutableMapOf<Key, Boolean>()
    private val mouseButtons = BooleanArray(5)

    // Предыдущие состояния
    private val prevKeysPressed = mutableMapOf<Key, Boolean>()
    private val prevMouseButtons = BooleanArray(5)

    // зажатие кнопки
    private val keyHoldTime = mutableMapOf<Key, Double>()
    private val mouseButtonTime = mutableMapOf<Int, Double>()

    // интервальное зажатие
    private val keyIntervalTimers = mutableMapOf<Key, Double>()
    private val mouseIntervalTimers = mutableMapOf<Int, Double>()

    // Позиция мыши
    var mouseX = 0
    var mouseY = 0

    fun setupInput(component: JComponent) {
        // Обработка мыши (остается без изменений)
        component.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                when (e.button) {
                    1 -> mouseButtons[0] = true
                    2 -> mouseButtons[1] = true
                    3 -> mouseButtons[2] = true
                    4 -> mouseButtons[3] = true
                    5 -> mouseButtons[4] = true
                }
            }

            override fun mouseReleased(e: MouseEvent) {
                when (e.button) {
                    1 -> mouseButtons[0] = false
                    2 -> mouseButtons[1] = false
                    3 -> mouseButtons[2] = false
                    4 -> mouseButtons[3] = false
                    5 -> mouseButtons[4] = false
                }
            }
        })

        // Отслеживание движения мыши (остается без изменений)
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

        // Обработка клавиатуры - теперь используем enum Key
        component.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                val key = Key.fromCode(e.keyCode)
                key?.let { keysPressed[it] = true }
            }

            override fun keyReleased(e: KeyEvent) {
                val key = Key.fromCode(e.keyCode)
                key?.let { keysPressed[it] = false }
            }
        })

        // Компонент должен быть focusable для получения событий клавиатуры
        component.isFocusable = true
        component.requestFocusInWindow()
    }

    // Обновление состояний
    fun update(deltaTime: Double) {
        // Сохраняем текущие состояния как предыдущие
        prevKeysPressed.clear()
        prevKeysPressed.putAll(keysPressed)
        System.arraycopy(mouseButtons, 0, prevMouseButtons, 0, mouseButtons.size)

        // Обновляем время зажатия и интервалы
        updateHoldTimes(deltaTime)
    }

    private fun updateHoldTimes(deltaTime: Double) {
        // Обновление для клавиш
        for ((key, isPressed) in keysPressed) {
            if (isPressed) {
                keyHoldTime[key] = (keyHoldTime[key] ?: 0.0) + deltaTime
                keyIntervalTimers[key] = (keyIntervalTimers[key] ?: 0.0) + deltaTime
            } else {
                keyHoldTime.remove(key)
                keyIntervalTimers.remove(key)
            }
        }

        // Обновление для кнопок мыши (без изменений)
        for (button in mouseButtons.indices) {
            if (mouseButtons[button]) {
                mouseButtonTime[button] = (mouseButtonTime[button] ?: 0.0) + deltaTime
                mouseIntervalTimers[button] = (mouseIntervalTimers[button] ?: 0.0) + deltaTime
            } else {
                mouseButtonTime.remove(button)
                mouseIntervalTimers.remove(button)
            }
        }
    }

    // === KEYBOARD FUNCTIONS === (теперь работают с enum Key)

    fun isKeyPressed(key: Key): Boolean {
        return keysPressed[key] ?: false
    }

    fun isKeyJustPressed(key: Key): Boolean {
        return (keysPressed[key] ?: false) && !(prevKeysPressed[key] ?: false)
    }

    fun isKeyJustReleased(key: Key): Boolean {
        return !(keysPressed[key] ?: false) && (prevKeysPressed[key] ?: false)
    }

    fun getKeyHoldTime(key: Key): Double {
        return keyHoldTime[key] ?: 0.0
    }

    fun isKeyHoldInterval(key: Key, interval: Double, reset: Boolean = true): Boolean {
        val timer = keyIntervalTimers[key] ?: return false
        if (timer >= interval && (keysPressed[key] ?: false)) {
            if (reset) {
                keyIntervalTimers[key] = 0.0
            }
            return true
        }
        return false
    }

    fun resetKeyInterval(key: Key) {
        keyIntervalTimers[key] = 0.0
    }

    // === MOUSE FUNCTIONS === (без изменений)

    fun isMouseButtonPressed(button: MouseButton): Boolean {
        return mouseButtons[button.index]
    }

    fun isMouseButtonJustPressed(button: MouseButton): Boolean {
        return mouseButtons[button.index] && !prevMouseButtons[button.index]
    }

    fun isMouseButtonJustReleased(button: MouseButton): Boolean {
        return !mouseButtons[button.index] && prevMouseButtons[button.index]
    }

    fun getMouseButtonHoldTime(button: MouseButton): Double {
        return mouseButtonTime[button.index] ?: 0.0
    }

    fun isMouseButtonHoldInterval(button: MouseButton, interval: Double, reset: Boolean = true): Boolean {
        val timer = mouseIntervalTimers[button.index] ?: return false
        if (timer >= interval && mouseButtons[button.index]) {
            if (reset) {
                mouseIntervalTimers[button.index] = 0.0
            }
            return true
        }
        return false
    }

    fun resetMouseButtonInterval(button: MouseButton) {
        mouseIntervalTimers[button.index] = 0.0
    }
}