package com.uilover.project304.util

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Uploads images to Cloudinary using an unsigned upload preset, so no API secret
 * ever ships inside the APK (a secret embedded in a signed upload could be extracted
 * by decompiling the app and used to upload/delete arbitrary content on the account).
 *
 * Configure your cloud name and an unsigned upload preset (Cloudinary Console ->
 * Settings -> Upload -> Upload presets -> Add upload preset -> Signing Mode: Unsigned)
 * in AndroidManifest.xml before uploads will work.
 */
@Singleton
class CloudinaryManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var isInitialized = false
    private var uploadPreset: String = ""

    init {
        initCloudinary()
    }

    private fun initCloudinary() {
        if (isInitialized) return

        try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            val cloudName = appInfo.metaData?.getString(META_CLOUD_NAME).orEmpty()
            uploadPreset = appInfo.metaData?.getString(META_UPLOAD_PRESET).orEmpty()

            if (cloudName.isBlank() || cloudName == PLACEHOLDER_CLOUD_NAME) {
                Log.w(TAG, "Cloudinary cloud name is not configured. Set $META_CLOUD_NAME in AndroidManifest.xml")
                return
            }

            MediaManager.init(context, mapOf("cloud_name" to cloudName))
            isInitialized = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Cloudinary", e)
        }
    }

    fun uploadImage(
        uri: Uri,
        folder: String = "luxe_realty",
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!isInitialized) {
            onError("Cloudinary is not configured. Set your cloud name in AndroidManifest.xml.")
            return
        }
        if (uploadPreset.isBlank() || uploadPreset == PLACEHOLDER_UPLOAD_PRESET) {
            onError("Cloudinary upload preset is not configured. Set an unsigned upload preset in AndroidManifest.xml.")
            return
        }

        val publicId = "$folder/${System.currentTimeMillis()}"

        MediaManager.get().upload(uri)
            .unsigned(uploadPreset)
            .option("public_id", publicId)
            .option("folder", folder)
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {}

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    val url = resultData?.get("secure_url") as? String
                    if (url != null) {
                        onSuccess(url)
                    } else {
                        onError("Failed to get URL from response")
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    onError(error?.description ?: "Unknown error")
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    onError(error?.description ?: "Upload rescheduled")
                }
            })
            .dispatch()
    }

    fun uploadAvatar(
        uri: Uri,
        userId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        uploadImage(
            uri = uri,
            folder = "luxe_realty/avatars",
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun uploadPropertyImage(
        uri: Uri,
        propertyId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        uploadImage(
            uri = uri,
            folder = "luxe_realty/properties",
            onSuccess = onSuccess,
            onError = onError
        )
    }

    companion object {
        private const val TAG = "CloudinaryManager"
        const val META_CLOUD_NAME = "com.uilover.project304.CLOUDINARY_CLOUD_NAME"
        const val META_UPLOAD_PRESET = "com.uilover.project304.CLOUDINARY_UPLOAD_PRESET"
        const val PLACEHOLDER_CLOUD_NAME = "YOUR_CLOUD_NAME"
        const val PLACEHOLDER_UPLOAD_PRESET = "YOUR_UNSIGNED_UPLOAD_PRESET"
    }
}
