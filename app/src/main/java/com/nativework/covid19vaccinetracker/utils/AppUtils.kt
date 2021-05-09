package com.nativework.covid19vaccinetracker.utils

import android.content.Context
import com.google.gson.Gson
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.models.SavedPreferences
import com.nativework.covid19vaccinetracker.models.locality.CitiesList
import com.nativework.covid19vaccinetracker.models.locality.StatesList
import java.io.IOException
import java.io.InputStream
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

    private fun inputStreamToString(inputStream: InputStream): String? {
        return try {
            val bytes = ByteArray(inputStream.available())
            inputStream.read(bytes, 0, bytes.size)
            String(bytes)
        } catch (e: IOException) {
            null
        }
    }

    fun getStatesModelFromJson(id:Int, context: Context): StatesList {
        val json = inputStreamToString(context.resources.openRawResource(id))
        return Gson().fromJson(json, StatesList::class.java)
    }

    fun getCitiesModelFromJson(id:Int, context: Context): CitiesList {
        val json = inputStreamToString(context.resources.openRawResource(id))
        return Gson().fromJson(json, CitiesList::class.java)
    }

    fun saveSelectedSearch(
        context: Context,
        state: String,
        district: String,
        dateSelected: String?,
        districtId: String?,
        stateId: String?
    ) {
        val listOfPref = ArrayList<SavedPreferences>()
        listOfPref.add(SavedPreferences("SearchByDistrict", districtId,district, stateId,state, dateSelected))
        val json = Gson().toJson(listOfPref)
        PreferenceConnector.writeString(context, PreferenceConnector.SAVED_PREF, json)
    }

}