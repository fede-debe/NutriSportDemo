package com.nutrisportdemo.shared.util

fun String.splitAndTrim(): List<String> {
    return this.split(",")
        .map { text -> text.trim() }
        .filter { value -> value.isNotEmpty() }
}