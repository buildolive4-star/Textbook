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
import androidx.compose.material.icons.filled.Translate
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
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary

private enum class ProfileSubPage { MAIN, SUBSCRIPTIONS, SAVED_AND_NOTES }

@Composable
fun ProfileAndManagementScreen(
    profile: UserProfile?,
    personalNotes: List<PersonalNote>,
    savedMaterials: List<SavedMaterial>,
    isDarkMode: Boolean,
    hasAdminAccess: Boolean,
    isMainAdmin: Boolean,
    onOpenLanguagePicker: () -> Unit,
    onOpenAdminPanel: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onEditPreferences: () -> Unit,
    onOpenPlannerAndProgress: () -> Unit,
    onOpenNotifications: () -> Unit,
    onSelectPlan: (tierId: String) -> Unit,
    onDeleteNote: (noteId: String) -> Unit,
    onSignOut: () -> Unit
) {
    val strings = LocalAppStrings.current
    val currentLanguage = LocalAppLanguage.current
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
                                icon = Icons.Filled.Translate,
                                title = strings.appLanguageLabel,
                                subtitle = "${currentLanguage.nativeName} • ${currentLanguage.englishName}",
                                accent = EmeraldSecondary,
                                onClick = onOpenLanguagePicker
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            ProfileRowItem(
                                icon = Icons.Filled.School,
                                title = strings.academicPreferences,
                                subtitle = "${profile?.board ?: "CBSE"} • ${profile?.classLevel ?: "Class 10"} • ${profile?.medium ?: "English"}",
                                accent = NavyPrimary,
                                onClick = onEditPreferences
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            ProfileRowItem(
                                icon = Icons.Filled.CalendarMonth,
                                title = "${strings.studyPlanner} & ${strings.progressReport}",
                                subtitle = strings.studyPlannerSubtitle,
                                accent = EmeraldSecondary,
                                onClick = onOpenPlannerAndProgress
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            ProfileRowItem(
                                icon = Icons.Filled.Bookmark,
                                title = strings.savedAndNotes,
                                subtitle = "${savedMaterials.size} saved • ${personalNotes.size} notes",
                                accent = AmberAccent,
                                onClick = { subPage = ProfileSubPage.SAVED_AND_NOTES }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            ProfileRowItem(
                                icon = Icons.Filled.WorkspacePremium,
                                title = strings.subscriptionPlans,
                                subtitle = "Monthly ₹99 • Quarterly ₹249 • Yearly ₹799",
                                accent = Color(0xFFD97706),
                                onClick = { subPage = ProfileSubPage.SUBSCRIPTIONS }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            ProfileRowItem(
                                icon = Icons.Filled.Notifications,
                                title = strings.announcements,
                                subtitle = strings.notificationsTitle,
                                accent = Color(0xFF4F46E5),
                                onClick = onOpenNotifications
                            )
                        }
                    }
                }

                // Admin Panel Quick Access (shown only for verified Main Admin or unlocked Co-Admin; hidden from regular students)
                if (hasAdminAccess) {
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            ProfileRowItem(
                                icon = Icons.Filled.AdminPanelSettings,
                                title = if (isMainAdmin) "Main Admin Panel" else "Co-Admin Panel",
                                subtitle = "Manage Zoom Live Classes, Cloudinary Lectures, Books & Co-Admins",
                                accent = Color(0xFFBE123C),
                                onClick = onOpenAdminPanel
                            )
                        }
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
                                    Text(strings.darkTheme, style = MaterialTheme.typography.titleSmall)
                                    Text(strings.darkThemeSubtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        Text(strings.signOut, style = MaterialTheme.typography.labelLarge)
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
