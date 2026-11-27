package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Textbook Brand Palette (Exact specification)
val NavyPrimary = Color(0xFF183B56)
val NavyDark = Color(0xFF0F273A)
val NavyLight = Color(0xFF28557A)
val NavyContainer = Color(0xFFD9E8F6)
val OnNavyContainer = Color(0xFF0D2538)

val EmeraldSecondary = Color(0xFF4CAF8A)
val EmeraldDark = Color(0xFF2F8564)
val EmeraldContainer = Color(0xFFD6F5E8)
val OnEmeraldContainer = Color(0xFF0B3B28)

val AmberAccent = Color(0xFFF59E0B)
val AmberContainer = Color(0xFFFEF3C7)
val OnAmberContainer = Color(0xFF451A03)

val WarmWhiteBg = Color(0xFFF8FAFC)
val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFEFF4F9)
val OutlineLight = Color(0xFFCBD5E1)
val DarkSlateText = Color(0xFF1E293B)
val MutedSlateText = Color(0xFF64748B)

// Dark Mode Palette (Deep Midnight Academic Navy)
val DarkBg = Color(0xFF0B1622)
val DarkSurface = Color(0xFF122233)
val DarkSurfaceVariant = Color(0xFF1B3047)
val DarkPrimary = Color(0xFF8CC5F2)
val DarkOnPrimary = Color(0xFF0A2438)
val DarkSecondary = Color(0xFF63D0A8)
val DarkOnSecondary = Color(0xFF063322)
val DarkTertiary = Color(0xFFFBBF24)
val DarkTextPrimary = Color(0xFFE2E8F0)
val DarkTextMuted = Color(0xFF94A3B8)
val DarkOutline = Color(0xFF334155)

// Reader Sepia Palette
val SepiaBg = Color(0xFFF6EFE0)
val SepiaSurface = Color(0xFFEDE2CC)
val SepiaText = Color(0xFF3B2F21)

// Subject-wise Brand Color Coding
val SubjectMathColor = Color(0xFF183B56)
val SubjectScienceColor = Color(0xFF0D9488)
val SubjectEnglishColor = Color(0xFF4F46E5)
val SubjectHindiColor = Color(0xFFD97706)
val SubjectSocialColor = Color(0xFFBE123C)
val SubjectComputerColor = Color(0xFF0284C7)

val ErrorRed = Color(0xFFDC2626)
val ErrorContainerLight = Color(0xFFFEE2E2)
val SuccessGreen = Color(0xFF16A34A)

fun subjectColor(subject: String): Color {
    return when (subject.lowercase()) {
        "mathematics", "maths", "math" -> SubjectMathColor
        "science", "physics", "chemistry", "biology" -> SubjectScienceColor
        "english" -> SubjectEnglishColor
        "hindi" -> SubjectHindiColor
        "social science", "history", "geography", "civics" -> SubjectSocialColor
        else -> SubjectComputerColor
    }
}
