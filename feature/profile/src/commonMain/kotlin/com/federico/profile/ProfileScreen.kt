package com.federico.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nutrisportdemo.shared.FontSize
import com.nutrisportdemo.shared.IconPrimary
import com.nutrisportdemo.shared.Resources
import com.nutrisportdemo.shared.Surface
import com.nutrisportdemo.shared.TextPrimary
import com.nutrisportdemo.shared.bebasNeueFont
import com.nutrisportdemo.shared.component.card.ErrorCard
import com.nutrisportdemo.shared.component.PrimaryButton
import com.nutrisportdemo.shared.component.ProfileForm
import com.nutrisportdemo.shared.component.card.LoadingCard
import com.nutrisportdemo.shared.util.DisplayResult
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigateBack: () -> Unit
) {
    val viewModel = koinViewModel<ProfileViewModel>()
    val screenReady = viewModel.screenReady
    val screenState = viewModel.screenState

    Scaffold(containerColor = Surface, topBar = {
        TopAppBar(
            title = {
                Text(
                    "My profile",
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
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Surface,
                scrolledContainerColor = Surface,
                navigationIconContentColor = IconPrimary,
                titleContentColor = TextPrimary,
                actionIconContentColor = IconPrimary
            )
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            ).padding(
                horizontal = 24.dp
            ).padding(top = 12.dp, bottom = 24.dp)
        ) {
            screenReady.DisplayResult(
                onLoading = {
                    LoadingCard(modifier = Modifier.fillMaxSize())
                },
                onSuccess = { state ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        screenState.apply {
                            ProfileForm(
                                modifier = Modifier.weight(1f),
                                firstName = firstName,
                                country = country,
                                onCountrySelected = viewModel::updateCountry,
                                onFirstNameChange = viewModel::updateFirstName,
                                lastName = lastName,
                                onLastNameChange = viewModel::updateLastName,
                                email = email,
                                city = city,
                                onCityChange = viewModel::updateCity,
                                postalCode = postalCode,
                                onPostalCodeChange = viewModel::updatePostalCode,
                                address = address,
                                onAddressChange = viewModel::updateAddress,
                                phoneNumber = phoneNumber?.number,
                                onPhoneNumberChange = viewModel::updatePhoneNumber
                            )
                        }

                        PrimaryButton(text = "Update", icon = Resources.Icon.Check, onClick = {})
                    }
                },
                onError = { message ->
                    ErrorCard(message = message, fontSize = FontSize.REGULAR)

                }
            )
        }
    }
}