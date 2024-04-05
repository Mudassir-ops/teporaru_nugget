package com.aioapp.nuggetmvp.service.recorder

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.assemblyai.api.RealtimeTranscriber
import com.assemblyai.api.resources.realtime.types.FinalTranscript
import com.assemblyai.api.resources.realtime.types.PartialTranscript
import com.assemblyai.api.resources.realtime.types.SessionBegins

object RealtimeTranscriberManager {
    private var realtimeTranscriber: RealtimeTranscriber? = null
    private var thread: Thread? = null

    @SuppressLint("MissingPermission")
    fun startTranscription(
        apiKey: String,
        onSessionStarted: (String) -> Unit,
        onPartialTranscript: (String) -> Unit,
        onFinalTranscript: (String) -> Unit
    ) {
        if (realtimeTranscriber != null) {
            return
        }
        thread = Thread {
            try {
                realtimeTranscriber = RealtimeTranscriber.builder().apiKey(apiKey).sampleRate(16000)
                    .onSessionBegins { sessionBegins: SessionBegins ->
                        onSessionStarted("Session opened with ID: " + sessionBegins.sessionId)
                    }.onPartialTranscript { transcript: PartialTranscript ->
                        if (transcript.text.isNotEmpty()) {
                            onPartialTranscript(transcript.text)
                        }
                    }.onFinalTranscript { transcript: FinalTranscript ->
                        onFinalTranscript(transcript.text)
                        stopTranscription()
                    }.onError { exp: Throwable ->
                        exp.printStackTrace()
                    }.wordBoost(
                        listOf(
                            "drinks",
                            "food",
                            "caesar",
                            "wedge",
                            "caprese",
                            "pork",
                            "fish",
                            "beef",
                            "salmon",
                            "steak",
                            "chicken",
                            "pina colada",
                            "mojito",
                            "margarita",
                            "mile high",
                            "coke",
                            "maverick",
                            "wingman",
                            "martini",
                            "iceman",
                            "fudge",
                            "brownie",
                            "cheesecake",
                            "salad",
                            "nugget",
                            "meh"
                        )
                    ).build()

                println("Connecting to real-time transcript service")
                realtimeTranscriber?.connect()
                val bufferSize = AudioRecord.getMinBufferSize(
                    16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
                )
                val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    16000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
                )
                audioRecord.startRecording()
                val data = ByteArray(bufferSize)
                while (!Thread.currentThread().isInterrupted) {
                    audioRecord.read(data, 0, data.size)
                    realtimeTranscriber?.sendAudio(data)
                }
                println("Stopping recording")
                audioRecord.stop()
                audioRecord.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread?.start()
    }

    private fun stopTranscription() {
        realtimeTranscriber?.close()
        thread?.interrupt()
        realtimeTranscriber = null
        thread = null
    }
}
