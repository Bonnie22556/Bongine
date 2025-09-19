package org.bon26.engine

class Timer {
    private var startTime: Long = 0
    private var pausedTime: Long = 0
    private var isRunning: Boolean = true

    fun start() {
        startTime = System.nanoTime()
        isRunning = true
    }

    fun pause() {
        if (isRunning) {
            pausedTime = System.nanoTime() - startTime
            isRunning = false
        }
    }

    fun resume() {
        if (!isRunning) {
            startTime = System.nanoTime() - pausedTime
            isRunning = true
        }
    }

    fun getElapsedMillis(): Double {
        return if (isRunning) {
            (System.nanoTime() - startTime) / 1_000_000.0
        } else {
            pausedTime / 1_000_000.0
        }
    }
}
