package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CreationItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.CreatorMode
import com.example.ui.viewmodel.EthioCreatorViewModel
import com.example.ui.viewmodel.ScreenState
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EthioCreatorApp(viewModel: EthioCreatorViewModel) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val currentMode by viewModel.currentMode.collectAsState()
    val screenState by viewModel.screenState.collectAsState()

    val textPrompt by viewModel.textPrompt.collectAsState()
    val imagePrompt by viewModel.imagePrompt.collectAsState()
    val thumbnailTheme by viewModel.thumbnailTheme.collectAsState()
    val thumbnailStyle by viewModel.thumbnailStyle.collectAsState()
    val thumbnailTitle by viewModel.thumbnailTitle.collectAsState()
    val thumbnailSubtitle by viewModel.thumbnailSubtitle.collectAsState()
    val forexQuery by viewModel.forexQuery.collectAsState()
    val translationInput by viewModel.translationInput.collectAsState()
    val isAmharicToEnglish by viewModel.isAmharicToEnglish.collectAsState()
    val pdfContentText by viewModel.pdfContentText.collectAsState()
    val pdfTitleInput by viewModel.pdfTitleInput.collectAsState()
    val creationsHistory by viewModel.creationsHistory.collectAsState()

    // Handle Operation State Toasts
    LaunchedEffect(Unit) {
        viewModel.operationStatus.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrightGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ኢ",
                                color = DarkBlueBg,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                "Ethio AI Creator",
                                color = IceWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                "የኢትዮጵያ ቀዳሚ የፈጠራ AI ረዳት",
                                color = BrightGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlueBg,
                    titleContentColor = IceWhite
                ),
                actions = {
                    IconButton(
                        onClick = { viewModel.setMode(CreatorMode.HISTORY) },
                        modifier = Modifier.testTag("top_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "ታሪክ",
                            tint = if (currentMode is CreatorMode.HISTORY) BrightGold else IceWhite
                        )
                    }
                }
            )
        },
        containerColor = DarkBlueBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Elegant Gold Grid Header (Hero Image)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(DarkBlueSurface)
            ) {
                // Load img_hero_banner.jpg generated earlier
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner),
                    contentDescription = "የኢትዮ AI ክሬተር መግቢያ",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // High Contrast dark gradient overlay for text readability
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                            )
                        )
                )

                // Gold typography overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ኢትዮ AI ክሬተር",
                        color = BrightGold,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "በአማርኛ ጽሑፍ፣ ምስል፣ ዩቱብ ታምብነይል፣ ፎሬክስ እና ፒዲኤፍ ማዘጋጃ",
                        color = IceWhite.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Mode Selector: Modern Horizontal Scroll Chips
            ModeSelector(
                activeMode = currentMode,
                onModeSelected = { viewModel.setMode(it) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = CardBorderGold,
                thickness = 1.dp
            )

            // Dynamic Form Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, CardBorderGold, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkBlueSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = getTabTitle(currentMode),
                        color = BrightGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = getTabDescription(currentMode),
                        color = IceWhite.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Form bodies base on chosen mode
                    when (currentMode) {
                        CreatorMode.TEXT_GENERATOR -> {
                            OutlinedTextField(
                                value = textPrompt,
                                onValueChange = { viewModel.updateTextPrompt(it) },
                                label = { Text("የሚፈልጉትን የጽሑፍ ርዕስ ወይም ሀሳብ እዚህ ያብራሩ...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .testTag("input_text_prompt"),
                                colors = outLineColors(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        CreatorMode.IMAGE_GENERATOR -> {
                            OutlinedTextField(
                                value = imagePrompt,
                                onValueChange = { viewModel.updateImagePrompt(it) },
                                label = { Text("እንዴት ያለ ምስል እንዲዘጋጅ ይፈልጋሉ? (ለምሳሌ: የኢትዮጵያ ገበሬ...)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .testTag("input_image_prompt"),
                                colors = outLineColors(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        CreatorMode.THUMBNAIL_CREATOR -> {
                            OutlinedTextField(
                                value = thumbnailTheme,
                                onValueChange = { viewModel.updateThumbnailTheme(it) },
                                label = { Text("የቪዲዮ ገጽታ/ጭብጥ (ለምሳሌ: የፎርክስ ንግድ፣ አዲስ ስልክ...)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .testTag("input_thumbnail_theme"),
                                colors = outLineColors(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Text(
                                "የዲዛይን ዘይቤ (Visual Theme):",
                                color = IceWhite,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            val stylesList = listOf("የቴክኖሎጂ (Tech Vibe)", "የፋይናንስ/ፎሬክስ", "ማህበራዊ እውቀት", "መዝናኛ/ቅንጦት")
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                items(stylesList) { style ->
                                    FilterChip(
                                        selected = thumbnailStyle == style,
                                        onClick = { viewModel.updateThumbnailStyle(style) },
                                        label = { Text(style, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BrightGold,
                                            selectedLabelColor = DarkBlueBg,
                                            containerColor = DarkBlueBg,
                                            labelColor = IceWhite
                                        )
                                    )
                                }
                            }

                            // Interactive Live overlay entries
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = thumbnailTitle,
                                    onValueChange = { viewModel.updateThumbnailTitle(it) },
                                    label = { Text("ታምብነይል ዋና ጽሑፍ") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_thumbnail_title"),
                                    colors = outLineColors(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                OutlinedTextField(
                                    value = thumbnailSubtitle,
                                    onValueChange = { viewModel.updateThumbnailSubtitle(it) },
                                    label = { Text("ታምብነይል ንዑስ ጽሑፍ") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("input_thumbnail_subtitle"),
                                    colors = outLineColors(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                        CreatorMode.FOREX_ASSISTANT -> {
                            Text(
                                "የገንዘብ ጥንድ ይምረጡ ወይም ይጻፉ፡",
                                color = IceWhite,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            val pairs = listOf("USD/ETB - ዶላር ብር", "EUR/ETB - ዩሮ ብር", "GBP/ETB - ፓውንድ ብር", "AED/ETB - ድርሃም ብር", "CNY/ETB - ዩዋን ብር")
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                items(pairs) { pair ->
                                    FilterChip(
                                        selected = forexQuery.contains(pair.split(" ").first()),
                                        onClick = { viewModel.updateForexQuery(pair.split(" ").first()) },
                                        label = { Text(pair, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = BrightGold,
                                            selectedLabelColor = DarkBlueBg,
                                            containerColor = DarkBlueBg,
                                            labelColor = IceWhite
                                        )
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = forexQuery,
                                onValueChange = { viewModel.updateForexQuery(it) },
                                label = { Text("ቀጥታ ጥያቄ (ለምሳሌ፡ USD/ETB ዛሬ ምን ደረጃ ደረሰ?)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_forex_query"),
                                colors = outLineColors(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        CreatorMode.TRANSLATION -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isAmharicToEnglish) "🔄 አማርኛ ወደ እንግሊዝኛ" else "🔄 እንግሊዝኛ ወደ አማርኛ",
                                    color = BrightGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Button(
                                    onClick = { viewModel.toggleTranslationDirection() },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrightGold, contentColor = DarkBlueBg),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier
                                        .height(34.dp)
                                        .testTag("translation_toggle_button")
                                ) {
                                    Text("ቀይር", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedTextField(
                                value = translationInput,
                                onValueChange = { viewModel.updateTranslationInput(it) },
                                label = { Text(if (isAmharicToEnglish) "የሚተረጎመውን የአማርኛ ጽሑፍ ያስገቡ..." else "Enter the English text to translate...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .testTag("input_translation_text"),
                                colors = outLineColors(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        CreatorMode.EBOOK_PDF -> {
                            OutlinedTextField(
                                value = pdfTitleInput,
                                onValueChange = { viewModel.updatePdfTitleInput(it) },
                                label = { Text("የመጽሐፉ/ሰነዱ ርዕስ (eBook/PDF Title)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .testTag("input_pdf_title"),
                                colors = outLineColors(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = pdfContentText,
                                onValueChange = { viewModel.updatePdfContentText(it) },
                                label = { Text("የመጽሐፉ ይዘት (ጽሑፍ እዚህ ይጻፉ ወይም ከታሪክ ይውሰዱ)...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                                    .testTag("input_pdf_content"),
                                colors = outLineColors(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Text(
                                "💡 ምክር፡ ከዚህ በፊት በሌላ ታብ (ለምሳሌ የጽሑፍ ማመንጫ) ያገኙትን ስራ 'ወደ PDF ላክ' ቁልፍን በመጫን እዚህ በቀጥታ ማምጣት ይችላሉ።",
                                color = IceWhite.copy(alpha = 0.6f),
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        CreatorMode.HISTORY -> {
                            Text(
                                "ቀደም ብለው የሰሯቸው ስራዎች በሙሉ እዚህ ተቀምጠዋል፡",
                                color = IceWhite,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // LARGE AI GENERATE BUTTON (Strictly requested by design specs)
                    if (currentMode != CreatorMode.HISTORY) {
                        Button(
                            onClick = {
                                if (currentMode == CreatorMode.EBOOK_PDF) {
                                    val savedFile = viewModel.exportToPdf(context)
                                    if (savedFile != null) {
                                        openFileIntent(context, savedFile)
                                    }
                                } else {
                                    viewModel.generateContent()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("app_generate_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrightGold,
                                contentColor = DarkBlueBg
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (currentMode == CreatorMode.EBOOK_PDF) Icons.Default.Share else Icons.Default.Send,
                                    contentDescription = "ማመንጫ ቁልፍ"
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (currentMode == CreatorMode.EBOOK_PDF) "የተቀናጀውን የ PDF ሰነድ ፍጠር (Export)" else "በ AI ስራውን ፍጠር (Generate Content)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    } else {
                        // In History tab, clean wipe option
                        if (creationsHistory.isNotEmpty()) {
                            OutlinedButton(
                                onClick = { viewModel.clearHistory() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("clear_history_button"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                border = BorderStroke(1.dp, Color.Red),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "ታሪክ ሰርዝ")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("ታሪክ በሙሉ ሰርዝ (Clear All History)", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Results and Previews displays
            if (currentMode != CreatorMode.HISTORY) {
                // Screen Status Display
                AnimatedVisibility(
                    visible = screenState != ScreenState.Idle,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .border(1.dp, CardBorderGold, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkBlueSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                "የምላሽ ውጤት (AI Creation Outcome)",
                                color = BrightGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            when (screenState) {
                                is ScreenState.Loading -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(color = BrightGold)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            "AI የፈጠራ ስራውን በማዘጋጀት ላይ ነው። እባክዎ ጥቂት ሰከንዶችን ይጠብቁ...",
                                            color = IceWhite,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                is ScreenState.Success -> {
                                    val successState = screenState as ScreenState.Success
                                    
                                    SelectionContainer {
                                        Text(
                                            text = successState.message,
                                            color = IceWhite,
                                            fontSize = 14.sp,
                                            lineHeight = 22.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    // Quick action buttons for the output
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(successState.message))
                                                Toast.makeText(context, "ጽሑፉ ተገልብጧል።", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = DarkBlueBg, contentColor = BrightGold),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.Share, contentDescription = "ገለብጥ", modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("ኮፒ አድርግ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                viewModel.shareToPdfContent(successState.message)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = DarkBlueBg, contentColor = BrightGold),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(imageVector = Icons.Default.Share, contentDescription = "ፒዲኤፍ", modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("ወደ PDF ላክ", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                                is ScreenState.Error -> {
                                    val errorState = screenState as ScreenState.Error
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp)
                                    ) {
                                        Text(
                                            "ስህተት ተከስቷል! (Error occurred)",
                                            color = Color.Red,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(bottom = 6.dp)
                                        )
                                        Text(
                                            text = errorState.error,
                                            color = IceWhite,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                }

                // Interactive Live YouTube Thumbnail Preview (Displays for THUMBNAIL_CREATOR)
                if (currentMode is CreatorMode.THUMBNAIL_CREATOR) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📺 ድንቅ የቀጥታ ዩቱብ ታምብነይል እይታ (Live YouTube Thumbnail Model)",
                        color = BrightGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    InteractiveThumbnailPreview(
                        style = thumbnailStyle,
                        title = thumbnailTitle,
                        subtitle = thumbnailSubtitle
                    )
                }
            } else {
                // History List Viewer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    if (creationsHistory.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .border(1.dp, CardBorderGold, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = DarkBlueSurface),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = "ባዶ",
                                    tint = BrightGold.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "ምንም የቀደመ ስራ ታሪክ አልተገኘም።",
                                    color = IceWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    "በ AI የሚያመነጯቸው ፈጠራዎች በሙሉ እዚህ ተመዝግበው ይቀመጣሉ።",
                                    color = IceWhite.copy(alpha = 0.6f),
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    } else {
                        creationsHistory.forEach { item ->
                            val icon = when (item.contentType) {
                                "TEXT" -> Icons.Default.Edit
                                "IMAGE" -> Icons.Default.Star
                                "THUMBNAIL" -> Icons.Default.Star
                                "FOREX" -> Icons.Default.Info
                                "TRANSLATION" -> Icons.Default.Refresh
                                else -> Icons.Default.List
                            }
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                                    .border(1.dp, CardBorderGold, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = DarkBlueSurface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = "ዓይነት",
                                                tint = BrightGold,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = item.title,
                                                color = BrightGold,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteHistoryItem(item.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "ሰርዝ",
                                                tint = Color.Red.copy(alpha = 0.7f),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "ጥያቄ (Prompt): ${item.inputPrompt}",
                                        color = IceWhite.copy(alpha = 0.6f),
                                        fontSize = 11.sp,
                                        fontStyle = FontStyle.Italic
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = item.outputContent,
                                        color = IceWhite,
                                        fontSize = 13.sp,
                                        maxLines = 5,
                                        lineHeight = 18.sp,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = { viewModel.shareToPdfContent(item.outputContent) },
                                            colors = ButtonDefaults.buttonColors(containerColor = DarkBlueBg, contentColor = BrightGold),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier
                                                .height(28.dp)
                                                .padding(end = 8.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Share, contentDescription = "ፒዲኤፍ", modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("ወደ PDF ጫን", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(item.outputContent))
                                                Toast.makeText(context, "ኮፒ ተደርጓል!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = DarkBlueBg, contentColor = BrightGold),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Share, contentDescription = "ኮፒ", modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("ኮፒ አድርግ", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Interactive YouTube Thumbnail preview styled in Dark Navy & Ethiopian Gold
@Composable
fun InteractiveThumbnailPreview(
    style: String,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .padding(16.dp)
            .border(2.dp, BrightGold, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Draw decorative background grid and golden neon elements
                    val brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0C192E), Color(0xFF030D1E)),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )
                    drawRect(brush = brush)

                    // Draw golden glowing futuristic grids
                    val gridSpacing = size.width / 8
                    for (i in 1..8) {
                        drawLine(
                            color = BrightGold.copy(alpha = 0.08f),
                            start = Offset(i * gridSpacing, 0f),
                            end = Offset(i * gridSpacing, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Draw abstract vector orbits
                    drawCircle(
                        color = BrightGold.copy(alpha = 0.05f),
                        radius = size.width / 4,
                        center = Offset(size.width, size.height / 2),
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
        ) {
            // Gold shining border effects
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .border(1.dp, BrightGold.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            )

            // Content displays
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top tag row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(BrightGold, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE HD",
                            color = DarkBlueBg,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Text(
                        text = style,
                        color = BrightGold.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Middle Text stack
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title.ifBlank { "ኢትዮጵያ በ AI" },
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 26.sp,
                        modifier = Modifier
                            .drawBehind {
                                // Draw shadow bar under text for legibility
                                drawRect(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    topLeft = Offset(0f, 0f),
                                    size = size
                                )
                            }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = subtitle.ifBlank { "አዲሱ የቴክኖሎጂ ዘመን" },
                        color = BrightGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 16.sp,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Bottom watermark branding
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Ethio AI Creator 🇪🇹",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ModeSelector(
    activeMode: CreatorMode,
    onModeSelected: (CreatorMode) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val modes = listOf(
            Triple(CreatorMode.TEXT_GENERATOR, "✍️ ጽሑፍ ማመንጫ", "tab_text_creator"),
            Triple(CreatorMode.IMAGE_GENERATOR, "🎨 ምስል ፕሮምፕት", "tab_image_creator"),
            Triple(CreatorMode.THUMBNAIL_CREATOR, "📺 ዩቲዩብ ታምብነይል", "tab_thumbnail_creator"),
            Triple(CreatorMode.FOREX_ASSISTANT, "📈 ፎሬክስ ረዳት", "tab_forex_assistant"),
            Triple(CreatorMode.EBOOK_PDF, "📚 ፒዲኤፍ ማዘጋጃ", "tab_ebook_pdf"),
            Triple(CreatorMode.HISTORY, "የቀድሞ ስራዎች ታሪክ", "tab_history_logs")
        )

        items(modes) { item ->
            val isSelected = activeMode == item.first
            Button(
                onClick = { onModeSelected(item.first) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) BrightGold else DarkBlueSurface,
                    contentColor = if (isSelected) DarkBlueBg else IceWhite
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (isSelected) BrightGold else CardBorderGold),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag(item.third)
            ) {
                Text(
                    text = item.second,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// Local helper to launch generated PDF reader or notify completion path
private fun openFileIntent(context: Context, file: File) {
    try {
        val uri = Uri.fromFile(file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "ሰነዱ በተሳካ ሁኔታ ተቀምጧል፡ ${file.absolutePath}", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun outLineColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = IceWhite,
    unfocusedTextColor = IceWhite,
    focusedBorderColor = BrightGold,
    unfocusedBorderColor = CardBorderGold,
    focusedLabelColor = BrightGold,
    unfocusedLabelColor = IceWhite.copy(alpha = 0.5f),
    cursorColor = BrightGold,
    focusedPlaceholderColor = IceWhite.copy(alpha = 0.5f)
)

private fun getTabTitle(mode: CreatorMode): String {
    return when (mode) {
        CreatorMode.TEXT_GENERATOR -> "በአማርኛ AI ጽሑፍ ማመንጨት"
        CreatorMode.IMAGE_GENERATOR -> "AI ምስል (Image) ፕሮምፕት ማመንጨት"
        CreatorMode.THUMBNAIL_CREATOR -> "YouTube Thumbnail መፍጠር"
        CreatorMode.FOREX_ASSISTANT -> "Forex Analysis ረዳት"
        CreatorMode.TRANSLATION -> "አማርኛ <-> እንግሊዝኛ ትርጉም"
        CreatorMode.EBOOK_PDF -> "PDF እና eBook ማዘጋጀት"
        CreatorMode.HISTORY -> "የተቀመጡ ስራዎች ታሪክ (Saved Creations)"
    }
}

private fun getTabDescription(mode: CreatorMode): String {
    return when (mode) {
        CreatorMode.TEXT_GENERATOR -> "ለማህበራዊ ገጽ መግለጫዎች፣ ግጥሞች ወይም ንግድ ሰነዶች ማራኪ ይዘቶችን በአማርኛ ያዘጋጁ።"
        CreatorMode.IMAGE_GENERATOR -> "ለ Midjourney, Stable Diffusion ወይም Imagen የሚሆኑ ድንቅ ባለሙያ የምስል ፈጠራ ጥያቄዎችን (prompts) ያውጡ።"
        CreatorMode.THUMBNAIL_CREATOR -> "የቪዲዮ ገጽታዎን ያስገቡ፤ AI ከፍተኛ ተደራሽነት ያላቸውን አርእስት እና ዲዛይኖች ይመርጥልዎታል።"
        CreatorMode.FOREX_ASSISTANT -> "ስለ ዶላር፣ ዩሮ ወይም ሌሎች የውጭ ገንዘቦች ከአማርኛ ማብራሪያ ጋር የተሟላ የገበያ ትንተና ያግኙ።"
        CreatorMode.TRANSLATION -> "ጥልቅ ትርጉም፣ የቃላት አጠራር መዝገብ እና ባህላዊ ዘይቤዎችን በጥልቅ የሚተነትን የቋንቋ ረዳት።"
        CreatorMode.EBOOK_PDF -> "ጽሑፍዎን ወደ ውብ የኢቡክ (eBook) ቅርጽ በመቀየር በቀጥታ ወደ ስልክዎ የ PDF ሰነድ ያውርዱ።"
        CreatorMode.HISTORY -> "በ AI ያመነጯቸውን እና የቀደሙ ስራዎችዎን በቀላሉ ማየት፣ ኮፒ ማድረግ ወይም ወደ ፒዲኤፍ መቀየር ይችላሉ።"
    }
}
