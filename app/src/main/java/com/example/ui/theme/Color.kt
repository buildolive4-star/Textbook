package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Vibrant Competitive Exam Brand Palette (Royal Indigo, Vivid Emerald, Trophy Gold, Electric Cyan & Coral)
val NavyPrimary = Color(0xFF3B28CC)
val NavyDark = Color(0xFF1A1054)
val NavyLight = Color(0xFF6366F1)
val NavyContainer = Color(0xFFE0E7FF)
val OnNavyContainer = Color(0xFF1E1466)

val EmeraldSecondary = Color(0xFF00B884)
val EmeraldDark = Color(0xFF008762)
val EmeraldContainer = Color(0xFFD1FAE5)
val OnEmeraldContainer = Color(0xFF064E3B)

val AmberAccent = Color(0xFFFF9F1C)
val AmberContainer = Color(0xFFFEF3C7)
val OnAmberContainer = Color(0xFF5C2D00)

// Extra Vibrant Competitive Accents
val ElectricCyan = Color(0xFF06B6D4)
val VividCoral = Color(0xFFFF4D6D)
val RoyalPurple = Color(0xFF7C3AED)
val SunsetOrange = Color(0xFFF97316)
val GoldMedal = Color(0xFFFFB703)
val SilverMedal = Color(0xFF94A3B8)
val BronzeMedal = Color(0xFFCD7F32)

val WarmWhiteBg = Color(0xFFF4F6FF)
val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFECEFFD)
val OutlineLight = Color(0xFFCBD5E1)
val DarkSlateText = Color(0xFF14183B)
val MutedSlateText = Color(0xFF5B6488)

// Dark Mode Palette (Deep Cosmic Indigo)
val DarkBg = Color(0xFF0D0B21)
val DarkSurface = Color(0xFF171433)
val DarkSurfaceVariant = Color(0xFF231F4A)
val DarkPrimary = Color(0xFFA5B4FC)
val DarkOnPrimary = Color(0xFF160E52)
val DarkSecondary = Color(0xFF34D399)
val DarkOnSecondary = Color(0xFF043B2B)
val DarkTertiary = Color(0xFFFBBF24)
val DarkTextPrimary = Color(0xFFF1F5F9)
val DarkTextMuted = Color(0xFFA5B0CF)
val DarkOutline = Color(0xFF363268)

// Reader Sepia Palette
val SepiaBg = Color(0xFFF7F0E2)
val SepiaSurface = Color(0xFFEDE2CC)
val SepiaText = Color(0xFF3B2F21)

// Subject-wise Vibrant Color Coding
val SubjectMathColor = Color(0xFF4338CA)       // Quantitative Aptitude / Maths (Royal Indigo)
val SubjectScienceColor = Color(0xFF00A896)    // General Science / Physics / Bio (Vibrant Teal)
val SubjectEnglishColor = Color(0xFF7C3AED)    // English Comprehension (Vivid Violet)
val SubjectHindiColor = Color(0xFFF97316)      // Current Affairs / Hindi (Sunset Orange)
val SubjectSocialColor = Color(0xFFE11D48)     // General Studies / Polity / History (Crimson Rose)
val SubjectComputerColor = Color(0xFF0284C7)   // Logical Reasoning / Computer (Electric Blue)

val ErrorRed = Color(0xFFE11D48)
val ErrorContainerLight = Color(0xFFFFE4E6)
val SuccessGreen = Color(0xFF10B981)

fun subjectColor(subject: String): Color {
    val s = subject.lowercase()
    return when {
        "quant" in s || "math" in s -> SubjectMathColor
        "reason" in s || "logic" in s || "computer" in s -> SubjectComputerColor
        "science" in s || "physic" in s || "chem" in s || "bio" in s -> SubjectScienceColor
        "english" in s || "verbal" in s -> SubjectEnglishColor
        "current" in s || "hindi" in s || "economy" in s -> SubjectHindiColor
        "general studies" in s || "gk" in s || "polity" in s || "history" in s || "geography" in s || "social" in s -> SubjectSocialColor
        else -> RoyalPurple
    }
}

fun subjectGradient(subject: String): List<Color> {
    val s = subject.lowercase()
    return when {
        "quant" in s || "math" in s -> listOf(Color(0xFF3B28CC), Color(0xFF6366F1))
        "reason" in s || "logic" in s || "computer" in s -> listOf(Color(0xFF0284C7), Color(0xFF06B6D4))
        "science" in s || "physic" in s || "chem" in s || "bio" in s -> listOf(Color(0xFF059669), Color(0xFF10B981))
        "english" in s || "verbal" in s -> listOf(Color(0xFF6D28D9), Color(0xFFA855F7))
        "current" in s || "hindi" in s || "economy" in s -> listOf(Color(0xFFEA580C), Color(0xFFF59E0B))
        "general studies" in s || "gk" in s || "polity" in s || "history" in s || "social" in s -> listOf(Color(0xFFBE123C), Color(0xFFFF4D6D))
        else -> listOf(Color(0xFF4F46E5), Color(0xFF06B6D4))
    }
}

fun examBadgeColor(exam: String): Color {
    val e = exam.lowercase()
    return when {
        "ssc" in e -> Color(0xFF4338CA)
        "upsc" in e -> Color(0xFFBE123C)
        "bank" in e || "ibps" in e || "sbi" in e -> Color(0xFF059669)
        "rail" in e || "rrb" in e -> Color(0xFFEA580C)
        "neet" in e -> Color(0xFF0D9488)
        "jee" in e -> Color(0xFF7C3AED)
        "defence" in e || "nda" in e || "cds" in e -> Color(0xFF0284C7)
        else -> AmberAccent
    }
}
