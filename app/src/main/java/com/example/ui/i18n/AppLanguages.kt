package com.example.ui.i18n

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ui.theme.EmeraldSecondary
import com.example.ui.theme.NavyPrimary

data class AppLanguage(
    val code: String,
    val englishName: String,
    val nativeName: String,
    val shortBadge: String,
    val regionLabel: String,
    val isRtl: Boolean = false
)

object AppLanguages {
    val ENGLISH = AppLanguage("en", "English", "English", "EN", "All Boards")
    val HINDI = AppLanguage("hi", "Hindi", "हिन्दी", "हिंदी", "भारत • CBSE / State")
    val TAMIL = AppLanguage("ta", "Tamil", "தமிழ்", "தமிழ்", "தமிழ்நாடு")
    val TELUGU = AppLanguage("te", "Telugu", "తెలుగు", "తెలుగు", "ఆంధ్ర / తెలంగాణ")
    val BENGALI = AppLanguage("bn", "Bengali", "বাংলা", "বাংলা", "পশ্চিমবঙ্গ")
    val MARATHI = AppLanguage("mr", "Marathi", "मराठी", "मराठी", "महाराष्ट्र")
    val GUJARATI = AppLanguage("gu", "Gujarati", "ગુજરાતી", "ગુજ", "ગુજરાત")
    val KANNADA = AppLanguage("kn", "Kannada", "ಕನ್ನಡ", "ಕನ್ನಡ", "ಕರ್ನಾಟಕ")
    val MALAYALAM = AppLanguage("ml", "Malayalam", "മലയാളം", "മലയാളം", "കേരളം")
    val PUNJABI = AppLanguage("pa", "Punjabi", "ਪੰਜਾਬੀ", "ਪੰਜਾਬੀ", "ਪੰਜਾਬ")
    val ODIA = AppLanguage("or", "Odia", "ଓଡ଼ିଆ", "ଓଡ଼ିଆ", "ଓଡ଼ିଶା")
    val URDU = AppLanguage("ur", "Urdu", "اردو", "اردو", "بھارت", isRtl = true)
    val ASSAMESE = AppLanguage("as", "Assamese", "অসমীয়া", "অসমীয়া", "অসম")
    val SANSKRIT = AppLanguage("sa", "Sanskrit", "संस्कृतम्", "संस्कृत", "देववाणी")
    val MAITHILI = AppLanguage("mai", "Maithili", "मैथिली", "मैथिली", "बिहार / मिथिला")

    val all: List<AppLanguage> = listOf(
        ENGLISH,
        HINDI,
        TAMIL,
        TELUGU,
        BENGALI,
        MARATHI,
        GUJARATI,
        KANNADA,
        MALAYALAM,
        PUNJABI,
        ODIA,
        URDU,
        ASSAMESE,
        SANSKRIT,
        MAITHILI
    )

    val allEnglishNames: List<String> = all.map { it.englishName }
    val allMediums: List<String> = allEnglishNames + "Bilingual"

    fun byCode(code: String?): AppLanguage =
        all.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH

    fun byEnglishName(name: String?): AppLanguage =
        all.firstOrNull { it.englishName.equals(name, ignoreCase = true) } ?: ENGLISH
}

val LocalAppLanguage = compositionLocalOf { AppLanguages.ENGLISH }
val LocalAppStrings = compositionLocalOf { englishStrings() }

@Composable
fun LanguagePillChip(
    currentLanguage: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier
            .testTag("language_picker_chip")
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Translate,
                contentDescription = "Change Language",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = currentLanguage.shortBadge,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerSheet(
    currentLanguage: AppLanguage,
    title: String,
    subtitle: String,
    onSelectLanguage: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) AppLanguages.all
        else AppLanguages.all.filter {
            it.englishName.contains(query, ignoreCase = true) ||
                it.nativeName.contains(query, ignoreCase = true) ||
                it.regionLabel.contains(query, ignoreCase = true)
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Translate,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(26.dp)
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search Hindi, Tamil, Telugu, Bengali...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("language_search_input")
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filtered, key = { it.code }) { lang ->
                    val isSelected = lang.code == currentLanguage.code
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) NavyPrimary.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) EmeraldSecondary else Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lang_option_${lang.code}")
                            .clickable {
                                onSelectLanguage(lang)
                                onDismiss()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lang.nativeName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${lang.englishName} • ${lang.regionLabel}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = EmeraldSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}
