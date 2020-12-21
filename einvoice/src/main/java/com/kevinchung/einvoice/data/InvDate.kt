package com.kevinchung.einvoice.data

data class InvDate (
    val year: Int,
    val month: Int,
    val date: Int,
    val day: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val time: Long,
    val timezoneOffset: Int
)