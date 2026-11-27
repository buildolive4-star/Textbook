package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.unit.dp
import com.example.data.model.Assessment
import com.example.data.model.QuizAttempt
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.subjectColor
import kotlinx.coroutines.delay

private sealed interface PracticeStage {
    data object Hub : PracticeStage
    data class Instructions(val assessment: Assessment) : PracticeStage
    data class Active(val assessment: Assessment) : PracticeStage
    data class Result(
        val assessment: Assessment,
        val selectedIndices: Map<Int, Int>,
        val fillAnswers: Map<Int, String>,
        val correctCount: Int,
        val incorrectCount: Int,
        val unattemptedCount: Int,
        val obtainedMarks: Int,
        val totalMarks: Int,
        val scorePercent: Int,
        val weakTopics: List<String>
    ) : PracticeStage
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuizAndExamScreen(
    assessments: List<Assessment>,
    attempts: List<QuizAttempt>,
    onRecordAttempt: (
        assessment: Assessment,
        correctCount: Int,
        incorrectCount: Int,
        unattemptedCount: Int,
        obtainedMarks: Int,
        maxMarks: Int,
        scorePercent: Int,
        durationSeconds: Int,
        weakTopics: List<String>
    ) -> Unit
) {
    val strings = LocalAppStrings.current
    var stage by remember { mutableStateOf<PracticeStage>(PracticeStage.Hub) }
    var hubTab by remember { mutableIntStateOf(0) } // 0 = Practice Quizzes, 1 = Online Exams

    BackHandler(enabled = stage !is PracticeStage.Hub) {
        stage = PracticeStage.Hub
    }

    when (val current = stage) {
        is PracticeStage.Hub -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                            Text(
                                text = strings.quizExamCenter,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Chapter MCQs, Daily Quizzes, and Timed Board Mock Exams",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TabRow(selectedTabIndex = hubTab) {
                            Tab(
                                selected = hubTab == 0,
                                onClick = { hubTab = 0 },
                                text = { Text(strings.practiceQuizzes) }
                            )
                            Tab(
                                selected = hubTab == 1,
                                onClick = { hubTab = 1 },
                                text = { Text(strings.onlineExams) }
                            )
                        }
                    }
                }

                val targetMode = if (hubTab == 0) "QUIZ" else "EXAM"
                val filtered = assessments.filter { it.mode == targetMode }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { item ->
                        val lastAttempt = attempts.firstOrNull { it.quizId == item.id }
                        val accent = subjectColor(item.subject)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("assessment_card_${item.id}")
                                .clickable {
                                    stage = if (item.mode == "EXAM") {
                                        PracticeStage.Instructions(item)
                                    } else {
                                        PracticeStage.Active(item)
                                    }
                                },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = accent.copy(alpha = 0.14f)
                                    ) {
                                        Text(
                                            text = "${item.subject} • ${item.category}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = accent,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    if (lastAttempt != null) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = EmeraldSecondary.copy(alpha = 0.16f)
                                        ) {
                                            Text(
                                                text = "Last: ${lastAttempt.scorePercent}%",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = EmeraldSecondary,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(
                                            imageVector = if (item.mode == "EXAM") Icons.Filled.Assignment else Icons.Filled.Quiz,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${item.questions.size} Questions • ${item.totalMarks} Marks",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(
                                            imageVector = Icons.Filled.Timer,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = AmberAccent
                                        )
                                        Text(
                                            text = "${item.durationMinutes} min",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        stage = if (item.mode == "EXAM") {
                                            PracticeStage.Instructions(item)
                                        } else {
                                            PracticeStage.Active(item)
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(if (item.mode == "EXAM") "View Instructions & Start Exam" else "Start Practice Quiz")
                                }
                            }
                        }
                    }
                }
            }
        }

        is PracticeStage.Instructions -> {
            val exam = current.assessment
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { stage = PracticeStage.Hub }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text("Online Examination Instructions", style = MaterialTheme.typography.titleLarge)
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(exam.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${exam.subject} • ${exam.classLevel} • ${exam.durationMinutes} Minutes • ${exam.totalMarks} Marks",
                            style = MaterialTheme.typography.labelMedium,
                            color = EmeraldSecondary
                        )
                        Text(
                            text = exam.instructions.ifBlank {
                                "• Answer all questions before the countdown ends.\n• Mark questions for review using the flag icon.\n• Exam auto-submits when time reaches 00:00."
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { stage = PracticeStage.Active(exam) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("start_exam_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Begin Online Examination", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        is PracticeStage.Active -> {
            ActiveQuizRunner(
                assessment = current.assessment,
                onExit = { stage = PracticeStage.Hub },
                onSubmit = { selectedMap, fillMap, elapsedSeconds ->
                    val questions = current.assessment.questions
                    var correct = 0
                    var incorrect = 0
                    var unattempted = 0
                    var obtainedMarks = 0
                    var maxMarks = 0
                    val weakTopics = mutableListOf<String>()

                    questions.forEachIndexed { idx, q ->
                        val qMarks = q.marks.toInt().coerceAtLeast(1)
                        maxMarks += qMarks
                        if (q.type == "FILL_BLANK") {
                            val ans = fillMap[idx]?.trim().orEmpty()
                            if (ans.isEmpty()) {
                                unattempted++
                                if (q.topic.isNotBlank()) weakTopics.add(q.topic)
                            } else if (ans.equals(q.correctText.trim(), ignoreCase = true)) {
                                correct++
                                obtainedMarks += qMarks
                            } else {
                                incorrect++
                                if (q.topic.isNotBlank()) weakTopics.add(q.topic)
                            }
                        } else {
                            val chosen = selectedMap[idx]
                            if (chosen == null) {
                                unattempted++
                                if (q.topic.isNotBlank()) weakTopics.add(q.topic)
                            } else if (chosen == q.correctIndex.toInt()) {
                                correct++
                                obtainedMarks += qMarks
                            } else {
                                incorrect++
                                if (q.topic.isNotBlank()) weakTopics.add(q.topic)
                            }
                        }
                    }

                    val pct = if (maxMarks > 0) ((obtainedMarks * 100f) / maxMarks).toInt() else 0
                    onRecordAttempt(
                        current.assessment,
                        correct,
                        incorrect,
                        unattempted,
                        obtainedMarks,
                        maxMarks,
                        pct,
                        elapsedSeconds,
                        weakTopics.distinct()
                    )
                    stage = PracticeStage.Result(
                        assessment = current.assessment,
                        selectedIndices = selectedMap,
                        fillAnswers = fillMap,
                        correctCount = correct,
                        incorrectCount = incorrect,
                        unattemptedCount = unattempted,
                        obtainedMarks = obtainedMarks,
                        totalMarks = maxMarks,
                        scorePercent = pct,
                        weakTopics = weakTopics.distinct()
                    )
                }
            )
        }

        is PracticeStage.Result -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = NavyPrimary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (current.assessment.mode == "EXAM") "EXAMINATION REPORT" else "QUIZ COMPLETED",
                                style = MaterialTheme.typography.labelMedium,
                                color = AmberAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${current.scorePercent}%",
                                style = MaterialTheme.typography.displayLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${current.obtainedMarks} / ${current.totalMarks} Marks • ${current.assessment.title}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ScoreStatCol("Correct", "${current.correctCount}", EmeraldSecondary)
                                ScoreStatCol("Incorrect", "${current.incorrectCount}", ErrorRed)
                                ScoreStatCol("Skipped", "${current.unattemptedCount}", AmberAccent)
                            }
                        }
                    }
                }

                if (current.weakTopics.isNotEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Topics to Revise", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    current.weakTopics.forEach { topic ->
                                        Surface(shape = RoundedCornerShape(10.dp), color = AmberAccent.copy(alpha = 0.16f)) {
                                            Text(
                                                text = topic,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = AmberAccent,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Text("Detailed Answer Key & Explanations", style = MaterialTheme.typography.titleLarge)
                }

                itemsIndexed(current.assessment.questions) { idx, q ->
                    val isCorrect = if (q.type == "FILL_BLANK") {
                        current.fillAnswers[idx]?.trim().equals(q.correctText.trim(), ignoreCase = true)
                    } else {
                        current.selectedIndices[idx] == q.correctIndex.toInt()
                    }
                    val correctDisplay = if (q.type == "FILL_BLANK") q.correctText else q.options.getOrNull(q.correctIndex.toInt()).orEmpty()

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Q${idx + 1}. ${q.prompt}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isCorrect) "✓ Correct ($correctDisplay)" else "✗ Correct Answer: $correctDisplay",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isCorrect) EmeraldSecondary else ErrorRed,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Explanation: ${q.explanation}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = { stage = PracticeStage.Hub },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Back to Quiz & Exam Hub")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ActiveQuizRunner(
    assessment: Assessment,
    onExit: () -> Unit,
    onSubmit: (Map<Int, Int>, Map<Int, String>, Int) -> Unit
) {
    val totalSeconds = (assessment.durationMinutes.toInt().coerceAtLeast(1)) * 60
    var remainingSeconds by remember { mutableIntStateOf(totalSeconds) }
    var currentIdx by remember { mutableIntStateOf(0) }
    var showPalette by remember { mutableStateOf(false) }

    val selectedOptions = remember { mutableStateMapOf<Int, Int>() }
    val fillAnswers = remember { mutableStateMapOf<Int, String>() }
    val reviewFlags = remember { mutableStateMapOf<Int, Boolean>() }

    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
        onSubmit(selectedOptions.toMap(), fillAnswers.toMap(), totalSeconds)
    }

    val questions = assessment.questions
    val q = questions.getOrNull(currentIdx) ?: return
    val mins = remainingSeconds / 60
    val secs = remainingSeconds % 60
    val isMarked = reviewFlags[currentIdx] == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Bar with Countdown & Palette
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onExit) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit")
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (remainingSeconds < 60) ErrorRed.copy(alpha = 0.16f) else NavyPrimary.copy(alpha = 0.12f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Filled.Timer,
                        contentDescription = "Remaining Time",
                        tint = if (remainingSeconds < 60) ErrorRed else NavyPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (remainingSeconds < 60) ErrorRed else NavyPrimary
                    )
                }
            }
            Row {
                IconButton(onClick = { reviewFlags[currentIdx] = !isMarked }) {
                    Icon(
                        Icons.Filled.Flag,
                        contentDescription = "Mark for Review",
                        tint = if (isMarked) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = { showPalette = true },
                    modifier = Modifier.testTag("question_palette_button")
                ) {
                    Icon(Icons.Filled.GridView, contentDescription = "Question Palette")
                }
            }
        }

        LinearProgressIndicator(
            progress = { (currentIdx + 1).toFloat() / questions.size.coerceAtLeast(1).toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            strokeCap = StrokeCap.Round
        )

        Text(
            text = "Question ${currentIdx + 1} of ${questions.size} • ${q.marks} Mark(s)" +
                if (isMarked) " • Marked for Review" else "",
            style = MaterialTheme.typography.labelMedium,
            color = if (isMarked) AmberAccent else MaterialTheme.colorScheme.primary
        )

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Text(
                text = q.prompt,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(18.dp)
            )
        }

        if (q.type == "FILL_BLANK") {
            OutlinedTextField(
                value = fillAnswers[currentIdx].orEmpty(),
                onValueChange = { fillAnswers[currentIdx] = it },
                label = { Text("Type your answer") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                q.options.forEachIndexed { optIdx, optionText ->
                    val chosen = selectedOptions[currentIdx] == optIdx
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (chosen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (chosen) 2.dp else 1.dp,
                                color = if (chosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedOptions[currentIdx] = optIdx }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (chosen) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = ('A' + optIdx).toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (chosen) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            if (chosen) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = "Selected", tint = NavyPrimary)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { if (currentIdx > 0) currentIdx-- },
                enabled = currentIdx > 0,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
            ) {
                Text("Previous")
            }

            if (currentIdx < questions.lastIndex) {
                Button(
                    onClick = { currentIdx++ },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("Next")
                }
            } else {
                Button(
                    onClick = {
                        onSubmit(selectedOptions.toMap(), fillAnswers.toMap(), totalSeconds - remainingSeconds)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("submit_quiz_button")
                ) {
                    Text("Submit")
                }
            }
        }
    }

    if (showPalette) {
        ModalBottomSheet(onDismissRequest = { showPalette = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Question Palette", style = MaterialTheme.typography.titleLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    questions.forEachIndexed { idx, question ->
                        val answered = if (question.type == "FILL_BLANK") {
                            !fillAnswers[idx].isNullOrBlank()
                        } else selectedOptions.containsKey(idx)
                        val flagged = reviewFlags[idx] == true
                        val bg = when {
                            flagged -> AmberAccent
                            answered -> EmeraldSecondary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bg)
                                .clickable {
                                    currentIdx = idx
                                    showPalette = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${idx + 1}",
                                fontWeight = FontWeight.Bold,
                                color = if (answered || flagged) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Button(
                    onClick = {
                        showPalette = false
                        onSubmit(selectedOptions.toMap(), fillAnswers.toMap(), totalSeconds - remainingSeconds)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Submit Now")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun ScoreStatCol(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, color = color, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
    }
}
