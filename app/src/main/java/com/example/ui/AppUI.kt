package com.example.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import android.util.Log
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoFriendApp(
    viewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val stats by viewModel.userStats.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            CustomBottomNavigationBar(
                selectedTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Eco,
                            contentDescription = "EcoFriend Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EcoFriend",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            fontFamily = FontFamily.Serif
                        )
                    }
                },
                actions = {
                    stats?.let { s ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Stars,
                                contentDescription = "Points Medal",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${s.greenPoints} GP",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Lvl ${s.level}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    AppTab.HOME -> HomeTab(viewModel = viewModel)
                    AppTab.SCAN -> ScanTab(viewModel = viewModel)
                    AppTab.CHAT -> ChatTab(viewModel = viewModel)
                    AppTab.GROWTH -> GrowthTab(viewModel = viewModel)
                    AppTab.COMMUNITY -> CommunityTab(viewModel = viewModel)
                    AppTab.PROFILE -> ProfileTab(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun CustomBottomNavigationBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = Modifier.navigationBarsPadding()
    ) {
        val items = listOf(
            NavigationItem(AppTab.HOME, Icons.Rounded.Home, "Home"),
            NavigationItem(AppTab.SCAN, Icons.Rounded.QrCodeScanner, "Scan"),
            NavigationItem(AppTab.CHAT, Icons.Rounded.Forum, "PrakritiMitra"),
            NavigationItem(AppTab.GROWTH, Icons.Rounded.SsidChart, "Growth"),
            NavigationItem(AppTab.COMMUNITY, Icons.Rounded.FamilyRestroom, "Community"),
            NavigationItem(AppTab.PROFILE, Icons.Rounded.Person, "Profile")
        )

        items.forEach { item ->
            val isSelected = selectedTab == item.tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF2E7D32),
                    selectedTextColor = Color(0xFF2E7D32),
                    indicatorColor = Color(0xFFE8F5E9),
                    unselectedIconColor = Color(0xFF94A3B8),
                    unselectedTextColor = Color(0xFF94A3B8)
                ),
                modifier = Modifier.testTag("nav_tab_${item.tab.name.lowercase()}")
            )
        }
    }
}

data class NavigationItem(
    val tab: AppTab,
    val icon: ImageVector,
    val label: String
)

// ==========================================
// 🏠 HOME TAB
// ==========================================
@Composable
fun HomeTab(viewModel: AppViewModel) {
    val scrollState = rememberScrollState()
    val recommendationRs by viewModel.recommendationResult.collectAsStateWithLifecycle()
    val isRecommending by viewModel.isRecommending.collectAsStateWithLifecycle()
    val stats by viewModel.userStats.collectAsStateWithLifecycle()

    var showQuickRecDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming card (Sleek Theme with organic background decorations)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(28.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        // - "absolute -right-4 -bottom-4 w-32 h-32 bg-[#81C784] opacity-20 rounded-full"
                        drawCircle(
                            color = Color(0xFF81C784).copy(alpha = 0.20f),
                            radius = 64.dp.toPx(),
                            center = Offset(size.width + 16.dp.toPx(), size.height + 16.dp.toPx())
                        )
                        // - "absolute right-6 top-6 w-16 h-16 bg-[#4FC3F7] opacity-10 rounded-full"
                        drawCircle(
                            color = Color(0xFF4FC3F7).copy(alpha = 0.10f),
                            radius = 32.dp.toPx(),
                            center = Offset(size.width - 24.dp.toPx(), 24.dp.toPx())
                        )
                    }
                    .padding(20.dp)
            ) {
                // Background Emoji Decoration: "absolute right-4 bottom-12 text-6xl opacity-20: 🌿"
                Text(
                    text = "🌿",
                    fontSize = 64.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 10.dp, y = 20.dp)
                        .scale(1.2f)
                        .alpha(0.2f)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "AI RECOMMENDATION",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                        )
                    }

                    Text(
                        text = "Season Recommendation: Tulsi",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )

                    Text(
                        text = "Ideal climate conditions detected. Great for medicinal use and air purification in small balconys.",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth(0.75f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { showQuickRecDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD54F),
                            contentColor = Color(0xFF795548)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("home_recommend_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = "AI Recommender",
                            tint = Color(0xFF795548)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Plant Recommender",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Real-time Weather Intelligence card
        Text(
            text = "🌦 Weather Intelligence",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        WeatherIntelligenceCard()

        // Quick Stats / Sensors grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: Soil Health
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(112.dp)
                    .shadow(1.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)) // border-slate-100
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Soil Health",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B), // text-slate-500
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Optimal",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF16A34A), // text-green-600
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "84%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B) // text-slate-800
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9)) // bg-slate-100
                                .align(Alignment.CenterVertically)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.84f)
                                    .background(Color(0xFF81C784)) // bg-[#81C784]
                            )
                        }
                    }
                }
            }

            // Card 2: Growth Score
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(112.dp)
                    .shadow(1.dp, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)) // border-slate-100
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Growth Score",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B), // text-slate-500
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "+4.2%",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF3B82F6), // text-blue-500
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "7.2",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B) // text-slate-800
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "/10",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8), // text-slate-400
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.padding(bottom = 2.dp)
                        ) {
                            Box(modifier = Modifier.size(width = 6.dp, height = 12.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF4FC3F7).copy(alpha = 0.3f)))
                            Box(modifier = Modifier.size(width = 6.dp, height = 18.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF4FC3F7).copy(alpha = 0.5f)))
                            Box(modifier = Modifier.size(width = 6.dp, height = 24.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF4FC3F7).copy(alpha = 0.8f)))
                            Box(modifier = Modifier.size(width = 6.dp, height = 16.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF4FC3F7)))
                        }
                    }
                }
            }
        }

        // AI Companion Entry (PrakritiMitra Shortcut Component)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.selectTab(AppTab.CHAT)
                    viewModel.sendChatMessage("How do I fix yellowing leaves?")
                }
                .shadow(1.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)) // border-slate-100
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Robot Icon Square
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)) // bg-slate-50
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)), // border-slate-100
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🤖", fontSize = 24.sp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PrakritiMitra AI",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8), // text-slate-400
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "\"How do I fix yellowing leaves?\"",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF334155) // text-slate-700
                        )
                    )
                }

                // Voice microphone icon button action indicator
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF4FC3F7).copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎤", fontSize = 16.sp)
                }
            }
        }

        // Gamification Preview (achievement card with dashed brown border)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .drawBehind {
                    val stroke = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                            floatArrayOf(10f, 10f), 0f
                        )
                    )
                    drawRoundRect(
                        color = Color(0xFF795548).copy(alpha = 0.3f),
                        style = stroke,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx())
                    )
                }
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentLvl = stats?.level ?: 1
                val currentGP = stats?.greenPoints ?: 0
                val titleLvl = when (currentLvl) {
                    in 1..3 -> "Sprout Guardian"
                    in 4..7 -> "Tree Guardian"
                    else -> "Forest Master"
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "🏆", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "Level $currentLvl: $titleLvl",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "120 points to reach Master",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }
                Text(
                    text = "$currentGP GP",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFF59E0B) // Golden yellow
                )
            }
        }

        // Daily eco advice card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.TipsAndUpdates,
                    contentDescription = "Eco tip icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Daily Eco Tip",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Watering early in the morning reduces water loss through evaporation by up to 30%, giving roots ample time to drink.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Climate compatibility checker triggers
        ClimateSuitabilityChecker(viewModel)

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showQuickRecDialog) {
        PlantRecommenderDialog(
            viewModel = viewModel,
            onDismiss = { showQuickRecDialog = false },
            result = recommendationRs,
            loading = isRecommending
        )
    }
}

@Composable
fun WeatherIntelligenceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.WbSunny,
                        contentDescription = "Sun weather",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Bengaluru, IN",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sunny conditions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = "31°C",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherMetricItem(Icons.Rounded.Opacity, "Humidity", "54%")
                WeatherMetricItem(Icons.Rounded.Grain, "Precipitation", "10%")
                WeatherMetricItem(Icons.Rounded.Speed, "Wind Speed", "14 km/h")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(10.dp)
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Rounded.Verified,
                        contentDescription = "Safe Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Optimal water schedule check: Watering not required until tomorrow evening based on soil evaporation index.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherMetricItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantRecommenderDialog(
    viewModel: AppViewModel,
    onDismiss: () -> Unit,
    result: String?,
    loading: Boolean
) {
    var location by remember { mutableStateOf("Bengaluru Patio") }
    var climate by remember { mutableStateOf("Warm Sub-tropical") }
    var soil by remember { mutableStateOf("Loamy Red Soil") }
    var space by remember { mutableStateOf("Small Balcony Balcony") }
    var purpose by remember { mutableStateOf("Medicinal & Air Purification") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (result == null) {
                Button(
                    onClick = { viewModel.performRecommendation(location, climate, soil, space, purpose) },
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Get AI Recommendations")
                    }
                }
            } else {
                TextButton(onClick = onDismiss) { Text("Close Workspace") }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = {
            Text(text = "Smart Plant AI Recommender", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (result == null) {
                    Text(
                        text = "Customize plantation environment info. Let's ask Gemini Pro/Flash to calculate suitable plant candidates.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location Context") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = climate,
                        onValueChange = { climate = it },
                        label = { Text("Local Climate info") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = soil,
                        onValueChange = { soil = it },
                        label = { Text("Soil substrate description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = space,
                        onValueChange = { space = it },
                        label = { Text("Available Space") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = purpose,
                        onValueChange = { purpose = it },
                        label = { Text("Purpose of Growing") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = result,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ClimateSuitabilityChecker(viewModel: AppViewModel) {
    val suitabilityResult by viewModel.suitabilityResult.collectAsStateWithLifecycle()
    val isChecking by viewModel.isCheckingSuitability.collectAsStateWithLifecycle()

    var plant by remember { mutableStateOf("Tomato") }
    var location by remember { mutableStateOf("Bengaluru") }
    var temperature by remember { mutableStateOf("28°C") }
    var rainfall by remember { mutableStateOf("Medium (800mm)") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📊 Climate Suitability Checker",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Audit compatibility factors based on local atmosphere variables.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = plant,
                    onValueChange = { plant = it },
                    label = { Text("Plant species") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("City/Zone") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = temperature,
                    onValueChange = { temperature = it },
                    label = { Text("Avg Temp") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = rainfall,
                    onValueChange = { rainfall = it },
                    label = { Text("Rainfall level") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.checkClimateSuitability(plant, location, temperature, rainfall) },
                enabled = !isChecking,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_suitability_button")
            ) {
                if (isChecking) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text("Check Climate Score")
                }
            }

            suitabilityResult?.let { result ->
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = result,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// ==========================================
// 🔍 SCAN TAB (Leaf disease & Soil analysis)
// ==========================================
@Composable
fun ScanTab(viewModel: AppViewModel) {
    val leafResult by viewModel.leafScanResult.collectAsStateWithLifecycle()
    val isScanningLeaf by viewModel.isScanningLeaf.collectAsStateWithLifecycle()
    val soilResult by viewModel.soilAnalysisResult.collectAsStateWithLifecycle()
    val isAnalyzingSoil by viewModel.isAnalyzingSoil.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableStateOf(0) } // 0: Leaves, 1: Soil

    val context = LocalContext.current

    // Trigger local photo upload simulator which maps to bitmap
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (activeSubTab == 0) {
                    viewModel.scanLeafDisease(bitmap)
                } else {
                    viewModel.analyzeSoil("", bitmap)
                }
            } catch (e: Exception) {
                Log.e("ScanTab", "Error loading loaded image", e)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Headers picker
        TabRow(selectedTabIndex = activeSubTab) {
            Tab(
                selected = activeSubTab == 0,
                onClick = { activeSubTab = 0 },
                text = { Text("Leaves Diagnose", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Rounded.Eco, contentDescription = "Leaf icon") }
            )
            Tab(
                selected = activeSubTab == 1,
                onClick = { activeSubTab = 1 },
                text = { Text("Soil Analysis", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Rounded.Terrain, contentDescription = "Soil icon") }
            )
        }

        if (activeSubTab == 0) {
            // Leaf scan page
            Text(
                text = "🌱 Leaf Scan Sickness Detection",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Snap or upload a leaf photo to diagnose diseases, pests, severity levels and fetch organic treatments immediately from Gemini pathobiology AI.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Stylized Leaf Scan Templates Playground (Saves the day for emulator testing!)
            Text(
                text = "No leaf in focus? Tap one of these template cases to test instantly:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val leafTemplates = listOf(
                LeafTemplateItem("Rust on Wheat Leaf", "wheat_rust", Icons.Rounded.Agriculture),
                LeafTemplateItem("Tomato Early Blight", "tomato_blight", Icons.Rounded.Grass),
                LeafTemplateItem("Dried Citrus Spot", "citrus_spot", Icons.Rounded.Spa)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                leafTemplates.forEach { template ->
                    Button(
                        onClick = {
                            // generate dummy color bitmap representing test data for pathobiology call
                            val mockBitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888)
                            viewModel.scanLeafDisease(mockBitmap)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(72.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(template.icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = template.title,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 2,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Choose Image Upload triggers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Icon(Icons.Rounded.PhotoAlbum, contentDescription = "Gallery upload")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pick Gallery Photo", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        // Simulating a high-end AI leaf snapshot
                        val mockBmp = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
                        viewModel.scanLeafDisease(mockBmp)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Icon(Icons.Rounded.PhotoCamera, contentDescription = "Camera shot")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Trigger AI Scan", fontWeight = FontWeight.Bold)
                }
            }

            if (isScanningLeaf) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Gemini scanning leaf pathobiology... Please wait", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            leafResult?.let { result ->
                LeafResultDisplayCard(result)
            }

        } else {
            // Soil Analysis Page
            Text(
                text = "🪨 Interactive Soil Analyzer",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            var manualSoilNotes by remember { mutableStateOf("Red volcanic sand, slightly dry, located in sub-tropical backyard. Wants to cultivate organic tomatoes.") }

            OutlinedTextField(
                value = manualSoilNotes,
                onValueChange = { manualSoilNotes = it },
                label = { Text("Describe Soil features manually") },
                maxLines = 5,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { viewModel.analyzeSoil(manualSoilNotes, null) },
                    enabled = !isAnalyzingSoil,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    if (isAnalyzingSoil) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Analyze Soil properties")
                    }
                }

                OutlinedButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    enabled = !isAnalyzingSoil,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Icon(Icons.Rounded.Camera, contentDescription = "Camera Upload soil")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Scan Soil Image")
                }
            }

            soilResult?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Recommend, contentDescription = "recommend icon", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Soil analysis recommendation result:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = result, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

data class LeafTemplateItem(
    val title: String,
    val type: String,
    val icon: ImageVector
)

@Composable
fun LeafResultDisplayCard(history: ScanHistory) {
    val severityColor = when (history.severity.lowercase()) {
        "high" -> Color(0xFFD32F2F)
        "moderate" -> Color(0xFFF57C00)
        "low" -> Color(0xFF388E3C)
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(2.dp, severityColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = history.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "DIAGNOSED LEAF DISEASE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(severityColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Severity: ${history.severity}",
                        color = severityColor,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🔬 Sickness analysis:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = history.info,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            HorizontalDivider()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "🍀 Recommended Organic Treatment Guidelines:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = history.remedies,
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Normal
            )
        }
    }
}

// ==========================================
// 🤖 PRAKRITIMITRA COMPANION CHAT TAB
// ==========================================
@Composable
fun ChatTab(viewModel: AppViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val isTtsActive by viewModel.isTtsActive.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    var useTTSForSubsequentReplies by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Speech reading launcher
    val speechRecognitionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                textInput = spokenText
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Controls header bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.RecordVoiceOver,
                    contentDescription = "Voice mode toggle",
                    tint = if (useTTSForSubsequentReplies) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PrakritiMitra Voice Companion",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Switch(
                    checked = useTTSForSubsequentReplies,
                    onCheckedChange = { useTTSForSubsequentReplies = it },
                    modifier = Modifier.scale(0.8f).testTag("chat_voice_switch")
                )
            }

            IconButton(
                onClick = { viewModel.clearChat() },
                modifier = Modifier.testTag("chat_clear_button")
            ) {
                Icon(Icons.Rounded.DeleteForever, contentDescription = "Clear History", tint = MaterialTheme.colorScheme.error)
            }
        }

        if (isTtsActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Rounded.VolumeUp, contentDescription = "TTS Active", tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("PrakritiMitra is speaking...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(16.dp))
                TextButton(onClick = { viewModel.stopSpeaking() }) {
                    Text("Stop", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        // Messenger thread list view
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(msg, onSpeakRequested = { viewModel.speak(it) })
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PrakritiMitra is thinking concept... 🧠🌿",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input bottom tray
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Speech activation button
            IconButton(
                onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    }
                    try {
                        speechRecognitionLauncher.launch(intent)
                    } catch (e: Exception) {
                        Log.e("Chat", "Speech recognition failed", e)
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .size(48.dp)
                    .testTag("chat_speech_button")
            ) {
                Icon(Icons.Rounded.Mic, contentDescription = "Speech recognition", tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }

            // Input field
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                placeholder = { Text("Ask PrakritiMitra anything...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_text_field"),
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    if (textInput.isNotEmpty()) {
                        IconButton(onClick = { textInput = "" }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear input")
                        }
                    }
                },
                singleLine = true
            )

            // Submit Send
            IconButton(
                onClick = {
                    if (textInput.isNotBlank()) {
                        viewModel.sendChatMessage(textInput, useTTSForSubsequentReplies)
                        textInput = ""
                    }
                },
                enabled = textInput.isNotBlank(),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (textInput.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)
                    .size(48.dp)
                    .testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Send,
                    contentDescription = "Send",
                    tint = if (textInput.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage, onSpeakRequested: (String) -> Unit) {
    val isUser = msg.sender == ChatMessage.Sender.USER
    val colorPrimary = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val colorText = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.Top),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Spa,
                    contentDescription = "Bot Avatar",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            modifier = Modifier
                .widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(containerColor = colorPrimary),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorText
                )

                if (!isUser) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = { onSpeakRequested(msg.text) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Rounded.VolumeUp,
                                contentDescription = "Listen voice",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .align(Alignment.Top),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Face,
                    contentDescription = "User Avatar",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ==========================================
// 📈 GROWTH TRACKER & HISTORIC VISUALS
// ==========================================
@Composable
fun GrowthTab(viewModel: AppViewModel) {
    val trackedPlants by viewModel.plants.collectAsStateWithLifecycle()

    var showAddPlantDialog by remember { mutableStateOf(false) }
    var selectedPlantForAnalysis by remember { mutableStateOf<TrackedPlant?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tracker stats introductory banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "📈 Growth Forecaster & Dashboard",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Observe parameters, log heights, predict expected flowering countdown and track hydration schedules safely.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Button(
            onClick = { showAddPlantDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("growth_add_plant_button")
        ) {
            Icon(Icons.Rounded.AddCircleOutline, contentDescription = "Add green plant")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Plant to Cultivate", fontWeight = FontWeight.Bold)
        }

        if (trackedPlants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Rounded.Spa,
                        contentDescription = "empty leaves",
                        tint = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No plants tracked yet.", style = MaterialTheme.typography.labelLarge)
                    Text("Press 'Add Plant' to start your digital plantation garden!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            // List of tracked plants
            trackedPlants.forEach { plant ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPlantForAnalysis = plant },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
                        if (selectedPlantForAnalysis?.id == plant.id) 2.dp else 1.dp,
                        if (selectedPlantForAnalysis?.id == plant.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(plant.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                                Text(plant.species, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Health: ${plant.healthScore}%", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val daysToWater = calculateRemainingWaterDays(plant)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.WaterDrop,
                                    contentDescription = "Water droplet",
                                    tint = if (daysToWater <= 0) Color.Red else Color(0xFF2196F3),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (daysToWater <= 0) "Thirsty! Water immediately." else "Next water in $daysToWater days",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (daysToWater <= 0) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = { viewModel.updatePlantWatering(plant) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Water 🌱", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            selectedPlantForAnalysis?.let { plant ->
                DividerIndicatorSection(plant, viewModel)
            }
        }
    }

    if (showAddPlantDialog) {
        AddNewPlantDialog(
            viewModel = viewModel,
            onDismiss = { showAddPlantDialog = false }
        )
    }
}

fun calculateRemainingWaterDays(plant: TrackedPlant): Int {
    val elapsedMs = System.currentTimeMillis() - plant.lastWatered
    val elapsedDays = (elapsedMs / (1000 * 60 * 60 * 24)).toInt()
    return (plant.waterFrequencyDays - elapsedDays).coerceAtLeast(0)
}

@Composable
fun DividerIndicatorSection(plant: TrackedPlant, viewModel: AppViewModel) {
    var loggedHeightInput by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "📊 Historical Growth Progress Chart: ${plant.name}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Bezier calculated progress trends based on recorded height increments.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas Drawing Chart
            GrowthNativeCanvasChart(plant = plant)

            Spacer(modifier = Modifier.height(16.dp))

            // Text input height logs
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = loggedHeightInput,
                    onValueChange = { loggedHeightInput = it },
                    label = { Text("Log current height (cm)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(
                    onClick = {
                        val parsed = loggedHeightInput.toFloatOrNull()
                        if (parsed != null && parsed > 0) {
                            viewModel.addPlantHeightLog(plant, parsed)
                            loggedHeightInput = ""
                        }
                    },
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Add Log")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Clear expectations predictions card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Row {
                    Icon(Icons.Rounded.OnlinePrediction, contentDescription = "AI prediction banner", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Expected Yield & Maturity Simulation:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(plant.yieldPrediction, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("Flowering Window expectancy: Starts in approximately 12 days.", style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic)
                    }
                }
            }
        }
    }
}

@Composable
fun GrowthNativeCanvasChart(plant: TrackedPlant) {
    val heights = plant.heightProgressHistory.split(",").mapNotNull { it.toFloatOrNull() }
    val maxGridValue = (heights.maxOrNull() ?: 50f).coerceAtLeast(10f) + 10f

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
    ) {
        val width = size.width
        val height = size.height
        val padding = 30f

        // Draw horizontal Grid lines
        val gridLinesCount = 4
        for (i in 0..gridLinesCount) {
            val y = padding + i * (height - 2 * padding) / gridLinesCount
            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(padding, y),
                end = Offset(width - padding, y),
                strokeWidth = 2f
            )
        }

        if (heights.isNotEmpty()) {
            val pointsCount = heights.size
            val xIncrement = if (pointsCount > 1) (width - 2 * padding) / (pointsCount - 1) else width - 2 * padding

            var previousOffset: Offset? = null

            for (i in 0 until pointsCount) {
                val currentVal = heights[i]
                val x = padding + i * xIncrement
                // calculate relative inverse Y (as 0 is top)
                val relativeHeightFraction = currentVal / maxGridValue
                val y = height - padding - relativeHeightFraction * (height - 2 * padding)

                val activeOffset = Offset(x, y)

                // draw dot node
                drawCircle(
                    color = Color(0xFF2E7D32),
                    radius = 8f,
                    center = activeOffset
                )

                // connects with a line
                previousOffset?.let { prev ->
                    drawLine(
                        color = Color(0xFF81C784),
                        start = prev,
                        end = activeOffset,
                        strokeWidth = 6f,
                        cap = StrokeCap.Round
                    )
                }

                previousOffset = activeOffset
            }
        }
    }
}

@Composable
fun AddNewPlantDialog(
    viewModel: AppViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("Backyard Basil") }
    var species by remember { mutableStateOf("Ocimum basilicum") }
    var category by remember { mutableStateOf("Ornamental") }
    var description by remember { mutableStateOf("Warm organic basil leaves used for flavoring.") }
    var waterDays by remember { mutableStateOf("3") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val daysInt = waterDays.toIntOrNull() ?: 3
                    viewModel.addTrackedPlant(name, species, category, description, daysInt)
                    onDismiss()
                }
            ) {
                Text("Add Plant")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = {
            Text("Add New Tracked Plant", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Plant Custom Name") })
                OutlinedTextField(value = species, onValueChange = { species = it }, label = { Text("Species Botanical Name") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Medicinal, Shade, Fruit)") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Short description notes") })
                OutlinedTextField(value = waterDays, onValueChange = { waterDays = it }, label = { Text("Water Frequency Days") })
            }
        }
    )
}

// ==========================================
// 🌍 COMMUNITY TAB
// ==========================================
@Composable
fun CommunityTab(viewModel: AppViewModel) {
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val stats by viewModel.userStats.collectAsStateWithLifecycle()

    var postInputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Active Plantation Challenges Column
        Text(
            text = "🏆 Weekly Eco-Challenges Dashboard",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))

        stats?.let { s ->
            val challengesStatus = s.challengeProgress.split(",").map { it == "1" }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ChallengeGridItem("Plant 5 Trees Target Challenge (30pt)", challengesStatus.getOrElse(0) { false }) { checked ->
                    viewModel.toggleChallenge(0, checked)
                }
                ChallengeGridItem("Water My Organic Plants Target (30pt)", challengesStatus.getOrElse(1) { false }) { checked ->
                    viewModel.toggleChallenge(1, checked)
                }
                ChallengeGridItem("Invite neighbor to green campaigning (30pt)", challengesStatus.getOrElse(2) { false }) { checked ->
                    viewModel.toggleChallenge(2, checked)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Discussion list header
        Text(
            text = "🌍 Community Plantation Discussions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input Submit box inline
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = postInputText,
                onValueChange = { postInputText = it },
                placeholder = { Text("Share tips, photos, achievements...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("community_input_post"),
                singleLine = true
            )
            Button(
                onClick = {
                    if (postInputText.isNotBlank()) {
                        viewModel.submitPost(postInputText)
                        postInputText = ""
                    }
                },
                modifier = Modifier.height(56.dp)
            ) {
                Text("Post")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scrollable discussion lists
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(posts) { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Rounded.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(msg.username, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                                    Text(msg.role, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            val formattedDate = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp))
                            Text(formattedDate, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(msg.message, style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.toggleLikePost(msg) }, modifier = Modifier.size(32.dp)) {
                                Icon(
                                    imageVector = if (msg.userLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                    contentDescription = "Like button",
                                    tint = if (msg.userLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${msg.likes} Likes", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeGridItem(title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Task, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(10.dp))
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
        Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}

// ==========================================
// 👤 PROFILE & ECO ENCYCLOPEDIA TAB
// ==========================================
@Composable
fun ProfileTab(viewModel: AppViewModel) {
    val stats by viewModel.userStats.collectAsStateWithLifecycle()
    val scans by viewModel.scans.collectAsStateWithLifecycle()

    var activeProfileSubTab by remember { mutableStateOf(0) } // 0: saved reports, 1: Encyclopedia

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        stats?.let { s ->
            // Level, badges summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.EmojiEvents, contentDescription = "Medal icon", tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Tree Guardian Level ${s.level}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Sustainability score stats", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(12.dp))

                    val maxPointsForLevel = 150
                    val pointsInThisLevel = s.greenPoints % maxPointsForLevel
                    val progressRatio = pointsInThisLevel.toFloat() / maxPointsForLevel

                    LinearProgressIndicator(
                        progress = { progressRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.outlineVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${s.greenPoints} total GP", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text("${maxPointsForLevel - pointsInThisLevel} GP until next Level Up!", style = MaterialTheme.typography.labelSmall)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Badges row
                    Text("Earned Badges", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val badgesList = s.badgesUnlocked.split(",").map { it.trim() }
                        badgesList.forEach { badge ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(badge, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        TabRow(selectedTabIndex = activeProfileSubTab) {
            Tab(selected = activeProfileSubTab == 0, onClick = { activeProfileSubTab = 0 }) {
                Text("Saved Diagnosis Reports", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
            Tab(selected = activeProfileSubTab == 1, onClick = { activeProfileSubTab = 1 }) {
                Text("Eco Encyclopedia", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
            }
        }

        if (activeProfileSubTab == 0) {
            if (scans.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("No saved diagnosis reports found. Use Scan screen!", style = MaterialTheme.typography.labelMedium)
                }
            } else {
                scans.forEach { scan ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (scan.type == "LEAF") Icons.Rounded.Eco else Icons.Rounded.Terrain,
                                        contentDescription = scan.type,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(scan.title, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Severity: ${scan.severity}", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(scan.info, style = MaterialTheme.typography.bodySmall, maxLines = 3, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        } else {
            // Eco Encyclopedia collapsible guidelines
            Text("📚 Eco Learning Hub", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            CollapsibleGuideItem("1. How to make homemade compost?", "Compost is rich organic soil material made of green (kitchen wastes, coffee grounds) and brown (dry leaves, twigs, paper) remnants. Blend them together at a 1:2 ratio, keep slightly damp like a wrung-out sponge, and turn the compost pile weekly. It matures into black gold in 2-3 months to feed your garden.")
            CollapsibleGuideItem("2. Companion planting guide", "Certain plants grow better when cultivated together! Tomatoes thrive beside basil (which deters pests) and marigolds (which repel underground root-knot nematodes). Never plant potatoes near tomatoes as they share early blight susceptibility.")
            CollapsibleGuideItem("3. High-efficiency drip savings", "Switch to localized drip watering pipelines which apply water drop-by-drop directly to the root soil zone. Prevents water losses from wind drift and leaves evaporate, reducing risk of fungal infections on plant leaves!")
        }
    }
}

@Composable
fun CollapsibleGuideItem(title: String, desc: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Icon(
                    imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Expand collapsible guide"
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(desc, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
