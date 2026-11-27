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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.UserProfile
import com.example.ui.i18n.AppLanguages
import com.example.ui.i18n.LanguagePickerSheet
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.i18n.appStringsFor
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.NavyPrimary
import com.example.ui.viewmodel.AiStreamState
import com.example.ui.viewmodel.AiTeacherViewModel

@Composable
fun AiTeacherScreen(
    viewModel: AiTeacherViewModel,
    profile: UserProfile?,
    onOpenLanguagePicker: () -> Unit = {}
) {
    val uiStrings = LocalAppStrings.current
    val chatHistory by viewModel.chatHistory.collectAsStateWithLifecycle()
    val streamState by viewModel.streamState.collectAsStateWithLifecycle()
    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val pendingParagraph by viewModel.pendingParagraphContext.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    var showAiLangSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val activeLang = remember(selectedLanguage) { AppLanguages.byEnglishName(selectedLanguage) }
    val aiStrings = remember(activeLang.code) { appStringsFor(activeLang.code) }

    val isPremium = (profile?.planTier ?: "FREE") != "FREE"
    val queriesToday = profile?.aiQueriesToday ?: 0L
    val studentClass = profile?.classLevel ?: "Class 10"
    val board = profile?.board ?: "CBSE"

    val quickPrompts = aiStrings.quickPrompts

    LaunchedEffect(chatHistory.size, streamState) {
        val total = chatHistory.size + if (streamState is AiStreamState.Streaming || streamState is AiStreamState.Thinking) 1 else 0
        if (total > 0) {
            listState.animateScrollToItem(total - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        // AI Teacher Brand Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_ai_teacher),
                            contentDescription = "AI Teacher Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                        )
                        Column {
                            Text(
                                text = uiStrings.aiTeacher,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$studentClass • $board • " +
                                    if (isPremium) "Unlimited Pro" else "Free: $queriesToday/10",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isPremium) EmeraldSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (chatHistory.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearHistory() }) {
                            Icon(Icons.Filled.DeleteSweep, contentDescription = "Clear AI Chat History")
                        }
                    }
                }

                // Quick 15-Language Bar (EN, हिन्दी, தமிழ் + All 15 Languages Picker)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        FilterChip(
                            selected = selectedLanguage == "English",
                            onClick = { viewModel.setLanguage("English") },
                            label = { Text("English") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedLanguage == "Hindi",
                            onClick = { viewModel.setLanguage("Hindi") },
                            label = { Text("हिन्दी") }
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedLanguage == "Tamil",
                            onClick = { viewModel.setLanguage("Tamil") },
                            label = { Text("தமிழ்") }
                        )
                    }
                    if (activeLang.code !in setOf("en", "hi", "ta")) {
                        item {
                            FilterChip(
                                selected = true,
                                onClick = { showAiLangSheet = true },
                                label = { Text("${activeLang.nativeName} (${activeLang.englishName})") }
                            )
                        }
                    }
                    item {
                        FilterChip(
                            selected = false,
                            onClick = { showAiLangSheet = true },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Translate,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(uiStrings.allLanguagesLabel) },
                            modifier = Modifier.testTag("ai_all_languages_chip")
                        )
                    }
                }
            }
        }

        // Chat Stream Area
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (chatHistory.isEmpty() && streamState is AiStreamState.Idle) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = AmberAccent)
                                Text(
                                    text = aiStrings.askAnythingTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = aiStrings.askAnythingSubtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(chatHistory, key = { it.id.ifBlank { "${it.sender}_${it.message.hashCode()}" } }) { msg ->
                val isUser = msg.sender == "USER"
                ChatBubble(text = msg.message, isUser = isUser, language = msg.language)
            }

            when (val state = streamState) {
                is AiStreamState.Thinking -> {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Text(
                                text = aiStrings.aiThinking,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                is AiStreamState.Streaming -> {
                    item {
                        ChatBubble(text = state.partialText, isUser = false, language = selectedLanguage)
                    }
                }
                is AiStreamState.Error -> {
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
                is AiStreamState.Idle -> Unit
            }
        }

        // Attached Textbook Paragraph Banner ("Explain This Paragraph")
        if (!pendingParagraph.isNullOrBlank()) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Textbook Paragraph Attached",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = pendingParagraph!!,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(onClick = { viewModel.clearParagraphContext() }) {
                        Icon(Icons.Filled.Close, contentDescription = "Remove paragraph context")
                    }
                }
            }
        }

        // Quick Academic Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!pendingParagraph.isNullOrBlank()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldSecondary,
                        modifier = Modifier
                            .testTag("explain_attached_paragraph_button")
                            .clickable {
                                viewModel.askTeacher(
                                    question = aiStrings.explainPromptTemplate,
                                    studentClass = studentClass,
                                    board = board,
                                    isPremium = isPremium,
                                    queriesUsedToday = queriesToday
                                )
                            }
                    ) {
                        Text(
                            text = aiStrings.explainInSimpleWords,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
            items(quickPrompts) { prompt ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable {
                        viewModel.askTeacher(
                            question = prompt,
                            studentClass = studentClass,
                            board = board,
                            isPremium = isPremium,
                            queriesUsedToday = queriesToday
                        )
                    }
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Chat Composer
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(aiStrings.aiPlaceholder)
                    },
                    maxLines = 3,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_input")
                )
                val isBusy = streamState is AiStreamState.Thinking || streamState is AiStreamState.Streaming
                Surface(
                    shape = CircleShape,
                    color = if (!isBusy && (inputText.isNotBlank() || !pendingParagraph.isNullOrBlank())) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("ai_send_button")
                        .clickable(enabled = !isBusy) {
                            val prompt = inputText.ifBlank {
                                if (!pendingParagraph.isNullOrBlank()) "Explain this paragraph simply." else ""
                            }
                            if (prompt.isNotBlank()) {
                                inputText = ""
                                viewModel.askTeacher(
                                    question = prompt,
                                    studentClass = studentClass,
                                    board = board,
                                    isPremium = isPremium,
                                    queriesUsedToday = queriesToday
                                )
                            }
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send to AI Teacher",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    if (showAiLangSheet) {
        LanguagePickerSheet(
            currentLanguage = activeLang,
            title = uiStrings.chooseLanguageTitle,
            subtitle = uiStrings.chooseLanguageSubtitle,
            onSelectLanguage = { lang ->
                viewModel.setLanguage(lang.englishName)
            },
            onDismiss = { showAiLangSheet = false }
        )
    }
}

@Composable
private fun ChatBubble(text: String, isUser: Boolean, language: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 18.dp
            ),
            color = if (isUser) NavyPrimary else MaterialTheme.colorScheme.surface,
            tonalElevation = if (isUser) 0.dp else 2.dp,
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (!isUser) {
                    Text(
                        text = "AI Teacher • $language",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
