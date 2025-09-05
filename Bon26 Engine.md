# Документация к библиотеке Bon26 Engine

## Обзор

Bon26 Engine — это простая игровая библиотека на Kotlin, предоставляющая основные компоненты для создания 2D-игр, включая графику, обработку ввода, звук и управление игровыми объектами.

## Классы и API

### Класс: Engine

Основной класс движка, предоставляющий доступ ко всем компонентам.

#### Методы:

- `initialize(width: Int, height: Int, title: String = "Unnamed")`
  Инициализирует движок, создает окно заданного размера с указанным заголовком.

- `createObject(elementId: Int, hitbox: Hitbox, tag: String = ""): Object`
  Создает игровой объект с указанным ID, хитбоксом и тегом.

- `getObjectAtPoint(x: Int, y: Int): Object?`
  Возвращает объект в указанной точке или null, если объекта нет.

- `getIntersections(elementId: Int): List<Object>`
  Возвращает список объектов, пересекающихся с объектом указанного ID.

- `update()`
  Обновляет состояние движка (вызывать в игровом цикле).

- `cleanup()`
  Освобождает ресурсы движка.

### Класс: SimpleGraphics

Предоставляет функциональность для отрисовки графики.

#### Методы:

- `createWindow(width: Int, height: Int, title: String)`
  Создает окно для отрисовки.

- `setWindowTitle(title: String)`
  Устанавливает заголовок окна.

- `setWindowIcon(imagePath: String)`
  Устанавливает иконку окна.

- `drawRichText(text: String, x: Int, y: Int, fontName: String, fontSize: Int, color: Color, style: Int): Int`
  Отрисовывает текстовый элемент и возвращает его ID.

- `drawImage(imagePath: String, x: Int, y: Int, filter: ImageFilter, scaleX: Double, scaleY: Double, rotation: Double): Int`
  Отрисовывает изображение и возвращает его ID.

- `drawRect(x: Int, y: Int, width: Int, height: Int, color: Color, isFilled: Boolean): Int`
  Отрисовывает прямоугольник и возвращает его ID.

- `createBitmap(imagePath: String, filter: ImageFilter): Bitmap`
  Создает объект Bitmap из изображения.

- `drawBitmap(bitmap: Bitmap, x: Int, y: Int, scaleX: Double, scaleY: Double, rotation: Double): Int`
  Отрисовывает Bitmap и возвращает его ID.

- `addAnimation(animation: Animation): Int`
  Добавляет анимацию и возвращает её ID.

- `removeElement(id: Int): Boolean`
  Удаляет элемент по ID.

- `updateElementPosition(id: Int, x: Int, y: Int): Boolean`
  Обновляет позицию элемента.

- `updateElementScale(id: Int, scaleX: Double, scaleY: Double): Boolean`
  Обновляет масштаб элемента.

- `updateElementRotation(id: Int, rotation: Double): Boolean`
  Обновляет поворот элемента.

- `updateElement(id: Int, x: Int?, y: Int?, scaleX: Double?, scaleY: Double?, rotation: Double?): Boolean`
  Обновляет параметры элемента.

- `getElementIds(type: ElementType): List<Int>`
  Возвращает список ID элементов указанного типа.

- `getElementInfo(id: Int): ElementInfo?`
  Возвращает информацию об элементе.

- `update()`
  Обновляет экран.

- `clear(color: Color)`
  Очищает экран указанным цветом.

- `clearAllElements()`
  Удаляет все элементы.

- `waitForClose()`
  Ожидает закрытия окна.

### Класс: Input

Обрабатывает пользовательский ввод (клавиатура и мышь).

#### Методы:

- `setupInput(component: JComponent)`
  Настраивает обработку ввода для компонента.

- `update()`
  Обновляет состояния ввода (вызывать в начале каждого кадра).

- `isKeyPressed(keyCode: Int): Boolean`
  Проверяет, нажата ли клавиша.

- `isKeyJustPressed(keyCode: Int): Boolean`
  Проверяет, была ли клавиша только что нажата.

- `isKeyJustReleased(keyCode: Int): Boolean`
  Проверяет, была ли клавиша только что отпущена.

- `isMouseButtonPressed(button: Int): Boolean`
  Проверяет, нажата ли кнопка мыши.

- `isMouseButtonJustPressed(button: Int): Boolean`
  Проверяет, была ли кнопка мыши только что нажата.

- `isMouseButtonJustReleased(button: Int): Boolean`
  Проверяет, была ли кнопка мыши только что отпущена.

#### Свойства:

- `mouseX: Int` - текущая X-координата мыши
- `mouseY: Int` - текущая Y-координата мыши

### Класс: AudioEngine

Управляет воспроизведением звуков.

#### Методы:

- `loadSound(filePath: String): AudioClip?`
  Загружает звуковой файл.

- `stopAll()`
  Останавливает все звуки.

- `cleanup()`
  Освобождает ресурсы аудио системы.

### Класс: AudioClip

Представляет звуковой клип.

#### Методы:

- `play()`
  Воспроизводит звук.

- `pause()`
  Приостанавливает воспроизведение.

- `stop()`
  Останавливает воспроизведение.

- `setVolume(gain: Float)`
  Устанавливает громкость.

- `setSpeed(speed: Float)`
  Устанавливает скорость воспроизведения.

- `applyEcho()`
  Применяет эффект эха.

### Класс: GameObjectManager

Управляет игровыми объектами.

#### Методы:

- `addGameObject(elementId: Int, hitbox: Hitbox, tag: String = ""): Object`
  Добавляет игровой объект.

- `removeGameObject(elementId: Int)`
  Удаляет игровой объект.

- `getGameObject(elementId: Int): Object?`
  Возвращает объект по ID.

- `getObjectAtPoint(x: Int, y: Int): Object?`
  Возвращает объект в указанной точке.

- `getIntersections(elementId: Int): List<Object>`
  Возвращает пересекающиеся объекты.

- `updateHitboxPosition(elementId: Int, x: Int, y: Int)`
  Обновляет позицию хитбокса объекта.

### Класс: Object

Представляет игровой объект.

#### Свойства:

- `elementId: Int` - ID объекта
- `hitbox: Hitbox` - хитбокс объекта
- `tag: String` - тег объекта
- `isEnabled: Boolean` - флаг активности объекта
- `x: Int` - X-координата объекта
- `y: Int` - Y-координата объекта

#### Методы:

- `containsPoint(x: Int, y: Int): Boolean`
  Проверяет, содержит ли объект точку.

- `intersects(other: Object): Boolean`
  Проверяет пересечение с другим объектом.

### Структуры данных

- `Hitbox` - абстрактный класс для хитбоксов
- `RectHitbox` - прямоугольный хитбокс
- `ElementType` - перечисление типов элементов (TEXT, IMAGE, BITMAP, ANIMATION, RECT)
- `ElementInfo` - информация об элементе
- `Animation` - класс для работы с анимациями
- `Bitmap` - класс для работы с изображениями

## Пример использования

```kotlin
fun main() {
    val engine = Engine()
    engine.initialize(800, 600, "My Game")
    
    // Создание объекта
    val hitbox = RectHitbox(100, 100, 50, 50)
    val player = engine.createObject(1, hitbox, "player")
    
    // Игровой цикл
    while (true) {
        // Обработка ввода
        if (engine.Input.isKeyPressed(KeyEvent.VK_SPACE)) {
            // Действие при нажатии пробела
        }
        
        // Отрисовка
        engine.Graphics.clear(Color.WHITE)
        engine.Graphics.drawRect(player.x, player.y, 50, 50, Color.RED, true)
        
        // Обновление
        engine.update()
        Thread.sleep(16) // ~60 FPS
    }
    
    engine.cleanup()
}
```

## Примечания

- Библиотека использует Java Swing для отрисовки
- Для работы с аудио используется Java Sound API
- Все координаты и размеры задаются в пикселях
- Библиотека предоставляет базовую функциональность для создания 2D-игр

Для более подробной информации о конкретных методах и их параметрах, обращайтесь к исходному коду библиотеки.