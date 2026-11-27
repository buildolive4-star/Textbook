package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Announcement
import com.example.data.model.Assessment
import com.example.data.model.Chapter
import com.example.data.model.CurriculumCatalog
import com.example.data.model.PersonalNote
import com.example.data.model.QuizAttempt
import com.example.data.model.ReadingProgress
import com.example.data.model.SavedMaterial
import com.example.data.model.StudyMaterial
import com.example.data.model.StudyTask
import com.example.data.model.Textbook
import com.example.data.model.UiState
import com.example.data.model.UserProfile
import com.example.data.repository.TextbookRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "MainViewModel"
private const val STOP_TIMEOUT_MS = 5000L

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: TextbookRepository,
    val currentUserId: String,
    initialDisplayName: String,
    initialEmail: String?
) : ViewModel() {

    val isEducatorOrAdmin: Boolean = repository.isCurrentUserBootstrappedAdmin()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    private val _selectedBookIdForChapters = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            repository.ensureUserProfileExists(initialDisplayName, initialEmail)
        }
    }

    private fun <T> Flow<T>.toSafeUiState(label: String): StateFlow<UiState<T>> =
        this.map<T, UiState<T>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing $label", rootError)
                emit(UiState.Error(rootError.localizedMessage ?: "Failed to load $label"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                initialValue = UiState.Loading
            )

    val userProfileState: StateFlow<UiState<UserProfile?>> =
        repository.observeUserProfile(currentUserId).toSafeUiState("profile")

    val textbooksState: StateFlow<UiState<List<Textbook>>> =
        repository.observePublishedTextbooks(currentUserId).toSafeUiState("textbooks")

    val activeBookChapters: StateFlow<List<Chapter>> = _selectedBookIdForChapters
        .flatMapLatest { bookId ->
            if (bookId.isNullOrBlank()) {
                flowOf(emptyList())
            } else {
                repository.observePublishedChapters(currentUserId, bookId)
                    .catch { error ->
                        val firestoreError = generateSequence(error) { it.cause }
                            .filterIsInstance<FirebaseFirestoreException>()
                            .firstOrNull()
                        if (firestoreError == null && error is CancellationException) throw error
                        Log.w(TAG, "Chapter stream fallback for $bookId", error)
                        emit(CurriculumCatalog.starterChaptersByBook[bookId].orEmpty())
                    }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    val readingProgressList: StateFlow<List<ReadingProgress>> =
        repository.observeReadingProgress(currentUserId)
            .catch { error ->
                if (error is CancellationException && error.cause !is FirebaseFirestoreException) throw error
                Log.w(TAG, "Reading progress error", error)
                emit(emptyList())
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    val personalNotes: StateFlow<List<PersonalNote>> =
        repository.observePersonalNotes(currentUserId)
            .catch { error ->
                if (error is CancellationException && error.cause !is FirebaseFirestoreException) throw error
                Log.w(TAG, "Personal notes error", error)
                emit(emptyList())
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    val studyTasks: StateFlow<List<StudyTask>> =
        repository.observeStudyTasks(currentUserId)
            .catch { error ->
                if (error is CancellationException && error.cause !is FirebaseFirestoreException) throw error
                Log.w(TAG, "Study tasks error", error)
                emit(emptyList())
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    val quizAttempts: StateFlow<List<QuizAttempt>> =
        repository.observeQuizAttempts(currentUserId)
            .catch { error ->
                if (error is CancellationException && error.cause !is FirebaseFirestoreException) throw error
                Log.w(TAG, "Quiz attempts error", error)
                emit(emptyList())
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    val studyMaterials: StateFlow<List<StudyMaterial>> =
        repository.observePublishedStudyMaterials(currentUserId)
            .catch { error ->
                if (error is CancellationException && error.cause !is FirebaseFirestoreException) throw error
                emit(CurriculumCatalog.starterStudyMaterials)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                CurriculumCatalog.starterStudyMaterials
            )

    val assessments: StateFlow<List<Assessment>> =
        repository.observePublishedAssessments(currentUserId)
            .catch { error ->
                if (error is CancellationException && error.cause !is FirebaseFirestoreException) throw error
                emit(CurriculumCatalog.starterAssessments)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                CurriculumCatalog.starterAssessments
            )

    val announcements: StateFlow<List<Announcement>> =
        repository.observePublishedAnnouncements(currentUserId)
            .catch { error ->
                if (error is CancellationException && error.cause !is FirebaseFirestoreException) throw error
                emit(CurriculumCatalog.starterAnnouncements)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                CurriculumCatalog.starterAnnouncements
            )

    val savedMaterials: StateFlow<List<SavedMaterial>> =
        repository.observeSavedMaterials(currentUserId)
            .catch { error ->
                if (error is CancellationException && error.cause !is FirebaseFirestoreException) throw error
                emit(emptyList())
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    fun selectBookForChapters(bookId: String) {
        _selectedBookIdForChapters.value = bookId
    }

    fun clearBanner() {
        _statusBanner.value = null
    }

    fun saveAcademicPreferences(
        displayName: String,
        board: String,
        classLevel: String,
        medium: String,
        academicSession: String,
        subjects: List<String>,
        onSaved: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val res = repository.updateAcademicPreferences(
                displayName = displayName,
                board = board,
                classLevel = classLevel,
                medium = medium,
                academicSession = academicSession,
                subjects = subjects,
                onboardingDone = true
            )
            if (res.isSuccess) {
                _statusBanner.value = "Academic preferences saved"
                onSaved()
            } else {
                _statusBanner.value = res.exceptionOrNull()?.localizedMessage ?: "Could not save preferences"
            }
        }
    }

    fun selectSubscriptionPlan(tierId: String) {
        viewModelScope.launch {
            val res = repository.updatePlanTier(tierId)
            _statusBanner.value = if (res.isSuccess) {
                "Plan updated to $tierId (Prototype Mode)"
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Failed to update plan"
            }
        }
    }

    fun saveReadingState(
        book: Textbook,
        chapter: Chapter,
        pageIndex: Int,
        completedChapters: List<String>,
        bookmarkedPages: List<String>,
        highlightedParagraphs: List<String>,
        totalChaptersInBook: Int
    ) {
        viewModelScope.launch {
            repository.saveReadingProgress(
                book = book,
                chapter = chapter,
                pageIndex = pageIndex,
                completedChapters = completedChapters,
                bookmarkedPages = bookmarkedPages,
                highlightedParagraphs = highlightedParagraphs,
                totalChaptersInBook = totalChaptersInBook
            )
        }
    }

    fun recordReadingSessionMinutes(minutes: Long = 5L) {
        viewModelScope.launch {
            repository.incrementStudyStats(addMinutes = minutes)
        }
    }

    fun addNote(
        bookId: String,
        bookTitle: String,
        chapterId: String,
        chapterTitle: String,
        content: String,
        excerpt: String? = null
    ) {
        viewModelScope.launch {
            val res = repository.addPersonalNote(bookId, bookTitle, chapterId, chapterTitle, content, excerpt)
            _statusBanner.value = if (res.isSuccess) "Personal note saved" else "Could not save note"
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            repository.deletePersonalNote(noteId)
        }
    }

    fun addStudyTask(title: String, subject: String, dueDate: String, targetMinutes: Int) {
        viewModelScope.launch {
            val res = repository.addStudyTask(title, subject, dueDate, targetMinutes)
            _statusBanner.value = if (res.isSuccess) "Study goal added to planner" else "Could not add task"
        }
    }

    fun toggleStudyTask(taskId: String, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleStudyTask(taskId, completed)
            if (completed) {
                repository.incrementStudyStats(addMinutes = 15L)
            }
        }
    }

    fun deleteStudyTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteStudyTask(taskId)
        }
    }

    fun submitQuizAttempt(
        assessment: Assessment,
        correctCount: Int,
        incorrectCount: Int,
        unattemptedCount: Int,
        scorePercent: Int,
        durationSeconds: Int,
        weakTopics: List<String>
    ) {
        viewModelScope.launch {
            val res = repository.recordQuizAttempt(
                assessment, correctCount, incorrectCount, unattemptedCount,
                scorePercent, durationSeconds, weakTopics
            )
            if (res.isFailure) {
                _statusBanner.value = "Result saved locally; cloud sync pending"
            }
        }
    }

    fun toggleSavedMaterial(materialId: String, itemType: String, title: String, subject: String) {
        val currentlySaved = savedMaterials.value.any { it.materialId == materialId }
        viewModelScope.launch {
            val res = repository.toggleSaveMaterial(materialId, itemType, title, subject, currentlySaved)
            if (res.isSuccess) {
                _statusBanner.value = if (currentlySaved) "Removed from bookmarks" else "Saved to your profile"
            }
        }
    }

    // --- Educator / Admin Cloud Publishing ---

    fun publishTextbook(
        title: String,
        subject: String,
        classLevel: String,
        category: String,
        board: String,
        medium: String,
        description: String,
        premiumOnly: Boolean,
        firstChapterTitle: String,
        firstChapterBody: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.publishTextbookWithChapter(
                title, subject, classLevel, category, board, medium,
                description, premiumOnly, firstChapterTitle, firstChapterBody
            )
            if (res.isSuccess) {
                _statusBanner.value = "Textbook published live to Firestore!"
                onComplete()
            } else {
                _statusBanner.value = res.exceptionOrNull()?.localizedMessage
                    ?: "Requires Teacher or Admin permission to publish."
            }
        }
    }

    fun publishStudyMaterial(
        title: String,
        category: String,
        subject: String,
        classLevel: String,
        chapterTitle: String,
        description: String,
        contentBody: String,
        videoDurationMinutes: Int,
        premiumOnly: Boolean,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.publishStudyMaterial(
                title, category, subject, classLevel, chapterTitle,
                description, contentBody, videoDurationMinutes, premiumOnly
            )
            if (res.isSuccess) {
                _statusBanner.value = "$category published to students!"
                onComplete()
            } else {
                _statusBanner.value = res.exceptionOrNull()?.localizedMessage
                    ?: "Requires Teacher or Admin permission."
            }
        }
    }

    fun publishAnnouncement(
        title: String,
        body: String,
        category: String,
        targetAudience: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.publishAnnouncement(title, body, category, targetAudience)
            if (res.isSuccess) {
                _statusBanner.value = "Announcement sent to $targetAudience!"
                onComplete()
            } else {
                _statusBanner.value = res.exceptionOrNull()?.localizedMessage
                    ?: "Requires Teacher or Admin permission."
            }
        }
    }

    fun publishQuickAssessment(
        title: String,
        subject: String,
        classLevel: String,
        mode: String,
        durationMinutes: Int,
        questionPrompt: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.publishQuickAssessment(
                title, subject, classLevel, mode, durationMinutes,
                questionPrompt, options, correctIndex, explanation
            )
            if (res.isSuccess) {
                _statusBanner.value = "${if (mode == "EXAM") "Exam" else "Quiz"} published!"
                onComplete()
            } else {
                _statusBanner.value = res.exceptionOrNull()?.localizedMessage
                    ?: "Requires Teacher or Admin permission."
            }
        }
    }

    fun syncStarterCurriculumToCloud() {
        viewModelScope.launch {
            val res = repository.syncStarterCurriculumToCloud()
            _statusBanner.value = if (res.isSuccess) {
                "Synced ${res.getOrNull() ?: 0} textbooks & chapters to Firestore!"
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Admin privileges required to sync catalog."
            }
        }
    }
}
