package com.ipp.bookEmit.domain.service.employee.dto

data class BookDto(
    val id: Long,
    val title: String,
    val author: AuthorDto,
    val libraries: List<LibraryDto>
)
