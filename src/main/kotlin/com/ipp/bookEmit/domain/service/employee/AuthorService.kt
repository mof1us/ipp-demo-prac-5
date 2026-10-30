package com.ipp.bookEmit.domain.service.employee

import com.ipp.bookEmit.domain.service.employee.dto.AuthorDto
import com.ipp.bookEmit.domain.service.employee.entity.AuthorEntity
import com.ipp.bookEmit.domain.service.employee.exceptions.author.AuthorNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthorService {
    private val authors: MutableList<AuthorEntity> = mutableListOf(
        AuthorEntity(
            id = 1,
            name = "Автор 1"
        )
    )

    fun getAllAuthors(): List<AuthorDto> {
        return authors.map { it.toAuthorDto() }
    }

    fun getAuthorById(id: Long): AuthorDto {
        val author = authors.find { it.id == id } ?: throw AuthorNotFoundException()
        return author.toAuthorDto()
    }

    fun createAuthor(name: String): AuthorDto {
        val id = getNextId()
        authors.add(
            AuthorEntity(
                id,
                name
            )
        )
        return getAuthorById(id)
    }

    fun updateAuthor(
        authorId: Long, name: String?
    ): AuthorDto {
        val author = authors.find { it.id == authorId } ?: throw AuthorNotFoundException()
        val updatedAuthor = author.copy(name = name ?: author.name)
        authors.remove(author)
        authors.add(updatedAuthor)
        return updatedAuthor.toAuthorDto()
    }

    fun deleteAuthor(
        authorId: Long,
    ) {
        val author = authors.find { it.id == authorId } ?: throw AuthorNotFoundException()
        authors.remove(author)
    }

    fun getAuthorByIds(ids: List<Long>): List<AuthorDto> {
        val authors = authors
            .filter { ids.contains(it.id) }.map { it.toAuthorDto() }
        val foundedIds = authors.map { it.id }
        if (!foundedIds.containsAll(ids)) throw AuthorNotFoundException()
        return authors
    }

    /**
     * Имитация автоинкримента
     */
    private fun getNextId(): Long {
        return (authors.maxOfOrNull { it.id } ?: 0) + 1
    }

    private fun AuthorEntity.toAuthorDto() = AuthorDto(
        id = id,
        name = name
    )
}