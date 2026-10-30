package com.ipp.bookEmit.server.request

data class CreateBookRequest(
    val title: String,
    val author: Long,
    val libraries: List<Long>,
)
