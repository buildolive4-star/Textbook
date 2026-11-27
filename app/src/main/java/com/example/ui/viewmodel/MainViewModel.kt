package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Announcement
import com.example.data.model.Assessment
import com.example.data.model.Chapter
import com.example.data.model.CoAdminAccount
import com.example.data.model.CoAdminSession
import com.example.data.model.CurriculumCatalog
import com.example.data.model.LiveClass
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

    val isMainAdmin: Boolean = repository.isCurrentUserBootstrappedAdmin()
    val isEducatorOrAdmin: Boolean get() = isMainAdmin || coAdminSession.value != null

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

    val liveClasses: StateFlow<List<LiveClass>> =
        repository.observePublishedLiveClasses(currentUserId)
            .catch { error ->
                if (error is CancellationException && error.cause !is FirebaseFirestoreException) throw error
                Log.w(TAG, "Live classes stream error", error)
                emit(emptyList())
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    val coAdminSession: StateFlow<CoAdminSession?> =
        repository.observeCoAdminSession(currentUserId)
            .catch { error ->
                if (error is CancellationException && error.cause !is FirebaseFirestoreException) throw error
                emit(null)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), null)

    val coAdmins: StateFlow<List<CoAdminAccount>> =
        if (isMainAdmin) {
            repository.observeCoAdmins(currentUserId)
                .catch { error ->
                    if (error is CancellationException && error.cause !is FirebaseFirestoreException) throw error
                    Log.w(TAG, "Co-Admins stream error", error)
                    emit(emptyList())
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())
        } else {
            flowOf(emptyList<CoAdminAccount>())
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())
        }

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
        videoUrl: String = "",
        videoDurationMinutes: Int,
        premiumOnly: Boolean,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.publishStudyMaterial(
                title, category, subject, classLevel, chapterTitle,
                description, contentBody, videoUrl, videoDurationMinutes, premiumOnly
            )
            if (res.isSuccess) {
                _statusBanner.value = "$category published to students!"
                onComplete()
            } else {
                _statusBanner.value = res.exceptionOrNull()?.localizedMessage
                    ?: "Requires Admin or Co-Admin permission."
            }
        }
    }

    fun deleteStudyMaterial(materialId: String) {
        viewModelScope.launch {
            val res = repository.deleteStudyMaterial(materialId)
            _statusBanner.value = if (res.isSuccess) "Material removed" else "Could not remove material"
        }
    }

    fun publishLiveClass(
        title: String,
        subject: String,
        classLevel: String,
        teacherName: String,
        scheduleLabel: String,
        zoomLink: String,
        meetingId: String,
        passcode: String,
        isLiveNow: Boolean,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.publishLiveClass(
                title, subject, classLevel, teacherName, scheduleLabel,
                zoomLink, meetingId, passcode, isLiveNow
            )
            if (res.isSuccess) {
                _statusBanner.value = if (isLiveNow) "Zoom Live Class is LIVE now!" else "Zoom Live Class scheduled!"
                onComplete()
            } else {
                _statusBanner.value = res.exceptionOrNull()?.localizedMessage
                    ?: "Could not publish live class."
            }
        }
    }

    fun toggleLiveClassStatus(liveId: String, isLiveNow: Boolean) {
        viewModelScope.launch {
            val res = repository.toggleLiveClassStatus(liveId, isLiveNow)
            if (res.isSuccess) {
                _statusBanner.value = if (isLiveNow) "Class marked LIVE NOW" else "Class marked Upcoming"
            }
        }
    }

    fun deleteLiveClass(liveId: String) {
        viewModelScope.launch {
            val res = repository.deleteLiveClass(liveId)
            _statusBanner.value = if (res.isSuccess) "Live class removed" else "Could not remove live class"
        }
    }

    fun createCoAdmin(name: String, password: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val res = repository.createCoAdmin(name, password)
            if (res.isSuccess) {
                _statusBanner.value = "Co-Admin \"$name\" created! They can now unlock Admin Panel."
                onComplete()
            } else {
                _statusBanner.value = res.exceptionOrNull()?.localizedMessage
                    ?: "Only Main Admin (buildolive4@gmail.com) can create Co-Admins."
            }
        }
    }

    fun deleteCoAdmin(coAdminId: String) {
        viewModelScope.launch {
            val res = repository.deleteCoAdmin(coAdminId)
            _statusBanner.value = if (res.isSuccess) {
                "Co-Admin access revoked immediately."
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Failed to remove Co-Admin."
            }
        }
    }

    fun loginAsCoAdmin(
        name: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.verifyAndActivateCoAdmin(name, password)
            if (res.isSuccess) {
                _statusBanner.value = "Welcome, Co-Admin ${res.getOrNull().orEmpty()}!"
                onSuccess()
            } else {
                onError(res.exceptionOrNull()?.localizedMessage ?: "Invalid Co-Admin name or password.")
            }
        }
    }

    fun lockCoAdminSession(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.lockCoAdminSession()
            _statusBanner.value = "Admin Panel locked. Switched to User Panel."
            onComplete()
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
                    ?: "Requires Admin or Co-Admin permission."
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
                    ?: "Requires Admin or Co-Admin permission."
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
