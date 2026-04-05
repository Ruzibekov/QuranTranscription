package com.ruzibekov.quran.transcription.ui.screens.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ruzibekov.quran.transcription.ui.theme.PrimaryGreenLight
import kotlinx.coroutines.delay

@Composable
fun AudioPlayerBar(
    audioUrl: String,
    savedPositionMs: Long,
    onSavePosition: (Long) -> Unit,
    modifier: Modifier = Modifier,
    verseCount: Int = 0,
    onCurrentAyahChanged: ((Int) -> Unit)? = null,
    seekToAyahIndex: Int = -1,
    onSeekHandled: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var lastAyahIndex by remember { mutableIntStateOf(-1) }

    val player = remember(audioUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(audioUrl))
            prepare()
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isLoading = playbackState == Player.STATE_BUFFERING
                hasError = playbackState == Player.STATE_IDLE && player.playerError != null
                if (playbackState == Player.STATE_READY) {
                    duration = player.duration.coerceAtLeast(0L)
                }
                if (playbackState == Player.STATE_ENDED) {
                    isPlaying = false
                    onSavePosition(0L)
                    lastAyahIndex = -1
                    onCurrentAyahChanged?.invoke(-1)
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
                if (!playing && player.playbackState != Player.STATE_ENDED) {
                    onSavePosition(player.currentPosition)
                }
            }
        }
        player.addListener(listener)

        onDispose {
            onSavePosition(player.currentPosition)
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(player, savedPositionMs) {
        if (savedPositionMs > 0L) {
            player.seekTo(savedPositionMs)
        }
    }

    LaunchedEffect(seekToAyahIndex) {
        if (seekToAyahIndex >= 0 && verseCount > 0 && duration > 0L) {
            val targetMs = (seekToAyahIndex.toLong() * duration / verseCount)
            player.seekTo(targetMs)
            if (!isPlaying) player.play()
            onSeekHandled?.invoke()
        }
    }

    LaunchedEffect(player, isPlaying) {
        while (isPlaying) {
            if (!isDragging) {
                currentPosition = player.currentPosition.coerceAtLeast(0L)
                if (duration > 0L) {
                    sliderPosition = currentPosition.toFloat() / duration.toFloat()
                    if (verseCount > 0 && onCurrentAyahChanged != null) {
                        val newIndex = (currentPosition * verseCount / duration)
                            .toInt()
                            .coerceIn(0, verseCount - 1)
                        if (newIndex != lastAyahIndex) {
                            lastAyahIndex = newIndex
                            onCurrentAyahChanged(newIndex)
                        }
                    }
                }
            }
            delay(200L)
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(5000L)
            onSavePosition(player.currentPosition)
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 6.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Play button with gradient
            AnimatedContent(
                targetState = when {
                    isLoading -> PlayerButtonState.LOADING
                    hasError -> PlayerButtonState.ERROR
                    isPlaying -> PlayerButtonState.PLAYING
                    else -> PlayerButtonState.PAUSED
                },
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "player_button",
            ) { state ->
                when (state) {
                    PlayerButtonState.LOADING -> {
                        Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                strokeWidth = 2.5.dp,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    PlayerButtonState.ERROR -> {
                        Surface(
                            onClick = {
                                hasError = false
                                player.prepare()
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(6.dp, CircleShape),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Replay,
                                    contentDescription = "Қайта уриниш",
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                        }
                    }
                    PlayerButtonState.PLAYING -> {
                        Surface(
                            onClick = { player.pause() },
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(6.dp, CircleShape),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Pause,
                                    contentDescription = "Тўхтатиш",
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                        }
                    }
                    PlayerButtonState.PAUSED -> {
                        Surface(
                            onClick = { player.play() },
                            modifier = Modifier
                                .size(48.dp)
                                .shadow(6.dp, CircleShape),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Тинглаш",
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                        }
                    }
                }
            }

            // Progress + time
            Column(
                modifier = Modifier.weight(1f),
            ) {
                if (isLoading) {
                    Text(
                        text = "Юкланмоқда...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                } else if (hasError) {
                    Text(
                        text = "Audio юкланмади",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                Slider(
                    value = sliderPosition,
                    onValueChange = { value ->
                        isDragging = true
                        sliderPosition = value
                        currentPosition = (value * duration).toLong()
                    },
                    onValueChangeFinished = {
                        isDragging = false
                        player.seekTo(currentPosition)
                        onSavePosition(currentPosition)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = formatTime(currentPosition),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    if (verseCount > 0 && lastAyahIndex >= 0) {
                        Text(
                            text = "Оят ${lastAyahIndex + 1}/$verseCount",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Text(
                        text = formatTime(duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
            }
        }
    }
}

private enum class PlayerButtonState {
    LOADING, ERROR, PLAYING, PAUSED
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
