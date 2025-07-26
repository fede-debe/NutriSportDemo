package com.nutrisportdemo.shared.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.nutrisportdemo.shared.FontSize
import com.nutrisportdemo.shared.Resources
import com.nutrisportdemo.shared.component.textField.AlertTextField
import com.nutrisportdemo.shared.component.textField.CustomTextField
import com.nutrisportdemo.shared.component.PrimaryButton
import com.nutrisportdemo.shared.component.ProfileForm
import com.nutrisportdemo.shared.component.dialog.CountryPickerDialog
import com.nutrisportdemo.shared.domain.Country
import org.jetbrains.compose.resources.DrawableResource

@Preview(showBackground = true)
@Composable
fun CustomTextFieldPreviews(
    @PreviewParameter(CustomTextFieldVariant::class) isError: Boolean
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "CustomTextField",
                fontSize = FontSize.REGULAR,
                fontWeight = FontWeight.SemiBold
            )

            var text by remember { mutableStateOf("") }
            CustomTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = "Enter your name",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = isError
            )
        }
    }
}

class CustomTextFieldVariant : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(false, true)
}


@Preview(showBackground = true)
@Composable
fun AlertTextFieldPreview(
    @PreviewParameter(AlertTextFieldVariant::class) icon: DrawableResource?
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "AlertTextField",
                fontSize = FontSize.REGULAR,
                fontWeight = FontWeight.SemiBold
            )

            AlertTextField(text = "Some alert", icon = icon, onClick = {})
        }
    }
}

class AlertTextFieldVariant : PreviewParameterProvider<DrawableResource?> {
    override val values = sequenceOf(Resources.Icon.Plus, null)
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview(
    @PreviewParameter(PrimaryButtonVariant::class) isEnabled: Boolean
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "PrimaryButton",
                fontSize = FontSize.REGULAR,
                fontWeight = FontWeight.SemiBold
            )

            PrimaryButton(text = "Continue", enabled = isEnabled, onClick = {})
        }
    }
}

class PrimaryButtonVariant : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(false, true)
}

@Preview(showBackground = true)
@Composable
fun ProfileFormPreview(
    @PreviewParameter(ProfileFormVariant::class) profile: ProfileFormData
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "ProfileForm (for ${profile.firstName.ifEmpty { "Empty Fields" }})",
                fontSize = FontSize.REGULAR,
                fontWeight = FontWeight.SemiBold
            )

            ProfileForm(
                country = profile.country,
                onCountrySelected = {},
                firstName = profile.firstName,
                onFirstNameChange = {},
                lastName = profile.lastName,
                onLastNameChange = {},
                email = profile.email,
                city = profile.city,
                onCityChange = {},
                postalCode = profile.postalCode,
                onPostalCodeChange = {},
                address = profile.address,
                onAddressChange = {},
                phoneNumber = profile.phoneNumber,
                onPhoneNumberChange = {}
            )
        }
    }
}

data class ProfileFormData(
    val country: Country,
    val firstName: String,
    val lastName: String,
    val email: String,
    val city: String,
    val postalCode: Int?,
    val address: String,
    val phoneNumber: String?
)

class ProfileFormVariant : PreviewParameterProvider<ProfileFormData> {
    override val values: Sequence<ProfileFormData> = sequenceOf(
        // All fields empty — triggers all validation errors
        ProfileFormData(
            country = Country.Serbia,
            firstName = "",
            lastName = "",
            email = "",
            city = "",
            postalCode = null,
            address = "",
            phoneNumber = null
        ),
        // Valid profile data
        ProfileFormData(
            country = Country.India,
            firstName = "Amit",
            lastName = "Kumar",
            email = "amit.kumar@example.com",
            city = "Mumbai",
            postalCode = 400001,
            address = "Marine Drive",
            phoneNumber = "9876543210"
        ),
        // Only phone number is too short
        ProfileFormData(
            country = Country.Serbia,
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            city = "Belgrade",
            postalCode = 11000,
            address = "Main St 10",
            phoneNumber = "12"
        )
    )
}

@Preview(showBackground = true)
@Composable
fun CountryPickerDialogPreview(
    @PreviewParameter(CountryPickerDialogVariant::class) country: Country
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "CountryPickerDialog - query: \"$country\"",
                fontSize = FontSize.REGULAR,
                fontWeight = FontWeight.SemiBold
            )

            CountryPickerDialog(
                country = country,
                onDismiss = {},
                onConfirmClick = {}
            )
        }
    }
}

class CountryPickerDialogVariant : PreviewParameterProvider<Country> {
    override val values = sequenceOf(Country.Serbia, Country.Serbia, Country.India)
}

