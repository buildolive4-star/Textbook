package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AiChatMessage
import com.example.data.repository.AiTeacherRepository
import com.example.data.repository.TextbookRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val TAG = "AiTeacherViewModel"

sealed interface AiStreamState {
    data object Idle : AiStreamState
    data object Thinking : AiStreamState
    data class Streaming(val partialText: String) : AiStreamState
    data class Error(val message: String) : AiStreamState
}

class AiTeacherViewModel(
    private val textbookRepository: TextbookRepository,
    private val aiRepository: AiTeacherRepository,
    private val currentUserId: String
) : ViewModel() {

    val chatHistory: StateFlow<List<AiChatMessage>> = textbookRepository
        .observeAiMessages(currentUserId)
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            Log.w(TAG, "Error observing AI chat history", error)
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _streamState = MutableStateFlow<AiStreamState>(AiStreamState.Idle)
    val streamState: StateFlow<AiStreamState> = _streamState.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _pendingParagraphContext = MutableStateFlow<String?>(null)
    val pendingParagraphContext: StateFlow<String?> = _pendingParagraphContext.asStateFlow()

    private val _pendingSubjectContext = MutableStateFlow<String?>(null)
    val pendingSubjectContext: StateFlow<String?> = _pendingSubjectContext.asStateFlow()

    fun setLanguage(lang: String) {
        val valid = com.example.ui.i18n.AppLanguages.byEnglishName(lang).englishName
        _selectedLanguage.value = valid
    }

    fun attachParagraphContext(subject: String, paragraph: String) {
        _pendingSubjectContext.value = subject
        _pendingParagraphContext.value = paragraph
    }

    fun clearParagraphContext() {
        _pendingParagraphContext.value = null
        _pendingSubjectContext.value = null
    }

    fun askTeacher(
        question: String,
        studentClass: String,
        board: String,
        isPremium: Boolean,
        queriesUsedToday: Long
    ) {
        val cleanQuestion = question.trim()
        if (cleanQuestion.isEmpty()) return

        if (!isPremium && queriesUsedToday >= 10L) {
            _streamState.value = AiStreamState.Error(
                "Daily Free limit reached (10/10 questions). Upgrade in Profile → Subscription Plans for unlimited AI Teacher access."
            )
            return
        }

        val language = _selectedLanguage.value
        val paragraphContext = _pendingParagraphContext.value
        val subjectContext = _pendingSubjectContext.value

        val userDisplayMsg = if (!paragraphContext.isNullOrBlank()) {
            "Explain this paragraph:\n\"${paragraphContext.take(260)}...\"\n\nQuestion: $cleanQuestion"
        } else {
            cleanQuestion
        }

        _pendingParagraphContext.value = null
        _pendingSubjectContext.value = null

        viewModelScope.launch {
            _streamState.value = AiStreamState.Thinking

            // Persist student's message in Firestore chat history
            textbookRepository.saveAiMessage(
                sessionId = "default_session",
                sender = "USER",
                language = language,
                subject = subjectContext,
                message = userDisplayMsg
            )

            var finalResponse = ""
            try {
                aiRepository.streamTutorResponse(
                    studentClass = studentClass,
                    board = board,
                    language = language,
                    subject = subjectContext,
                    userPrompt = cleanQuestion,
                    paragraphContext = paragraphContext
                ).collect { partial ->
                    finalResponse = partial
                    _streamState.value = AiStreamState.Streaming(partial)
                }

                if (finalResponse.isNotBlank()) {
                    textbookRepository.saveAiMessage(
                        sessionId = "default_session",
                        sender = "AI",
                        language = language,
                        subject = subjectContext,
                        message = finalResponse
                    )
                    textbookRepository.incrementStudyStats(addMinutes = 2L, incrementAiQueries = true)
                    _streamState.value = AiStreamState.Idle
                } else {
                    _streamState.value = AiStreamState.Error("AI Teacher returned an empty answer.")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Teacher request failed", e)
                _streamState.value = AiStreamState.Error(
                    e.localizedMessage ?: "AI Teacher could not answer right now. Please retry."
                )
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            textbookRepository.clearAiHistory()
            _streamState.value = AiStreamState.Idle
        }
    }
}
