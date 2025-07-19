package com.federico.nutrisportdemo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform