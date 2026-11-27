package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.Chapter
import com.example.data.model.CurriculumCatalog
import com.example.data.model.ReadingProgress
import com.example.data.model.Textbook
import com.example.ui.components.TextbookCoverCard
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.subjectColor

@Composable
fun LibraryScreen(
    textbooks: List<Textbook>,
    readingProgressList: List<ReadingProgress>,
    onOpenBook: (String) -> Unit
) {
    val strings = LocalAppStrings.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedSubject by remember { mutableStateOf("All") }
    var selectedBoard by remember { mutableStateOf("All") }

    val progressByBook = remember(readingProgressList) {
        readingProgressList.associateBy { it.bookId }
    }

    val filteredBooks = remember(textbooks, searchQuery, selectedCategory, selectedSubject, selectedBoard) {
        textbooks.filter { book ->
            val matchesQuery = searchQuery.isBlank() ||
                book.title.contains(searchQuery, ignoreCase = true) ||
                book.subject.contains(searchQuery, ignoreCase = true) ||
                book.classLevel.contains(searchQuery, ignoreCase = true)
            val matchesCat = selectedCategory == "All" || book.category == selectedCategory
            val matchesSubj = selectedSubject == "All" || book.subject.equals(selectedSubject, ignoreCase = true)
            val matchesBoard = selectedBoard == "All" || book.board.equals(selectedBoard, ignoreCase = true)
            matchesQuery && matchesCat && matchesSubj && matchesBoard
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Editorial Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(142.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_library_banner),
                        contentDescription = "Digital Textbook Library Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xE6183B56),
                                        Color(0x88183B56),
                                        Color.Transparent
                                    )
                                )
                            )
                            .padding(18.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = strings.digitalLibrary,
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Class 1–12 • CBSE, ICSE & State Boards",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.88f)
                            )
                        }
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(strings.searchBooksPlaceholder) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("library_search_input")
            )
        }

        // Class Category Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(CurriculumCatalog.categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }
        }

        // Subject & Board Quick Filters
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val subjects = listOf("All") + CurriculumCatalog.allSubjects
                items(subjects) { subj ->
                    FilterChip(
                        selected = selectedSubject == subj,
                        onClick = { selectedSubject = subj },
                        label = { Text(subj) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.width(4.dp))
                }
                items(listOf("All", "CBSE", "ICSE", "State Board")) { board ->
                    if (board != "All") {
                        FilterChip(
                            selected = selectedBoard == board,
                            onClick = {
                                selectedBoard = if (selectedBoard == board) "All" else board
                            },
                            label = { Text(board) }
                        )
                    }
                }
            }
        }

        // Book Grid (2 columns per row)
        if (filteredBooks.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "No textbooks match your filters",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Try switching the class category or clearing the search bar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            val rows = filteredBooks.chunked(2)
            items(rows) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { book ->
                        val pct = (progressByBook[book.id]?.progressPercent ?: 0L).toInt()
                        TextbookCoverCard(
                            book = book,
                            progressPercent = pct,
                            onClick = { onOpenBook(book.id) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextbookDetailScreen(
    book: Textbook,
    chapters: List<Chapter>,
    progress: ReadingProgress?,
    isSaved: Boolean,
    onBack: () -> Unit,
    onToggleSaveBook: () -> Unit,
    onOpenChapter: (chapterId: String) -> Unit
) {
    val strings = LocalAppStrings.current
    val accent = subjectColor(book.subject)
    val completedSet = progress?.completedChapters.orEmpty().toSet()
    val bookmarkedChapters = progress?.bookmarkedPages.orEmpty()
        .map { it.substringBefore(":") }
        .toSet()
    val pct = (progress?.progressPercent ?: 0L).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = book.subject,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleSaveBook,
                        modifier = Modifier.testTag("bookmark_book_button")
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Save Book",
                            tint = if (isSaved) AmberAccent else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Book Cover Hero Card
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(accent, accent.copy(alpha = 0.78f))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MetadataBadge(book.board)
                                MetadataBadge(book.classLevel)
                                MetadataBadge(book.medium)
                                if (book.premiumOnly) {
                                    Surface(shape = RoundedCornerShape(8.dp), color = AmberAccent) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Icon(
                                                Icons.Filled.WorkspacePremium,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                "PREMIUM",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Text(
                                text = book.title,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = book.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${chapters.size} Chapters • Session ${book.academicYear}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = "$pct% Read",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            LinearProgressIndicator(
                                progress = { (pct / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = AmberAccent,
                                trackColor = Color.White.copy(alpha = 0.22f),
                                strokeCap = StrokeCap.Round
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val targetChapter = progress?.lastChapterId
                                            ?.takeIf { id -> chapters.any { it.id == id } }
                                            ?: chapters.firstOrNull()?.id
                                        if (targetChapter != null) {
                                            onOpenChapter(targetChapter)
                                        }
                                    },
                                    enabled = chapters.isNotEmpty(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = accent
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("start_reading_button")
                                ) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (progress != null) strings.continueReading else strings.startReading,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = onToggleSaveBook,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                    modifier = Modifier.height(48.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Filled.DownloadDone else Icons.Filled.BookmarkBorder,
                                        contentDescription = "Offline Save"
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isSaved) "Saved" else "Save")
                                }
                            }
                        }
                    }
                }
            }

            // Chapters Header
            item {
                Text(
                    text = "${strings.chapters} (${chapters.size})",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Ordered Chapter Cards
            items(chapters, key = { it.id }) { chapter ->
                val isCompleted = chapter.id in completedSet
                val hasBookmark = chapter.id in bookmarkedChapters
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chapter_card_${chapter.id}")
                        .clickable { onOpenChapter(chapter.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCompleted) EmeraldSecondary.copy(alpha = 0.16f)
                                    else accent.copy(alpha = 0.12f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = EmeraldSecondary
                                )
                            } else {
                                Text(
                                    text = "${chapter.chapterNumber}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = accent
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = chapter.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = chapter.summary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${chapter.paragraphs.size} pages • ${chapter.estimatedMinutes} min read" +
                                    if (isCompleted) " • Completed" else "",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isCompleted) EmeraldSecondary else MaterialTheme.colorScheme.primary
                            )
                        }

                        if (hasBookmark) {
                            Icon(
                                imageVector = Icons.Filled.Bookmark,
                                contentDescription = "Bookmarked",
                                tint = AmberAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetadataBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}
