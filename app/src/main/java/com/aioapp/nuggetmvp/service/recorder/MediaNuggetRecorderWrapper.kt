package com.aioapp.nuggetmvp.service.recorder

import android.annotation.SuppressLint
import android.media.MediaRecorder
import com.aioapp.nuggetmvp.service.constants.DEFAULT_BITRATE
import com.aioapp.nuggetmvp.service.constants.SAMPLE_RATE

@Suppress("DEPRECATION")
class MediaNuggetRecorderWrapper : NuggetRecorder {
    private var recorder = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setAudioEncodingBitRate(DEFAULT_BITRATE)
        setAudioSamplingRate(SAMPLE_RATE)
    }

    override fun setOutputFile(path: String) {
        recorder.setOutputFile(path)
    }

    override fun prepare() {
        recorder.prepare()
    }

    override fun start() {
        recorder.start()
    }

    override fun stop() {
        recorder.stop()
    }

    @SuppressLint("NewApi")
    override fun pause() {
        recorder.pause()
    }

    @SuppressLint("NewApi")
    override fun resume() {
        recorder.resume()
    }

    override fun release() {
        recorder.release()
    }

    override fun getMaxAmplitude(): Int {
        return recorder.maxAmplitude
    }
}
