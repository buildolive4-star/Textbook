package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.data.model.CurriculumCatalog
import com.example.data.model.QuizAttempt
import com.example.data.model.ReadingProgress
import com.example.data.model.StudyTask
import com.example.data.model.Textbook
import com.example.data.model.UserProfile
import com.example.ui.components.MetricPillCard
import com.example.ui.components.WeeklyActivityChart
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.subjectColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlannerAndProgressScreen(
    profile: UserProfile?,
    studyTasks: List<StudyTask>,
    textbooks: List<Textbook>,
    readingProgressList: List<ReadingProgress>,
    quizAttempts: List<QuizAttempt>,
    onBack: () -> Unit,
    onAddTask: (title: String, subject: String, dueDate: String, targetMinutes: Int) -> Unit,
    onToggleTask: (taskId: String, completed: Boolean) -> Unit,
    onDeleteTask: (taskId: String) -> Unit
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Planner, 1 = Progress Report
    var showAddDialog by remember { mutableStateOf(false) }

    val weekDays = listOf("Today", "Tomorrow", "Sat", "Sun", "Mon", "Tue", "Wed")
    var selectedDay by remember { mutableStateOf("All") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study Planner & Progress") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (activeTab == 0) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = NavyPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_study_task_fab")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Study Goal")
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
            TabRow(selectedTabIndex = activeTab) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Study Planner") }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Progress Report") }
                )
            }

            if (activeTab == 0) {
                // STUDY PLANNER
                val filteredTasks = if (selectedDay == "All") studyTasks else studyTasks.filter { it.dueDate == selectedDay }
                val completedCount = studyTasks.count { it.completed }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyPrimary)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Daily Study Goals",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "$completedCount of ${studyTasks.size} goals completed",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                                Surface(shape = RoundedCornerShape(12.dp), color = AmberAccent.copy(alpha = 0.22f)) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(18.dp))
                                        Text("${profile?.streakDays ?: 1} Day Streak", color = AmberAccent, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }
                    }

                    // Calendar Strip
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listOf("All") + weekDays) { day ->
                                FilterChip(
                                    selected = selectedDay == day,
                                    onClick = { selectedDay = day },
                                    label = { Text(day) }
                                )
                            }
                        }
                    }

                    if (filteredTasks.isEmpty()) {
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
                                    Text("No study goals yet", style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = "Tap '+' to schedule a chapter target or daily revision reminder.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredTasks, key = { it.id }) { task ->
                            val accent = subjectColor(task.subject)
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = task.completed,
                                        onCheckedChange = { onToggleTask(task.id, it) }
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None
                                        )
                                        Text(
                                            text = "${task.subject} • ${task.dueDate} • ${task.targetMinutes} min",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = accent
                                        )
                                    }
                                    IconButton(onClick = { onDeleteTask(task.id) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete goal")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // STUDENT PROGRESS REPORT
                val totalChapters = textbooks.sumOf { it.chapterCount.toInt() }.coerceAtLeast(1)
                val completedChapters = readingProgressList.sumOf { it.completedChapters.size }
                val avgScore = if (quizAttempts.isEmpty()) 0 else quizAttempts.map { it.scorePercent }.average().toInt()
                val weakTopics = quizAttempts.flatMap { it.weakTopics }.distinct()
                val progressByBook = readingProgressList.associateBy { it.bookId }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricPillCard(
                                icon = Icons.Filled.Schedule,
                                label = "Study Mins",
                                value = "${profile?.studyMinutesTotal ?: 0}m",
                                accent = NavyPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            MetricPillCard(
                                icon = Icons.Filled.CheckCircle,
                                label = "Chapters Done",
                                value = "$completedChapters/$totalChapters",
                                accent = EmeraldSecondary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricPillCard(
                                icon = Icons.Filled.TrendingUp,
                                label = "Avg Accuracy",
                                value = if (quizAttempts.isEmpty()) "—" else "$avgScore%",
                                accent = AmberAccent,
                                modifier = Modifier.weight(1f)
                            )
                            MetricPillCard(
                                icon = Icons.Filled.LocalFireDepartment,
                                label = "Streak",
                                value = "${profile?.streakDays ?: 1} Days",
                                accent = Color(0xFFBE123C),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("Weekly Learning Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                WeeklyActivityChart(
                                    dailyMinutes = listOf(
                                        25, 30, 20, 45, 35, 50,
                                        (profile?.studyMinutesTotal ?: 20L).toInt().coerceIn(15, 90)
                                    )
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("Subject-wise Progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                textbooks.take(5).forEach { book ->
                                    val pct = (progressByBook[book.id]?.progressPercent ?: 0L).toInt()
                                    val color = subjectColor(book.subject)
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(book.subject, style = MaterialTheme.typography.bodyMedium)
                                            Text("$pct%", style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
                                        }
                                        LinearProgressIndicator(
                                            progress = { (pct / 100f).coerceIn(0f, 1f) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(7.dp)
                                                .clip(RoundedCornerShape(4.dp)),
                                            color = color,
                                            strokeCap = StrokeCap.Round
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (weakTopics.isNotEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = AmberAccent.copy(alpha = 0.12f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Recommended Revision Topics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AmberAccent)
                                    weakTopics.forEach { topic ->
                                        Text("• $topic", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }

                    if (quizAttempts.isNotEmpty()) {
                        item { Text("Recent Quizzes & Exams", style = MaterialTheme.typography.titleLarge) }
                        items(quizAttempts.take(8), key = { it.id }) { att ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(att.quizTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text("${att.subject} • ${att.mode} • ${att.correctCount}/${att.totalQuestions} correct", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text("${att.scorePercent}%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EmeraldSecondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var subject by remember { mutableStateOf(CurriculumCatalog.allSubjects.first()) }
        var dueDate by remember { mutableStateOf("Today") }
        var minutesText by remember { mutableStateOf("30") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("New Study Goal") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it.take(140) },
                        label = { Text("Chapter or revision task") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(CurriculumCatalog.allSubjects) { subj ->
                            FilterChip(selected = subject == subj, onClick = { subject = subj }, label = { Text(subj) })
                        }
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(weekDays.take(5)) { day ->
                            FilterChip(selected = dueDate == day, onClick = { dueDate = day }, label = { Text(day) })
                        }
                    }
                    OutlinedTextField(
                        value = minutesText,
                        onValueChange = { minutesText = it.filter { ch -> ch.isDigit() }.take(3) },
                        label = { Text("Target minutes (e.g. 30)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onAddTask(title, subject, dueDate, minutesText.toIntOrNull() ?: 30)
                            showAddDialog = false
                        }
                    }
                ) { Text("Add Goal") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }
}
