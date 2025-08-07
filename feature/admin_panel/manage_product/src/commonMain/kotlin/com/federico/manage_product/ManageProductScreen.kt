package com.federico.manage_product

import ContentWithMessageBar
import MessageBarState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.federico.manage_product.util.PhotoPicker
import com.nutrisportdemo.shared.BorderIdle
import com.nutrisportdemo.shared.ButtonPrimary
import com.nutrisportdemo.shared.FontSize
import com.nutrisportdemo.shared.IconPrimary
import com.nutrisportdemo.shared.Resources
import com.nutrisportdemo.shared.Surface
import com.nutrisportdemo.shared.SurfaceDarker
import com.nutrisportdemo.shared.SurfaceLighter
import com.nutrisportdemo.shared.SurfaceSecondary
import com.nutrisportdemo.shared.TextPrimary
import com.nutrisportdemo.shared.TextSecondary
import com.nutrisportdemo.shared.bebasNeueFont
import com.nutrisportdemo.shared.component.PrimaryButton
import com.nutrisportdemo.shared.component.card.ErrorCard
import com.nutrisportdemo.shared.component.card.LoadingCard
import com.nutrisportdemo.shared.component.dialog.CategoriesDialog
import com.nutrisportdemo.shared.component.textField.AlertTextField
import com.nutrisportdemo.shared.component.textField.CustomTextField
import com.nutrisportdemo.shared.domain.ProductCategory
import com.nutrisportdemo.shared.util.DisplayResult
import com.nutrisportdemo.shared.util.RequestState
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import rememberMessageBarState


@Composable
fun ManageProductScreen(id: String?, navigateBack: () -> Unit) {
    val messageBarState = rememberMessageBarState()
    val viewModel = koinViewModel<ManageProductViewModel>()
    val screenState = viewModel.screenState
    val isFormValid = viewModel.isFormValid
    val thumbnailUploaderState = viewModel.thumbnailUploaderState
    var showCategoriesDialog by remember { mutableStateOf(false) }


    val photoPicker = koinInject<PhotoPicker>()

    photoPicker.InitializePhotoPicker(
        onImageSelected = { file ->
            viewModel.uploadThumbnailToStorage(
                file = file,
                onSuccess = { messageBarState.addSuccess("Thumbnail uploaded successfully!") }
            )
        }
    )

    AnimatedVisibility(visible = showCategoriesDialog) {
        CategoriesDialog(
            category = screenState.category,
            onDismiss = { showCategoriesDialog = false },
            onConfirmClick = { selectedCategory ->
                viewModel.updateCategory(selectedCategory)
                showCategoriesDialog = false
            }
        )
    }

    ManageProductScreen(
        id = id,
        screenState = screenState,
        messageBarState = messageBarState,
        thumbnailUploaderState = thumbnailUploaderState,
        isFormValid = isFormValid,
        onDeleteProduct = {
            viewModel.deleteProduct(
                onSuccess = navigateBack,
                onError = { message -> messageBarState.addError(message) })
        },
        onDeleteThumbnailFromStorage = {
            viewModel.deleteThumbnailFromStorage(
                onSuccess = {
                    messageBarState.addSuccess("Thumbnail removed successfully.")
                },
                onError = { message ->
                    messageBarState.addError(message)
                }
            )
        },
        onUpdateThumbnailUploaderState = {
            viewModel.updateThumbnailUploaderState(RequestState.Idle)
        },
        onOpenPhotoPicker = { photoPicker.open() },
        onUpdateTitle = viewModel::updateTitle,
        onUpdateDescription = viewModel::updateDescription,
        onUpdateWeight = viewModel::updateWeight,
        onUpdateFlavors = viewModel::updateFlavors,
        onUpdatePrice = viewModel::updatePrice,
        onUpdateIsNew = viewModel::updateIsNew,
        onUpdateIsPopular = viewModel::updateIsPopular,
        onUpdateIsDiscounted = viewModel::updateIsDiscounted,
        onToggleCategoryDialog = { showCategoriesDialog = true },
        onSubmitProduct = {
            viewModel.submitProduct(
                isUpdatingProduct = id != null,
                onSuccess = { messageBarState.addSuccess("Product Successfully added!") },
                onError = { message -> messageBarState.addError(message) }
            )
        },
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageProductScreen(
    id: String?,
    screenState: ManageProductState,
    messageBarState: MessageBarState,
    thumbnailUploaderState: RequestState<Unit>,
    isFormValid: Boolean,
    onOpenPhotoPicker: () -> Unit,
    onDeleteProduct: () -> Unit,
    onDeleteThumbnailFromStorage: () -> Unit,
    onUpdateThumbnailUploaderState: () -> Unit,
    onUpdateTitle: (String) -> Unit,
    onUpdateDescription: (String) -> Unit,
    onUpdateWeight: (String) -> Unit,
    onUpdateFlavors: (String) -> Unit,
    onUpdatePrice: (Double) -> Unit,
    onUpdateIsNew: (Boolean) -> Unit,
    onUpdateIsPopular: (Boolean) -> Unit,
    onUpdateIsDiscounted: (Boolean) -> Unit,
    onToggleCategoryDialog: () -> Unit,
    onSubmitProduct: () -> Unit,
    navigateBack: () -> Unit
) {
    var dropDownExpanded by remember { mutableStateOf(false) }

    val topBarTitle = when {
        id == null -> "New Product"
        else -> "Edit Product"
    }
    val buttonTitle = if (id == null) "Add new product" else "Update"
    val buttonIcon = if (id == null) Resources.Icon.Plus else Resources.Icon.Check

    Scaffold(containerColor = Surface, topBar = {
        TopAppBar(
            title = {
                Text(
                    topBarTitle,
                    fontFamily = bebasNeueFont(),
                    fontSize = FontSize.LARGE,
                    color = TextPrimary
                )
            },
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(
                        painter = painterResource(Resources.Icon.BackArrow),
                        contentDescription = "Back Arrow icon",
                        tint = IconPrimary
                    )
                }
            },
            actions = {
                id.takeIf { it != null }?.let {
                    Box {
                        IconButton(onClick = { dropDownExpanded = true }) {
                            Icon(
                                painter = painterResource(Resources.Icon.VerticalMenu),
                                contentDescription = "Vertical menu icon",
                                tint = IconPrimary
                            )
                        }
                        DropdownMenu(
                            containerColor = Surface,
                            expanded = dropDownExpanded,
                            onDismissRequest = { dropDownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                leadingIcon = {
                                    Icon(
                                        modifier = Modifier.size(14.dp),
                                        painter = painterResource(Resources.Icon.Delete),
                                        contentDescription = "Delete icon",
                                        tint = IconPrimary
                                    )
                                },
                                text = { Text(text = "Delete", color = TextPrimary) },
                                onClick = {
                                    dropDownExpanded = false
                                    onDeleteProduct()
                                },
                            )
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Surface,
                scrolledContainerColor = Surface,
                navigationIconContentColor = IconPrimary,
                titleContentColor = TextPrimary,
                actionIconContentColor = IconPrimary
            )
        )
    }
    ) { paddingValues ->
        ContentWithMessageBar(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            ),
            messageBarState = messageBarState,
            contentBackgroundColor = Surface,
            errorMaxLines = 2
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp, top = 12.dp).imePadding()
            ) {
                Column(
                    modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(size = 12.dp))
                            .border(
                                width = 1.dp,
                                color = BorderIdle,
                                shape = RoundedCornerShape(size = 12.dp)
                            )
                            .background(SurfaceLighter)
                            .clickable(enabled = thumbnailUploaderState.isIdle()) {
                                onOpenPhotoPicker()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        thumbnailUploaderState.DisplayResult(
                            onIdle = {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(Resources.Icon.Plus),
                                    contentDescription = "Plus icon",
                                    tint = IconPrimary
                                )
                            },
                            onLoading = {
                                LoadingCard(modifier = Modifier.fillMaxSize())
                            },
                            onSuccess = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.TopEnd
                                ) {
                                    AsyncImage(
                                        modifier = Modifier.fillMaxSize(),
                                        model = ImageRequest.Builder(
                                            LocalPlatformContext.current
                                        ).data(screenState.thumbnail)
                                            .crossfade(enable = true)
                                            .build(),
                                        contentDescription = "Product thumbnail image",
                                        contentScale = ContentScale.Crop,
                                        error = if (LocalInspectionMode.current) painterResource(
                                            Resources.Image.Placeholder
                                        ) else null,
                                    )

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(size = 6.dp))
                                            .padding(
                                                top = 12.dp,
                                                end = 12.dp
                                            )
                                            .background(ButtonPrimary)
                                            .clickable { onDeleteThumbnailFromStorage() }
                                            .padding(all = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            modifier = Modifier.size(14.dp),
                                            painter = painterResource(Resources.Icon.Delete),
                                            contentDescription = "Delete icon"
                                        )
                                    }
                                }
                            },
                            onError = { message ->
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    ErrorCard(
                                        message = message
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    TextButton(
                                        onClick = onUpdateThumbnailUploaderState,
                                        colors = ButtonDefaults.textButtonColors(
                                            containerColor = Color.Transparent
                                        )
                                    ) {
                                        Text(
                                            text = "Try again",
                                            fontSize = FontSize.SMALL,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        )
                    }

                    CustomTextField(
                        value = screenState.title,
                        onValueChange = onUpdateTitle,
                        placeholder = "Title"
                    )

                    CustomTextField(
                        modifier = Modifier.height(168.dp),
                        value = screenState.description,
                        onValueChange = onUpdateDescription,
                        placeholder = "Description",
                        expanded = true
                    )

                    AlertTextField(
                        modifier = Modifier.fillMaxWidth(),
                        text = screenState.category.title,
                        onClick = onToggleCategoryDialog
                    )

                    AnimatedVisibility(visible = screenState.category != ProductCategory.Accessories) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            CustomTextField(
                                value = "${screenState.weight ?: ""}",
                                onValueChange = onUpdateWeight,
                                placeholder = "Weight",
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )

                            CustomTextField(
                                value = screenState.flavors,
                                onValueChange = onUpdateFlavors,
                                placeholder = "Flavors"
                            )
                        }
                    }

                    CustomTextField(
                        value = "${screenState.price}",
                        onValueChange = { value ->
                            if (value.isEmpty() || value.toDoubleOrNull() != null) {
                                onUpdatePrice(value.toDoubleOrNull() ?: 0.0)
                            }
                        },
                        placeholder = "Price",
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(start = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "New", fontSize = FontSize.REGULAR, color = TextPrimary)

                            Switch(
                                checked = screenState.isNew,
                                onCheckedChange = onUpdateIsNew,
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = SurfaceSecondary,
                                    uncheckedTrackColor = SurfaceDarker,
                                    checkedThumbColor = Surface,
                                    uncheckedThumbColor = Surface,
                                    checkedBorderColor = SurfaceSecondary,
                                    uncheckedBorderColor = SurfaceDarker
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Popular", fontSize = FontSize.REGULAR, color = TextPrimary)

                            Switch(
                                checked = screenState.isPopular,
                                onCheckedChange = onUpdateIsPopular,
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = SurfaceSecondary,
                                    uncheckedTrackColor = SurfaceDarker,
                                    checkedThumbColor = Surface,
                                    uncheckedThumbColor = Surface,
                                    checkedBorderColor = SurfaceSecondary,
                                    uncheckedBorderColor = SurfaceDarker
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Discount",
                                fontSize = FontSize.REGULAR,
                                color = TextPrimary
                            )

                            Switch(
                                checked = screenState.isDiscounted,
                                onCheckedChange = onUpdateIsDiscounted,
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = SurfaceSecondary,
                                    uncheckedTrackColor = SurfaceDarker,
                                    checkedThumbColor = Surface,
                                    uncheckedThumbColor = Surface,
                                    checkedBorderColor = SurfaceSecondary,
                                    uncheckedBorderColor = SurfaceDarker
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                }
                PrimaryButton(
                    text = buttonTitle,
                    enabled = isFormValid,
                    icon = buttonIcon,
                    onClick = onSubmitProduct
                )
            }
        }
    }
}