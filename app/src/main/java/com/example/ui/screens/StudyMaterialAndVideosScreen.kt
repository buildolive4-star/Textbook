package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.firebase.CoAdminSecurity
import com.example.data.model.LiveClass
import com.example.data.model.SavedMaterial
import com.example.data.model.StudyMaterial
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.subjectColor
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyMaterialAndVideosScreen(
    liveClasses: List<LiveClass>,
    materials: List<StudyMaterial>,
    savedMaterials: List<SavedMaterial>,
    initialTab: Int = 0, // 0 = Live (Zoom), 1 = Video Lectures (Cloudinary), 2 = Notes & PYQs
    onBack: () -> Unit,
    onToggleSave: (material: StudyMaterial) -> Unit,
    onOpenPractice: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var activeTab by remember(initialTab) { mutableIntStateOf(initialTab.coerceIn(0, 2)) }
    var selectedCategory by remember { mutableStateOf("All") }
    var openedMaterial by remember { mutableStateOf<StudyMaterial?>(null) }

    val savedIds = remember(savedMaterials) { savedMaterials.map { it.materialId }.toSet() }
    val noteCategories = listOf(
        "All", "Formula Sheet", "Revision Notes", "Important Questions", "Previous Year Papers", "Chapter Notes"
    )

    val videosList = remember(materials) {
        materials.filter { it.category == "Video Lecture" }
    }
    val notesList = remember(materials, selectedCategory) {
        materials.filter { it.category != "Video Lecture" }
            .filter { selectedCategory == "All" || it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Classes, Lectures & Notes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
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
                    text = { Text("Live (Zoom)") },
                    modifier = Modifier.testTag("tab_live_zoom")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Lectures") },
                    modifier = Modifier.testTag("tab_video_lectures")
                )
                Tab(
                    selected = activeTab == 2,
                    onClick = { activeTab = 2 },
                    text = { Text("Notes & PYQs") },
                    modifier = Modifier.testTag("tab_notes_pyq")
                )
            }

            if (activeTab == 2) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(noteCategories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }

            if (activeTab == 0) {
                // Live Classes (Zoom)
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (liveClasses.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Filled.Videocam, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(40.dp))
                                    Text("No Live Zoom Classes Yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "When your teachers or admin schedule a live Zoom class, it will appear here with a one-tap Join button.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(liveClasses, key = { it.id }) { live ->
                            val accent = subjectColor(live.subject)
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = if (live.isLiveNow) ErrorRed else AmberAccent.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = if (live.isLiveNow) "● LIVE NOW" else "UPCOMING",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (live.isLiveNow) Color.White else AmberAccent,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                            Surface(shape = RoundedCornerShape(8.dp), color = accent.copy(alpha = 0.14f)) {
                                                Text(
                                                    text = live.subject,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = accent,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = live.scheduleLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Text(live.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "${live.classLevel} • Teacher: ${live.teacherName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (live.meetingId.isNotBlank() || live.passcode.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    if (live.meetingId.isNotBlank()) {
                                                        Text("Meeting ID: ${live.meetingId}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                                                    }
                                                    if (live.passcode.isNotBlank()) {
                                                        Text("Passcode: ${live.passcode}", style = MaterialTheme.typography.labelSmall)
                                                    }
                                                }
                                                IconButton(onClick = {
                                                    clipboard.setText(AnnotatedString("ID: ${live.meetingId} Passcode: ${live.passcode}"))
                                                }) {
                                                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy Zoom credentials", modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            if (CoAdminSecurity.isValidHttpsUrl(live.zoomLink)) {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(live.zoomLink))
                                                context.startActivity(intent)
                                            }
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (live.isLiveNow) ErrorRed else NavyPrimary
                                        ),
                                        modifier = Modifier.fillMaxWidth().height(48.dp)
                                    ) {
                                        Icon(Icons.Filled.Videocam, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(if (live.isLiveNow) "Join Live Class on Zoom" else "Open Zoom Link")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                val itemsToShow = if (activeTab == 1) videosList else notesList

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(itemsToShow, key = { it.id }) { item ->
                        val isSaved = item.id in savedIds
                        val accent = subjectColor(item.subject)
                        val hasCloudVideo = CoAdminSecurity.isValidHttpsUrl(item.videoUrl)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { openedMaterial = item },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(accent.copy(alpha = 0.14f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (item.category == "Video Lecture") Icons.Filled.PlayCircle else Icons.Filled.Description,
                                        contentDescription = item.category,
                                        tint = accent,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Surface(shape = RoundedCornerShape(6.dp), color = accent.copy(alpha = 0.12f)) {
                                            Text(
                                                text = if (hasCloudVideo) "Cloudinary HD" else item.category,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = accent,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        if (item.videoDurationMinutes > 0) {
                                            Text(
                                                text = "${item.videoDurationMinutes} min",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = AmberAccent,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${item.subject} • ${item.classLevel} • ${item.chapterTitle}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(onClick = { onToggleSave(item) }) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (isSaved) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Study Material / Cloudinary Video Lecture Sheet
    openedMaterial?.let { item ->
        ModalBottomSheet(onDismissRequest = { openedMaterial = null }) {
            val isVideo = item.category == "Video Lecture"
            val hasCloudVideo = isVideo && CoAdminSecurity.isValidHttpsUrl(item.videoUrl)

            var isPlaying by remember(item.id) { mutableStateOf(false) }
            var playbackSpeed by remember(item.id) { mutableFloatStateOf(1.0f) }
            var playbackProgress by remember(item.id) { mutableFloatStateOf(0.12f) }
            var videoBuffering by remember(item.id) { mutableStateOf(hasCloudVideo) }
            var videoLoadError by remember(item.id) { mutableStateOf(false) }

            LaunchedEffect(isPlaying, playbackSpeed, hasCloudVideo) {
                while (!hasCloudVideo && isPlaying && playbackProgress < 1f) {
                    delay((1000L / playbackSpeed).toLong())
                    playbackProgress = (playbackProgress + 0.02f).coerceAtMost(1f)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(item.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(
                    text = "${item.subject} • ${item.classLevel} • ${item.chapterTitle}",
                    style = MaterialTheme.typography.labelMedium,
                    color = EmeraldSecondary
                )

                if (hasCloudVideo) {
                    // Real Cloudinary Video Stream Player
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!videoLoadError) {
                                AndroidView(
                                    factory = { ctx ->
                                        VideoView(ctx).apply {
                                            val controller = MediaController(ctx)
                                            controller.setAnchorView(this)
                                            setMediaController(controller)
                                            setVideoURI(Uri.parse(item.videoUrl))
                                            setOnPreparedListener { mp ->
                                                videoBuffering = false
                                                mp.isLooping = false
                                                start()
                                            }
                                            setOnErrorListener { _, _, _ ->
                                                videoBuffering = false
                                                videoLoadError = true
                                                true
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            if (videoBuffering && !videoLoadError) {
                                CircularProgressIndicator(color = EmeraldSecondary)
                            }
                            if (videoLoadError) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        "In-app stream format unsupported. Open directly in browser.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.videoUrl))
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Fullscreen Video (Cloudinary)")
                    }
                } else if (isVideo) {
                    // Fallback Concept Walkthrough Player for Starter Lessons without Cloudinary URL
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp)
                                .background(Brush.linearGradient(listOf(NavyDark, NavyPrimary))),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = EmeraldSecondary,
                                    modifier = Modifier
                                        .size(58.dp)
                                        .clickable { isPlaying = !isPlaying }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                            contentDescription = if (isPlaying) "Pause" else "Play",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isPlaying) "Playing Concept Walkthrough (${playbackSpeed}x)" else "Tap to Start Lesson (${item.videoDurationMinutes} min)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    LinearProgressIndicator(
                        progress = { playbackProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Filled.Speed, contentDescription = null, modifier = Modifier.size(18.dp))
                            listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { spd ->
                                FilterChip(
                                    selected = playbackSpeed == spd,
                                    onClick = { playbackSpeed = spd },
                                    label = { Text("${spd}x") }
                                )
                            }
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.contentBody,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onToggleSave(item) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (item.id in savedIds) "Bookmarked" else "Save for Revision")
                    }
                    Button(
                        onClick = {
                            openedMaterial = null
                            onOpenPractice()
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Practice Quiz")
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
