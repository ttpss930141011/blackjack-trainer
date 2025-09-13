package org.ttpss930141011.bj.infrastructure.audio

import kotlinx.cinterop.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.ttpss930141011.bj.domain.services.SoundType
import platform.AVFoundation.*
import platform.Foundation.*
import blackjack_strategy_trainer.composeapp.generated.resources.Res

/**
 * iOS implementation of PlatformAudioPlayer using AVAudioPlayer
 * 
 * Uses iOS AVAudioPlayer API for audio playback with proper iOS audio session management.
 * Preloads all audio files during initialization for responsive playback.
 */
@OptIn(ExperimentalResourceApi::class, ExperimentalForeignApi::class)
actual class PlatformAudioPlayer {
    
    private var audioPlayers: Map<SoundType, AVAudioPlayer> = emptyMap()
    private var currentVolume: Float = 0.8f
    
    actual suspend fun initialize(): Boolean = withContext(Dispatchers.Main) {
        try {
            // Configure audio session for game audio
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(AVAudioSessionCategoryAmbient, null)
            audioSession.setActive(true, null)
            
            val players = mutableMapOf<SoundType, AVAudioPlayer>()
            
            // Load each sound file as AVAudioPlayer
            players[SoundType.CARD_DEAL] = createAudioPlayer("files/sounds/card.m4a")
            players[SoundType.CORRECT_FEEDBACK] = createAudioPlayer("files/sounds/correct.mp3")
            players[SoundType.WRONG_FEEDBACK] = createAudioPlayer("files/sounds/wrong.mp3")
            
            audioPlayers = players
            
            // Set initial volume
            setVolume(currentVolume)
            
            true
        } catch (e: Exception) {
            println("Failed to initialize iOS audio player: ${e.message}")
            false
        }
    }
    
    actual suspend fun playSound(soundType: SoundType): Boolean = withContext(Dispatchers.Main) {
        try {
            val audioPlayer = audioPlayers[soundType]
            if (audioPlayer != null) {
                // Stop current playback if running and restart
                if (audioPlayer.playing) {
                    audioPlayer.stop()
                }
                audioPlayer.currentTime = 0.0
                audioPlayer.play()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("Failed to play sound $soundType on iOS: ${e.message}")
            false
        }
    }
    
    actual fun release() {
        audioPlayers.values.forEach { player ->
            try {
                if (player.playing) {
                    player.stop()
                }
                // AVAudioPlayer doesn't need explicit release in modern iOS
            } catch (e: Exception) {
                println("Error releasing AVAudioPlayer: ${e.message}")
            }
        }
        audioPlayers = emptyMap()
    }
    
    actual fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0.0f, 1.0f)
        audioPlayers.values.forEach { player ->
            try {
                player.volume = currentVolume
            } catch (e: Exception) {
                println("Error setting volume on iOS: ${e.message}")
            }
        }
    }
    
    private suspend fun createAudioPlayer(resourcePath: String): AVAudioPlayer {
        try {
            // Load resource from compose resources
            val resourceBytes = Res.readBytes(resourcePath)
            val nsData = resourceBytes.toNSData()
            
            memScoped {
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                val audioPlayer = AVAudioPlayer(nsData, null, errorPtr.ptr)
                
                val error = errorPtr.value
                if (error != null) {
                    println("Audio player initialization error: ${error.localizedDescription}")
                }
                
                audioPlayer?.prepareToPlay()
                return audioPlayer ?: throw Exception("Failed to create AVAudioPlayer")
            }
        } catch (e: Exception) {
            println("Failed to load audio resource $resourcePath: ${e.message}")
            
            // Fallback: create a minimal silent player
            val silenceData = ByteArray(44) // Minimal wave header
            val nsData = silenceData.toNSData()
            
            memScoped {
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()
                val audioPlayer = AVAudioPlayer(nsData, null, errorPtr.ptr)
                audioPlayer?.prepareToPlay()
                return audioPlayer ?: throw Exception("Failed to create fallback AVAudioPlayer")
            }
        }
    }
}

/**
 * Extension function to convert ByteArray to NSData
 */
private fun ByteArray.toNSData(): NSData {
    return this.usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = this.size.toULong())
    }
}