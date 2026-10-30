package com.ipp.bookEmit.server.request

data class CreateContactRequest(
    val name: String,
    val phone: String,
    val email: String,
)
