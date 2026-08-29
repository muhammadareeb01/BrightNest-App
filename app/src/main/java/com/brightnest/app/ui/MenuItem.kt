package com.brightnest.app.ui

data class MenuItem(
    val title: String,
    val urdu: String,
    val emoji: String,
    val colorRes: Int,
    val destId: Int,
    val argTitle: String? = null
)
