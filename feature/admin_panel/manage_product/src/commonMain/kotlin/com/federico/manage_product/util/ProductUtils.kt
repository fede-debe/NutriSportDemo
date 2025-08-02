package com.federico.manage_product.util

import com.federico.manage_product.ManageProductState
import com.nutrisportdemo.shared.domain.Product
import com.nutrisportdemo.shared.util.splitAndTrim


fun ManageProductState.toProduct(isUpdatingProduct: Boolean): Product {
    val flavors = if (isUpdatingProduct) flavors.splitAndTrim()
    else flavors.split(",")

    return Product(
        id = this.id,
        createdAt = this.createdAt,
        title = this.title,
        description = this.description,
        thumbnail = this.thumbnail,
        category = this.category.name,
        flavors = flavors,
        weight = this.weight,
        price = this.price
    )
}