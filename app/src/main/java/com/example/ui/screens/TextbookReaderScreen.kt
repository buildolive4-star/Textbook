package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Chapter
import com.example.data.model.PersonalNote
import com.example.data.model.ReadingProgress
import com.example.data.model.Textbook
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkTextPrimary
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.MerriweatherFontFamily
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SepiaBg
import com.example.ui.theme.SepiaSurface
import com.example.ui.theme.SepiaText
import com.example.ui.theme.SurfaceWhite
import com.example.ui.theme.WarmWhiteBg

private enum class ReaderThemeMode { LIGHT, SEPIA, DARK }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextbookReaderScreen(
    book: Textbook,
    chapters: List<Chapter>,
    initialChapterId: String,
    progress: ReadingProgress?,
    chapterNotes: List<PersonalNote>,
    onBack: () -> Unit,
    onSaveProgress: (
        chapter: Chapter,
        pageIndex: Int,
        completedChapters: List<String>,
        bookmarkedPages: List<String>,
        highlightedParagraphs: List<String>
    ) -> Unit,
    onAddPersonalNote: (chapter: Chapter, content: String, excerpt: String?) -> Unit,
    onDeleteNote: (noteId: String) -> Unit,
    onExplainParagraph: (subject: String, paragraph: String) -> Unit
) {
    val initialChapterIndex = remember(chapters, initialChapterId) {
        chapters.indexOfFirst { it.id == initialChapterId }.coerceAtLeast(0)
    }
    var currentChapterIdx by remember(initialChapterIndex) { mutableIntStateOf(initialChapterIndex) }
    val currentChapter = chapters.getOrNull(currentChapterIdx)

    var fontSizeSp by remember { mutableFloatStateOf(16.5f) }
    var readerTheme by remember { mutableStateOf(ReaderThemeMode.LIGHT) }
    var fullScreen by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showTocSheet by remember { mutableStateOf(false) }
    var showNotesSheet by remember { mutableStateOf(false) }
    var showFontSheet by remember { mutableStateOf(false) }
    var noteDraftExcerpt by remember { mutableStateOf<String?>(null) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var noteDraftText by remember { mutableStateOf("") }

    val completedChapters = remember(progress) {
        mutableStateListOf<String>().apply { addAll(progress?.completedChapters.orEmpty()) }
    }
    val bookmarkedPages = remember(progress) {
        mutableStateListOf<String>().apply { addAll(progress?.bookmarkedPages.orEmpty()) }
    }
    val highlightedParagraphs = remember(progress) {
        mutableStateListOf<String>().apply { addAll(progress?.highlightedParagraphs.orEmpty()) }
    }

    val bgColor = when (readerTheme) {
        ReaderThemeMode.LIGHT -> WarmWhiteBg
        ReaderThemeMode.SEPIA -> SepiaBg
        ReaderThemeMode.DARK -> DarkBg
    }
    val cardColor = when (readerTheme) {
        ReaderThemeMode.LIGHT -> SurfaceWhite
        ReaderThemeMode.SEPIA -> SepiaSurface
        ReaderThemeMode.DARK -> DarkSurface
    }
    val textColor = when (readerTheme) {
        ReaderThemeMode.LIGHT -> Color(0xFF1E293B)
        ReaderThemeMode.SEPIA -> SepiaText
        ReaderThemeMode.DARK -> DarkTextPrimary
    }

    // Auto-save reading position whenever chapter changes
    LaunchedEffect(currentChapter?.id) {
        if (currentChapter != null) {
            onSaveProgress(
                currentChapter,
                0,
                completedChapters.toList(),
                bookmarkedPages.toList(),
                highlightedParagraphs.toList()
            )
        }
    }

    if (currentChapter == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Chapter not available", color = textColor)
                TextButton(onClick = onBack) { Text("Go Back") }
            }
        }
        return
    }

    val pageKey = "${currentChapter.id}:0"
    val isBookmarked = pageKey in bookmarkedPages
    val isChapterDone = currentChapter.id in completedChapters

    Scaffold(
        containerColor = bgColor,
        topBar = {
            if (!fullScreen) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = cardColor,
                        titleContentColor = textColor,
                        navigationIconContentColor = textColor,
                        actionIconContentColor = textColor
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("reader_back_button")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    title = {
                        Column {
                            Text(
                                text = "Ch. ${currentChapter.chapterNumber}: ${currentChapter.title}",
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = book.title,
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha = 0.65f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showSearch = !showSearch }) {
                            Icon(Icons.Filled.Search, contentDescription = "Search inside textbook")
                        }
                        IconButton(onClick = { showFontSheet = true }) {
                            Icon(Icons.Filled.FormatSize, contentDescription = "Font and Theme Settings")
                        }
                        IconButton(
                            onClick = {
                                if (isBookmarked) bookmarkedPages.remove(pageKey)
                                else bookmarkedPages.add(pageKey)
                                onSaveProgress(
                                    currentChapter,
                                    0,
                                    completedChapters.toList(),
                                    bookmarkedPages.toList(),
                                    highlightedParagraphs.toList()
                                )
                            },
                            modifier = Modifier.testTag("reader_bookmark_button")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = "Bookmark page",
                                tint = if (isBookmarked) AmberAccent else textColor
                            )
                        }
                        IconButton(onClick = { fullScreen = true }) {
                            Icon(Icons.Filled.Fullscreen, contentDescription = "Full-screen reading mode")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (!fullScreen) {
                Surface(
                    color = cardColor,
                    tonalElevation = 3.dp,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { if (currentChapterIdx > 0) currentChapterIdx-- },
                            enabled = currentChapterIdx > 0
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Chapter", tint = textColor)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showTocSheet = true },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Chapters", style = MaterialTheme.typography.labelMedium)
                            }
                            OutlinedButton(
                                onClick = { showNotesSheet = true },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("reader_notes_button")
                            ) {
                                Icon(Icons.Filled.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Notes (${chapterNotes.size})", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        IconButton(
                            onClick = { if (currentChapterIdx < chapters.lastIndex) currentChapterIdx++ },
                            enabled = currentChapterIdx < chapters.lastIndex
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Chapter", tint = textColor)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .let { if (fullScreen) it.windowInsetsPadding(WindowInsets.safeDrawing) else it }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (showSearch && !fullScreen) {
                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search inside this chapter...") },
                            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Filled.Close, contentDescription = "Clear search")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Chapter Title & Summary Banner
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "CHAPTER ${currentChapter.chapterNumber}",
                            style = MaterialTheme.typography.labelMedium,
                            color = EmeraldSecondary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentChapter.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = textColor,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = cardColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, textColor.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                        ) {
                            Text(
                                text = currentChapter.summary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor.copy(alpha = 0.8f),
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }

                // Textbook Paragraphs with Highlight, Note & AI "Explain This Paragraph"
                val matchingParagraphs = currentChapter.paragraphs.mapIndexed { idx, para -> idx to para }
                    .filter { (_, para) -> searchQuery.isBlank() || para.contains(searchQuery, ignoreCase = true) }

                itemsIndexed(matchingParagraphs) { _, (origIdx, paragraph) ->
                    val paraKey = "${currentChapter.id}:$origIdx"
                    val isHighlighted = paraKey in highlightedParagraphs

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isHighlighted) AmberAccent.copy(alpha = 0.16f) else cardColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isHighlighted) 1.5.dp else 1.dp,
                                color = if (isHighlighted) AmberAccent else textColor.copy(alpha = 0.07f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Section ${origIdx + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = textColor.copy(alpha = 0.55f)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            if (isHighlighted) highlightedParagraphs.remove(paraKey)
                                            else highlightedParagraphs.add(paraKey)
                                            onSaveProgress(
                                                currentChapter,
                                                origIdx,
                                                completedChapters.toList(),
                                                bookmarkedPages.toList(),
                                                highlightedParagraphs.toList()
                                            )
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Highlight,
                                            contentDescription = "Highlight paragraph",
                                            tint = if (isHighlighted) AmberAccent else textColor.copy(alpha = 0.55f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            noteDraftExcerpt = paragraph.take(180)
                                            noteDraftText = ""
                                            showAddNoteDialog = true
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.EditNote,
                                            contentDescription = "Add personal note",
                                            tint = textColor.copy(alpha = 0.65f),
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = paragraph,
                                fontFamily = MerriweatherFontFamily,
                                fontSize = fontSizeSp.sp,
                                lineHeight = (fontSizeSp * 1.62f).sp,
                                color = textColor
                            )

                            // Explain This Paragraph CTA
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NavyPrimary.copy(alpha = if (readerTheme == ReaderThemeMode.DARK) 0.55f else 0.09f),
                                modifier = Modifier
                                    .testTag("explain_paragraph_$origIdx")
                                    .clickable { onExplainParagraph(book.subject, paragraph) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = if (readerTheme == ReaderThemeMode.DARK) AmberAccent else NavyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Explain This Paragraph with AI Teacher",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (readerTheme == ReaderThemeMode.DARK) Color.White else NavyPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Key Takeaways Box
                if (currentChapter.keyPoints.isNotEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = EmeraldSecondary.copy(alpha = 0.12f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Key Concepts & Formulas",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = EmeraldSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                                currentChapter.keyPoints.forEach { point ->
                                    Text(
                                        text = "• $point",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = textColor
                                    )
                                }
                            }
                        }
                    }
                }

                // Mark Complete CTA
                item {
                    Button(
                        onClick = {
                            if (isChapterDone) completedChapters.remove(currentChapter.id)
                            else completedChapters.add(currentChapter.id)
                            onSaveProgress(
                                currentChapter,
                                currentChapter.paragraphs.lastIndex.coerceAtLeast(0),
                                completedChapters.toList(),
                                bookmarkedPages.toList(),
                                highlightedParagraphs.toList()
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isChapterDone) EmeraldSecondary else NavyPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("mark_chapter_complete_button")
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isChapterDone) "Chapter Completed" else "Mark Chapter as Completed",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }

            // Fullscreen Exit Floating Button
            AnimatedVisibility(
                visible = fullScreen,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavyPrimary.copy(alpha = 0.85f),
                    shadowElevation = 4.dp
                ) {
                    IconButton(onClick = { fullScreen = false }) {
                        Icon(
                            imageVector = Icons.Filled.FullscreenExit,
                            contentDescription = "Exit Full-Screen",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    // Font Size & Reading Mode Bottom Sheet
    if (showFontSheet) {
        ModalBottomSheet(onDismissRequest = { showFontSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Reading Appearance", style = MaterialTheme.typography.titleLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Font Size (${fontSizeSp.toInt()}sp)", style = MaterialTheme.typography.bodyLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { fontSizeSp = (fontSizeSp - 1.5f).coerceAtLeast(13f) }) {
                            Text("A-")
                        }
                        OutlinedButton(onClick = { fontSizeSp = (fontSizeSp + 1.5f).coerceAtMost(24f) }) {
                            Text("A+")
                        }
                    }
                }
                Text("Paper Mode", style = MaterialTheme.typography.bodyLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ReaderThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = readerTheme == mode,
                            onClick = { readerTheme = mode },
                            label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Table of Contents Sheet
    if (showTocSheet) {
        ModalBottomSheet(onDismissRequest = { showTocSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Table of Contents", style = MaterialTheme.typography.titleLarge)
                chapters.forEachIndexed { idx, ch ->
                    val active = idx == currentChapterIdx
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (active) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                currentChapterIdx = idx
                                showTocSheet = false
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ch. ${ch.chapterNumber}: ${ch.title}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                            if (ch.id in completedChapters) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = "Done", tint = EmeraldSecondary)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Personal Notes Sheet
    if (showNotesSheet) {
        ModalBottomSheet(onDismissRequest = { showNotesSheet = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Chapter Personal Notes", style = MaterialTheme.typography.titleLarge)
                    Button(
                        onClick = {
                            noteDraftExcerpt = null
                            noteDraftText = ""
                            showAddNoteDialog = true
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("+ Add Note")
                    }
                }
                if (chapterNotes.isEmpty()) {
                    Text(
                        text = "No personal notes for this chapter yet. Tap the note icon on any paragraph or '+ Add Note' above.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    chapterNotes.forEach { note ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (!note.excerpt.isNullOrBlank()) {
                                        Text(
                                            text = "\"${note.excerpt}...\"",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
                                }
                                IconButton(onClick = { onDeleteNote(note.id) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete note")
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Add Note Dialog
    if (showAddNoteDialog) {
        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = { Text("Add Personal Note") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!noteDraftExcerpt.isNullOrBlank()) {
                        Text(
                            text = "Excerpt: \"${noteDraftExcerpt!!.take(100)}...\"",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        HorizontalDivider()
                    }
                    OutlinedTextField(
                        value = noteDraftText,
                        onValueChange = { noteDraftText = it.take(1500) },
                        label = { Text("Write your takeaway or formula reminder") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteDraftText.isNotBlank()) {
                            onAddPersonalNote(currentChapter, noteDraftText, noteDraftExcerpt)
                            showAddNoteDialog = false
                        }
                    }
                ) { Text("Save Note") }
            },
            dismissButton = {
                TextButton(onClick = { showAddNoteDialog = false }) { Text("Cancel") }
            }
        )
    }
}
