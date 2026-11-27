package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CurriculumCatalog
import com.example.data.model.PersonalNote
import com.example.data.model.SavedMaterial
import com.example.data.model.UserProfile
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary

private enum class ProfileSubPage { MAIN, SUBSCRIPTIONS, SAVED_AND_NOTES, EDUCATOR_STUDIO }

@Composable
fun ProfileAndManagementScreen(
    profile: UserProfile?,
    personalNotes: List<PersonalNote>,
    savedMaterials: List<SavedMaterial>,
    isDarkMode: Boolean,
    isEducatorOrAdmin: Boolean,
    textbookCount: Int,
    materialCount: Int,
    assessmentCount: Int,
    onToggleDarkMode: () -> Unit,
    onEditPreferences: () -> Unit,
    onOpenPlannerAndProgress: () -> Unit,
    onOpenNotifications: () -> Unit,
    onSelectPlan: (tierId: String) -> Unit,
    onDeleteNote: (noteId: String) -> Unit,
    onPublishTextbook: (
        title: String, subject: String, classLevel: String, category: String,
        board: String, medium: String, description: String, premiumOnly: Boolean,
        chapterTitle: String, chapterBody: String, onDone: () -> Unit
    ) -> Unit,
    onPublishMaterial: (
        title: String, category: String, subject: String, classLevel: String,
        chapterTitle: String, description: String, contentBody: String,
        videoDurationMinutes: Int, premiumOnly: Boolean, onDone: () -> Unit
    ) -> Unit,
    onPublishAssessment: (
        title: String, subject: String, classLevel: String, mode: String,
        durationMinutes: Int, prompt: String, options: List<String>,
        correctIndex: Int, explanation: String, onDone: () -> Unit
    ) -> Unit,
    onPublishAnnouncement: (
        title: String, body: String, category: String, audience: String, onDone: () -> Unit
    ) -> Unit,
    onSyncCatalogToCloud: () -> Unit,
    onSignOut: () -> Unit
) {
    var subPage by remember { mutableStateOf(ProfileSubPage.MAIN) }

    BackHandler(enabled = subPage != ProfileSubPage.MAIN) {
        subPage = ProfileSubPage.MAIN
    }

    when (subPage) {
        ProfileSubPage.MAIN -> {
            val name = profile?.displayName ?: "Student"
            val email = profile?.email ?: ""
            val currentTier = profile?.planTier ?: "FREE"

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Profile Identity Header
                item {
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Brush.linearGradient(listOf(NavyDark, NavyPrimary)))
                                .padding(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldSecondary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = name.take(1).uppercase(),
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (email.isNotBlank()) {
                                        Text(
                                            text = email,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.78f)
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Surface(shape = RoundedCornerShape(8.dp), color = Color.White.copy(alpha = 0.16f)) {
                                            Text(
                                                text = "${profile?.classLevel ?: "Class 10"} • ${profile?.board ?: "CBSE"}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (currentTier == "FREE") EmeraldSecondary else AmberAccent
                                        ) {
                                            Text(
                                                text = currentTier.replace("_", " "),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Menu Cards
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            ProfileRowItem(
                                icon = Icons.Filled.School,
                                title = "Academic Preferences",
                                subtitle = "${profile?.board ?: "CBSE"} • ${profile?.classLevel ?: "Class 10"} • ${profile?.medium ?: "English"}",
                                accent = NavyPrimary,
                                onClick = onEditPreferences
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            ProfileRowItem(
                                icon = Icons.Filled.CalendarMonth,
                                title = "Study Planner & Progress Report",
                                subtitle = "Goals, weekly activity & weak-topic insights",
                                accent = EmeraldSecondary,
                                onClick = onOpenPlannerAndProgress
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            ProfileRowItem(
                                icon = Icons.Filled.Bookmark,
                                title = "Saved Materials & Personal Notes",
                                subtitle = "${savedMaterials.size} saved • ${personalNotes.size} personal notes",
                                accent = AmberAccent,
                                onClick = { subPage = ProfileSubPage.SAVED_AND_NOTES }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            ProfileRowItem(
                                icon = Icons.Filled.WorkspacePremium,
                                title = "Subscription Plans",
                                subtitle = "Monthly ₹99 • Quarterly ₹249 • Yearly ₹799",
                                accent = Color(0xFFD97706),
                                onClick = { subPage = ProfileSubPage.SUBSCRIPTIONS }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            ProfileRowItem(
                                icon = Icons.Filled.Notifications,
                                title = "Announcements & Exam Alerts",
                                subtitle = "View latest board & teacher notifications",
                                accent = Color(0xFF4F46E5),
                                onClick = onOpenNotifications
                            )
                        }
                    }
                }

                // Teacher & Admin Panel Access Card
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        ProfileRowItem(
                            icon = Icons.Filled.AdminPanelSettings,
                            title = "Teacher & Admin Studio",
                            subtitle = if (isEducatorOrAdmin) {
                                "Verified Admin Access • Publish books, notes, exams & alerts"
                            } else {
                                "Content management & catalog overview (Admin RBAC protected)"
                            },
                            accent = Color(0xFFBE123C),
                            onClick = { subPage = ProfileSubPage.EDUCATOR_STUDIO }
                        )
                    }
                }

                // Appearance Toggle
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Icon(Icons.Filled.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Column {
                                    Text("Dark Reading Theme", style = MaterialTheme.typography.titleSmall)
                                    Text("Comfortable night-time study mode", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(checked = isDarkMode, onCheckedChange = { onToggleDarkMode() })
                        }
                    }
                }

                // Sign Out Button
                item {
                    OutlinedButton(
                        onClick = onSignOut,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("sign_out_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        ProfileSubPage.SUBSCRIPTIONS -> {
            SubscriptionPlansView(
                currentTier = profile?.planTier ?: "FREE",
                onBack = { subPage = ProfileSubPage.MAIN },
                onSelectPlan = onSelectPlan
            )
        }

        ProfileSubPage.SAVED_AND_NOTES -> {
            SavedAndNotesView(
                personalNotes = personalNotes,
                savedMaterials = savedMaterials,
                onBack = { subPage = ProfileSubPage.MAIN },
                onDeleteNote = onDeleteNote
            )
        }

        ProfileSubPage.EDUCATOR_STUDIO -> {
            EducatorAdminStudioView(
                isEducatorOrAdmin = isEducatorOrAdmin,
                textbookCount = textbookCount,
                materialCount = materialCount,
                assessmentCount = assessmentCount,
                onBack = { subPage = ProfileSubPage.MAIN },
                onPublishTextbook = onPublishTextbook,
                onPublishMaterial = onPublishMaterial,
                onPublishAssessment = onPublishAssessment,
                onPublishAnnouncement = onPublishAnnouncement,
                onSyncCatalogToCloud = onSyncCatalogToCloud
            )
        }
    }
}

@Composable
private fun ProfileRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SubscriptionPlansView(
    currentTier: String,
    onBack: () -> Unit,
    onSelectPlan: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Subscription Plans", style = MaterialTheme.typography.headlineSmall)
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = AmberAccent.copy(alpha = 0.14f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Prototype Checkout Mode: You can preview and switch plan tiers immediately. For production billing, connect Razorpay Orders API with server-side webhook verification before activating subscriptions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        items(CurriculumCatalog.subscriptionPlans, key = { it.tierId }) { plan ->
            val isCurrent = currentTier == plan.tierId
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isCurrent) Modifier.border(2.dp, EmeraldSecondary, RoundedCornerShape(18.dp))
                        else Modifier
                    )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(plan.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(plan.durationLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (plan.priceInr == 0) "Free" else "₹${plan.priceInr}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            if (plan.badge != null) {
                                Surface(shape = RoundedCornerShape(8.dp), color = AmberAccent) {
                                    Text(
                                        text = plan.badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    plan.features.forEach { feat ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldSecondary, modifier = Modifier.size(16.dp))
                            Text(feat, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Button(
                        onClick = { onSelectPlan(plan.tierId) },
                        enabled = !isCurrent,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCurrent) EmeraldSecondary else NavyPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isCurrent) "Active Plan" else "Activate ${plan.name} (Prototype)")
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedAndNotesView(
    personalNotes: List<PersonalNote>,
    savedMaterials: List<SavedMaterial>,
    onBack: () -> Unit,
    onDeleteNote: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("My Notes & Saved Library", style = MaterialTheme.typography.headlineSmall)
            }
        }

        item {
            Text("Personal Textbook Notes (${personalNotes.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (personalNotes.isEmpty()) {
            item {
                Text(
                    "No personal notes saved yet. Open any chapter in the Textbook Reader and tap the Note icon.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(personalNotes, key = { it.id }) { note ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.EditNote, contentDescription = null, tint = NavyPrimary, modifier = Modifier.padding(top = 2.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("${note.bookTitle} • ${note.chapterTitle}", style = MaterialTheme.typography.labelMedium, color = EmeraldSecondary)
                            Text(note.content, style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = { onDeleteNote(note.id) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete note")
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("Saved Books, Notes & Videos (${savedMaterials.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        if (savedMaterials.isEmpty()) {
            item {
                Text(
                    "Bookmark any textbook, formula sheet, or video lecture to pin it here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(savedMaterials, key = { it.id }) { saved ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Filled.Bookmark, contentDescription = null, tint = AmberAccent)
                        Column {
                            Text(saved.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("${saved.itemType} • ${saved.subject}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EducatorAdminStudioView(
    isEducatorOrAdmin: Boolean,
    textbookCount: Int,
    materialCount: Int,
    assessmentCount: Int,
    onBack: () -> Unit,
    onPublishTextbook: (
        String, String, String, String, String, String, String, Boolean, String, String, () -> Unit
    ) -> Unit,
    onPublishMaterial: (
        String, String, String, String, String, String, String, Int, Boolean, () -> Unit
    ) -> Unit,
    onPublishAssessment: (
        String, String, String, String, Int, String, List<String>, Int, String, () -> Unit
    ) -> Unit,
    onPublishAnnouncement: (String, String, String, String, () -> Unit) -> Unit,
    onSyncCatalogToCloud: () -> Unit
) {
    var tab by remember { mutableIntStateOf(0) } // 0 = Book, 1 = Material, 2 = Exam/Quiz, 3 = Alert

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Teacher & Admin Studio", style = MaterialTheme.typography.headlineSmall)
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEducatorOrAdmin) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isEducatorOrAdmin) "Admin & Educator RBAC Active" else "Read-Only Preview (Student Account)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isEducatorOrAdmin) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Live Catalog: $textbookCount Textbooks • $materialCount Notes/Videos • $assessmentCount Quizzes & Exams",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isEducatorOrAdmin) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (isEducatorOrAdmin) {
                        Button(
                            onClick = onSyncCatalogToCloud,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSecondary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.CloudUpload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sync Starter Curriculum to Firestore")
                        }
                    }
                }
            }
        }

        item {
            TabRow(selectedTabIndex = tab) {
                Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Book") })
                Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Note/Vid") })
                Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text("Quiz") })
                Tab(selected = tab == 3, onClick = { tab = 3 }, text = { Text("Alert") })
            }
        }

        item {
            when (tab) {
                0 -> PublishBookForm(enabled = isEducatorOrAdmin, onPublish = onPublishTextbook)
                1 -> PublishMaterialForm(enabled = isEducatorOrAdmin, onPublish = onPublishMaterial)
                2 -> PublishAssessmentForm(enabled = isEducatorOrAdmin, onPublish = onPublishAssessment)
                else -> PublishAnnouncementForm(enabled = isEducatorOrAdmin, onPublish = onPublishAnnouncement)
            }
        }
    }
}

@Composable
private fun PublishBookForm(
    enabled: Boolean,
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
                enabled = enabled && title.length >= 2 && chTitle.length >= 2 && chBody.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Publish Textbook to Cloud") }
        }
    }
}

@Composable
private fun PublishMaterialForm(
    enabled: Boolean,
    onPublish: (String, String, String, String, String, String, String, Int, Boolean, () -> Unit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Chapter Notes") }
    var subject by remember { mutableStateOf("Science") }
    var body by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Upload Study Material or Video Lesson", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(listOf("Chapter Notes", "Formula Sheet", "Previous Year Papers", "Video Lecture")) { c ->
                    FilterChip(selected = category == c, onClick = { category = c }, label = { Text(c) })
                }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(CurriculumCatalog.allSubjects) { s ->
                    FilterChip(selected = subject == s, onClick = { subject = s }, label = { Text(s) })
                }
            }
            OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Notes / Lesson Summary Content") }, minLines = 3, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    onPublish(title, category, subject, "Class 10", "Chapter 1", title, body, if (category == "Video Lecture") 15 else 0, false) {
                        title = ""; body = ""
                    }
                },
                enabled = enabled && title.length >= 2 && body.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Publish Study Material") }
        }
    }
}

@Composable
private fun PublishAssessmentForm(
    enabled: Boolean,
    onPublish: (String, String, String, String, Int, String, List<String>, Int, String, () -> Unit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf("QUIZ") }
    var subject by remember { mutableStateOf("Mathematics") }
    var prompt by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var explanation by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Create MCQ Quiz or Online Exam", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = mode == "QUIZ", onClick = { mode = "QUIZ" }, label = { Text("Practice Quiz") })
                FilterChip(selected = mode == "EXAM", onClick = { mode = "EXAM" }, label = { Text("Online Exam") })
            }
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Quiz / Exam Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = prompt, onValueChange = { prompt = it }, label = { Text("Question Prompt") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = optA, onValueChange = { optA = it }, label = { Text("Option A (Correct)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = optB, onValueChange = { optB = it }, label = { Text("Option B") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = optC, onValueChange = { optC = it }, label = { Text("Option C") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = optD, onValueChange = { optD = it }, label = { Text("Option D") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = explanation, onValueChange = { explanation = it }, label = { Text("Answer Explanation") }, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    onPublish(title, subject, "Class 10", mode, 15, prompt, listOf(optA, optB, optC, optD), 0, explanation) {
                        title = ""; prompt = ""; optA = ""; optB = ""; optC = ""; optD = ""; explanation = ""
                    }
                },
                enabled = enabled && title.length >= 2 && prompt.isNotBlank() && optA.isNotBlank() && optB.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Publish ${if (mode == "EXAM") "Exam" else "Quiz"}") }
        }
    }
}

@Composable
private fun PublishAnnouncementForm(
    enabled: Boolean,
    onPublish: (String, String, String, String, () -> Unit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var audience by remember { mutableStateOf("ALL") }

    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Broadcast Notification", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Notification Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Announcement Message") }, minLines = 3, modifier = Modifier.fillMaxWidth())
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(listOf("ALL", "CLASS_10", "CBSE", "PREMIUM")) { aud ->
                    FilterChip(selected = audience == aud, onClick = { audience = aud }, label = { Text(aud) })
                }
            }
            Button(
                onClick = {
                    onPublish(title, body, "GENERAL", audience) {
                        title = ""; body = ""
                    }
                },
                enabled = enabled && title.length >= 2 && body.length >= 2,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Send Announcement") }
        }
    }
}
