package com.nativework.covid19vaccinetracker.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.nativework.covid19vaccinetracker.AgeGroups
import com.nativework.covid19vaccinetracker.R
import com.nativework.covid19vaccinetracker.models.SavedPreferences
import com.nativework.covid19vaccinetracker.models.locality.CitiesList
import com.nativework.covid19vaccinetracker.models.locality.StatesList
import com.nativework.covid19vaccinetracker.ui.center.VaccineCenterActivity
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
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

    fun getStatesModelFromJson(id: Int, context: Context): StatesList {
        val json = inputStreamToString(context.resources.openRawResource(id))
        return Gson().fromJson(json, StatesList::class.java)
    }

    fun saveSelectedSearch(
        context: Context,
        type:String?,
        state: String?,
        district: String?,
        dateSelected: String?,
        districtId: String?,
        stateId: String?,
        pinCode:String?
    ) {
        val listOfPref = ArrayList<SavedPreferences>()
        listOfPref.add(
            SavedPreferences(
                type,
                districtId,
                district,
                stateId,
                state,
                dateSelected,
                pinCode
            )
        )
        val json = Gson().toJson(listOfPref)
        PreferenceConnector.writeString(context, PreferenceConnector.SAVED_PREF, json)
    }

    fun getCurrentDate(): String {
        val currentDate = Calendar.getInstance().time
        val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        Timber.d("current date %s", simpleDateFormat.format(currentDate))
        return simpleDateFormat.format(currentDate)
    }

    fun saveNotificationPref(
        context: Context?,
        lowerGroup: Boolean,
        upperGroup: Boolean
    ) {
        context?.let {
            Timber.d("Saving the data lowerGroup %s upperGroup %s", lowerGroup, upperGroup)
            if (lowerGroup) {
                PreferenceConnector.writeBoolean(it, PreferenceConnector.IS_LOWER_GROUP, true)
                PreferenceConnector.writeBoolean(it, PreferenceConnector.IS_UPPER_GROUP, false)
            }
            if (upperGroup) {
                PreferenceConnector.writeBoolean(it, PreferenceConnector.IS_UPPER_GROUP, true)
                PreferenceConnector.writeBoolean(it, PreferenceConnector.IS_LOWER_GROUP, false)
            }
            if (upperGroup && lowerGroup) {
                PreferenceConnector.writeBoolean(it, PreferenceConnector.IS_ALL_GROUP, true)
                PreferenceConnector.writeBoolean(it, PreferenceConnector.IS_UPPER_GROUP, false)
                PreferenceConnector.writeBoolean(it, PreferenceConnector.IS_LOWER_GROUP, false)
            }
        }

    }

    fun readNotificationPref(
        context: Context?
    ): String {
        context?.let {
            val isLower =
                PreferenceConnector.readBoolean(it, PreferenceConnector.IS_LOWER_GROUP, false)
            val isUpper =
                PreferenceConnector.readBoolean(it, PreferenceConnector.IS_UPPER_GROUP, false)
            val isAll = PreferenceConnector.readBoolean(it, PreferenceConnector.IS_ALL_GROUP, true)

            Timber.d("Getting the data lowerGroup %s upperGroup %s", isLower, isUpper)
            if (isLower) return AgeGroups.AGE_18_44.name
            if (isUpper) return AgeGroups.AGE_45_ALL.name
            if (isAll) return AgeGroups.ALL_GROUP.name

        }

        return AgeGroups.ALL_GROUP.name
    }

    // check if the particular app is installed or not
    fun isAppInstalled(context: Context, uri:String): Boolean {
        val packageManager = context.packageManager
        try {
            packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        }catch (ex:PackageManager.NameNotFoundException){
            Timber.e(ex, "App is not installed on the device")
        }
        return false
    }

}