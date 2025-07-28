package com.federico.manage_product.util

import androidx.compose.runtime.Composable
import dev.gitlive.firebase.storage.File

actual class PhotoPicker {
    @Composable
    actual fun InitializePhotoPicker(onImageSelected: (File?) -> Unit) {
    }

    actual fun open() {
    }

}