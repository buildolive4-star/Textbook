package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.data.firebase.CoAdminSecurity
import com.example.data.model.CoAdminAccount
import com.example.data.model.CoAdminSession
import com.example.data.model.CurriculumCatalog
import com.example.data.model.LiveClass
import com.example.data.model.StudyMaterial
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    isMainAdmin: Boolean,
    coAdminSession: CoAdminSession?,
    coAdmins: List<CoAdminAccount>,
    liveClasses: List<LiveClass>,
    videoLectures: List<StudyMaterial>,
    textbookCount: Int,
    assessmentCount: Int,
    onBackToUserPanel: () -> Unit,
    onLockCoAdminSession: () -> Unit,
    onCreateCoAdmin: (name: String, password: String, onDone: () -> Unit) -> Unit,
    onDeleteCoAdmin: (coAdminId: String) -> Unit,
    onPublishLiveClass: (
        title: String, subject: String, classLevel: String, teacherName: String,
        scheduleLabel: String, zoomLink: String, meetingId: String, passcode: String,
        isLiveNow: Boolean, onDone: () -> Unit
    ) -> Unit,
    onToggleLiveClassStatus: (liveId: String, isLiveNow: Boolean) -> Unit,
    onDeleteLiveClass: (liveId: String) -> Unit,
    onPublishStudyMaterial: (
        title: String, category: String, subject: String, classLevel: String,
        chapterTitle: String, description: String, contentBody: String,
        videoUrl: String, videoDurationMinutes: Int, premiumOnly: Boolean, onDone: () -> Unit
    ) -> Unit,
    onDeleteStudyMaterial: (materialId: String) -> Unit,
    onPublishTextbook: (
        title: String, subject: String, classLevel: String, category: String,
        board: String, medium: String, description: String, premiumOnly: Boolean,
        chapterTitle: String, chapterBody: String, onDone: () -> Unit
    ) -> Unit,
    onPublishAssessment: (
        title: String, subject: String, classLevel: String, mode: String,
        durationMinutes: Int, prompt: String, options: List<String>,
        correctIndex: Int, explanation: String, onDone: () -> Unit
    ) -> Unit,
    onPublishAnnouncement: (
        title: String, body: String, category: String, audience: String, onDone: () -> Unit
    ) -> Unit,
    onSyncCatalogToCloud: () -> Unit
) {
    val tabs = remember(isMainAdmin) {
        buildList {
            add("Live (Zoom)")
            add("Lectures (Cloudinary)")
            add("Textbooks")
            add("Notes & Quiz")
            if (isMainAdmin) add("Co-Admins")
        }
    }
    var selectedTab by remember { mutableIntStateOf(0) }
    val safeTab = selectedTab.coerceIn(0, tabs.lastIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NavyPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    Column {
                        Text(
                            text = if (isMainAdmin) "Textbook Admin Panel" else "Co-Admin Panel",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isMainAdmin) "Main Admin • buildolive4@gmail.com"
                            else "Co-Admin • ${coAdminSession?.coAdminName ?: "Active"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackToUserPanel,
                        modifier = Modifier.testTag("admin_back_user_panel")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to User Panel")
                    }
                },
                actions = {
                    if (!isMainAdmin && coAdminSession != null) {
                        IconButton(
                            onClick = onLockCoAdminSession,
                            modifier = Modifier.testTag("lock_coadmin_button")
                        ) {
                            Icon(Icons.Filled.Lock, contentDescription = "Lock Co-Admin Panel")
                        }
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
            // Admin Overview Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(NavyDark, NavyPrimary)))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminStatChip("Live", "${liveClasses.size}", ErrorRed, Modifier.weight(1f))
                    AdminStatChip("Lectures", "${videoLectures.size}", AmberAccent, Modifier.weight(1f))
                    AdminStatChip("Books", "$textbookCount", EmeraldSecondary, Modifier.weight(1f))
                    if (isMainAdmin) {
                        AdminStatChip("Co-Admins", "${coAdmins.size}", Color(0xFF38BDF8), Modifier.weight(1f))
                    } else {
                        AdminStatChip("Tests", "$assessmentCount", Color(0xFF38BDF8), Modifier.weight(1f))
                    }
                }
            }

            ScrollableTabRow(
                selectedTabIndex = safeTab,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { idx, label ->
                    Tab(
                        selected = safeTab == idx,
                        onClick = { selectedTab = idx },
                        text = { Text(label, fontWeight = if (safeTab == idx) FontWeight.Bold else FontWeight.Medium) },
                        modifier = Modifier.testTag("admin_tab_$idx")
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (safeTab) {
                    0 -> {
                        item {
                            ZoomLiveClassForm(
                                defaultTeacher = if (isMainAdmin) "Main Admin" else coAdminSession?.coAdminName ?: "Faculty",
                                onPublish = onPublishLiveClass
                            )
                        }
                        if (liveClasses.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Published Zoom Live Classes (${liveClasses.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            items(liveClasses, key = { it.id }) { live ->
                                AdminLiveClassRow(
                                    live = live,
                                    onToggleLive = { onToggleLiveClassStatus(live.id, !live.isLiveNow) },
                                    onDelete = { onDeleteLiveClass(live.id) }
                                )
                            }
                        }
                    }

                    1 -> {
                        item {
                            CloudinaryLectureForm(onPublish = onPublishStudyMaterial)
                        }
                        val cloudVideos = videoLectures.filter { it.videoUrl.isNotBlank() }
                        if (cloudVideos.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Published Cloudinary Video Lectures (${cloudVideos.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            items(cloudVideos, key = { it.id }) { video ->
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(EmeraldSecondary.copy(alpha = 0.14f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = EmeraldSecondary)
                                        }
                                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(video.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                            Text(
                                                text = "${video.subject} • ${video.classLevel} • ${video.videoDurationMinutes}m",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = video.videoUrl,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = NavyPrimary,
                                                maxLines = 1
                                            )
                                        }
                                        IconButton(onClick = { onDeleteStudyMaterial(video.id) }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "Delete lecture", tint = ErrorRed)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        item {
                            Card(
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Sync Starter Books", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                        Text("Push built-in CBSE textbooks & chapters to Firestore", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Button(
                                        onClick = onSyncCatalogToCloud,
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Sync")
                                    }
                                }
                            }
                        }
                        item {
                            AdminPublishBookForm(onPublish = onPublishTextbook)
                        }
                    }

                    3 -> {
                        item {
                            AdminNotesQuizAlertSection(
                                onPublishMaterial = onPublishStudyMaterial,
                                onPublishAssessment = onPublishAssessment,
                                onPublishAnnouncement = onPublishAnnouncement
                            )
                        }
                    }

                    4 -> {
                        if (isMainAdmin) {
                            item {
                                CreateCoAdminCard(onCreateCoAdmin = onCreateCoAdmin)
                            }
                            item {
                                Text(
                                    text = "Active Co-Admins (${coAdmins.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (coAdmins.isEmpty()) {
                                item {
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Text(
                                            text = "No Co-Admins added yet. Enter a Co-Admin Name and Password above to create one. They can tap the Home Textbook icon 5 times and log in with their Name & Password.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                            } else {
                                items(coAdmins, key = { it.id }) { coAdmin ->
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                                                    .background(NavyPrimary.copy(alpha = 0.14f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Filled.SupervisorAccount, contentDescription = null, tint = NavyPrimary)
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(coAdmin.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                                Text(
                                                    text = "Active Co-Admin • Password salted & hashed",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = EmeraldSecondary
                                                )
                                            }
                                            IconButton(
                                                onClick = { onDeleteCoAdmin(coAdmin.id) },
                                                modifier = Modifier.testTag("delete_coadmin_${coAdmin.id}")
                                            ) {
                                                Icon(Icons.Filled.Delete, contentDescription = "Revoke Co-Admin", tint = ErrorRed)
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
}

@Composable
private fun AdminStatChip(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = accent)
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.85f))
        }
    }
}

@Composable
private fun ZoomLiveClassForm(
    defaultTeacher: String,
    onPublish: (String, String, String, String, String, String, String, String, Boolean, () -> Unit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Mathematics") }
    var classLevel by remember { mutableStateOf("Class 10") }
    var teacherName by remember { mutableStateOf(defaultTeacher) }
    var scheduleLabel by remember { mutableStateOf("Today • 6:00 PM IST") }
    var zoomLink by remember { mutableStateOf("") }
    var meetingId by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var isLiveNow by remember { mutableStateOf(true) }

    val linkValid = CoAdminSecurity.isValidHttpsUrl(zoomLink)
    val isZoomDomain = CoAdminSecurity.isZoomUrl(zoomLink)

    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Videocam, contentDescription = null, tint = ErrorRed)
                Text("Add Zoom Live Class", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Live Class Topic / Title") },
                placeholder = { Text("e.g. Quadratic Equations Doubt Class") },
                modifier = Modifier.fillMaxWidth().testTag("zoom_title_input")
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(CurriculumCatalog.allSubjects) { s ->
                    FilterChip(selected = subject == s, onClick = { subject = s }, label = { Text(s) })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = classLevel,
                    onValueChange = { classLevel = it },
                    label = { Text("Class") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = teacherName,
                    onValueChange = { teacherName = it },
                    label = { Text("Teacher Name") },
                    modifier = Modifier.weight(1f)
                )
            }
            OutlinedTextField(
                value = scheduleLabel,
                onValueChange = { scheduleLabel = it },
                label = { Text("Schedule / Timing") },
                placeholder = { Text("Today • 6:00 PM IST") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = zoomLink,
                onValueChange = { zoomLink = it },
                label = { Text("Zoom Meeting Link (https://...)") },
                placeholder = { Text("https://us05web.zoom.us/j/84512345678") },
                leadingIcon = { Icon(Icons.Filled.Link, contentDescription = null) },
                isError = zoomLink.isNotBlank() && !linkValid,
                supportingText = {
                    when {
                        zoomLink.isBlank() -> Text("Paste Zoom invite link for students to join directly")
                        isZoomDomain -> Text("Verified Zoom HTTPS link", color = EmeraldSecondary)
                        linkValid -> Text("Valid HTTPS meeting link", color = EmeraldSecondary)
                        else -> Text("Link must start with https://", color = ErrorRed)
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("zoom_link_input")
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = meetingId,
                    onValueChange = { meetingId = it },
                    label = { Text("Meeting ID (Optional)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = passcode,
                    onValueChange = { passcode = it },
                    label = { Text("Passcode (Optional)") },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Mark as LIVE NOW", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text("Shows a red LIVE banner on students' Home screen", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = isLiveNow, onCheckedChange = { isLiveNow = it })
            }
            Button(
                onClick = {
                    onPublish(
                        title, subject, classLevel, teacherName, scheduleLabel,
                        zoomLink, meetingId, passcode, isLiveNow
                    ) {
                        title = ""; zoomLink = ""; meetingId = ""; passcode = ""
                    }
                },
                enabled = title.trim().length >= 2 && linkValid,
                colors = ButtonDefaults.buttonColors(containerColor = if (isLiveNow) ErrorRed else NavyPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("publish_zoom_button")
            ) {
                Icon(Icons.Filled.Videocam, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isLiveNow) "Publish & Go LIVE on Zoom" else "Schedule Zoom Live Class")
            }
        }
    }
}

@Composable
private fun AdminLiveClassRow(
    live: LiveClass,
    onToggleLive: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = onToggleLive, shape = RoundedCornerShape(10.dp)) {
                        Text(if (live.isLiveNow) "End Live" else "Go Live", style = MaterialTheme.typography.labelSmall)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete live class", tint = ErrorRed)
                    }
                }
            }
            Text(live.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(
                text = "${live.subject} • ${live.classLevel} • ${live.teacherName} • ${live.scheduleLabel}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(live.zoomLink, style = MaterialTheme.typography.labelSmall, color = NavyPrimary, maxLines = 1)
        }
    }
}

@Composable
private fun CloudinaryLectureForm(
    onPublish: (String, String, String, String, String, String, String, String, Int, Boolean, () -> Unit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Mathematics") }
    var classLevel by remember { mutableStateOf("Class 10") }
    var chapterTitle by remember { mutableStateOf("Chapter 1") }
    var durationStr by remember { mutableStateOf("20") }
    var cloudinaryUrl by remember { mutableStateOf("") }
    var notesBody by remember { mutableStateOf("") }

    val urlValid = CoAdminSecurity.isValidHttpsUrl(cloudinaryUrl)
    val isCloudinary = CoAdminSecurity.isCloudinaryUrl(cloudinaryUrl)

    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = EmeraldSecondary)
                Text("Add Video Lecture (Cloudinary Link)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Lecture Title") },
                placeholder = { Text("e.g. Real Numbers One-Shot Lecture") },
                modifier = Modifier.fillMaxWidth().testTag("cloudinary_title_input")
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(CurriculumCatalog.allSubjects) { s ->
                    FilterChip(selected = subject == s, onClick = { subject = s }, label = { Text(s) })
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = classLevel,
                    onValueChange = { classLevel = it },
                    label = { Text("Class") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = durationStr,
                    onValueChange = { durationStr = it.filter { ch -> ch.isDigit() }.take(3) },
                    label = { Text("Mins") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(96.dp)
                )
            }
            OutlinedTextField(
                value = chapterTitle,
                onValueChange = { chapterTitle = it },
                label = { Text("Chapter Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = cloudinaryUrl,
                onValueChange = { cloudinaryUrl = it },
                label = { Text("Cloudinary Video Link (https://res.cloudinary.com/...)") },
                placeholder = { Text("https://res.cloudinary.com/your-cloud/video/upload/lecture.mp4") },
                leadingIcon = { Icon(Icons.Filled.Link, contentDescription = null) },
                isError = cloudinaryUrl.isNotBlank() && !urlValid,
                supportingText = {
                    when {
                        cloudinaryUrl.isBlank() -> Text("Paste your Cloudinary MP4 / video stream URL")
                        isCloudinary -> Text("Cloudinary video URL verified", color = EmeraldSecondary)
                        urlValid -> Text("Valid HTTPS video link", color = EmeraldSecondary)
                        else -> Text("Enter a valid https:// video link", color = ErrorRed)
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("cloudinary_url_input")
            )
            OutlinedTextField(
                value = notesBody,
                onValueChange = { notesBody = it },
                label = { Text("Lecture Summary / Key Notes") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val mins = durationStr.toIntOrNull()?.coerceIn(1, 240) ?: 15
                    onPublish(
                        title,
                        "Video Lecture",
                        subject,
                        classLevel,
                        chapterTitle,
                        "Cloudinary Video Lecture • $chapterTitle",
                        notesBody.ifBlank { "Watch the full HD video lecture for $title." },
                        cloudinaryUrl.trim(),
                        mins,
                        false
                    ) {
                        title = ""; cloudinaryUrl = ""; notesBody = ""
                    }
                },
                enabled = title.trim().length >= 2 && urlValid,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("publish_cloudinary_button")
            ) {
                Icon(Icons.Filled.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publish Cloudinary Lecture")
            }
        }
    }
}

@Composable
private fun CreateCoAdminCard(
    onCreateCoAdmin: (name: String, password: String, onDone: () -> Unit) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.PersonAdd, contentDescription = null, tint = NavyPrimary)
                Column {
                    Text("Create Co-Admin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "Only Main Admin (buildolive4@gmail.com) can add or remove Co-Admins",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Co-Admin Name") },
                placeholder = { Text("e.g. Rahul Sir") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("coadmin_name_input")
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Co-Admin Password") },
                placeholder = { Text("Minimum 4 characters") },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("coadmin_password_input")
            )
            Button(
                onClick = {
                    onCreateCoAdmin(name, password) {
                        name = ""
                        password = ""
                    }
                },
                enabled = name.trim().length >= 2 && password.length >= 4,
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("create_coadmin_button")
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Co-Admin")
            }
        }
    }
}

@Composable
private fun AdminPublishBookForm(
    onPublish: (String, String, String, String, String, String, String, Boolean, String, String, () -> Unit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Mathematics") }
    var classLevel by remember { mutableStateOf("Class 10") }
    var board by remember { mutableStateOf("CBSE") }
    var desc by remember { mutableStateOf("") }
    var chTitle by remember { mutableStateOf("") }
    var chBody by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Publish New Digital Textbook", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Textbook Title") }, modifier = Modifier.fillMaxWidth())
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(CurriculumCatalog.allSubjects) { s ->
                    FilterChip(selected = subject == s, onClick = { subject = s }, label = { Text(s) })
                }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(CurriculumCatalog.boards) { b ->
                    FilterChip(selected = board == b, onClick = { board = b }, label = { Text(b) })
                }
            }
            OutlinedTextField(value = classLevel, onValueChange = { classLevel = it }, label = { Text("Class (e.g. Class 10)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Book Description") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = chTitle, onValueChange = { chTitle = it }, label = { Text("Chapter 1 Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = chBody, onValueChange = { chBody = it }, label = { Text("Chapter 1 Content Paragraphs") }, minLines = 3, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    val cat = when {
                        classLevel.contains("11") || classLevel.contains("12") -> "Class 11-12"
                        classLevel.contains("9") || classLevel.contains("10") -> "Class 9-10"
                        classLevel.contains("6") || classLevel.contains("7") || classLevel.contains("8") -> "Class 6-8"
                        else -> "Class 1-5"
                    }
                    onPublish(title, subject, classLevel, cat, board, "English", desc, false, chTitle, chBody) {
                        title = ""; desc = ""; chTitle = ""; chBody = ""
                    }
                },
                enabled = title.length >= 2 && chTitle.length >= 2 && chBody.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Publish Textbook to Cloud") }
        }
    }
}

@Composable
private fun AdminNotesQuizAlertSection(
    onPublishMaterial: (String, String, String, String, String, String, String, String, Int, Boolean, () -> Unit) -> Unit,
    onPublishAssessment: (String, String, String, String, Int, String, List<String>, Int, String, () -> Unit) -> Unit,
    onPublishAnnouncement: (String, String, String, String, () -> Unit) -> Unit
) {
    var mode by remember { mutableIntStateOf(0) } // 0 = Notes, 1 = Quiz/Exam, 2 = Alert

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = mode == 0, onClick = { mode = 0 }, label = { Text("Study Notes") })
            FilterChip(selected = mode == 1, onClick = { mode = 1 }, label = { Text("Quiz / Exam") })
            FilterChip(selected = mode == 2, onClick = { mode = 2 }, label = { Text("Broadcast Alert") })
        }

        when (mode) {
            0 -> {
                var title by remember { mutableStateOf("") }
                var category by remember { mutableStateOf("Chapter Notes") }
                var subject by remember { mutableStateOf("Science") }
                var body by remember { mutableStateOf("") }
                Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Publish Notes / Formula Sheet / PYQ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listOf("Chapter Notes", "Formula Sheet", "Revision Notes", "Previous Year Papers")) { c ->
                                FilterChip(selected = category == c, onClick = { category = c }, label = { Text(c) })
                            }
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(CurriculumCatalog.allSubjects) { s ->
                                FilterChip(selected = subject == s, onClick = { subject = s }, label = { Text(s) })
                            }
                        }
                        OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Notes Content") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                        Button(
                            onClick = {
                                onPublishMaterial(title, category, subject, "Class 10", "Chapter 1", title, body, "", 0, false) {
                                    title = ""; body = ""
                                }
                            },
                            enabled = title.length >= 2 && body.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Publish Notes") }
                    }
                }
            }

            1 -> {
                var title by remember { mutableStateOf("") }
                var quizMode by remember { mutableStateOf("QUIZ") }
                var prompt by remember { mutableStateOf("") }
                var optA by remember { mutableStateOf("") }
                var optB by remember { mutableStateOf("") }
                var optC by remember { mutableStateOf("") }
                var optD by remember { mutableStateOf("") }
                var explanation by remember { mutableStateOf("") }
                Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Publish MCQ Quiz or Exam", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(selected = quizMode == "QUIZ", onClick = { quizMode = "QUIZ" }, label = { Text("Daily Quiz") })
                            FilterChip(selected = quizMode == "EXAM", onClick = { quizMode = "EXAM" }, label = { Text("Online Exam") })
                        }
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = prompt, onValueChange = { prompt = it }, label = { Text("Question") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = optA, onValueChange = { optA = it }, label = { Text("Option A (Correct)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = optB, onValueChange = { optB = it }, label = { Text("Option B") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = optC, onValueChange = { optC = it }, label = { Text("Option C") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = optD, onValueChange = { optD = it }, label = { Text("Option D") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = explanation, onValueChange = { explanation = it }, label = { Text("Explanation") }, modifier = Modifier.fillMaxWidth())
                        Button(
                            onClick = {
                                onPublishAssessment(title, "Mathematics", "Class 10", quizMode, 15, prompt, listOf(optA, optB, optC, optD), 0, explanation) {
                                    title = ""; prompt = ""; optA = ""; optB = ""; optC = ""; optD = ""; explanation = ""
                                }
                            },
                            enabled = title.length >= 2 && prompt.isNotBlank() && optA.isNotBlank() && optB.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Publish ${if (quizMode == "EXAM") "Exam" else "Quiz"}") }
                    }
                }
            }

            else -> {
                var title by remember { mutableStateOf("") }
                var body by remember { mutableStateOf("") }
                var audience by remember { mutableStateOf("ALL") }
                Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Send Student Announcement", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Message") }, minLines = 3, modifier = Modifier.fillMaxWidth())
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listOf("ALL", "CLASS_10", "CBSE", "PREMIUM")) { aud ->
                                FilterChip(selected = audience == aud, onClick = { audience = aud }, label = { Text(aud) })
                            }
                        }
                        Button(
                            onClick = {
                                onPublishAnnouncement(title, body, "GENERAL", audience) {
                                    title = ""; body = ""
                                }
                            },
                            enabled = title.length >= 2 && body.length >= 2,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) { Text("Send Announcement") }
                    }
                }
            }
        }
    }
}

@Composable
fun CoAdminLoginDialog(
    onDismiss: () -> Unit,
    onSubmit: (name: String, password: String, onError: (String) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isVerifying by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { if (!isVerifying) onDismiss() },
        icon = {
            Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(32.dp))
        },
        title = {
            Text("Admin / Co-Admin Login", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Enter the Co-Admin Name and Password created by Main Admin (buildolive4@gmail.com).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Co-Admin Name") },
                    singleLine = true,
                    enabled = !isVerifying,
                    modifier = Modifier.fillMaxWidth().testTag("coadmin_login_name")
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMessage = null },
                    label = { Text("Password") },
                    singleLine = true,
                    enabled = !isVerifying,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "Toggle password"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("coadmin_login_password")
                )
                if (errorMessage != null) {
                    Text(errorMessage!!, style = MaterialTheme.typography.bodySmall, color = ErrorRed)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isVerifying = true
                    errorMessage = null
                    onSubmit(name, password) { err ->
                        isVerifying = false
                        errorMessage = err
                    }
                },
                enabled = !isVerifying && name.trim().length >= 2 && password.isNotBlank(),
                modifier = Modifier.testTag("coadmin_login_submit")
            ) {
                if (isVerifying) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text("Unlock Admin Panel")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isVerifying) {
                Text("Cancel")
            }
        }
    )
}
