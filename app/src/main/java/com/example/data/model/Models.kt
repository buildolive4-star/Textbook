package com.example.data.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "Student",
    val email: String? = null,
    val board: String = "CBSE",
    val classLevel: String = "Class 10",
    val medium: String = "English",
    val academicSession: String = "2026-27",
    val subjects: List<String> = listOf("Mathematics", "Science", "English", "Hindi", "Social Science"),
    val onboardingDone: Boolean = false,
    val planTier: String = "FREE",
    val studyMinutesTotal: Long = 0L,
    val streakDays: Long = 1L,
    val aiQueriesToday: Long = 0L,
    val lastBookId: String? = null,
    val lastChapterId: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ReadingProgress(
    @DocumentId val id: String = "",
    val userId: String = "",
    val bookId: String = "",
    val bookTitle: String = "",
    val subject: String = "",
    val lastChapterId: String = "",
    val lastChapterNumber: Long = 1L,
    val lastChapterTitle: String = "",
    val lastPageIndex: Long = 0L,
    val completedChapters: List<String> = emptyList(),
    val progressPercent: Long = 0L,
    val bookmarkedPages: List<String> = emptyList(),
    val highlightedParagraphs: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class PersonalNote(
    @DocumentId val id: String = "",
    val userId: String = "",
    val bookId: String = "",
    val bookTitle: String = "",
    val chapterId: String = "",
    val chapterTitle: String = "",
    val content: String = "",
    val excerpt: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class StudyTask(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val subject: String = "Mathematics",
    val dueDate: String = "",
    val targetMinutes: Long = 30L,
    val completed: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class QuizAttempt(
    @DocumentId val id: String = "",
    val userId: String = "",
    val quizId: String = "",
    val quizTitle: String = "",
    val subject: String = "",
    val mode: String = "QUIZ",
    val totalQuestions: Long = 0L,
    val correctCount: Long = 0L,
    val incorrectCount: Long = 0L,
    val unattemptedCount: Long = 0L,
    val scorePercent: Long = 0L,
    val durationSeconds: Long = 0L,
    val weakTopics: List<String> = emptyList(),
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class AiChatMessage(
    @DocumentId val id: String = "",
    val userId: String = "",
    val sessionId: String = "default_session",
    val sender: String = "USER",
    val language: String = "English",
    val subject: String? = null,
    val message: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class SavedMaterial(
    @DocumentId val id: String = "",
    val userId: String = "",
    val materialId: String = "",
    val itemType: String = "NOTE",
    val title: String = "",
    val subject: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class Textbook(
    @DocumentId val id: String = "",
    val title: String = "",
    val subject: String = "",
    val classLevel: String = "",
    val category: String = "Class 9-10",
    val board: String = "CBSE",
    val medium: String = "English",
    val academicYear: String = "2026-27",
    val description: String = "",
    val chapterCount: Long = 0L,
    val premiumOnly: Boolean = false,
    val published: Boolean = true,
    val accentHex: String = "#183B56",
    val createdBy: String = "system",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class Chapter(
    @DocumentId val id: String = "",
    val bookId: String = "",
    val chapterNumber: Long = 1L,
    val title: String = "",
    val summary: String = "",
    val paragraphs: List<String> = emptyList(),
    val keyPoints: List<String> = emptyList(),
    val estimatedMinutes: Long = 15L,
    val published: Boolean = true,
    val createdBy: String = "system",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class StudyMaterial(
    @DocumentId val id: String = "",
    val title: String = "",
    val category: String = "Chapter Notes",
    val subject: String = "",
    val classLevel: String = "Class 10",
    val chapterTitle: String = "",
    val description: String = "",
    val contentBody: String = "",
    val videoUrl: String = "",
    val videoDurationMinutes: Long = 0L,
    val premiumOnly: Boolean = false,
    val published: Boolean = true,
    val createdBy: String = "system",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class LiveClass(
    @DocumentId val id: String = "",
    val title: String = "",
    val subject: String = "Mathematics",
    val classLevel: String = "Class 10",
    val teacherName: String = "",
    val scheduleLabel: String = "",
    val zoomLink: String = "",
    val meetingId: String = "",
    val passcode: String = "",
    val isLiveNow: Boolean = false,
    val published: Boolean = true,
    val createdBy: String = "system",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CoAdminAccount(
    @DocumentId val id: String = "",
    val name: String = "",
    val normalizedName: String = "",
    val passwordSalt: String = "",
    val proofDigest: String = "",
    val active: Boolean = true,
    val createdBy: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CoAdminSession(
    @DocumentId val id: String = "",
    val uid: String = "",
    val coAdminId: String = "",
    val coAdminName: String = "",
    val authProof: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class QuestionItem(
    val prompt: String = "",
    val type: String = "MCQ",
    val options: List<String> = emptyList(),
    val correctIndex: Long = 0L,
    val correctText: String = "",
    val explanation: String = "",
    val topic: String = "",
    val marks: Long = 1L
)

@Keep
@IgnoreExtraProperties
data class Assessment(
    @DocumentId val id: String = "",
    val title: String = "",
    val subject: String = "",
    val classLevel: String = "Class 10",
    val mode: String = "QUIZ",
    val category: String = "Chapter MCQ",
    val durationMinutes: Long = 15L,
    val totalMarks: Long = 10L,
    val instructions: String = "",
    val questions: List<QuestionItem> = emptyList(),
    val premiumOnly: Boolean = false,
    val published: Boolean = true,
    val createdBy: String = "system",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class Announcement(
    @DocumentId val id: String = "",
    val title: String = "",
    val body: String = "",
    val category: String = "GENERAL",
    val targetAudience: String = "ALL",
    val published: Boolean = true,
    val createdBy: String = "system",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class LeaderboardEntry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val assessmentId: String = "",
    val displayName: String = "Aspirant",
    val examGoal: String = "SSC",
    val scoreMarks: Long = 0L,
    val maxMarks: Long = 10L,
    val scorePercent: Long = 0L,
    val correctCount: Long = 0L,
    val incorrectCount: Long = 0L,
    val durationSeconds: Long = 0L,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class SubscriptionPlan(
    val tierId: String,
    val name: String,
    val priceInr: Int,
    val durationLabel: String,
    val badge: String?,
    val features: List<String>
)
