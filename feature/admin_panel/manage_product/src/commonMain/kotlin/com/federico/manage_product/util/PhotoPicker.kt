package com.federico.manage_product.util

import androidx.compose.runtime.Composable
import dev.gitlive.firebase.storage.File

/** We need to create a separate implementation for each platform.
 *  We also need to use it within a composable function.
 *  File type is provided by firebase storage and can be used by both platforms.
 *  File is nullable in case the user upload a corrupted image or similar.
 *
 *  open() is used to trigger the photo picker.
 * */
expect class PhotoPicker {
    @Composable
    fun InitializePhotoPicker(onImageSelected: (File?) -> Unit)
    fun open()
}