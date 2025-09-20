package org.bon26.engine

import java.io.File
import javax.sound.sampled.*

class AudioClip private constructor(
    private var clip: Clip,
    private val format: AudioFormat,
    private val originalData: ByteArray
) {
    private var isPaused = false
    private var currentFrame = 0L
    private var playbackSpeed = 1.0f

    companion object {
        fun create(path: String): AudioClip? {
            return try {
                val audioFile = File(path)
                val audioStream = AudioSystem.getAudioInputStream(audioFile)
                val format = audioStream.format
                val buffer = audioStream.readAllBytes()

                val clip = AudioSystem.getClip()
                clip.open(format, buffer, 0, buffer.size)

                AudioClip(clip, format, buffer).apply {
                    // Устанавливаем начальную громкость
                    setVolume(1.0f)
                }
            } catch (e: Exception) {
                System.err.println("Error loading audio file: ${e.message}")
                null
            }
        }
    }

    fun play() {
        when {
            isPaused -> {
                clip.start()
                isPaused = false
            }
            !clip.isActive -> {
                // Пересоздаем клип если он был остановлен
                resetClip()
                clip.start()
            }
            else -> clip.start()
        }
    }

    fun pause() {
        if (clip.isRunning) {
            currentFrame = clip.microsecondPosition
            clip.stop()
            isPaused = true
        }
    }

    fun stop() {
        clip.stop()
        currentFrame = 0
        isPaused = false
    }

    fun setVolume(volume: Float) {
        try {
            val gainControl = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
            val minGain = gainControl.minimum
            val maxGain = gainControl.maximum
            gainControl.value = (maxGain - minGain) * volume.coerceIn(0f, 1f) + minGain
        } catch (e: Exception) {
            System.err.println("Volume control not supported: ${e.message}")
        }
    }

    fun setSpeed(speed: Float) {
        playbackSpeed = speed.coerceIn(0.5f, 2.0f)
        // Для реального изменения скорости требуется перекодирование аудио
        System.err.println("Speed change not fully implemented")
    }

    fun applyEcho() {
        try {
            // Создаем копию оригинальных данных для обработки
            val processedData = originalData.copyOf()
            val sampleSize = format.sampleSizeInBits / 8
            val echoDelay = (format.sampleRate * 0.2f).toInt() // Задержка 200ms

            // Обрабатываем данные для добавления эха
            for (i in 0 until processedData.size - echoDelay * sampleSize) {
                val original = processedData[i].toInt()
                val echo = processedData[i + echoDelay * sampleSize].toInt()
                val mixed = (original * 0.7 + echo * 0.3).toInt().toByte()
                processedData[i + echoDelay * sampleSize] = mixed
            }

            // Перезагружаем клип с обработанными данными
            clip.stop()
            clip.close()
            clip = AudioSystem.getClip()
            clip.open(format, processedData, 0, processedData.size)
        } catch (e: Exception) {
            System.err.println("Error applying echo effect: ${e.message}")
        }
    }

    private fun resetClip() {
        clip.close()
        clip = AudioSystem.getClip()
        clip.open(format, originalData, 0, originalData.size)
        clip.microsecondPosition = currentFrame
    }
}

class AudioEngine {
    private val activeClips = mutableListOf<AudioClip>()

    fun loadSound(filePath: String): AudioClip? {
        return AudioClip.create(filePath)?.also {
            activeClips.add(it)
        }
    }

    fun stopAll() {
        activeClips.forEach { it.stop() }
        activeClips.clear()
    }

    fun cleanup() {
        stopAll()
    }
}