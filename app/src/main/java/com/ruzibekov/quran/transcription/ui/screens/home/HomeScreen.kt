package com.ruzibekov.quran.transcription.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.res.Configuration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ruzibekov.quran.transcription.data.Surah
import com.ruzibekov.quran.transcription.ui.theme.ArabicFontFamily
import kotlin.math.PI
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSurahSelected: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val haptic = LocalHapticFeedback.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.scrollToTop.collect {
            listState.animateScrollToItem(0)
        }
    }

    val isSearchVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 &&
                listState.firstVisibleItemScrollOffset < 100
        }
    }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .windowInsetsPadding(WindowInsets.statusBars),
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Spacer(modifier = Modifier.height(if (isLandscape) 4.dp else 8.dp))
                    if (isLandscape) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            Text(
                                text = "Қуръон суралар",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = if (uiState.searchQuery.isNotBlank() || uiState.selectedFilter == SurahFilter.FAVORITES)
                                    "${uiState.surahs.size} сура топилди"
                                else "114 сура",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                            )
                        }
                    } else {
                        Text(
                            text = "Қуръон суралар",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = if (uiState.searchQuery.isNotBlank() || uiState.selectedFilter == SurahFilter.FAVORITES)
                                "${uiState.surahs.size} сура топилди"
                            else "114 сура — ўзбек транслитерация",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    }

                    AnimatedVisibility(
                        visible = isSearchVisible || uiState.searchQuery.isNotBlank(),
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut(),
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::updateSearchQuery,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Сура номи бўйича қидириш",
                                color = MaterialTheme.colorScheme.outline,
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                            )
                        },
                        trailingIcon = {
                            AnimatedVisibility(
                                visible = uiState.searchQuery.isNotBlank(),
                                enter = fadeIn(animationSpec = tween(150)),
                                exit = fadeOut(animationSpec = tween(150)),
                            ) {
                                IconButton(
                                    onClick = {
                                        viewModel.updateSearchQuery("")
                                        focusManager.clearFocus()
                                    },
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Тозалаш",
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = { focusManager.clearFocus() },
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        ),
                    )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 8.dp),
                ) {
                    item {
                        FilterChip(
                            text = "Барчаси",
                            selected = uiState.selectedFilter == SurahFilter.ALL,
                            onClick = { viewModel.selectFilter(SurahFilter.ALL) },
                        )
                    }
                    item {
                        FilterChip(
                            text = "⭐ Севимлилар",
                            selected = uiState.selectedFilter == SurahFilter.FAVORITES,
                            onClick = { viewModel.selectFilter(SurahFilter.FAVORITES) },
                        )
                    }
                }

                if (uiState.surahs.isEmpty()) {
                    val isFavoritesFilter = uiState.selectedFilter == SurahFilter.FAVORITES

                    val emptyAnim = remember { Animatable(0f) }
                    LaunchedEffect(uiState.selectedFilter) {
                        emptyAnim.snapTo(0f)
                        emptyAnim.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .graphicsLayer {
                                alpha = emptyAnim.value
                                translationY = (1f - emptyAnim.value) * 40f
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                modifier = Modifier.size(88.dp),
                                shape = RoundedCornerShape(28.dp),
                                color = if (isFavoritesFilter) MaterialTheme.colorScheme.secondaryContainer
                                else MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isFavoritesFilter) Icons.Outlined.Star
                                        else Icons.Filled.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(40.dp),
                                        tint = if (isFavoritesFilter) MaterialTheme.colorScheme.secondary
                                        else MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (isFavoritesFilter) "Севимли суралар йўқ"
                                else "Натижа топилмади",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isFavoritesFilter) "⭐ белгисини босиб сура қўшинг"
                                else "Бошқа калит сўз билан қидириб кўринг",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            bottom = 24.dp,
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = uiState.surahs,
                            key = { surah -> surah.id },
                        ) { surah ->
                            val animatable = remember { Animatable(0f) }
                            LaunchedEffect(Unit) {
                                animatable.animateTo(
                                    1f,
                                    animationSpec = tween(
                                        durationMillis = 350,
                                        easing = androidx.compose.animation.core.FastOutSlowInEasing,
                                    ),
                                )
                            }
                            Box(
                                modifier = Modifier.graphicsLayer {
                                    alpha = animatable.value
                                    translationY = (1f - animatable.value) * 30f
                                },
                            ) {
                                SurahCard(
                                    surah = surah,
                                    isFavorite = uiState.favoriteIds.contains(surah.id),
                                    isListened = uiState.listenedIds.contains(surah.id),
                                    onClick = {
                                        focusManager.clearFocus()
                                        onSurahSelected(surah.id)
                                    },
                                    onToggleFavorite = {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        viewModel.toggleFavorite(surah.id)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(
            1.5.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
        ),
        shadowElevation = if (selected) 2.dp else 0.dp,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun SurahCard(
    surah: Surah,
    isFavorite: Boolean,
    isListened: Boolean = false,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val containerColor = MaterialTheme.colorScheme.primaryContainer
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale = androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        label = "card_scale",
    )

    val favScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clip(RoundedCornerShape(18.dp))
            .drawBehind {
                val borderColor = if (isFavorite) secondaryColor else primaryColor
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            borderColor.copy(alpha = 0.3f),
                            borderColor.copy(alpha = 0.05f),
                        ),
                    ),
                    size = Size(3.5.dp.toPx(), size.height),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
                )
            },
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        onClick = onClick,
        interactionSource = interactionSource,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.size(48.dp)) {
                    drawSurahNumberOrnament(containerColor, primaryColor)
                }
                Text(
                    text = "${surah.id}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = surah.latinName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = surah.arabicName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = ArabicFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                        ),
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }

                val snippet = surah.transliteration
                    .lineSequence()
                    .firstOrNull()
                    ?.trim()
                    ?.take(60)

                if (!snippet.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = snippet + if (surah.transliteration.length > snippet.length) "…" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val ayahCount = surah.transliteration.lines()
                        .count { it.isNotBlank() }
                        .let { count ->
                            val first = surah.transliteration.lines().firstOrNull()?.trim() ?: ""
                            if (first.startsWith("Бисмиллааҳир", ignoreCase = true)) count - 1 else count
                        }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                    ) {
                        Text(
                            text = "$ayahCount оят",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (surah.revelationType == "Маданий")
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    ) {
                        Text(
                            text = surah.revelationType,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (surah.revelationType == "Маданий")
                                MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        )
                    }
                    if (isListened) {
                        Icon(
                            imageVector = Icons.Filled.Headphones,
                            contentDescription = "Тинглаган",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    scope.launch {
                        favScale.animateTo(
                            1.3f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium,
                            ),
                        )
                        favScale.animateTo(
                            1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium,
                            ),
                        )
                    }
                    onToggleFavorite()
                },
                modifier = Modifier
                    .size(44.dp)
                    .scale(favScale.value)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isFavorite) MaterialTheme.colorScheme.secondaryContainer
                        else Color.Transparent,
                    ),
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = if (isFavorite) "Sevimlidan chiqarish" else "Sevimlilarga qo'shish",
                    tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

private fun DrawScope.drawSurahNumberOrnament(
    containerColor: Color,
    primaryColor: Color,
) {
    val cx = size.width / 2
    val cy = size.height / 2
    val outerRadius = size.width / 2 - 1
    val innerRadius = outerRadius * 0.78f

    drawCircle(
        color = containerColor,
        radius = innerRadius,
        center = Offset(cx, cy),
    )
    drawCircle(
        color = primaryColor.copy(alpha = 0.3f),
        radius = innerRadius,
        center = Offset(cx, cy),
        style = Stroke(width = 1f),
    )

    val starPath = Path()
    val points = 8
    for (i in 0 until points * 2) {
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = (i * PI / points - PI / 2).toFloat()
        val x = cx + radius * cos(angle)
        val y = cy + radius * sin(angle)
        if (i == 0) starPath.moveTo(x, y)
        else starPath.lineTo(x, y)
    }
    starPath.close()

    drawPath(
        path = starPath,
        color = containerColor.copy(alpha = 0.6f),
    )
    drawPath(
        path = starPath,
        color = primaryColor.copy(alpha = 0.25f),
        style = Stroke(width = 0.8f),
    )
}
