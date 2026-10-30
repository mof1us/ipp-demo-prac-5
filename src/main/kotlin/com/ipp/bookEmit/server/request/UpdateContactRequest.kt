package com.ipp.bookEmit.server.request

data class UpdateContactRequest(
    val name: String,
    val phone: String,
    val email: String,
)
