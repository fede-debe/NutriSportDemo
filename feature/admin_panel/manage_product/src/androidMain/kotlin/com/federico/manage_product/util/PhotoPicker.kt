package com.federico.manage_product.util

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.gitlive.firebase.storage.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class PhotoPicker {
    /** openPhotoPicker has to be a MutableState in order to be able to update it.*/
    private var openPhotoPicker = mutableStateOf(false)

    /** When this function is triggered we can update the openPhotoPicker value.
     * When we trigger this function we also trigger the photo picker within the
     * InitializePhotoPicker
     * */
    actual fun open() {
        openPhotoPicker.value = true
    }

    @Composable
    actual fun InitializePhotoPicker(onImageSelected: (File?) -> Unit) {
        val openPhotoPickerState by remember { openPhotoPicker }
        val pickMedia = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let { onImageSelected(File(it)) } ?: onImageSelected(null)
            openPhotoPicker.value = false
        }

        LaunchedEffect(openPhotoPickerState) {
            if (openPhotoPickerState) {
                pickMedia.launch(
                    PickVisualMediaRequest(
                        mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        }
    }
}