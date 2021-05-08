package com.nativework.covid19vaccinetracker.utils

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and

object AppUtils {

    fun getSha256Hash(password: String): String? {
        return try {
            var digest: MessageDigest? = null
            try {
                digest = MessageDigest.getInstance("SHA-256")
            } catch (e1: NoSuchAlgorithmException) {
                e1.printStackTrace()
            }
            digest?.reset()
            bin2hex(digest?.digest(password.toByteArray()))
        } catch (ignored: Exception) {
            null
        }
    }

    private fun bin2hex(data: ByteArray?): String {
        val hex = data?.size?.times(2)?.let { StringBuilder(it) }
        if (data != null) {
            for (b in data) hex?.append(String.format("%02x", b and 0xFF.toByte()))
        }
        return hex.toString()
    }
}