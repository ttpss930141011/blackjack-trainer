package org.ttpss930141011.bj.infrastructure.audio

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.ttpss930141011.bj.domain.services.SoundType
import javax.sound.sampled.*
import blackjack_strategy_trainer.composeapp.generated.resources.Res

/**
 * JVM (Desktop) implementation of PlatformAudioPlayer using Java Sound API
 * 
 * Uses javax.sound.sampled for cross-platform desktop audio playback.
 * Preloads audio clips for responsive playback performance.
 */
@OptIn(ExperimentalResourceApi::class)
actual class PlatformAudioPlayer {
    
    private var audioClips: Map<SoundType, Clip> = emptyMap()
    private var currentVolume: Float = 0.8f
    
    actual suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            val clips = mutableMapOf<SoundType, Clip>()
            
            // Load each sound file as a Clip
            clips[SoundType.CARD_DEAL] = loadAudioClip("files/sounds/card.m4a")
            clips[SoundType.CORRECT_FEEDBACK] = loadAudioClip("files/sounds/correct.mp3")
            clips[SoundType.WRONG_FEEDBACK] = loadAudioClip("files/sounds/wrong.mp3")
            
            audioClips = clips
            
            // Set initial volume
            setVolume(currentVolume)
            
            true
        } catch (e: Exception) {
            println("Failed to initialize JVM audio player: ${e.message}")
            false
        }
    }
    
    actual suspend fun playSound(soundType: SoundType): Boolean = withContext(Dispatchers.IO) {
        try {
            val clip = audioClips[soundType]
            if (clip != null) {
                // Reset to beginning and play
                clip.framePosition = 0
                clip.start()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("Failed to play sound $soundType on JVM: ${e.message}")
            false
        }
    }
    
    actual fun release() {
        audioClips.values.forEach { clip ->
            try {
                if (clip.isRunning) {
                    clip.stop()
                }
                clip.close()
            } catch (e: Exception) {
                println("Error releasing audio clip: ${e.message}")
            }
        }
        audioClips = emptyMap()
    }
    
    actual fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0.0f, 1.0f)
        audioClips.values.forEach { clip ->
            try {
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    val gainControl = clip.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
                    val range = gainControl.maximum - gainControl.minimum
                    val gain = (range * currentVolume) + gainControl.minimum
                    gainControl.value = gain
                }
            } catch (e: Exception) {
                println("Error setting volume on JVM: ${e.message}")
            }
        }
    }
    
    private suspend fun loadAudioClip(resourcePath: String): Clip {
        val clip = AudioSystem.getClip()
        
        try {
            // Load resource from compose resources
            val resourceBytes = Res.readBytes(resourcePath)
            val inputStream = resourceBytes.inputStream()
            val audioInputStream = AudioSystem.getAudioInputStream(inputStream)
            
            clip.open(audioInputStream)
            
        } catch (e: Exception) {
            println("Failed to load audio resource $resourcePath: ${e.message}")
            // Return uninitialized clip that won't crash the game
        }
        
        return clip
    }
}