package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CurriculumCatalog
import com.example.data.model.UserProfile
import com.example.ui.i18n.AppLanguages
import com.example.ui.i18n.LocalAppLanguage
import com.example.ui.i18n.LocalAppStrings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    existingProfile: UserProfile?,
    isEditingFromProfile: Boolean = false,
    onOpenLanguagePicker: () -> Unit = {},
    onSave: (
        displayName: String,
        board: String,
        classLevel: String,
        medium: String,
        academicSession: String,
        subjects: List<String>
    ) -> Unit
) {
    val strings = LocalAppStrings.current
    val currentLanguage = LocalAppLanguage.current
    var displayName by remember(existingProfile) {
        mutableStateOf(existingProfile?.displayName?.ifBlank { "Student" } ?: "Student")
    }
    var selectedBoard by remember(existingProfile) {
        mutableStateOf(existingProfile?.board ?: "CBSE")
    }
    var selectedClass by remember(existingProfile) {
        mutableStateOf(existingProfile?.classLevel ?: "Class 10")
    }
    var selectedMedium by remember(existingProfile) {
        mutableStateOf(existingProfile?.medium ?: "English")
    }
    var academicSession by remember(existingProfile) {
        mutableStateOf(existingProfile?.academicSession ?: "2026-27")
    }
    val selectedSubjects = remember(existingProfile) {
        mutableStateListOf<String>().apply {
            addAll(existingProfile?.subjects?.ifEmpty { CurriculumCatalog.allSubjects } ?: CurriculumCatalog.allSubjects)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.School,
                    contentDescription = "Academic Setup",
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = if (isEditingFromProfile) strings.academicPreferences else strings.onboardingTitle,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = strings.onboardingSubtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Prominent 15-Language Picker Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onOpenLanguagePicker)
                        .testTag("onboarding_language_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Translate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Column {
                                Text(
                                    text = strings.appLanguageLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "${currentLanguage.nativeName} (${currentLanguage.englishName})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Text(
                            text = "Change",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it.take(80) },
                    label = { Text(strings.studentName) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("onboarding_name_input")
                )

                // Board Selector
                PreferenceSectionLabel(strings.educationBoard)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CurriculumCatalog.boards.forEach { board ->
                        val selected = selectedBoard == board
                        FilterChip(
                            selected = selected,
                            onClick = { selectedBoard = board },
                            label = { Text(board) },
                            leadingIcon = if (selected) {
                                { Icon(Icons.Filled.Check, contentDescription = null) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                // Class Selector
                PreferenceSectionLabel(strings.selectYourClass)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CurriculumCatalog.classLevels.forEach { level ->
                        val selected = selectedClass == level
                        FilterChip(
                            selected = selected,
                            onClick = { selectedClass = level },
                            label = { Text(level) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

                // Medium Selector (All 15 Indian Languages + Bilingual)
                PreferenceSectionLabel(strings.preferredMedium)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CurriculumCatalog.mediums.forEach { med ->
                        val selected = selectedMedium == med
                        val displayLabel = AppLanguages.all.firstOrNull { it.englishName == med }
                            ?.let { if (it.code == "en") "English" else "${it.nativeName} (${it.englishName})" }
                            ?: med
                        FilterChip(
                            selected = selected,
                            onClick = { selectedMedium = med },
                            label = { Text(displayLabel) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                            )
                        )
                    }
                }

                // Subjects Multi-Select
                PreferenceSectionLabel(strings.yourSubjects)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CurriculumCatalog.allSubjects.forEach { subj ->
                        val selected = subj in selectedSubjects
                        FilterChip(
                            selected = selected,
                            onClick = {
                                if (selected && selectedSubjects.size > 1) {
                                    selectedSubjects.remove(subj)
                                } else if (!selected) {
                                    selectedSubjects.add(subj)
                                }
                            },
                            label = { Text(subj) },
                            leadingIcon = if (selected) {
                                { Icon(Icons.Filled.Check, contentDescription = null) }
                            } else null
                        )
                    }
                }

                OutlinedTextField(
                    value = academicSession,
                    onValueChange = { academicSession = it.take(16) },
                    label = { Text(strings.academicSession) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        onSave(
                            displayName.ifBlank { "Student" },
                            selectedBoard,
                            selectedClass,
                            selectedMedium,
                            academicSession.ifBlank { "2026-27" },
                            selectedSubjects.toList()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("onboarding_save_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (isEditingFromProfile) strings.savePreferences else strings.startLearning,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun PreferenceSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}
