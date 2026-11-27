package com.example.data.firebase

import android.net.Uri
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object CoAdminSecurity {
    private const val PBKDF2_ITERATIONS = 60_000
    private const val KEY_LENGTH_BITS = 256

    fun normalizeName(rawName: String): String {
        val cleaned = rawName.trim().lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
            .take(48)
        return cleaned
    }

    fun buildCoAdminDocId(rawName: String): String {
        val norm = normalizeName(rawName)
        return if (norm.length >= 2) "coadmin_$norm" else ""
    }

    fun generateSaltHex(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun deriveAuthProof(password: String, saltHex: String): String {
        val spec = PBEKeySpec(
            password.toCharArray(),
            saltHex.toByteArray(Charsets.UTF_8),
            PBKDF2_ITERATIONS,
            KEY_LENGTH_BITS
        )
        return try {
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val encoded = factory.generateSecret(spec).encoded
            encoded.joinToString("") { "%02x".format(it) }
        } finally {
            spec.clearPassword()
        }
    }

    fun computeProofDigest(authProof: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(authProof.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02X".format(it) }
    }

    fun constantTimeEquals(a: String, b: String): Boolean {
        return MessageDigest.isEqual(
            a.toByteArray(Charsets.UTF_8),
            b.toByteArray(Charsets.UTF_8)
        )
    }

    fun isValidHttpsUrl(rawUrl: String): Boolean {
        val trimmed = rawUrl.trim()
        if (trimmed.length < 10 || trimmed.length > 1000) return false
        if (!trimmed.startsWith("https://", ignoreCase = false)) return false
        return try {
            val uri = Uri.parse(trimmed)
            uri.scheme == "https" && !uri.host.isNullOrBlank() && uri.host!!.contains('.')
        } catch (_: Exception) {
            false
        }
    }

    fun isCloudinaryUrl(rawUrl: String): Boolean {
        if (!isValidHttpsUrl(rawUrl)) return false
        val host = Uri.parse(rawUrl.trim()).host?.lowercase().orEmpty()
        return host == "res.cloudinary.com" || host.endsWith(".cloudinary.com") || host == "cloudinary.com"
    }

    fun isZoomUrl(rawUrl: String): Boolean {
        if (!isValidHttpsUrl(rawUrl)) return false
        val host = Uri.parse(rawUrl.trim()).host?.lowercase().orEmpty()
        return host == "zoom.us" || host.endsWith(".zoom.us") || host == "zoom.com" || host.endsWith(".zoom.com")
    }
}
