package com.ipp.bookEmit.server.request

data class UpdateBookRequest(
    val title: String,
    val author: Long,
    val libraries: List<Long>,
)
