package com.ipp.bookEmit.server.request

data class PartitionUpdateBookRequest(
    val title: String? = null,
    val authorId: Long? = null,
    val libraries: List<Long>? = null,
)
