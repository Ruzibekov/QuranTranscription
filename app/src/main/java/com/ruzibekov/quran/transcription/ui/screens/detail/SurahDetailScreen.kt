package com.ruzibekov.quran.transcription.ui.screens.detail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruzibekov.quran.transcription.ui.theme.PrimaryGreenLight
import com.ruzibekov.quran.transcription.ui.theme.TextMutedColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: (Int) -> Unit,
    viewModel: SurahDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val surah = uiState.surah
    val nextSurahId = uiState.nextSurahId
    val fontSizeSp = uiState.fontSizeSp
    val nextSurahName = uiState.nextSurahName
    val audioUrl = uiState.audioUrl
    val savedPositionMs = uiState.savedPositionMs
    val isFullScreen = uiState.isFullScreen
    val currentAyahIndex = uiState.currentAyahIndex
    val seekToAyahIndex = uiState.seekToAyahIndex

    // Fullscreen toggle animations
    val headerAlpha by animateFloatAsState(
        targetValue = if (isFullScreen) 0f else 1f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "header_alpha",
    )
    val headerScale by animateFloatAsState(
        targetValue = if (isFullScreen) 0.8f else 1f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "header_scale",
    )
    val headerHeight by animateFloatAsState(
        targetValue = if (isFullScreen) 0f else 1f,
        animationSpec = tween(350, easing = FastOutSlowInEasing),
        label = "header_height",
    )
    val translitCorner by animateDpAsState(
        targetValue = if (isFullScreen) 16.dp else 24.dp,
        animationSpec = tween(300),
        label = "translit_corner",
    )
    val translitPadding by animateDpAsState(
        targetValue = if (isFullScreen) 8.dp else 20.dp,
        animationSpec = tween(300),
        label = "translit_padding",
    )

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.07f),
            MaterialTheme.colorScheme.background,
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
    ) {
        Scaffold(
            containerColor = Color.Transparent,
        ) { innerPadding ->
            if (surah == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Сура топилмади",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            } else {
                val allLines = remember(surah) {
                    surah.transliteration.lines().filter { it.isNotBlank() }
                }
                val firstLine = allLines.firstOrNull()?.trim() ?: ""
                val isBismillah = firstLine.startsWith("Бисмиллааҳир", ignoreCase = true)
                val verseLines = if (isBismillah) allLines.drop(1) else allLines
                val verseCount = verseLines.size

                val enterAnim = remember { Animatable(0f) }
                LaunchedEffect(Unit) {
                    enterAnim.animateTo(
                        1f,
                        animationSpec = tween(400, easing = FastOutSlowInEasing),
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    // Top bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            onClick = {
                                if (isFullScreen) viewModel.toggleFullScreen()
                                else onNavigateBack()
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 1.dp,
                            modifier = Modifier.size(48.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isFullScreen) Icons.Filled.FullscreenExit
                                    else Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = if (isFullScreen) "Кичрайтириш" else "Орқага",
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = surah.latinName,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            if (!isFullScreen) {
                                Text(
                                    text = "Сура № ${surah.id}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedColor,
                                )
                            }
                        }

                        Surface(
                            onClick = { viewModel.toggleFullScreen() },
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shadowElevation = 1.dp,
                            modifier = Modifier.size(48.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isFullScreen) Icons.Filled.FullscreenExit
                                    else Icons.Filled.Fullscreen,
                                    contentDescription = if (isFullScreen) "Кичрайтириш" else "Катталаштириш",
                                    modifier = Modifier.size(22.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    // Header Card (animated collapse)
                    if (headerAlpha > 0.01f) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .graphicsLayer {
                                    alpha = headerAlpha * enterAnim.value
                                    scaleX = headerScale
                                    scaleY = headerScale
                                    translationY = (1f - headerHeight) * -50f
                                },
                            shape = RoundedCornerShape(28.dp),
                            shadowElevation = 8.dp,
                            color = Color.Transparent,
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                MaterialTheme.colorScheme.primary,
                                                PrimaryGreenLight,
                                                Color(0xFF1AAB95),
                                            ),
                                        ),
                                        shape = RoundedCornerShape(28.dp),
                                    )
                                    .padding(24.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.06f)),
                                )
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .align(Alignment.BottomStart)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.04f)),
                                )

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = Color.White.copy(alpha = 0.2f),
                                        ) {
                                            Text(
                                                text = "Маккий",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = surah.arabicName,
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 42.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Box(
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(2.dp)
                                            .background(
                                                Color.White.copy(alpha = 0.3f),
                                                RoundedCornerShape(1.dp),
                                            ),
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "Очилиш — ${surah.transliteration.lines().size} оят",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.85f),
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height((16 * headerHeight).dp))
                    }

                    // Audio Player (normal mode only)
                    if (!isFullScreen && audioUrl.isNotEmpty()) {
                        AudioPlayerBar(
                            audioUrl = audioUrl,
                            savedPositionMs = savedPositionMs,
                            onSavePosition = viewModel::saveAudioPosition,
                            verseCount = verseCount,
                            onCurrentAyahChanged = viewModel::updateCurrentAyah,
                            seekToAyahIndex = seekToAyahIndex,
                            onSeekHandled = viewModel::clearSeek,
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .graphicsLayer {
                                    alpha = enterAnim.value
                                },
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Transliteration Area (animated corners & padding)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = translitPadding)
                            .graphicsLayer {
                                alpha = enterAnim.value
                            },
                        shape = RoundedCornerShape(translitCorner),
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp,
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "ТРАНСЛИТЕРАЦИЯ",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        letterSpacing = 0.8.sp,
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Surface(
                                        onClick = { viewModel.decreaseFontSize() },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (fontSizeSp <= 14)
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(36.dp),
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "A-",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (fontSizeSp <= 14)
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                    Surface(
                                        onClick = { viewModel.increaseFontSize() },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (fontSizeSp >= 22)
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(36.dp),
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "A+",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (fontSizeSp >= 22)
                                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
                            )

                            val scrollState = rememberScrollState()

                            // Auto-scroll to active ayah
                            LaunchedEffect(currentAyahIndex) {
                                if (currentAyahIndex >= 0) {
                                    val estimatedOffset = currentAyahIndex * 200
                                    scrollState.animateScrollTo(
                                        estimatedOffset,
                                        animationSpec = tween(400),
                                    )
                                }
                            }

                            Box(modifier = Modifier.fillMaxSize()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(scrollState)
                                        .padding(horizontal = 18.dp, vertical = 16.dp),
                                ) {
                                    if (isBismillah) {
                                        Text(
                                            text = firstLine,
                                            fontSize = fontSizeSp.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Box(
                                            modifier = Modifier
                                                .width(40.dp)
                                                .height(1.5.dp)
                                                .align(Alignment.CenterHorizontally)
                                                .background(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                                    RoundedCornerShape(1.dp),
                                                ),
                                        )

                                        Spacer(modifier = Modifier.height(14.dp))
                                    }

                                    verseLines.forEachIndexed { index, verseLine ->
                                        val isActive = index == currentAyahIndex
                                        val bgColor by animateColorAsState(
                                            targetValue = if (isActive)
                                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                            else Color.Transparent,
                                            animationSpec = tween(250),
                                            label = "verse_bg_$index",
                                        )

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(bgColor)
                                                .clickable { viewModel.seekToAyah(index) }
                                                .padding(
                                                    horizontal = if (isActive) 4.dp else 0.dp,
                                                    vertical = if (isActive) 6.dp else 0.dp,
                                                ),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = if (isActive) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.primaryContainer,
                                                modifier = Modifier.size(28.dp),
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = "${index + 1}",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isActive) MaterialTheme.colorScheme.onPrimary
                                                        else MaterialTheme.colorScheme.onPrimaryContainer,
                                                    )
                                                }
                                            }
                                            Text(
                                                text = verseLine.trim(),
                                                fontSize = fontSizeSp.sp,
                                                lineHeight = (fontSizeSp + 12).sp,
                                                color = if (isActive) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.onSurface,
                                                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                                                modifier = Modifier.weight(1f),
                                            )
                                        }

                                        if (index < verseLines.lastIndex) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 10.dp, horizontal = 40.dp)
                                                    .height(0.5.dp)
                                                    .background(
                                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                                                    ),
                                            )
                                        }
                                    }
                                }

                                if (scrollState.canScrollBackward) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(24.dp)
                                            .align(Alignment.TopCenter)
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        MaterialTheme.colorScheme.surface,
                                                        Color.Transparent,
                                                    ),
                                                ),
                                            ),
                                    )
                                }

                                if (scrollState.canScrollForward) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(24.dp)
                                            .align(Alignment.BottomCenter)
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        MaterialTheme.colorScheme.surface,
                                                    ),
                                                ),
                                            ),
                                    )
                                }
                            }
                        }
                    }

                    // Bottom area: audio (fullscreen) or next button (normal)
                    if (isFullScreen && audioUrl.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        AudioPlayerBar(
                            audioUrl = audioUrl,
                            savedPositionMs = savedPositionMs,
                            onSavePosition = viewModel::saveAudioPosition,
                            verseCount = verseCount,
                            onCurrentAyahChanged = viewModel::updateCurrentAyah,
                            seekToAyahIndex = seekToAyahIndex,
                            onSeekHandled = viewModel::clearSeek,
                            modifier = Modifier
                                .padding(horizontal = 20.dp)
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .padding(bottom = 8.dp),
                        )
                    }

                    if (!isFullScreen) {
                        Spacer(modifier = Modifier.height(16.dp))

                        val btnInteraction = remember { MutableInteractionSource() }
                        val btnPressed by btnInteraction.collectIsPressedAsState()
                        val btnScale by animateFloatAsState(
                            targetValue = if (btnPressed) 0.96f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessHigh,
                            ),
                            label = "btn_scale",
                        )

                        Button(
                            onClick = { nextSurahId?.let(onNavigateNext) },
                            enabled = nextSurahId != null,
                            interactionSource = btnInteraction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .padding(bottom = 12.dp)
                                .height(52.dp)
                                .graphicsLayer {
                                    scaleX = btnScale
                                    scaleY = btnScale
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                                disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 2.dp,
                            ),
                        ) {
                            Text(
                                text = when {
                                    nextSurahName != null -> "Keyingisi — $nextSurahName"
                                    nextSurahId == null -> "Охирги сура"
                                    else -> "Keyingisi"
                                },
                                style = MaterialTheme.typography.titleMedium,
                            )
                            if (nextSurahId != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
