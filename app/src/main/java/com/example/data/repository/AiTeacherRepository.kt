package com.example.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "AiTeacherRepository"

class AiTeacherRepository {

    // Lazily initialized so Firebase.ai is only evaluated after App Check is wired in MainActivity.onCreate()
    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    You are "Textbook AI Teacher", an encouraging, accurate, school-level academic tutor for Indian board students (CBSE, ICSE, State Boards, Classes 1–12).
                    Guidelines:
                    - Keep explanations age-appropriate, clear, and well-structured with short headings or numbered steps.
                    - For Mathematics, show every step clearly and verify the final answer.
                    - For Science and Social Science, include a relatable everyday example when helpful.
                    - When a textbook paragraph is provided, ground your explanation strictly in that paragraph and clearly note if something goes beyond the given excerpt.
                    - If an answer is uncertain or depends on a specific board edition, say so honestly instead of guessing.
                    - Respond strictly in the requested language (English, Hindi, Tamil, Telugu, Bengali, Marathi, Gujarati, Kannada, Malayalam, Punjabi, Odia, Urdu, Assamese, Sanskrit, or Maithili) using its authentic native script and clear, school-friendly wording.
                    - When explaining formulas or technical terms in an Indian regional language, keep standard mathematical symbols and optionally include the common English exam term in parentheses so board students can score well.
                    """.trimIndent()
                )
            }
        )
    }

    fun streamTutorResponse(
        studentClass: String,
        board: String,
        language: String,
        subject: String?,
        userPrompt: String,
        paragraphContext: String? = null
    ): Flow<String> = flow {
        val fullPrompt = buildString {
            appendLine("Student Profile: $studentClass ($board Board)")
            if (!subject.isNullOrBlank()) {
                appendLine("Subject: $subject")
            }
            appendLine("Preferred Response Language: $language")
            if (!paragraphContext.isNullOrBlank()) {
                appendLine()
                appendLine("Textbook Paragraph to Explain:")
                appendLine("\"\"\"")
                appendLine(paragraphContext.trim())
                appendLine("\"\"\"")
            }
            appendLine()
            appendLine("Student Question:")
            appendLine(userPrompt.trim())
        }

        val accumulated = StringBuilder()
        try {
            withTimeout(60_000L) {
                model.generateContentStream(fullPrompt).collect { chunk ->
                    val piece = chunk.text.orEmpty()
                    if (piece.isNotEmpty()) {
                        accumulated.append(piece)
                        emit(accumulated.toString())
                    }
                }
            }
            if (accumulated.isBlank()) {
                throw IllegalStateException("AI Teacher returned an empty response. Please try again.")
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI Teacher streaming timed out", e)
            throw IllegalStateException("AI Teacher took too long to respond (60s timeout). Please retry.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI Teacher generation failed", e)
            throw e
        }
    }
}
