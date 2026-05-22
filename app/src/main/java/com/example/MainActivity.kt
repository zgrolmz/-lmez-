package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import android.util.Log
import com.example.ui.*
import com.example.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private val viewModel: PlantViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Log all uncaught exceptions on any thread
        val originalHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("FatalCrash", "Uncaught exception on thread ${thread.name}: ${throwable.message}", throwable)
            originalHandler?.uncaughtException(thread, throwable)
        }

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel)
            }
        }
    }
}

enum class FloraTab(val title: String) {
    LIBRARY("Kütüphane"),
    GUIDE("Yetiştirme"),
    BOTANIST("Botanist AI"),
    GARDEN("Bahçem")
}

@Composable
fun MainAppScreen(viewModel: PlantViewModel) {
    var activeTab by remember { mutableStateOf(FloraTab.LIBRARY) }

    // Dialog state collections
    val selectedPlantDetail by viewModel.selectedPlant.collectAsStateWithLifecycle()
    val plantToAddToGarden by viewModel.plantToAddToGarden.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                FloraTab.values().forEach { tab ->
                    val isSelected = activeTab == tab
                    val (icon, label) = when (tab) {
                        FloraTab.LIBRARY -> Pair(Icons.Default.Home, "Kütüphane")
                        FloraTab.GUIDE -> Pair(Icons.Default.Info, "Rehber")
                        FloraTab.BOTANIST -> Pair(Icons.Default.Send, "Botanist AI")
                        FloraTab.GARDEN -> Pair(Icons.Default.Favorite, "Bahçem")
                    }
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { activeTab = tab },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // App Header
            AppHeader(activeTab)

            // Screen Content Area with animated transitions
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    FloraTab.LIBRARY -> LibraryScreen(viewModel)
                    FloraTab.GUIDE -> GuideScreen(viewModel)
                    FloraTab.BOTANIST -> BotanistChatScreen(viewModel)
                    FloraTab.GARDEN -> VirtualGardenScreen(viewModel)
                }
            }
        }

        // Details & Modal Dialog Management
        selectedPlantDetail?.let { plant ->
            PlantDetailDialog(plant = plant, onDismiss = { viewModel.selectPlant(null) })
        }

        plantToAddToGarden?.let { plant ->
            AddToGardenDialog(
                plant = plant,
                onDismiss = { viewModel.setPlantToAddToGarden(null) },
                onAdd = { name, notes ->
                    viewModel.addToGarden(plant, name, notes)
                }
            )
        }
    }
}

@Composable
fun AppHeader(tab: FloraTab) {
    val subtitle = when (tab) {
        FloraTab.LIBRARY -> "Türkiye'nin Eşsiz Biyoçeşitlilik Mirası"
        FloraTab.GUIDE -> "Yetiştirme ve Tarım Koşulları"
        FloraTab.BOTANIST -> "Uzman Yapay Zeka Botanikçi Asistanı"
        FloraTab.GARDEN -> "Kişisel Endemik Bitki Günlüğünüz"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        Color.Transparent
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Endemik Flora",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "TR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

// ----------------------------------------------------
// 1. LIBRARY SCREEN
// ----------------------------------------------------
@Composable
fun LibraryScreen(viewModel: PlantViewModel) {
    val searchVal by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCat by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val plantsList by viewModel.filteredPlants.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = searchVal,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("library_search_input"),
            placeholder = { Text("Bitki sınıfı, yerel ismi veya bilimsel adı aratın...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ara") },
            trailingIcon = {
                if (searchVal.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Temizle")
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // Category Selection Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "Tümü" Filter
            FilterChip(
                selected = selectedCat == null,
                onClick = { viewModel.selectCategory(null) },
                label = { Text("Tümü") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // "Endemik Bitki" Filter
            FilterChip(
                selected = selectedCat == PlantCategory.ENDEMIC_PLANT,
                onClick = { viewModel.selectCategory(PlantCategory.ENDEMIC_PLANT) },
                label = { Text("Endemik Bitkiler") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // "Yerel Baharat" Filter
            FilterChip(
                selected = selectedCat == PlantCategory.SPICE,
                onClick = { viewModel.selectCategory(PlantCategory.SPICE) },
                label = { Text("Yerel Baharatlar") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Grid/List of Plants
        if (plantsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Aranan kriterlere uygun endemik bitki veya baharat bulunamadı.",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Farklı bir terim aramayı deneyin ya da kategori filtrelerini temizleyin.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(plantsList, key = { it.id }) { plant ->
                    PlantLibraryItemCard(
                        plant = plant,
                        onClick = { viewModel.selectPlant(plant) },
                        onAddToGardenClick = { viewModel.setPlantToAddToGarden(plant) }
                    )
                }
            }
        }
    }
}

@Composable
fun PlantLibraryItemCard(
    plant: Plant,
    onClick: () -> Unit,
    onAddToGardenClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("plant_card_${plant.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row with visual icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Circle visual representation
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = when (plant.category) {
                                    PlantCategory.ENDEMIC_PLANT -> listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                                    )
                                    PlantCategory.SPICE -> listOf(
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                                    )
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (plant.id) {
                            "ters_lale" -> "🌷"
                            "zahter_kekigi" -> "🌿"
                            "safran" -> "🪻"
                            "dag_cayi" -> "🍵"
                            "antalya_cigdemi" -> "🌸"
                            "anadolu_sumagi" -> "🪵"
                            "kapadokya_sogani" -> "🧅"
                            "maras_biberi" -> "🌶️"
                            else -> "🌱"
                        },
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Title and Scientific Name
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = plant.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = plant.scientificName,
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Short lore excerpt
            Text(
                text = plant.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Badges & Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badges
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Region tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = plant.region.split("(").first().trim(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    // Conservation Badge
                    val badgeColors = when (plant.conservationStatus) {
                        "CR", "EN" -> Pair(Color(0xFFF2D1D1), Color(0xFFD32F2F))
                        "VU" -> Pair(Color(0xFFFFE9D0), Color(0xFFE65100))
                        else -> Pair(Color(0xFFE2F0D9), Color(0xFF388E3C))
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColors.first)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = plant.conservationStatus,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = badgeColors.second
                        )
                    }
                }

                // Add to Garden Button
                Button(
                    onClick = { onAddToGardenClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Bahçeme ekle",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sanal Bahçe", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ----------------------------------------------------
// 2. GROWING GUIDE SCREEN
// ----------------------------------------------------
@Composable
fun GuideScreen(viewModel: PlantViewModel) {
    val searchVal by viewModel.searchQuery.collectAsStateWithLifecycle()
    val plantsList by viewModel.filteredPlants.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Quick local guide search
        OutlinedTextField(
            value = searchVal,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Hangi bitkinin yetiştirme koşullarını arıyorsunuz?") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Süz") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(plantsList, key = { "guide_${it.id}" }) { plant ->
                PlantGuideCard(
                    plant = plant,
                    onAddToGarden = { viewModel.setPlantToAddToGarden(plant) }
                )
            }
        }
    }
}

@Composable
fun PlantGuideCard(plant: Plant, onAddToGarden: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header showing plant name and scientific name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (plant.category) {
                            PlantCategory.ENDEMIC_PLANT -> "🌸"
                            PlantCategory.SPICE -> "🌶️"
                        },
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = plant.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = plant.scientificName,
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Difficulty Chip
                val diffColor = when (plant.difficultyLevel) {
                    "Kolay" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                    "Orta" -> Color(0xFFFFF3E0) to Color(0xFFEF6C00)
                    else -> Color(0xFFFFEBEE) to Color(0xFFC62828)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(diffColor.first)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = plant.difficultyLevel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = diffColor.second
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid of 4 Cultivation Metrics
            Row(modifier = Modifier.fillMaxWidth()) {
                // Soil & Sowing Season Left Column
                Column(modifier = Modifier.weight(1f)) {
                    // Toprak İhtiyacı
                    GuideDetailRow(title = "Toprak Türü", desc = plant.soilRequirements, emoji = "🪵")
                    Spacer(modifier = Modifier.height(12.dp))
                    // Ekim Zamanı
                    GuideDetailRow(title = "Ekim Mevsimi", desc = plant.sowingSeason, emoji = "📅")
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Water & Sunlight Right Column
                Column(modifier = Modifier.weight(1f)) {
                    // Sulama derecesi
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("💧", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Sulama Sıklığı",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            repeat(3) { i ->
                                val active = i < plant.wateringFrequency
                                Text(
                                    text = "💧",
                                    fontSize = 16.sp,
                                    modifier = Modifier.alpha(if (active) 1.0f else 0.2f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Güneş ışığı derecesi
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (plant.lightType) {
                                    "Güneşli" -> "☀️"
                                    "Yarı Gölge" -> "⛅"
                                    else -> "☁️"
                                },
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Işık Koşulu",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = plant.sunlight,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(12.dp))

            // Core step instruction
            Column {
                Text(
                    text = "Nasıl Yetiştirilir? 👨‍🌾",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = plant.wateringInstructions,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 16.sp,
                    fontStyle = FontStyle.Normal
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Quick add to Virtual Garden
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onAddToGarden,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sanal Bahçeme Dik", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
fun GuideDetailRow(title: String, desc: String, emoji: String) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            desc,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.sp
        )
    }
}

// ----------------------------------------------------
// 3. AI BOTANIST CHAT SCREEN
// ----------------------------------------------------
@Composable
fun BotanistChatScreen(viewModel: PlantViewModel) {
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val uiState by viewModel.chatState.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val listState = rememberLazyListState()

    // Scroll down whenever a new message appears
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Quick tools header panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💡 Örnek Sorular: 'Safran saksıda yetişir mi?', 'Zahter toprağı ne olmalı?'",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { viewModel.clearChat() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Temizle",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Chat Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg)
            }

            if (uiState is ChatState.Loading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Yapay Zeka Botanikçi cevap yazıyor...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }

            if (uiState is ChatState.Error) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = (uiState as ChatState.Error).message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Bottom Input box
        Surface(
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Botanist AI'ya soru sorun...") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("bot_chat_input"),
                    singleLine = false,
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendMessageToBot(textInput)
                                textInput = ""
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.sendMessageToBot(textInput)
                            textInput = ""
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Gönder",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val bubbleShape = if (message.isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp)
    }

    val bubbleBg = if (message.isUser) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val textColor = if (message.isUser) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val alignment = if (message.isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(bubbleShape)
                .background(bubbleBg)
                .border(
                    width = if (message.isUser) 0.dp else 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    shape = bubbleShape
                )
                .padding(14.dp)
        ) {
            Text(
                text = message.text,
                fontSize = 14.sp,
                color = textColor,
                lineHeight = 19.sp
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = if (message.isUser) "Siz" else "Yapay Zeka Botanikçiniz",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

// ----------------------------------------------------
// 4. VIRTUAL GARDEN SCREEN
// ----------------------------------------------------
@Composable
fun VirtualGardenScreen(viewModel: PlantViewModel) {
    val gardenList by viewModel.gardenPlants.collectAsStateWithLifecycle()

    var showEditNotesDialogForPlant by remember { mutableStateOf<GardenPlant?>(null) }

    if (gardenList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text("🏡", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Bahçeniz henüz boş!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Kütüphanedeki 'Sanal Bahçe' butonuna basarak, yetiştirmek istediğiniz harika endemik bitkileri bahçenize ekleyebilir ve adımlarını takip edebilirsiniz.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(gardenList, key = { it.id }) { item ->
                val associatedPlant = PlantRepository.getPlantById(item.plantId)
                if (associatedPlant != null) {
                    GardenPlantCard(
                        gardenItem = item,
                        plant = associatedPlant,
                        onWater = { viewModel.waterPlant(item) },
                        onEditNotes = { showEditNotesDialogForPlant = item },
                        onRemove = { viewModel.removeFromGarden(item) }
                    )
                }
            }
        }
    }

    // Modal dialog to update custom plant notes
    showEditNotesDialogForPlant?.let { gardenItem ->
        EditGardenNotesDialog(
            gardenPlant = gardenItem,
            onDismiss = { showEditNotesDialogForPlant = null },
            onSave = { newNotes ->
                viewModel.updateGardenNotes(gardenItem, newNotes)
                showEditNotesDialogForPlant = null
            }
        )
    }
}

@Composable
fun GardenPlantCard(
    gardenItem: GardenPlant,
    plant: Plant,
    onWater: () -> Unit,
    onEditNotes: () -> Unit,
    onRemove: () -> Unit
) {
    val dateStr = remember(gardenItem.datePlanted) {
        try {
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("tr"))
            sdf.format(Date(gardenItem.datePlanted))
        } catch (e: Exception) {
            "Bilinmeyen tarih"
        }
    }

    val hoursSinceWatered = remember(gardenItem.lastWatered) {
        val diffMs = System.currentTimeMillis() - gardenItem.lastWatered
        diffMs / (1000 * 60 * 60)
    }

    val (wateringRecommendation, wateringTint) = when {
        hoursSinceWatered < 12 -> "Yeni Sulanmış" to MaterialTheme.colorScheme.primary
        hoursSinceWatered < 48 -> "Nemli / Doygun" to MaterialTheme.colorScheme.secondary
        else -> "Sulama İhtiyacı Olabilir" to MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Nickname, Scientific title, trash button
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (plant.id) {
                            "ters_lale" -> "🌷"
                            "zahter_kekigi" -> "🌿"
                            "safran" -> "🪻"
                            "dag_cayi" -> "🍵"
                            "antalya_cigdemi" -> "🌸"
                            "anadolu_sumagi" -> "🪵"
                            "kapadokya_sogani" -> "🧅"
                            "maras_biberi" -> "🌶️"
                            else -> "🌱"
                        },
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = gardenItem.customName,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${plant.name} • ${plant.scientificName}",
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Bahçemden Sil",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Planting date stats
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Dikim Tarihi",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        dateStr,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Su Durumu",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        wateringRecommendation,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = wateringTint
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // User notes diary
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onEditNotes)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Bakım Günlüğüm 📝",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Düzenle",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = gardenItem.notes.ifBlank { "Bitkinizle ilgili sulama, canlanma, güneşe tepki notları eklemek için buraya basın..." },
                    fontSize = 12.sp,
                    color = if (gardenItem.notes.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 16.sp,
                    fontStyle = if (gardenItem.notes.isBlank()) FontStyle.Italic else FontStyle.Normal
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Water instant button
            Button(
                onClick = onWater,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("💧 Suladım!", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}


// ----------------------------------------------------
// 5. MODAL DIALOGS AND SHEETS
// ----------------------------------------------------
@Composable
fun PlantDetailDialog(plant: Plant, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Header (Close, category, title)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = plant.category.displayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title details
                Text(
                    text = plant.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = plant.scientificName,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Region & Conservation Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Coğrafi Bölge", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                plant.region,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text("Korunma Durumu", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                plant.statusText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Core Lore Description
                Text(
                    text = "Hakkında 📖",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = plant.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Medicinal or Culinary Benefits
                Text(
                    text = if (plant.category == PlantCategory.SPICE) "Tıbbi ve Aromatik Değeri 🌶️" else "Tıbbi ve Aromatik Özelliği 🌿",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = plant.benefits,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Kapat", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddToGardenDialog(
    plant: Plant,
    onDismiss: () -> Unit,
    onAdd: (nickname: String, notes: String) -> Unit
) {
    var nickname by remember { mutableStateOf(plant.name) }
    var startingNotes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Sanal Bahçeme Dik",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${plant.name} bitkisini kendi bahçenize ekleyerek yetiştirme gelişimini takip edebilirsiniz.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Plant nickname field
                Text(
                    text = "Bitkinizin Rumuzu (Örn: Balkondaki Lalem)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Lütfen bir isim verin") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Starting notes field
                Text(
                    text = "İlk Bakım Notları",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = startingNotes,
                    onValueChange = { startingNotes = it },
                    singleLine = false,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Örn: Bugün ponza karışımlı süzek saksıya tohumları diktim. Can suyunu verdim...") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("İptal")
                    }

                    Button(
                        onClick = { onAdd(nickname, startingNotes) },
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Text("Bahçeme Dik", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditGardenNotesDialog(
    gardenPlant: GardenPlant,
    onDismiss: () -> Unit,
    onSave: (newNotes: String) -> Unit
) {
    var notesInput by remember { mutableStateOf(gardenPlant.notes) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Bakım Günlüğünü Düzenle",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${gardenPlant.customName} bitkiniz için gözlemleri, sulama sıklıklarını ve yeni yaprak/tomurcuk oluşumlarını buraya kaydedin.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Ekim, saksı değişimi veya gelişim notları yazın...") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Vazgeç")
                    }

                    Button(
                        onClick = { onSave(notesInput) },
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Text("Günlüğü Kaydet", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
