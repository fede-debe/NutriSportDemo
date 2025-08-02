package com.federico.mapper

import com.nutrisportdemo.shared.domain.Product
import dev.gitlive.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toProduct(): Product {
    return Product(
        id = id,
        title = get("title"),
        createdAt = get("createdAt"),
        description = get("description"),
        thumbnail = get("thumbnail"),
        category = get("category"),
        flavors = get("flavors"),
        weight = get("weight"),
        price = get("price"),
        isPopular = get("isPopular"),
        isDiscounted = get("isDiscounted"),
        isNew = get("isNew")
    )
}
