package com.nutrisportdemo.shared.domain

import androidx.compose.ui.graphics.Color
import com.nutrisportdemo.shared.CategoryBlue
import com.nutrisportdemo.shared.CategoryGreen
import com.nutrisportdemo.shared.CategoryPurple
import com.nutrisportdemo.shared.CategoryRed
import com.nutrisportdemo.shared.CategoryYellow
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class Product @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    /** We need createdAt to be able to fetch the latest 10 items from the database */
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val title: String,
    val description: String,
    val thumbnail: String,
    val category: String,
    val flavors: List<String>? = null,
    val weight: Int? = null,
    val price: Double? = null,
    /** These 3 parameter below can be manually set from the firestore database */
    val isPopular: Boolean = false,
    val isDiscounted: Boolean = false,
    val isNew: Boolean = false
)

enum class ProductCategory(
    val title: String,
    val color: Color
) {
    Protein(
        title = "Protein",
        color = CategoryYellow
    ),
    Creatine(
        title = "Creatine",
        color = CategoryBlue
    ),
    PreWorkout(
        title = "Pre-Workout",
        color = CategoryGreen
    ),
    Gainers(
        title = "Gainers",
        color = CategoryPurple
    ),
    Accessories(
        title = "Accessories",
        color = CategoryRed
    )
}

