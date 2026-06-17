package com.example.ui.viewmodel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.CreationItem
import com.example.data.repository.CreationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

sealed class CreatorMode {
    object TEXT_GENERATOR : CreatorMode()
    object IMAGE_GENERATOR : CreatorMode()
    object THUMBNAIL_CREATOR : CreatorMode()
    object FOREX_ASSISTANT : CreatorMode()
    object EBOOK_PDF : CreatorMode()
    object TRANSLATION : CreatorMode()
    object HISTORY : CreatorMode()
}

sealed interface ScreenState {
    object Idle : ScreenState
    object Loading : ScreenState
    data class Success(val message: String) : ScreenState
    data class Error(val error: String) : ScreenState
}

class EthioCreatorViewModel(private val repository: CreationRepository) : ViewModel() {

    private val _currentMode = MutableStateFlow<CreatorMode>(CreatorMode.TEXT_GENERATOR)
    val currentMode: StateFlow<CreatorMode> = _currentMode.asStateFlow()

    private val _screenState = MutableStateFlow<ScreenState>(ScreenState.Idle)
    val screenState: StateFlow<ScreenState> = _screenState.asStateFlow()

    // Inputs
    private val _textPrompt = MutableStateFlow("")
    val textPrompt: StateFlow<String> = _textPrompt.asStateFlow()

    private val _imagePrompt = MutableStateFlow("")
    val imagePrompt: StateFlow<String> = _imagePrompt.asStateFlow()

    private val _thumbnailTheme = MutableStateFlow("")
    val thumbnailTheme: StateFlow<String> = _thumbnailTheme.asStateFlow()

    private val _thumbnailStyle = MutableStateFlow("የቴክኖሎጂ (Tech Vibe)")
    val thumbnailStyle: StateFlow<String> = _thumbnailStyle.asStateFlow()

    private val _thumbnailTitle = MutableStateFlow("ኢትዮጵያ በ AI")
    val thumbnailTitle: StateFlow<String> = _thumbnailTitle.asStateFlow()

    private val _thumbnailSubtitle = MutableStateFlow("አዲሱ የኢትዮጵያ የቴክኖሎጂ ዘመን")
    val thumbnailSubtitle: StateFlow<String> = _thumbnailSubtitle.asStateFlow()

    private val _forexQuery = MutableStateFlow("USD/ETB")
    val forexQuery: StateFlow<String> = _forexQuery.asStateFlow()

    private val _translationInput = MutableStateFlow("")
    val translationInput: StateFlow<String> = _translationInput.asStateFlow()

    private val _isAmharicToEnglish = MutableStateFlow(true)
    val isAmharicToEnglish: StateFlow<Boolean> = _isAmharicToEnglish.asStateFlow()

    // Interactive PDF Text compiler input
    private val _pdfContentText = MutableStateFlow("")
    val pdfContentText: StateFlow<String> = _pdfContentText.asStateFlow()

    private val _pdfTitleInput = MutableStateFlow("የእኔ የፈጠራ ስራ (eBook)")
    val pdfTitleInput: StateFlow<String> = _pdfTitleInput.asStateFlow()

    // Latest Generated Outputs
    private val _latestOutputText = MutableStateFlow("")
    val latestOutputText: StateFlow<String> = _latestOutputText.asStateFlow()

    // General operations toast status
    private val _operationStatus = MutableSharedFlow<String>()
    val operationStatus: SharedFlow<String> = _operationStatus.asSharedFlow()

    // Database History Flow
    val creationsHistory: StateFlow<List<CreationItem>> = repository.allCreations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setMode(mode: CreatorMode) {
        _currentMode.value = mode
        _screenState.value = ScreenState.Idle
    }

    fun updateTextPrompt(value: String) { _textPrompt.value = value }
    fun updateImagePrompt(value: String) { _imagePrompt.value = value }
    fun updateThumbnailTheme(value: String) { _thumbnailTheme.value = value }
    fun updateThumbnailStyle(value: String) { _thumbnailStyle.value = value }
    fun updateThumbnailTitle(value: String) { _thumbnailTitle.value = value }
    fun updateThumbnailSubtitle(value: String) { _thumbnailSubtitle.value = value }
    fun updateForexQuery(value: String) { _forexQuery.value = value }
    fun updateTranslationInput(value: String) { _translationInput.value = value }
    fun toggleTranslationDirection() { _isAmharicToEnglish.value = !_isAmharicToEnglish.value }
    fun updatePdfContentText(value: String) { _pdfContentText.value = value }
    fun updatePdfTitleInput(value: String) { _pdfTitleInput.value = value }

    fun shareToPdfContent(text: String) {
        _pdfContentText.value = text
        _currentMode.value = CreatorMode.EBOOK_PDF
    }

    // Call Gemini API based on mode
    fun generateContent() {
        viewModelScope.launch {
            val mode = _currentMode.value
            _screenState.value = ScreenState.Loading

            val prompt: String
            val contentType: String
            val systemInstruction: String
            val title: String

            when (mode) {
                CreatorMode.TEXT_GENERATOR -> {
                    if (_textPrompt.value.isBlank()) {
                        _screenState.value = ScreenState.Error("እባክዎ ርዕስ ወይም ሀሳብ ያስገቡ።")
                        return@launch
                    }
                    prompt = _textPrompt.value
                    contentType = "TEXT"
                    title = "ጽሑፍ: " + if (prompt.length > 15) prompt.take(15) + "..." else prompt
                    systemInstruction = "You are an expert Amharic creative writer, copywriter, and professional content creator. Craft engaging, highly structured, clean Amharic (አማርኛ) content. Organize sections visually with beautiful headers and modern bullet points."
                }
                CreatorMode.IMAGE_GENERATOR -> {
                    if (_imagePrompt.value.isBlank()) {
                        _screenState.value = ScreenState.Error("እባክዎ የምስሉን መግለጫ ያስገቡ።")
                        return@launch
                    }
                    prompt = "ምስል ሀሳብ (Image Prompt Idea): " + _imagePrompt.value
                    contentType = "IMAGE"
                    title = "የምስል ፕሮምፕት: " + if (_imagePrompt.value.length > 15) _imagePrompt.value.take(15) + "..." else _imagePrompt.value
                    systemInstruction = "You are an expert AI digital painter, visual artist, and image prompt engineer. Based on the user's brief Amharic/English description, generate a highly detailed prompt blueprint in BOTH Amharic (አማርኛ) and English. Include highly specific prompt elements (e.g. photorealistic, cinematic lighting, style context, camera choice) suitable for Stable Diffusion, Midjourney, or Imagen. Include safe visual concepts representing modern positive Ethiopia and Ethiopian beauty."
                }
                CreatorMode.THUMBNAIL_CREATOR -> {
                    if (_thumbnailTheme.value.isBlank()) {
                        _screenState.value = ScreenState.Error("እባክዎ የዩቱብ ቪዲዮውን ርዕሰ ጉዳይ ያስገቡ።")
                        return@launch
                    }
                    prompt = "የዩቱብ ታምብነይል ዲዛይን ገጽታ ለ: ${_thumbnailTheme.value} (የዲዛይን ስታይል: ${_thumbnailStyle.value})"
                    contentType = "THUMBNAIL"
                    title = "ታምብነይል: " + if (_thumbnailTheme.value.length > 15) _thumbnailTheme.value.take(15) + "..." else _thumbnailTheme.value
                    systemInstruction = "You are an elite YouTube designer. Base on the video topic, explain top high-click-through visual elements, placement recommendations, background textures, and high-impact style advice. Output: 1. Catchy primary dynamic titles in Amharic, 2. Contrast color recommendation, 3. Image blueprint. PLEASE also output in a special final section exactly what labels to write on the graphic, following this format:\n[TITLE]: (Catchy Amharic Title)\n[SUBTITLE]: (Catchy Amharic Subtitle)."
                }
                CreatorMode.FOREX_ASSISTANT -> {
                    if (_forexQuery.value.isBlank()) {
                        _screenState.value = ScreenState.Error("እባክዎ የገንዘብ ጥንድ (e.g. USD/ETB) ይጻፉ።")
                        return@launch
                    }
                    prompt = "የገንዘብ ጥንድ ወይም ገበያ ትንተና: ${_forexQuery.value}"
                    contentType = "FOREX"
                    title = "ፎሬክስ: ${_forexQuery.value}"
                    systemInstruction = "You are an expert financial consultant, stock analyst, and Forex advisor specializing in official trade exchange rates and Ethiopian Birr (ETB) analytics. Deliver an educational, comprehensive Forex analysis in professional Amharic. Include: 1) Market Trends & Patterns, 2) Key levels to watch, 3) Risk factors to consider, and 4) Clear, instructive disclaimer that this is educational content."
                }
                CreatorMode.TRANSLATION -> {
                    if (_translationInput.value.isBlank()) {
                        _screenState.value = ScreenState.Error("እባክዎ የሚተረጎም ጽሑፍ ያስገቡ።")
                        return@launch
                    }
                    val direction = if (_isAmharicToEnglish.value) "Amharic to English" else "English to Amharic"
                    prompt = "Translate from $direction: ${_translationInput.value}"
                    contentType = "TRANSLATION"
                    title = "ትርጉም: " + if (_translationInput.value.length > 15) _translationInput.value.take(15) + "..." else _translationInput.value
                    systemInstruction = "You are an elite bilingual translator. Translate the text accurately. Structure your reply nicely with: 1) Direct Translation (የቀጥታ ትርጉም), 2) Pronunciation and Key terms table, 3) Cultural notes or figurative idioms explain in clear detail."
                }
                else -> return@launch
            }

            val result = repository.generateAICreatorContent(prompt, contentType, systemInstruction)
            result.onSuccess { text ->
                _latestOutputText.value = text
                _screenState.value = ScreenState.Success(text)

                // If mode is Thumbnail, let's extract [TITLE] and [SUBTITLE] if present to sync live interactive UI values
                if (mode == CreatorMode.THUMBNAIL_CREATOR) {
                    val titleRegex = "\\[TITLE\\]:(.*)".toRegex(RegexOption.IGNORE_CASE)
                    val subtitleRegex = "\\[SUBTITLE\\]:(.*)".toRegex(RegexOption.IGNORE_CASE)
                    
                    val titleMatch = titleRegex.find(text)
                    val subtitleMatch = subtitleRegex.find(text)

                    titleMatch?.groupValues?.getOrNull(1)?.trim()?.let { extracted ->
                        _thumbnailTitle.value = extracted
                    }
                    subtitleMatch?.groupValues?.getOrNull(1)?.trim()?.let { extracted ->
                        _thumbnailSubtitle.value = extracted
                    }
                }

                // Autocommit to history
                val item = CreationItem(
                    title = title,
                    contentType = contentType,
                    inputPrompt = if (mode == CreatorMode.TRANSLATION) _translationInput.value else prompt,
                    outputContent = text
                )
                repository.insertCreation(item)
            }.onFailure { exception ->
                val errMessage = exception.message ?: "ያልታወቀ ስህተት አጋጥሟል። እባክዎ ኢንተርኔትዎን አረጋግጠው እንደገና ይሞክሩ።"
                _screenState.value = ScreenState.Error(errMessage)
            }
        }
    }

    fun deleteHistoryItem(id: Int) {
        viewModelScope.launch {
            repository.deleteCreation(id)
            _operationStatus.emit("የተመረጠው ታሪክ ተሰርዟል።")
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _operationStatus.emit("የቀድሞ ስራዎች ታሪክ በሙሉ ተሰርዟል።")
        }
    }

    // Export custom designed PDF using Android Graphics PDF Engine
    fun exportToPdf(context: Context): File? {
        if (_pdfContentText.value.isBlank()) {
            viewModelScope.launch { _operationStatus.emit("የሚቀየር ጽሑፍ ባዶ ነው።") }
            return null
        }

        try {
            val pdfDocument = PdfDocument()
            val textPaint = Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }

            val titlePaint = Paint().apply {
                color = android.graphics.Color.parseColor("#FFD700") // Gold accent
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                isAntiAlias = true
            }

            val subtitlePaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                isAntiAlias = true
            }

            val headerPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#0F1A35") // Dark Blue Header
                style = Paint.Style.FILL
            }

            val borderPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#FFD700") // Gold border
                strokeWidth = 3f
                style = Paint.Style.STROKE
            }

            val labelPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#0F1A35")
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                isAntiAlias = true
            }

            // Split content into printable paragraphs/lines
            val margin = 40f
            val pageWidth = 595 // A4 dimensions in postscript points (72 points per inch)
            val pageHeight = 842
            val maxTextWidth = pageWidth - (margin * 2)

            val lines = mutableListOf<String>()
            val rawParagraphs = _pdfContentText.value.split("\n")
            for (paragraph in rawParagraphs) {
                if (paragraph.trim().isEmpty()) {
                    lines.add("")
                    continue
                }
                val words = paragraph.split(" ")
                var currentLine = ""
                for (word in words) {
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    val testLineWidth = textPaint.measureText(testLine)
                    if (testLineWidth > maxTextWidth) {
                        lines.add(currentLine)
                        currentLine = word
                    } else {
                        currentLine = testLine
                    }
                }
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine)
                }
            }

            // Draw multi-page PDF if needed
            var currentLineIndex = 0
            var pageNumber = 1

            while (currentLineIndex < lines.size || currentLineIndex == 0) {
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas: Canvas = page.canvas

                // Draw luxury background / headers elements on the first page
                if (pageNumber == 1) {
                    // Top banner
                    canvas.drawRect(0f, 0f, pageWidth.toFloat(), 130f, headerPaint)
                    canvas.drawLine(0f, 130f, pageWidth.toFloat(), 130f, borderPaint)

                    // Ethio Creator Logos
                    canvas.drawText("ETHIO AI CREATOR", 40f, 60f, titlePaint)
                    canvas.drawText("በ አማርኛ የተዘጋጀ ኢ-መጽሐፍ (eBook / PDF Document)", 40f, 90f, subtitlePaint)
                    canvas.drawText("ቀን: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(System.currentTimeMillis())}", 40f, 112f, subtitlePaint)

                    // Title tag
                    canvas.drawText("ርዕስ (Document Title):", 40f, 170f, labelPaint)
                    val drawTitle = if (_pdfTitleInput.value.length > 50) _pdfTitleInput.value.take(50) + "..." else _pdfTitleInput.value
                    val boldTitlePaint = Paint(textPaint).apply { typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD) }
                    canvas.drawText(drawTitle, 40f, 195f, boldTitlePaint)
                    canvas.drawLine(40f, 210f, pageWidth - 40f, 210f, textPaint.apply { color = android.graphics.Color.LTGRAY })

                    // Draw body text starting below title
                    var yOffset = 250f
                    val linesPerPageOnFirstPage = 25
                    var linesDrawn = 0

                    while (currentLineIndex < lines.size && linesDrawn < linesPerPageOnFirstPage) {
                        val line = lines[currentLineIndex]
                        if (line.isNotEmpty()) {
                            canvas.drawText(line, margin, yOffset, textPaint.apply { color = android.graphics.Color.BLACK })
                        }
                        yOffset += 22f
                        currentLineIndex++
                        linesDrawn++
                    }
                } else {
                    // Regular secondary pages
                    canvas.drawRect(0f, 0f, pageWidth.toFloat(), 45f, headerPaint)
                    canvas.drawLine(0f, 45f, pageWidth.toFloat(), 45f, borderPaint)
                    canvas.drawText("Ethio AI Creator - ${pageNumber}", 40f, 28f, subtitlePaint)

                    var yOffset = 90f
                    val linesPerPage = 32
                    var linesDrawn = 0

                    while (currentLineIndex < lines.size && linesDrawn < linesPerPage) {
                        val line = lines[currentLineIndex]
                        if (line.isNotEmpty()) {
                            canvas.drawText(line, margin, yOffset, textPaint.apply { color = android.graphics.Color.BLACK })
                        }
                        yOffset += 22f
                        currentLineIndex++
                        linesDrawn++
                    }
                }

                // Page frame border outline
                canvas.drawRect(15f, 15f, pageWidth - 15f, pageHeight - 15f, borderPaint)
                canvas.drawText("ገጽ $pageNumber", (pageWidth / 2) - 15f, pageHeight - 30f, textPaint.apply { textSize = 10f; color = android.graphics.Color.GRAY })

                pdfDocument.finishPage(page)
                pageNumber++

                // Security check for empty list loop
                if (lines.isEmpty() && currentLineIndex == 0) {
                    break
                }
            }

            // Save document to downloads directory
            val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(documentsDir, "EthioCreator_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()

            viewModelScope.launch {
                _operationStatus.emit("PDF በስኬት ተዘጋጅቷል፡ ${file.name}")
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            viewModelScope.launch {
                _operationStatus.emit("PDF ማመንጨት አልተሳካም፡ ${e.message}")
            }
            return null
        }
    }
}

class EthioCreatorViewModelFactory(private val repository: CreationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EthioCreatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EthioCreatorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
