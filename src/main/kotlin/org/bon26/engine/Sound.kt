package org.bon26.engine

import java.io.File
import javax.sound.sampled.*

class AudioClip private constructor(
    private val clip: Clip,
    private val control: FloatControl,
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
                val buffer = ByteArray(audioStream.available())
                audioStream.read(buffer)

                val clip = AudioSystem.getClip()
                clip.open(format, buffer, 0, buffer.size)

                val gainControl = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl

                AudioClip(clip, gainControl, buffer)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun play() {
        if (isPaused) {
            clip.start()
            isPaused = false
        } else if (!clip.isRunning) {
            clip.start()
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
        clip.close()
        currentFrame = 0
        isPaused = false
    }

    fun setVolume(gain: Float) {
        val minGain = control.minimum
        val maxGain = control.maximum
        control.value = gain.coerceIn(minGain, maxGain)
    }

    fun setSpeed(speed: Float) {
        playbackSpeed = speed.coerceIn(0.5f, 2.0f)
        // Для изменения скорости потребуется дополнительная реализация
    }

    fun applyEcho() {
        // Упрощенная реализация эффекта эха
        val format = clip.format
        val newData = originalData.copyOf()

        for (i in 0 until newData.size - 5000) {
            newData[i + 5000] = ((newData[i + 5000].toInt() + newData[i].toInt() * 0.5).toInt()).toByte()
        }

        clip.stop()
        clip.close()
        clip.open(format, newData, 0, newData.size)
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