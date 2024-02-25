package com.hyper.taskvault.constants

import android.app.Activity
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.hyper.taskvault.R

object Constants {

    const val USERS : String = "users"
    const val IMAGE: String = "image"
    const val NAME: String = "name"
    const val MOBILE: String = "mobile"
    const val BOARDS : String = "boards"
    const val ASSIGNED_TO: String = "assignedTo"

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}