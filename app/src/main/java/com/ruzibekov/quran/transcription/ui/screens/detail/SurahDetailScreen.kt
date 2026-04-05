package com.ruzibekov.quran.transcription.ui.screens.detail

import android.content.res.Configuration
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruzibekov.quran.transcription.ui.theme.ArabicFontFamily
import com.ruzibekov.quran.transcription.ui.theme.PrimaryGreenLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: (Int) -> Unit,
    viewModel: SurahDetailViewModel = hiltViewModel(),
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

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
        targetValue = if (isFullScreen) 0.dp else 24.dp,
        animationSpec = tween(300),
        label = "translit_corner",
    )
    val translitPadding by animateDpAsState(
        targetValue = if (isFullScreen) 0.dp else 20.dp,
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
            contentWindowInsets = WindowInsets(0),
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

                // Content padding values based on mode
                val contentHPad = if (isFullScreen) 12.dp else 18.dp
                val contentVPad = if (isFullScreen) 8.dp else 16.dp
                val verseSepPad = if (isFullScreen) 6.dp else 10.dp

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .windowInsetsPadding(WindowInsets.statusBars),
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
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Орқага",
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
                            if (!isFullScreen && !isLandscape) {
                                Text(
                                    text = "Сура № ${surah.id}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline,
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

                    // Header Card (hidden in landscape/fullscreen)
                    if (headerAlpha > 0.01f && !isLandscape) {
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
                            val isMadaniy = surah.revelationType == "Маданий"
                            val headerGradient = if (isMadaniy) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0A5C6B),
                                        Color(0xFF148A91),
                                        Color(0xFF1AABB0),
                                    ),
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        PrimaryGreenLight,
                                        Color(0xFF1AAB95),
                                    ),
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = headerGradient,
                                        shape = RoundedCornerShape(28.dp),
                                    )
                                    .padding(18.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(100.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.06f)),
                                )
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
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
                                                text = surah.revelationType,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = surah.arabicName,
                                        fontFamily = ArabicFontFamily,
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 42.sp,
                                        color = Color.White,
                                        textAlign = TextAlign.Center,
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Box(
                                        modifier = Modifier
                                            .width(50.dp)
                                            .height(2.dp)
                                            .background(
                                                Color.White.copy(alpha = 0.3f),
                                                RoundedCornerShape(1.dp),
                                            ),
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "Очилиш — $verseCount оят",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.85f),
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height((12 * headerHeight).dp))
                    }

                    // Transliteration Area
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
                        shadowElevation = if (isFullScreen) 0.dp else 4.dp,
                    ) {
                        Column {
                            // Header row — hide in fullscreen, show A-/A+ in a compact row
                            if (!isFullScreen) {
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
                                            letterSpacing = 0.5.sp,
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    FontSizeControls(
                                        fontSizeSp = fontSizeSp,
                                        onDecrease = viewModel::decreaseFontSize,
                                        onIncrease = viewModel::increaseFontSize,
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)),
                                )
                            } else {
                                // Compact controls in fullscreen
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    FontSizeControls(
                                        fontSizeSp = fontSizeSp,
                                        onDecrease = viewModel::decreaseFontSize,
                                        onIncrease = viewModel::increaseFontSize,
                                    )
                                }
                            }

                            // LazyColumn for precise scroll-to-item
                            val lazyListState = rememberLazyListState()

                            // Auto-scroll to active ayah — exact position
                            LaunchedEffect(currentAyahIndex) {
                                if (currentAyahIndex >= 0) {
                                    val targetItem = if (isBismillah) currentAyahIndex + 1 else currentAyahIndex
                                    lazyListState.animateScrollToItem(
                                        index = targetItem.coerceAtMost(verseCount - 1 + if (isBismillah) 1 else 0),
                                    )
                                }
                            }

                            Box(modifier = Modifier.fillMaxSize()) {
                                LazyColumn(
                                    state = lazyListState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = contentHPad, vertical = contentVPad),
                                ) {
                                    // Bismillah item
                                    if (isBismillah) {
                                        item(key = "bismillah") {
                                            if (isFullScreen) {
                                                // Compact bismillah in fullscreen
                                                Text(
                                                    text = firstLine,
                                                    fontSize = fontSizeSp.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    textAlign = TextAlign.Center,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 8.dp),
                                                )
                                            } else {
                                                Surface(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 16.dp),
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                    ) {
                                                        Text(
                                                            text = "\u0628\u0650\u0633\u0652\u0645\u0650 \u0627\u0644\u0644\u0651\u0647\u0650 \u0627\u0644\u0631\u0651\u064E\u062D\u0652\u0645\u064E\u0646\u0650 \u0627\u0644\u0631\u0651\u064E\u062D\u0650\u064A\u0645\u0650",
                                                            fontFamily = ArabicFontFamily,
                                                            fontSize = 22.sp,
                                                            fontWeight = FontWeight.Normal,
                                                            lineHeight = 34.sp,
                                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier.fillMaxWidth(),
                                                        )
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .width(60.dp)
                                                                .height(2.dp)
                                                                .background(
                                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                                    RoundedCornerShape(1.dp),
                                                                ),
                                                        )
                                                        Spacer(modifier = Modifier.height(6.dp))
                                                        Text(
                                                            text = firstLine,
                                                            fontSize = fontSizeSp.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            textAlign = TextAlign.Center,
                                                            modifier = Modifier.fillMaxWidth(),
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Verse items
                                    itemsIndexed(
                                        items = verseLines,
                                        key = { index, _ -> "verse_$index" },
                                    ) { index, verseLine ->
                                        val isActive = index == currentAyahIndex
                                        val bgColor by animateColorAsState(
                                            targetValue = if (isActive)
                                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                            else Color.Transparent,
                                            animationSpec = tween(250),
                                            label = "verse_bg_$index",
                                        )

                                        Column {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(bgColor)
                                                    .clickable(
                                                        interactionSource = remember { MutableInteractionSource() },
                                                        indication = androidx.compose.material3.ripple(
                                                            bounded = true,
                                                            color = MaterialTheme.colorScheme.primary,
                                                        ),
                                                    ) { viewModel.seekToAyah(index) }
                                                    .padding(
                                                        horizontal = if (isActive) 4.dp else 0.dp,
                                                        vertical = if (isActive) 4.dp else 0.dp,
                                                    ),
                                                horizontalArrangement = Arrangement.spacedBy(if (isFullScreen) 10.dp else 14.dp),
                                            ) {
                                                Surface(
                                                    shape = CircleShape,
                                                    color = if (isActive) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.primaryContainer,
                                                    modifier = Modifier.size(if (isFullScreen) 28.dp else 32.dp),
                                                ) {
                                                    Box(
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentAlignment = Alignment.Center,
                                                    ) {
                                                        Text(
                                                            text = "${index + 1}",
                                                            fontSize = if (isFullScreen) 11.sp else 13.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isActive) MaterialTheme.colorScheme.onPrimary
                                                            else MaterialTheme.colorScheme.onPrimaryContainer,
                                                            textAlign = TextAlign.Center,
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = verseLine.trim(),
                                                    fontSize = fontSizeSp.sp,
                                                    lineHeight = (fontSizeSp + if (isFullScreen) 8 else 12).sp,
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
                                                        .padding(vertical = verseSepPad),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth(0.6f)
                                                            .height(1.dp)
                                                            .background(
                                                                brush = Brush.horizontalGradient(
                                                                    colors = listOf(
                                                                        Color.Transparent,
                                                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                                                                        Color.Transparent,
                                                                    ),
                                                                ),
                                                            ),
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Top fade
                                val canScrollBack by remember {
                                    derivedStateOf { lazyListState.canScrollBackward }
                                }
                                if (canScrollBack) {
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

                                // Bottom fade
                                val canScrollFwd by remember {
                                    derivedStateOf { lazyListState.canScrollForward }
                                }
                                if (canScrollFwd) {
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

                    // Bottom area: Next button + Audio player
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(bottom = 8.dp),
                    ) {
                        if (!isFullScreen && nextSurahId != null) {
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

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { onNavigateNext(nextSurahId) },
                                interactionSource = btnInteraction,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .height(46.dp)
                                    .graphicsLayer {
                                        scaleX = btnScale
                                        scaleY = btnScale
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 4.dp,
                                    pressedElevation = 1.dp,
                                ),
                            ) {
                                Text(
                                    text = "Keyingisi — ${nextSurahName ?: ""}",
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (audioUrl.isNotEmpty()) {
                            AudioPlayerBar(
                                audioUrl = audioUrl,
                                savedPositionMs = savedPositionMs,
                                onSavePosition = viewModel::saveAudioPosition,
                                verseCount = verseCount,
                                onCurrentAyahChanged = viewModel::updateCurrentAyah,
                                seekToAyahIndex = seekToAyahIndex,
                                onSeekHandled = viewModel::clearSeek,
                                modifier = Modifier
                                    .padding(horizontal = if (isFullScreen) 8.dp else 20.dp)
                                    .graphicsLayer {
                                        alpha = enterAnim.value
                                    },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FontSizeControls(
    fontSizeSp: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            onClick = onDecrease,
            shape = RoundedCornerShape(10.dp),
            color = if (fontSizeSp <= 14)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(34.dp),
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
            onClick = onIncrease,
            shape = RoundedCornerShape(10.dp),
            color = if (fontSizeSp >= 22)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(34.dp),
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
