package com.nativework.covid19vaccinetracker.networks

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import java.io.IOException
import java.net.HttpURLConnection.HTTP_FORBIDDEN
import java.net.HttpURLConnection.HTTP_UNAUTHORIZED

/**
 * This class handles all kind of the HTTP error code while making API calls with Retrofit client
 *
 * @author Gauravkumar Mishra
 */

class NetworkError(val error: Throwable?)
    : Throwable(error) {

    val isAuthFailure: Boolean
        get() = error is HttpException && error.code() == HTTP_UNAUTHORIZED or HTTP_FORBIDDEN

    val appErrorMessage: String
        get() {
            if (this.error is IOException) return NETWORK_ERROR_MESSAGE
            if (this.error !is HttpException) return DEFAULT_ERROR_MESSAGE
            val response = this.error.response()
            if (response != null) {
                val status = getJsonStringFromResponse(response)
                if (!TextUtils.isEmpty(status)) return status!!

                val headers = response.headers().toMultimap()
                if (headers.containsKey(ERROR_MESSAGE_HEADER))
                    return headers[ERROR_MESSAGE_HEADER]?.get(0)!!
            }

            return DEFAULT_ERROR_MESSAGE
        }


    private fun getJsonStringFromResponse(response: retrofit2.Response<*>): String? {
        return try {
            val jsonString = response.errorBody()!!.string()
            val errorResponse = Gson().fromJson(jsonString, Response::class.java)
            errorResponse.error
        } catch (e: Exception) {
            null
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val that = other as NetworkError?

        return if (error != null) error == that!!.error else that!!.error == null

    }

    override fun hashCode(): Int {
        return error?.hashCode() ?: 0
    }


    companion object {
        const val DEFAULT_ERROR_MESSAGE = "Something went wrong! Please try again."
        const val NETWORK_ERROR_MESSAGE = "No Internet Connection!"
        private const val ERROR_MESSAGE_HEADER = "Error-Message"
    }

    data class Response(@SerializedName("err")var error: String?=null,
                        @SerializedName("resp")var response:EmptyResponse?=null)

    class EmptyResponse
}
