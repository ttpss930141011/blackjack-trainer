package org.ttpss930141011.bj.infrastructure.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.AudioAttributes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.ttpss930141011.bj.domain.services.SoundType
import org.ttpss930141011.bj.infrastructure.PlatformContext
import blackjack_strategy_trainer.composeapp.generated.resources.Res

/**
 * Android implementation of PlatformAudioPlayer using MediaPlayer
 * 
 * Uses Android MediaPlayer API for audio playback with proper resource management.
 * Preloads all audio files during initialization for responsive playback.
 */
@OptIn(ExperimentalResourceApi::class)
actual class PlatformAudioPlayer {
    
    private var mediaPlayers: Map<SoundType, MediaPlayer> = emptyMap()
    private var currentVolume: Float = 0.8f
    
    actual suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            val context = PlatformContext.get() as? Context
                ?: return@withContext false
            
            val players = mutableMapOf<SoundType, MediaPlayer>()
            
            // Initialize MediaPlayer for each sound type
            players[SoundType.CARD_DEAL] = createMediaPlayer(context, "files/sounds/card.m4a")
            players[SoundType.CORRECT_FEEDBACK] = createMediaPlayer(context, "files/sounds/correct.mp3")
            players[SoundType.WRONG_FEEDBACK] = createMediaPlayer(context, "files/sounds/wrong.mp3")
            
            mediaPlayers = players
            
            // Set initial volume
            setVolume(currentVolume)
            
            true
        } catch (e: Exception) {
            println("Failed to initialize Android audio player: ${e.message}")
            false
        }
    }
    
    actual suspend fun playSound(soundType: SoundType): Boolean = withContext(Dispatchers.IO) {
        try {
            val mediaPlayer = mediaPlayers[soundType]
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.seekTo(0) // Restart if already playing
                } else {
                    mediaPlayer.start()
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("Failed to play sound $soundType on Android: ${e.message}")
            false
        }
    }
    
    actual fun release() {
        mediaPlayers.values.forEach { player ->
            try {
                if (player.isPlaying) {
                    player.stop()
                }
                player.release()
            } catch (e: Exception) {
                println("Error releasing MediaPlayer: ${e.message}")
            }
        }
        mediaPlayers = emptyMap()
    }
    
    actual fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0.0f, 1.0f)
        mediaPlayers.values.forEach { player ->
            try {
                player.setVolume(currentVolume, currentVolume)
            } catch (e: Exception) {
                println("Error setting volume on Android: ${e.message}")
            }
        }
    }
    
    private suspend fun createMediaPlayer(context: Context, resourcePath: String): MediaPlayer {
        val mediaPlayer = MediaPlayer()
        
        try {
            // Set audio attributes for game sounds
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build()
            )
            
            // Load resource from compose resources
            val resourceBytes = Res.readBytes(resourcePath)
            val inputStream = resourceBytes.inputStream()
            
            // Create a temporary file descriptor from the input stream (API 24+ compatible)
            val tempFile = java.io.File.createTempFile("audio", ".tmp")
            tempFile.writeBytes(resourceBytes)
            
            mediaPlayer.setDataSource(tempFile.absolutePath)
            mediaPlayer.prepare()
            
            // Clean up temp file after preparation
            tempFile.delete()
            
        } catch (e: Exception) {
            println("Failed to load audio resource $resourcePath: ${e.message}")
            // Return a silent MediaPlayer that won't crash the game
        }
        
        return mediaPlayer
    }
}