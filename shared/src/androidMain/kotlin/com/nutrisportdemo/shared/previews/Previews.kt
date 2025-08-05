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
import com.nutrisportdemo.shared.component.PrimaryButton
import com.nutrisportdemo.shared.component.ProfileForm
import com.nutrisportdemo.shared.component.QuantityCounter
import com.nutrisportdemo.shared.component.card.ErrorCard
import com.nutrisportdemo.shared.component.card.InfoCard
import com.nutrisportdemo.shared.component.dialog.CategoriesDialog
import com.nutrisportdemo.shared.component.dialog.CountryPickerDialog
import com.nutrisportdemo.shared.component.textField.AlertTextField
import com.nutrisportdemo.shared.component.textField.CustomTextField
import com.nutrisportdemo.shared.domain.Country
import com.nutrisportdemo.shared.domain.ProductCategory
import com.nutrisportdemo.shared.domain.QuantityCounterSize
import org.jetbrains.compose.resources.DrawableResource

@Preview(showBackground = true, name = "CustomTextField Preview")
@Composable
fun CustomTextFieldPreviews(
    @PreviewParameter(CustomTextFieldVariant::class) isError: Boolean
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
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


@Preview(showBackground = true, name = "AlertTextField Preview")
@Composable
fun AlertTextFieldPreview(
    @PreviewParameter(AlertTextFieldVariant::class) icon: DrawableResource?
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AlertTextField(text = "Some alert", icon = icon, onClick = {})
        }
    }
}

class AlertTextFieldVariant : PreviewParameterProvider<DrawableResource?> {
    override val values = sequenceOf(Resources.Icon.Plus, null)
}

@Preview(showBackground = true, name = "PrimaryButton Preview")
@Composable
fun PrimaryButtonPreview(
    @PreviewParameter(PrimaryButtonVariant::class) isEnabled: Boolean
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            PrimaryButton(text = "Continue", enabled = isEnabled, onClick = {})
        }
    }
}

class PrimaryButtonVariant : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(false, true)
}

@Preview(showBackground = true, name = "ProfileForm Preview")
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

@Preview(showBackground = true, name = "CountryPickerDialog Preview")
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

@Preview(showBackground = true, name = "CategoriesDialog Preview")
@Composable
fun CategoriesDialogPreview(
    @PreviewParameter(CategoriesDialogVariant::class) category: ProductCategory
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            CategoriesDialog(
                category = category,
                onDismiss = {},
                onConfirmClick = {}
            )
        }
    }
}

class CategoriesDialogVariant : PreviewParameterProvider<ProductCategory> {
    override val values = sequenceOf(
        ProductCategory.Protein,
        ProductCategory.Creatine,
        ProductCategory.PreWorkout,
        ProductCategory.Gainers,
        ProductCategory.Accessories
    )
}

@Preview(showBackground = true, name = "InfoCard Preview")
@Composable
fun InfoCardPreview(
    @PreviewParameter(InfoCardVariant::class) image: DrawableResource
) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        {
            InfoCard(image = image, title = "InfoTitle", subtitle = "InfoSubtitle")
        }
    }
}

class InfoCardVariant : PreviewParameterProvider<DrawableResource> {
    override val values = sequenceOf(Resources.Image.ShoppingCart, Resources.Image.Cat)
}

@Preview(showBackground = true, name = "ErrorCard Preview")
@Composable
fun ErrorCardPreview(
    @PreviewParameter(ErrorCardVariant::class) message: String

) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            ErrorCard(message = message)
        }
    }
}

class ErrorCardVariant : PreviewParameterProvider<String> {
    override val values = sequenceOf("Country not found", "Category not found")
}

@Preview(showBackground = true, name = "QuantityCounter Preview")
@Composable
fun QuantityCounterPreview(
    @PreviewParameter(QuantityCounterSizeProvider::class) size: QuantityCounterSize
) {
    var count by remember { mutableStateOf(1) }
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            QuantityCounter(
                size = size,
                value = count,
                onMinusClick = { count = it },
                onPlusClick = { count = it }
            )
        }
    }
}

class QuantityCounterSizeProvider : PreviewParameterProvider<QuantityCounterSize> {
    override val values = sequenceOf(
        QuantityCounterSize.Small,
        QuantityCounterSize.Large
    )
}

