package com.aioapp.nuggetmvp.service.recorder

interface NuggetRecorder {
    fun setOutputFile(path: String)
    fun prepare()
    fun start()
    fun stop()
    fun pause()
    fun resume()
    fun release()
    fun getMaxAmplitude(): Int
}