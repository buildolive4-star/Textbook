package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.firebase.CoAdminSecurity
import com.example.data.firebase.OperationType
import com.example.data.firebase.handleFirestoreError
import com.example.data.model.Announcement
import com.example.data.model.Assessment
import com.example.data.model.AiChatMessage
import com.example.data.model.Chapter
import com.example.data.model.CoAdminAccount
import com.example.data.model.CoAdminSession
import com.example.data.model.CurriculumCatalog
import com.example.data.model.LeaderboardEntry
import com.example.data.model.LiveClass
import com.example.data.model.PersonalNote
import com.example.data.model.QuestionItem
import com.example.data.model.QuizAttempt
import com.example.data.model.ReadingProgress
import com.example.data.model.SavedMaterial
import com.example.data.model.StudyMaterial
import com.example.data.model.StudyTask
import com.example.data.model.Textbook
import com.example.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID

class TextbookRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun isCurrentUserBootstrappedAdmin(): Boolean {
        val user = auth.currentUser ?: return false
        return user.email.equals("buildolive4@gmail.com", ignoreCase = true) && user.isEmailVerified
    }

    private fun <T> Flow<T>.logFirestoreErrors(uid: String, op: OperationType, path: String): Flow<T> =
        this.catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val exceptionToLog = firestoreError ?: (error as? Exception)
            if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                handleFirestoreError(exceptionToLog, op, path)
            }
            throw error
        }

    // --- 1. User Profile & Onboarding ---

    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snap ->
                    if (snap.exists()) {
                        snap.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    } else null
                }
                .logFirestoreErrors(userId, OperationType.GET, path)
        )
    }

    suspend fun ensureUserProfileExists(displayName: String, email: String?): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Not signed in"))
        val path = "users/$uid"
        return try {
            val docRef = db.collection("users").document(uid)
            val existing = docRef.get().await()
            if (!existing.exists()) {
                val safeName = displayName.trim().ifBlank { "Student" }.take(80)
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "email" to email?.take(120),
                    "board" to "SSC",
                    "classLevel" to "SSC CGL / CHSL",
                    "medium" to "Bilingual",
                    "academicSession" to "2026-27",
                    "subjects" to listOf("Quantitative Aptitude", "Logical Reasoning", "General Studies & GK", "English Comprehension", "Current Affairs"),
                    "onboardingDone" to false,
                    "planTier" to "FREE",
                    "studyMinutesTotal" to 0L,
                    "streakDays" to 1L,
                    "aiQueriesToday" to 0L,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                ).filterValues { it != null }
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateAcademicPreferences(
        displayName: String,
        board: String,
        classLevel: String,
        medium: String,
        academicSession: String,
        subjects: List<String>,
        onboardingDone: Boolean = true
    ): Result<Unit> {
        var path = "users/unknown"
        return try {
            val uid = requireUserId()
            path = "users/$uid"
            val docRef = db.collection("users").document(uid)
            val safeName = displayName.trim().ifBlank { "Student" }.take(80)
            val safeSubjects = subjects.take(12)
            docRef.update(
                mapOf(
                    "displayName" to safeName,
                    "board" to board,
                    "classLevel" to classLevel,
                    "medium" to medium,
                    "academicSession" to academicSession.take(20),
                    "subjects" to safeSubjects,
                    "onboardingDone" to onboardingDone,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun updatePlanTier(planTier: String): Result<Unit> {
        var path = "users/unknown"
        return try {
            val uid = requireUserId()
            path = "users/$uid"
            db.collection("users").document(uid).update(
                mapOf(
                    "planTier" to planTier,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun incrementStudyStats(addMinutes: Long = 5L, incrementAiQueries: Boolean = false): Result<Unit> {
        var path = "users/unknown"
        return try {
            val uid = requireUserId()
            path = "users/$uid"
            val updates = mutableMapOf<String, Any>(
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (addMinutes > 0L) {
                updates["studyMinutesTotal"] = FieldValue.increment(addMinutes)
            }
            if (incrementAiQueries) {
                updates["aiQueriesToday"] = FieldValue.increment(1L)
            }
            db.collection("users").document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    // --- 2. Reading Progress, Bookmarks, Highlights & Personal Notes ---

    fun observeReadingProgress(userId: String): Flow<List<ReadingProgress>> = flow {
        val path = "users/$userId/readingProgress"
        emitAll(
            db.collection("users").document(userId).collection("readingProgress")
                .snapshots()
                .map { snap ->
                    snap.toObjects(ReadingProgress::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.updatedAt?.seconds ?: 0L }
                }
                .logFirestoreErrors(userId, OperationType.LIST, path)
        )
    }

    suspend fun saveReadingProgress(
        book: Textbook,
        chapter: Chapter,
        pageIndex: Int,
        completedChapters: List<String>,
        bookmarkedPages: List<String>,
        highlightedParagraphs: List<String>,
        totalChaptersInBook: Int
    ): Result<Unit> {
        var path = "users/unknown/readingProgress/${book.id}"
        return try {
            val uid = requireUserId()
            val safeBookId = book.id.ifBlank { "book_default" }
            path = "users/$uid/readingProgress/$safeBookId"
            val docRef = db.collection("users").document(uid)
                .collection("readingProgress").document(safeBookId)

            val percent = if (totalChaptersInBook > 0) {
                ((completedChapters.size.toFloat() / totalChaptersInBook.toFloat()) * 100f)
                    .toInt().coerceIn(5, 100)
            } else 10

            val existing = docRef.get().await()
            if (existing.exists()) {
                docRef.update(
                    mapOf(
                        "bookTitle" to book.title.take(140),
                        "subject" to book.subject.take(60),
                        "lastChapterId" to chapter.id.take(128),
                        "lastChapterNumber" to chapter.chapterNumber.coerceIn(1L, 100L),
                        "lastChapterTitle" to chapter.title.take(140),
                        "lastPageIndex" to pageIndex.toLong().coerceIn(0L, 500L),
                        "completedChapters" to completedChapters.distinct().take(60),
                        "progressPercent" to percent.toLong(),
                        "bookmarkedPages" to bookmarkedPages.distinct().take(100),
                        "highlightedParagraphs" to highlightedParagraphs.distinct().take(100),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "bookId" to safeBookId,
                        "bookTitle" to book.title.take(140),
                        "subject" to book.subject.take(60),
                        "lastChapterId" to chapter.id.take(128),
                        "lastChapterNumber" to chapter.chapterNumber.coerceIn(1L, 100L),
                        "lastChapterTitle" to chapter.title.take(140),
                        "lastPageIndex" to pageIndex.toLong().coerceIn(0L, 500L),
                        "completedChapters" to completedChapters.distinct().take(60),
                        "progressPercent" to percent.toLong(),
                        "bookmarkedPages" to bookmarkedPages.distinct().take(100),
                        "highlightedParagraphs" to highlightedParagraphs.distinct().take(100),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }

            // Update user's last opened book pointer
            db.collection("users").document(uid).update(
                mapOf(
                    "lastBookId" to safeBookId,
                    "lastChapterId" to chapter.id.take(128),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    fun observePersonalNotes(userId: String): Flow<List<PersonalNote>> = flow {
        val path = "users/$userId/personalNotes"
        emitAll(
            db.collection("users").document(userId).collection("personalNotes")
                .snapshots()
                .map { snap ->
                    snap.toObjects(PersonalNote::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .logFirestoreErrors(userId, OperationType.LIST, path)
        )
    }

    suspend fun addPersonalNote(
        bookId: String,
        bookTitle: String,
        chapterId: String,
        chapterTitle: String,
        content: String,
        excerpt: String? = null
    ): Result<Unit> {
        var path = "users/unknown/personalNotes"
        return try {
            val uid = requireUserId()
            val cleanContent = content.trim().take(2000)
            require(cleanContent.isNotEmpty()) { "Note content cannot be empty." }
            val docRef = db.collection("users").document(uid).collection("personalNotes").document()
            path = docRef.path
            val payload = mapOf(
                "userId" to uid,
                "bookId" to bookId.take(128),
                "bookTitle" to bookTitle.take(140),
                "chapterId" to chapterId.take(128),
                "chapterTitle" to chapterTitle.take(140),
                "content" to cleanContent,
                "excerpt" to excerpt?.trim()?.take(500)?.takeIf { it.isNotEmpty() },
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun deletePersonalNote(noteId: String): Result<Unit> {
        var path = "users/unknown/personalNotes/$noteId"
        return try {
            val uid = requireUserId()
            path = "users/$uid/personalNotes/$noteId"
            db.collection("users").document(uid)
                .collection("personalNotes").document(noteId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // --- 3. Study Planner Tasks ---

    fun observeStudyTasks(userId: String): Flow<List<StudyTask>> = flow {
        val path = "users/$userId/studyTasks"
        emitAll(
            db.collection("users").document(userId).collection("studyTasks")
                .snapshots()
                .map { snap ->
                    snap.toObjects(StudyTask::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedBy { it.completed }
                }
                .logFirestoreErrors(userId, OperationType.LIST, path)
        )
    }

    suspend fun addStudyTask(title: String, subject: String, dueDate: String, targetMinutes: Int): Result<Unit> {
        var path = "users/unknown/studyTasks"
        return try {
            val uid = requireUserId()
            val cleanTitle = title.trim().take(140)
            require(cleanTitle.isNotEmpty()) { "Task title is required." }
            val docRef = db.collection("users").document(uid).collection("studyTasks").document()
            path = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "title" to cleanTitle,
                    "subject" to subject.trim().ifBlank { "Mathematics" }.take(60),
                    "dueDate" to dueDate.trim().ifBlank { "Today" }.take(24),
                    "targetMinutes" to targetMinutes.coerceIn(5, 360).toLong(),
                    "completed" to false,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun toggleStudyTask(taskId: String, completed: Boolean): Result<Unit> {
        var path = "users/unknown/studyTasks/$taskId"
        return try {
            val uid = requireUserId()
            path = "users/$uid/studyTasks/$taskId"
            db.collection("users").document(uid).collection("studyTasks").document(taskId)
                .update(
                    mapOf(
                        "completed" to completed,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteStudyTask(taskId: String): Result<Unit> {
        var path = "users/unknown/studyTasks/$taskId"
        return try {
            val uid = requireUserId()
            path = "users/$uid/studyTasks/$taskId"
            db.collection("users").document(uid).collection("studyTasks").document(taskId)
                .delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // --- 4. Quiz & Exam Attempts ---

    fun observeQuizAttempts(userId: String): Flow<List<QuizAttempt>> = flow {
        val path = "users/$userId/quizAttempts"
        emitAll(
            db.collection("users").document(userId).collection("quizAttempts")
                .snapshots()
                .map { snap ->
                    snap.toObjects(QuizAttempt::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .logFirestoreErrors(userId, OperationType.LIST, path)
        )
    }

    suspend fun recordQuizAttempt(
        assessment: Assessment,
        correctCount: Int,
        incorrectCount: Int,
        unattemptedCount: Int,
        scorePercent: Int,
        durationSeconds: Int,
        weakTopics: List<String>
    ): Result<Unit> {
        var path = "users/unknown/quizAttempts"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("quizAttempts").document()
            path = docRef.path
            docRef.set(
                mapOf(
                    "userId" to uid,
                    "quizId" to assessment.id.take(128),
                    "quizTitle" to assessment.title.take(140),
                    "subject" to assessment.subject.take(60),
                    "mode" to assessment.mode,
                    "totalQuestions" to assessment.questions.size.coerceAtLeast(1).toLong(),
                    "correctCount" to correctCount.coerceAtLeast(0).toLong(),
                    "incorrectCount" to incorrectCount.coerceAtLeast(0).toLong(),
                    "unattemptedCount" to unattemptedCount.coerceAtLeast(0).toLong(),
                    "scorePercent" to scorePercent.coerceIn(0, 100).toLong(),
                    "durationSeconds" to durationSeconds.coerceIn(0, 14400).toLong(),
                    "weakTopics" to weakTopics.distinct().take(12),
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
            incrementStudyStats(addMinutes = (durationSeconds / 60L).coerceAtLeast(2L))
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    fun observeTestLeaderboard(userId: String, assessmentId: String): Flow<List<LeaderboardEntry>> = flow {
        val safeId = assessmentId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(128)
        val path = "assessments/$safeId/leaderboard"
        emitAll(
            db.collection("assessments").document(safeId).collection("leaderboard")
                .snapshots()
                .map { snap ->
                    snap.toObjects(LeaderboardEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedWith(
                            compareByDescending<LeaderboardEntry> { it.scorePercent }
                                .thenByDescending { it.scoreMarks }
                                .thenBy { it.durationSeconds }
                                .thenBy { it.updatedAt?.seconds ?: Long.MAX_VALUE }
                        )
                }
                .logFirestoreErrors(userId, OperationType.LIST, path)
        )
    }

    suspend fun submitLeaderboardScore(
        assessmentId: String,
        displayName: String,
        examGoal: String,
        scoreMarks: Int,
        maxMarks: Int,
        scorePercent: Int,
        correctCount: Int,
        incorrectCount: Int,
        durationSeconds: Int
    ): Result<Unit> {
        var path = "assessments/$assessmentId/leaderboard"
        return try {
            val uid = requireUserId()
            val safeAssessmentId = assessmentId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(128)
            path = "assessments/$safeAssessmentId/leaderboard/$uid"
            val safeMax = maxMarks.coerceIn(1, 1000)
            val safeScore = scoreMarks.coerceIn(0, safeMax)
            val payload = mapOf(
                "userId" to uid,
                "assessmentId" to safeAssessmentId,
                "displayName" to displayName.trim().ifBlank { "Aspirant" }.take(80),
                "examGoal" to examGoal.trim().ifBlank { "SSC" }.take(40),
                "scoreMarks" to safeScore.toLong(),
                "maxMarks" to safeMax.toLong(),
                "scorePercent" to scorePercent.coerceIn(0, 100).toLong(),
                "correctCount" to correctCount.coerceIn(0, 200).toLong(),
                "incorrectCount" to incorrectCount.coerceIn(0, 200).toLong(),
                "durationSeconds" to durationSeconds.coerceIn(0, 14400).toLong(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("assessments").document(safeAssessmentId)
                .collection("leaderboard").document(uid)
                .set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // --- 5. AI Chat History ---

    fun observeAiMessages(userId: String): Flow<List<AiChatMessage>> = flow {
        val path = "users/$userId/aiMessages"
        emitAll(
            db.collection("users").document(userId).collection("aiMessages")
                .snapshots()
                .map { snap ->
                    snap.toObjects(AiChatMessage::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedBy { it.createdAt?.seconds ?: Long.MAX_VALUE }
                }
                .logFirestoreErrors(userId, OperationType.LIST, path)
        )
    }

    suspend fun saveAiMessage(
        sessionId: String,
        sender: String,
        language: String,
        subject: String?,
        message: String
    ): Result<Unit> {
        var path = "users/unknown/aiMessages"
        return try {
            val uid = requireUserId()
            val cleanMsg = message.trim().take(8000)
            require(cleanMsg.isNotEmpty()) { "Message cannot be empty" }
            val docRef = db.collection("users").document(uid).collection("aiMessages").document()
            path = docRef.path
            val payload = mapOf(
                "userId" to uid,
                "sessionId" to sessionId.take(64),
                "sender" to sender,
                "language" to language,
                "subject" to subject?.take(60),
                "message" to cleanMsg,
                "createdAt" to FieldValue.serverTimestamp()
            ).filterValues { it != null }
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun clearAiHistory(): Result<Unit> {
        var path = "users/unknown/aiMessages"
        return try {
            val uid = requireUserId()
            path = "users/$uid/aiMessages"
            val snaps = db.collection("users").document(uid).collection("aiMessages").get().await()
            val batch = db.batch()
            snaps.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // --- 6. Saved Study Materials / Bookmarks ---

    fun observeSavedMaterials(userId: String): Flow<List<SavedMaterial>> = flow {
        val path = "users/$userId/savedMaterials"
        emitAll(
            db.collection("users").document(userId).collection("savedMaterials")
                .snapshots()
                .map { snap ->
                    snap.toObjects(SavedMaterial::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .logFirestoreErrors(userId, OperationType.LIST, path)
        )
    }

    suspend fun toggleSaveMaterial(
        materialId: String,
        itemType: String,
        title: String,
        subject: String,
        currentlySaved: Boolean
    ): Result<Unit> {
        var path = "users/unknown/savedMaterials/$materialId"
        return try {
            val uid = requireUserId()
            val safeId = materialId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(128)
            path = "users/$uid/savedMaterials/$safeId"
            val docRef = db.collection("users").document(uid)
                .collection("savedMaterials").document(safeId)
            if (currentlySaved) {
                docRef.delete().await()
            } else {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "materialId" to safeId,
                        "itemType" to itemType,
                        "title" to title.take(140),
                        "subject" to subject.take(60),
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // --- 7. Shared Published Catalog (Filtered strictly by `published == true` per firestore.rules) ---

    fun observePublishedTextbooks(userId: String): Flow<List<Textbook>> = flow {
        val path = "textbooks"
        emitAll(
            db.collection("textbooks")
                .whereEqualTo("published", true)
                .snapshots()
                .map { snap ->
                    val cloudBooks = snap.toObjects(Textbook::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    mergeById(CurriculumCatalog.starterTextbooks, cloudBooks)
                }
                .logFirestoreErrors(userId, OperationType.LIST, path)
        )
    }

    fun observePublishedChapters(userId: String, bookId: String): Flow<List<Chapter>> = flow {
        val path = "textbooks/$bookId/chapters"
        val starter = CurriculumCatalog.starterChaptersByBook[bookId].orEmpty()
        emitAll(
            db.collection("textbooks").document(bookId).collection("chapters")
                .whereEqualTo("published", true)
                .snapshots()
                .map { snap ->
                    val cloudChapters = snap.toObjects(Chapter::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    mergeById(starter, cloudChapters).sortedBy { it.chapterNumber }
                }
                .logFirestoreErrors(userId, OperationType.LIST, path)
        )
    }

    fun observePublishedStudyMaterials(userId: String): Flow<List<StudyMaterial>> = flow {
        val path = "studyMaterials"
        emitAll(
            db.collection("studyMaterials")
                .whereEqualTo("published", true)
                .snapshots()
                .map { snap ->
                    val cloud = snap.toObjects(StudyMaterial::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    mergeById(CurriculumCatalog.starterStudyMaterials, cloud)
                }
                .logFirestoreErrors(userId, OperationType.LIST, path)
        )
    }

    fun observePublishedAssessments(userId: String): Flow<List<Assessment>> = flow {
        val path = "assessments"
        emitAll(
            db.collection("assessments")
                .whereEqualTo("published", true)
                .snapshots()
                .map { snap ->
                    val cloud = snap.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(Assessment::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        } catch (_: Exception) {
                            null
                        }
                    }
                    mergeById(CurriculumCatalog.starterAssessments, cloud)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .logFirestoreErrors(userId, OperationType.LIST, path)
        )
    }

    fun observePublishedAnnouncements(userId: String): Flow<List<Announcement>> = flow {
        val path = "announcements"
        emitAll(
            db.collection("announcements")
                .whereEqualTo("published", true)
                .snapshots()
                .map { snap ->
                    val cloud = snap.toObjects(Announcement::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    mergeById(CurriculumCatalog.starterAnnouncements, cloud)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .logFirestoreErrors(userId, OperationType.LIST, path)
        )
    }

    fun observePublishedLiveClasses(userId: String): Flow<List<LiveClass>> = flow {
        val path = "liveClasses"
        emitAll(
            db.collection("liveClasses")
                .whereEqualTo("published", true)
                .snapshots()
                .map { snap ->
                    snap.toObjects(LiveClass::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedWith(
                            compareByDescending<LiveClass> { it.isLiveNow }
                                .thenByDescending { it.updatedAt?.seconds ?: 0L }
                        )
                }
                .logFirestoreErrors(userId, OperationType.LIST, path)
        )
    }

    fun observeCoAdmins(userId: String): Flow<List<CoAdminAccount>> = flow {
        val path = "coAdmins"
        emitAll(
            db.collection("coAdmins")
                .snapshots()
                .map { snap ->
                    snap.toObjects(CoAdminAccount::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .logFirestoreErrors(userId, OperationType.LIST, path)
        )
    }

    fun observeCoAdminSession(userId: String): Flow<CoAdminSession?> = flow {
        val path = "coAdminSessions/$userId"
        emitAll(
            db.collection("coAdminSessions").document(userId)
                .snapshots()
                .map { snap ->
                    if (snap.exists()) {
                        snap.toObject(CoAdminSession::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    } else null
                }
                .logFirestoreErrors(userId, OperationType.GET, path)
        )
    }

    // --- 8. Teacher & Admin Panel Cloud Publishing Operations ---

    suspend fun publishTextbookWithChapter(
        title: String,
        subject: String,
        classLevel: String,
        category: String,
        board: String,
        medium: String,
        description: String,
        premiumOnly: Boolean,
        firstChapterTitle: String,
        firstChapterBody: String
    ): Result<Unit> {
        var path = "textbooks"
        return try {
            val uid = requireUserId()
            val bookId = "book_${UUID.randomUUID().toString().replace("-", "").take(10)}"
            val chapterId = "ch_1_${UUID.randomUUID().toString().replace("-", "").take(6)}"
            path = "textbooks/$bookId"

            val bookPayload = mapOf(
                "title" to title.trim().take(140),
                "subject" to subject.trim().take(60),
                "classLevel" to classLevel.trim().take(30),
                "category" to category,
                "board" to board,
                "medium" to medium,
                "academicYear" to "2026-27",
                "description" to description.trim().ifBlank { "Published digital textbook." }.take(800),
                "chapterCount" to 1L,
                "premiumOnly" to premiumOnly,
                "published" to true,
                "accentHex" to "#183B56",
                "createdBy" to uid,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("textbooks").document(bookId).set(bookPayload).await()

            val paragraphs = firstChapterBody
                .split("\n\n", "\n")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .ifEmpty { listOf(description.trim().ifBlank { "Chapter introduction." }) }
                .take(30)

            val chapterPayload = mapOf(
                "bookId" to bookId,
                "chapterNumber" to 1L,
                "title" to firstChapterTitle.trim().ifBlank { "Chapter 1: Introduction" }.take(140),
                "summary" to paragraphs.first().take(300),
                "paragraphs" to paragraphs,
                "keyPoints" to listOf("Core chapter concepts published by educator."),
                "estimatedMinutes" to 15L,
                "published" to true,
                "createdBy" to uid,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("textbooks").document(bookId)
                .collection("chapters").document(chapterId)
                .set(chapterPayload).await()

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun publishStudyMaterial(
        title: String,
        category: String,
        subject: String,
        classLevel: String,
        chapterTitle: String,
        description: String,
        contentBody: String,
        videoUrl: String = "",
        videoDurationMinutes: Int,
        premiumOnly: Boolean
    ): Result<Unit> {
        var path = "studyMaterials"
        return try {
            val uid = requireUserId()
            val cleanVideoUrl = videoUrl.trim()
            if (cleanVideoUrl.isNotEmpty() && !CoAdminSecurity.isValidHttpsUrl(cleanVideoUrl)) {
                return Result.failure(IllegalArgumentException("Please enter a valid https:// Cloudinary video link."))
            }
            val docId = "mat_${UUID.randomUUID().toString().replace("-", "").take(10)}"
            path = "studyMaterials/$docId"
            val payload = mutableMapOf<String, Any>(
                "title" to title.trim().take(140),
                "category" to category.take(50),
                "subject" to subject.take(60),
                "classLevel" to classLevel.take(30),
                "chapterTitle" to chapterTitle.trim().ifBlank { "General" }.take(140),
                "description" to description.trim().ifBlank { title.trim() }.take(600),
                "contentBody" to contentBody.trim().ifBlank { title.trim() }.take(6000),
                "videoDurationMinutes" to videoDurationMinutes.coerceIn(0, 240).toLong(),
                "premiumOnly" to premiumOnly,
                "published" to true,
                "createdBy" to uid,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (cleanVideoUrl.isNotEmpty()) {
                payload["videoUrl"] = cleanVideoUrl
            }
            db.collection("studyMaterials").document(docId).set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteStudyMaterial(materialId: String): Result<Unit> {
        val path = "studyMaterials/$materialId"
        return try {
            requireUserId()
            db.collection("studyMaterials").document(materialId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun publishLiveClass(
        title: String,
        subject: String,
        classLevel: String,
        teacherName: String,
        scheduleLabel: String,
        zoomLink: String,
        meetingId: String = "",
        passcode: String = "",
        isLiveNow: Boolean = true
    ): Result<Unit> {
        var path = "liveClasses"
        return try {
            val uid = requireUserId()
            val cleanZoom = zoomLink.trim()
            if (!CoAdminSecurity.isValidHttpsUrl(cleanZoom)) {
                return Result.failure(IllegalArgumentException("Please enter a valid https:// Zoom meeting link."))
            }
            val docId = "live_${UUID.randomUUID().toString().replace("-", "").take(10)}"
            path = "liveClasses/$docId"
            val payload = mutableMapOf<String, Any>(
                "title" to title.trim().take(140),
                "subject" to subject.trim().take(60),
                "classLevel" to classLevel.trim().take(30),
                "teacherName" to teacherName.trim().ifBlank { "Educator" }.take(80),
                "scheduleLabel" to scheduleLabel.trim().ifBlank { "Live Now" }.take(80),
                "zoomLink" to cleanZoom,
                "meetingId" to meetingId.trim().take(40),
                "passcode" to passcode.trim().take(40),
                "isLiveNow" to isLiveNow,
                "published" to true,
                "createdBy" to uid,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            db.collection("liveClasses").document(docId).set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun toggleLiveClassStatus(liveId: String, isLiveNow: Boolean): Result<Unit> {
        val path = "liveClasses/$liveId"
        return try {
            requireUserId()
            db.collection("liveClasses").document(liveId).update(
                mapOf(
                    "isLiveNow" to isLiveNow,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteLiveClass(liveId: String): Result<Unit> {
        val path = "liveClasses/$liveId"
        return try {
            requireUserId()
            db.collection("liveClasses").document(liveId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    // --- 9. Co-Admin Accounts & Cryptographic Session Login ---

    suspend fun createCoAdmin(name: String, password: String): Result<Unit> {
        var path = "coAdmins"
        return try {
            val uid = requireUserId()
            val cleanName = name.trim().take(60)
            val normName = CoAdminSecurity.normalizeName(cleanName)
            val docId = CoAdminSecurity.buildCoAdminDocId(cleanName)
            if (cleanName.length < 2 || docId.isEmpty()) {
                return Result.failure(IllegalArgumentException("Enter a valid Co-Admin name (min 2 letters)."))
            }
            if (password.length < 4) {
                return Result.failure(IllegalArgumentException("Password must be at least 4 characters."))
            }
            path = "coAdmins/$docId"
            val (salt, proofDigest) = withContext(Dispatchers.Default) {
                val s = CoAdminSecurity.generateSaltHex()
                val proof = CoAdminSecurity.deriveAuthProof(password, s)
                s to CoAdminSecurity.computeProofDigest(proof)
            }
            db.collection("coAdmins").document(docId).set(
                mapOf(
                    "name" to cleanName,
                    "normalizedName" to normName,
                    "passwordSalt" to salt,
                    "proofDigest" to proofDigest,
                    "active" to true,
                    "createdBy" to uid,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteCoAdmin(coAdminId: String): Result<Unit> {
        val path = "coAdmins/$coAdminId"
        return try {
            requireUserId()
            db.collection("coAdmins").document(coAdminId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun verifyAndActivateCoAdmin(name: String, password: String): Result<String> {
        var path = "coAdmins"
        return try {
            val uid = requireUserId()
            val docId = CoAdminSecurity.buildCoAdminDocId(name)
            if (docId.isEmpty() || password.isBlank()) {
                return Result.failure(IllegalArgumentException("Enter both Co-Admin Name and Password."))
            }
            path = "coAdmins/$docId"
            val snap = db.collection("coAdmins").document(docId).get().await()
            if (!snap.exists()) {
                return Result.failure(IllegalArgumentException("Invalid Co-Admin name or password."))
            }
            val account = snap.toObject(CoAdminAccount::class.java)
                ?: return Result.failure(IllegalArgumentException("Invalid Co-Admin record."))
            if (!account.active) {
                return Result.failure(IllegalArgumentException("This Co-Admin account has been disabled."))
            }

            val (derivedProof, matches) = withContext(Dispatchers.Default) {
                val proof = CoAdminSecurity.deriveAuthProof(password, account.passwordSalt)
                val digest = CoAdminSecurity.computeProofDigest(proof)
                proof to CoAdminSecurity.constantTimeEquals(digest, account.proofDigest)
            }
            if (!matches) {
                return Result.failure(IllegalArgumentException("Invalid Co-Admin name or password."))
            }

            // Write cryptographically verified Co-Admin session (re-verified server-side in firestore.rules!)
            path = "coAdminSessions/$uid"
            db.collection("coAdminSessions").document(uid).set(
                mapOf(
                    "uid" to uid,
                    "coAdminId" to docId,
                    "coAdminName" to account.name,
                    "authProof" to derivedProof,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(account.name)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun lockCoAdminSession(): Result<Unit> {
        var path = "coAdminSessions"
        return try {
            val uid = requireUserId()
            path = "coAdminSessions/$uid"
            db.collection("coAdminSessions").document(uid).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun publishAnnouncement(
        title: String,
        body: String,
        category: String,
        targetAudience: String
    ): Result<Unit> {
        var path = "announcements"
        return try {
            val uid = requireUserId()
            val docId = "ann_${UUID.randomUUID().toString().replace("-", "").take(10)}"
            path = "announcements/$docId"
            db.collection("announcements").document(docId).set(
                mapOf(
                    "title" to title.trim().take(120),
                    "body" to body.trim().take(600),
                    "category" to category,
                    "targetAudience" to targetAudience.take(40),
                    "published" to true,
                    "createdBy" to uid,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun publishQuickAssessment(
        title: String,
        subject: String,
        classLevel: String,
        mode: String,
        durationMinutes: Int,
        questionPrompt: String,
        options: List<String>,
        correctIndex: Int,
        explanation: String
    ): Result<Unit> {
        var path = "assessments"
        return try {
            val uid = requireUserId()
            val docId = "assess_${UUID.randomUUID().toString().replace("-", "").take(10)}"
            path = "assessments/$docId"
            val qMap = mapOf(
                "prompt" to questionPrompt.trim().take(400),
                "type" to "MCQ",
                "options" to options.map { it.trim() }.filter { it.isNotEmpty() }.take(4),
                "correctIndex" to correctIndex.coerceIn(0, 3).toLong(),
                "correctText" to "",
                "explanation" to explanation.trim().ifBlank { "Verified by educator." }.take(400),
                "topic" to subject.take(50),
                "marks" to 4L
            )
            db.collection("assessments").document(docId).set(
                mapOf(
                    "title" to title.trim().take(140),
                    "subject" to subject.take(60),
                    "classLevel" to classLevel.take(30),
                    "mode" to mode,
                    "category" to if (mode == "EXAM") "Mock Exam" else "Chapter MCQ",
                    "durationMinutes" to durationMinutes.coerceIn(1, 180).toLong(),
                    "totalMarks" to 4L,
                    "instructions" to "Read carefully and submit before time expires.",
                    "questions" to listOf(qMap),
                    "premiumOnly" to false,
                    "published" to true,
                    "createdBy" to uid,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun publishFullAssessment(
        title: String,
        subject: String,
        classLevel: String,
        mode: String,
        category: String,
        durationMinutes: Int,
        instructions: String,
        questions: List<QuestionItem>
    ): Result<Unit> {
        var path = "assessments"
        return try {
            val uid = requireUserId()
            require(questions.isNotEmpty()) { "Add at least 1 question before publishing." }
            val docId = "assess_${UUID.randomUUID().toString().replace("-", "").take(10)}"
            path = "assessments/$docId"

            val sanitizedQuestions = questions.take(50).map { q ->
                val cleanOpts = q.options.map { it.trim() }.filter { it.isNotEmpty() }.take(4)
                val safeCorrect = q.correctIndex.toInt().coerceIn(0, (cleanOpts.size - 1).coerceAtLeast(0))
                val safeMarks = q.marks.coerceIn(1L, 10L)
                mapOf(
                    "prompt" to q.prompt.trim().take(600),
                    "type" to "MCQ",
                    "options" to cleanOpts,
                    "correctIndex" to safeCorrect.toLong(),
                    "correctText" to "",
                    "explanation" to q.explanation.trim().ifBlank { "Verified by Admin faculty." }.take(1200),
                    "topic" to q.topic.trim().ifBlank { subject.take(50) }.take(60),
                    "marks" to safeMarks
                )
            }
            val totalMarks = sanitizedQuestions.sumOf { (it["marks"] as Long) }.coerceIn(1L, 500L)

            db.collection("assessments").document(docId).set(
                mapOf(
                    "title" to title.trim().take(140),
                    "subject" to subject.trim().take(60),
                    "classLevel" to classLevel.trim().take(30),
                    "mode" to if (mode == "EXAM") "EXAM" else "QUIZ",
                    "category" to category.trim().ifBlank { if (mode == "EXAM") "All-India Mock" else "Daily Speed Quiz" }.take(50),
                    "durationMinutes" to durationMinutes.coerceIn(1, 180).toLong(),
                    "totalMarks" to totalMarks,
                    "instructions" to instructions.trim().ifBlank {
                        "Attempt all questions before timer expires. Automatic Live Rank & detailed Admin Answer Explanations will unlock immediately after submission."
                    }.take(800),
                    "questions" to sanitizedQuestions,
                    "premiumOnly" to false,
                    "published" to true,
                    "createdBy" to uid,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun deleteAssessment(assessmentId: String): Result<Unit> {
        val path = "assessments/$assessmentId"
        return try {
            requireUserId()
            db.collection("assessments").document(assessmentId).delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path)
            Result.failure(e)
        }
    }

    suspend fun syncStarterCurriculumToCloud(): Result<Int> {
        var path = "textbooks"
        return try {
            val uid = requireUserId()
            var count = 0
            for (book in CurriculumCatalog.starterTextbooks) {
                path = "textbooks/${book.id}"
                db.collection("textbooks").document(book.id).set(
                    mapOf(
                        "title" to book.title,
                        "subject" to book.subject,
                        "classLevel" to book.classLevel,
                        "category" to book.category,
                        "board" to book.board,
                        "medium" to book.medium,
                        "academicYear" to book.academicYear,
                        "description" to book.description,
                        "chapterCount" to book.chapterCount,
                        "premiumOnly" to book.premiumOnly,
                        "published" to true,
                        "accentHex" to book.accentHex,
                        "createdBy" to uid,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
                count++
                val chapters = CurriculumCatalog.starterChaptersByBook[book.id].orEmpty()
                for (ch in chapters) {
                    path = "textbooks/${book.id}/chapters/${ch.id}"
                    db.collection("textbooks").document(book.id)
                        .collection("chapters").document(ch.id).set(
                            mapOf(
                                "bookId" to book.id,
                                "chapterNumber" to ch.chapterNumber,
                                "title" to ch.title,
                                "summary" to ch.summary,
                                "paragraphs" to ch.paragraphs,
                                "keyPoints" to ch.keyPoints,
                                "estimatedMinutes" to ch.estimatedMinutes,
                                "published" to true,
                                "createdBy" to uid,
                                "createdAt" to FieldValue.serverTimestamp(),
                                "updatedAt" to FieldValue.serverTimestamp()
                            )
                        ).await()
                }
            }
            Result.success(count)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    private fun <T> mergeById(starter: List<T>, cloud: List<T>): List<T> {
        val map = LinkedHashMap<String, T>()
        starter.forEach { item ->
            val key = extractId(item)
            if (key.isNotEmpty()) map[key] = item
        }
        cloud.forEach { item ->
            val key = extractId(item)
            if (key.isNotEmpty()) map[key] = item
        }
        return map.values.toList()
    }

    private fun extractId(item: Any?): String = when (item) {
        is Textbook -> item.id
        is Chapter -> item.id
        is StudyMaterial -> item.id
        is Assessment -> item.id
        is Announcement -> item.id
        else -> ""
    }
}
