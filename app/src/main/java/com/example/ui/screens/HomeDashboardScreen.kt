package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.firebase.CoAdminSecurity
import com.example.data.model.Announcement
import com.example.data.model.LiveClass
import com.example.data.model.QuizAttempt
import com.example.data.model.ReadingProgress
import com.example.data.model.Textbook
import com.example.data.model.UserProfile
import com.example.ui.components.MetricPillCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.TextbookCoverCard
import com.example.ui.components.WeeklyActivityChart
import com.example.ui.i18n.LanguagePillChip
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SubjectEnglishColor
import com.example.ui.theme.SubjectHindiColor
import com.example.ui.theme.SubjectScienceColor
import com.example.ui.theme.SubjectSocialColor

@Composable
fun HomeDashboardScreen(
    profile: UserProfile?,
    textbooks: List<Textbook>,
    liveClasses: List<LiveClass>,
    readingProgressList: List<ReadingProgress>,
    quizAttempts: List<QuizAttempt>,
    announcements: List<Announcement>,
    isDarkMode: Boolean,
    onOpenLanguagePicker: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onSecretAdminTrigger: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenBook: (String) -> Unit,
    onContinueReading: (bookId: String, chapterId: String) -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenAiTeacher: () -> Unit,
    onOpenPractice: () -> Unit,
    onOpenNotesAndVideos: (initialTab: Int) -> Unit,
    onOpenPlannerAndProgress: () -> Unit
) {
    val strings = LocalAppStrings.current
    val currentLanguage = LocalAppLanguage.current
    val context = LocalContext.current
    var adminTapCount by remember { mutableIntStateOf(0) }
    var lastTapTimeMs by remember { mutableLongStateOf(0L) }

    val progressByBook = readingProgressList.associateBy { it.bookId }
    val continueProgress = readingProgressList.firstOrNull()
    val fallbackBook = textbooks.firstOrNull()
    val featuredLiveClass = liveClasses.firstOrNull { it.isLiveNow } ?: liveClasses.firstOrNull()

    val studentName = profile?.displayName?.substringBefore(" ") ?: "Scholar"
    val completedChaptersTotal = readingProgressList.sumOf { it.completedChapters.size }
    val studyHours = ((profile?.studyMinutesTotal ?: 0L) / 60.0)
    val studyHoursLabel = if (studyHours < 1.0) "${profile?.studyMinutesTotal ?: 0}m" else String.format("%.1fh", studyHours)
    val avgQuizScore = if (quizAttempts.isEmpty()) 0 else quizAttempts.map { it.scorePercent }.average().toInt()

    // Personalized recommendation
    val weakTopic = quizAttempts.firstOrNull { it.weakTopics.isNotEmpty() }?.weakTopics?.firstOrNull()
    val recommendedText = if (weakTopic != null) {
        "Revise \"$weakTopic\" with AI Teacher to boost your next exam score."
    } else if (continueProgress != null) {
        "Complete ${continueProgress.lastChapterTitle} in ${continueProgress.bookTitle} to keep your streak alive."
    } else {
        "Open your first textbook chapter and tap 'Explain This Paragraph' to try your Gemini AI Teacher."
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Top Textbook Brand Icon (5-Tap Secret Admin Trigger) + Greeting Header
        item {
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
                    // Top Textbook Icon — 5 taps opens Admin / Co-Admin Panel
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                val now = System.currentTimeMillis()
                                adminTapCount = if (now - lastTapTimeMs <= 2500L) adminTapCount + 1 else 1
                                lastTapTimeMs = now
                                if (adminTapCount >= 5) {
                                    adminTapCount = 0
                                    onSecretAdminTrigger()
                                }
                            }
                            .testTag("home_textbook_icon_secret_trigger"),
                        shape = RoundedCornerShape(14.dp),
                        shadowElevation = 3.dp,
                        color = NavyPrimary
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_icon_textbook),
                            contentDescription = "Textbook Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Column {
                        Text(
                            text = "${strings.hiGreeting}, $studentName",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${profile?.classLevel ?: "Class 10"} • ${profile?.board ?: "CBSE"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AmberAccent.copy(alpha = 0.16f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.LocalFireDepartment,
                                        contentDescription = "Streak",
                                        tint = AmberAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "${profile?.streakDays ?: 1}d",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberAccent
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    LanguagePillChip(
                        currentLanguage = currentLanguage,
                        onClick = onOpenLanguagePicker
                    )
                    IconButton(
                        onClick = onToggleDarkMode,
                        modifier = Modifier.testTag("toggle_theme_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = "Toggle Light or Dark Mode",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier.testTag("notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (announcements.isNotEmpty()) {
                                    Badge(containerColor = AmberAccent) {
                                        Text("${announcements.size}", color = Color.White)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Announcements & Notifications",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        // 1.5 Featured Live Zoom Class Banner (when scheduled/live by Admin or Co-Admin)
        if (featuredLiveClass != null) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (featuredLiveClass.isLiveNow) ErrorRed.copy(alpha = 0.10f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_live_zoom_banner")
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
                                .size(46.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (featuredLiveClass.isLiveNow) ErrorRed else NavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Videocam, contentDescription = "Live Zoom Class", tint = Color.White)
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (featuredLiveClass.isLiveNow) ErrorRed else AmberAccent
                                ) {
                                    Text(
                                        text = if (featuredLiveClass.isLiveNow) strings.liveOnZoom else strings.upcomingZoom,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = featuredLiveClass.scheduleLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = featuredLiveClass.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${featuredLiveClass.subject} • ${featuredLiveClass.teacherName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = {
                                if (CoAdminSecurity.isValidHttpsUrl(featuredLiveClass.zoomLink)) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(featuredLiveClass.zoomLink))
                                    context.startActivity(intent)
                                } else {
                                    onOpenNotesAndVideos(0)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (featuredLiveClass.isLiveNow) ErrorRed else NavyPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(strings.join, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }

        // 2. Continue Learning Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("continue_learning_card"),
                shape = RoundedCornerShape(22.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(NavyDark, NavyPrimary, Color(0xFF215278))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldSecondary.copy(alpha = 0.22f)
                            ) {
                                Text(
                                    text = strings.continueLearning,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "${continueProgress?.progressPercent ?: 0}% ${strings.completeSuffix}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Text(
                            text = continueProgress?.bookTitle ?: fallbackBook?.title ?: "Mathematics — Class 10",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (continueProgress != null) {
                                "Ch. ${continueProgress.lastChapterNumber}: ${continueProgress.lastChapterTitle}"
                            } else {
                                "Start Chapter 1 • Real Numbers & Prime Proofs"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.82f)
                        )

                        LinearProgressIndicator(
                            progress = { ((continueProgress?.progressPercent ?: 8L) / 100f).coerceIn(0.05f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = AmberAccent,
                            trackColor = Color.White.copy(alpha = 0.2f),
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Button(
                            onClick = {
                                if (continueProgress != null) {
                                    onContinueReading(continueProgress.bookId, continueProgress.lastChapterId)
                                } else if (fallbackBook != null) {
                                    onOpenBook(fallbackBook.id)
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldSecondary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("continue_reading_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (continueProgress != null) strings.continueReading else strings.startReading,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. My Textbooks Shelf
        item {
            SectionHeader(
                title = strings.myTextbooks,
                actionLabel = strings.allBooks,
                onActionClick = onOpenLibrary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(textbooks.take(6), key = { it.id }) { book ->
                    val pct = (progressByBook[book.id]?.progressPercent ?: 0L).toInt()
                    TextbookCoverCard(
                        book = book,
                        progressPercent = pct,
                        onClick = { onOpenBook(book.id) },
                        modifier = Modifier.width(172.dp)
                    )
                }
            }
        }

        // 4. Learning Tools 2x3 Grid
        item {
            SectionHeader(title = strings.learningTools)
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LearningToolTile(
                        icon = Icons.Filled.AutoAwesome,
                        title = strings.aiTeacher,
                        subtitle = strings.aiTeacherSubtitle,
                        accent = NavyPrimary,
                        onClick = onOpenAiTeacher,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tool_ai_teacher")
                    )
                    LearningToolTile(
                        icon = Icons.Filled.Quiz,
                        title = strings.dailyQuiz,
                        subtitle = strings.dailyQuizSubtitle,
                        accent = EmeraldSecondary,
                        onClick = onOpenPractice,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tool_daily_quiz")
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LearningToolTile(
                        icon = Icons.Filled.Videocam,
                        title = strings.liveClasses,
                        subtitle = if (liveClasses.isNotEmpty()) "${liveClasses.size} Zoom Live" else "Zoom Sessions",
                        accent = ErrorRed,
                        onClick = { onOpenNotesAndVideos(0) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tool_live_classes")
                    )
                    LearningToolTile(
                        icon = Icons.Filled.PlayCircle,
                        title = strings.videoLectures,
                        subtitle = strings.videoLecturesSubtitle,
                        accent = SubjectHindiColor,
                        onClick = { onOpenNotesAndVideos(1) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tool_video_lectures")
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    LearningToolTile(
                        icon = Icons.Filled.Description,
                        title = strings.notesAndPyqs,
                        subtitle = strings.notesAndPyqsSubtitle,
                        accent = SubjectEnglishColor,
                        onClick = { onOpenNotesAndVideos(2) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tool_notes_pyq")
                    )
                    LearningToolTile(
                        icon = Icons.Filled.CalendarMonth,
                        title = strings.studyPlanner,
                        subtitle = strings.studyPlannerSubtitle,
                        accent = SubjectScienceColor,
                        onClick = onOpenPlannerAndProgress,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("tool_study_planner")
                    )
                }
            }
        }

        // 5. Performance Snapshot & Weekly Activity
        item {
            SectionHeader(
                title = strings.yourPerformance,
                actionLabel = strings.fullReport,
                onActionClick = onOpenPlannerAndProgress
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricPillCard(
                    icon = Icons.Filled.Schedule,
                    label = strings.studyTime,
                    value = studyHoursLabel,
                    accent = NavyPrimary,
                    modifier = Modifier.weight(1f)
                )
                MetricPillCard(
                    icon = Icons.Filled.TrendingUp,
                    label = strings.avgQuiz,
                    value = if (quizAttempts.isEmpty()) "—" else "$avgQuizScore%",
                    accent = EmeraldSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = strings.weeklyActivity,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$completedChaptersTotal chapters completed • ${quizAttempts.size} tests taken",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    val baseToday = ((profile?.studyMinutesTotal ?: 20L).toInt().coerceAtLeast(15)).coerceAtMost(90)
                    WeeklyActivityChart(
                        dailyMinutes = listOf(20, 35, 25, 40, 30, 45, baseToday)
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onOpenAiTeacher)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = "Recommendation",
                                tint = EmeraldSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = recommendedText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
private fun LearningToolTile(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
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
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accent,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
