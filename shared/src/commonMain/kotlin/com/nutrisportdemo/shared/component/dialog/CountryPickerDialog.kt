package com.nutrisportdemo.shared.component.dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nutrisportdemo.shared.Alpha
import com.nutrisportdemo.shared.FontSize
import com.nutrisportdemo.shared.IconWhite
import com.nutrisportdemo.shared.Resources
import com.nutrisportdemo.shared.Surface
import com.nutrisportdemo.shared.SurfaceLighter
import com.nutrisportdemo.shared.SurfaceSecondary
import com.nutrisportdemo.shared.TextPrimary
import com.nutrisportdemo.shared.TextSecondary
import com.nutrisportdemo.shared.component.textField.CustomTextField
import com.nutrisportdemo.shared.component.card.ErrorCard
import com.nutrisportdemo.shared.domain.Country
import org.jetbrains.compose.resources.painterResource

@Composable
fun CountryPickerDialog(
    country: Country,
    onDismiss: () -> Unit,
    onConfirmClick: (Country) -> Unit,
) {
    var selectedCountry by remember(country) { mutableStateOf(country) }
    val allCountries = remember { Country.entries.toList() }

    /** this filtered countries will contain all our countries from our enum class,
     *  and this is actually a mutable type or mutable property that we are going to be
     *  able to modify from within our custom text field when we actually start typing some search query */
    val filteredCountries = remember {
        mutableStateListOf<Country>().apply {
            addAll(allCountries)
        }
    }

    var searchQuery by remember { mutableStateOf("") }

    AlertDialog(
        containerColor = Surface,
        title = {
            Text(
                text = "Pick a Country",
                fontSize = FontSize.EXTRA_MEDIUM,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
            ) {
                CustomTextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                        if (searchQuery.isNotBlank()) {
                            val filtered = allCountries.filterByCountry(query)
                            filteredCountries.clear()
                            filteredCountries.addAll(filtered)
                        } else {
                            filteredCountries.clear()
                            filteredCountries.addAll(allCountries)
                        }
                    },
                    placeholder = "Search"
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredCountries.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = filteredCountries,
                            key = { it.ordinal }
                        ) { country ->
                            CountryPicker(
                                country = country,
                                isSelected = selectedCountry == country,
                                onSelect = { selectedCountry = country }
                            )
                        }
                    }
                } else {
                    ErrorCard(modifier = Modifier.weight(1f), message = "Country not found")
                }
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onConfirmClick(selectedCountry) },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextSecondary
                )
            ) {
                Text(
                    text = "Confirm",
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextPrimary.copy(alpha = Alpha.HALF)
                )
            ) {
                Text(
                    text = "Cancel",
                    fontSize = FontSize.REGULAR,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}

@Composable
private fun CountryPicker(
    modifier: Modifier = Modifier,
    country: Country,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    val saturation = remember { Animatable(if (isSelected) 1f else 0f) }

    LaunchedEffect(isSelected) {
        saturation.animateTo(if (isSelected) 1f else 0f)
    }

    val colorMatrix = remember(saturation.value) {
        ColorMatrix().apply {
            setToSaturation(saturation.value)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(14.dp),
            painter = painterResource(country.flag),
            contentDescription = "Country flag image",
            colorFilter = ColorFilter.colorMatrix(colorMatrix = colorMatrix)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            modifier = Modifier.weight(1f),
            text = "+${country.dialCode} (${country.name})",
            fontSize = FontSize.REGULAR,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Selector(isSelected = isSelected)
    }
}

@Composable
private fun Selector(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    val animatedBackground by animateColorAsState(
        targetValue = if (isSelected) SurfaceSecondary else SurfaceLighter
    )

    Box(
        modifier = modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(animatedBackground),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(visible = isSelected) {
            Icon(
                modifier = Modifier.size(14.dp),
                painter = painterResource(Resources.Icon.Check),
                contentDescription = "Checkmark icon",
                tint = IconWhite
            )
        }
    }
}

/**
 * extension function on a list of a country type that will help us to filter, uh, our list,
 * our country list by the country query. We are converting this query into the lowercase,
 * and we're converting this query into an integer value. The dial code that we want to pass to
 * this function is actually an integer value. We are filtering this whole list,
 * And we are comparing if that query or that dial code that we are passing to.
 * And this function will return a list of a multiple different countries with that as similar dial code query,
 * or it will return just an empty list,
 * if that dial code query doesn't belong to any of those countries from this list.*
 * */
fun List<Country>.filterByCountry(query: String): List<Country> {
    val queryLower = query.lowercase()
    val queryInt = query.toIntOrNull()

    return this.filter { country ->
        // Match by name or code
        country.name.lowercase().contains(queryLower) ||
                country.code.lowercase().contains(queryLower) ||

                // Match exact dial code or prefix match (only from the start)
                (queryInt != null && (
                        country.dialCode.toString() == queryInt.toString() ||
                                country.dialCode.toString().startsWith(queryInt.toString())
                        ))
    }
}


