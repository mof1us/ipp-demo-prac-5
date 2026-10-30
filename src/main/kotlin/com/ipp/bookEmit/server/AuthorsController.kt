package com.ipp.bookEmit.server

import com.ipp.bookEmit.domain.service.employee.AuthorService
import com.ipp.bookEmit.domain.service.employee.BooksService
import com.ipp.bookEmit.domain.service.employee.dto.AuthorDto
import com.ipp.bookEmit.domain.service.employee.exceptions.author.AuthorNotFoundException
import com.ipp.bookEmit.server.request.CreateAuthorRequest
import com.ipp.bookEmit.server.request.PartitionUpdateAuthorRequest
import com.ipp.bookEmit.server.request.UpdateAuthorRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class AuthorsController(
    private val authorService: AuthorService,
    private val booksService: BooksService,
) {
    @GetMapping("/authors")
    fun getAuthors(): List<AuthorDto> {
        return authorService.getAllAuthors()
    }

    @GetMapping("/authors/{id}")
    fun getAuthors(
        @PathVariable
        id: Long
    ): AuthorDto {
        val author = try {
            authorService.getAuthorById(id)
        } catch (e: AuthorNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        return author
    }

    @PostMapping("/authors")
    fun createAuthor(
        @RequestBody
        request: CreateAuthorRequest
    ): AuthorDto {
        return authorService.createAuthor(request.name)
    }

    @PutMapping("/authors/{id}")
    fun updateAuthor(
        @PathVariable
        id: Long,
        @RequestBody
        request: UpdateAuthorRequest
    ): AuthorDto {
        return try {
            authorService.updateAuthor(id, request.name)
        } catch (e: AuthorNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    @PatchMapping("/authors/{id}")
    fun partitionUpdateAuthor(
        @PathVariable
        id: Long,
        @RequestBody
        request: PartitionUpdateAuthorRequest,
    ): AuthorDto {
        return try {
            authorService.updateAuthor(id, request.name)
        } catch (e: AuthorNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("/authors/{id}")
    fun deleteAuthor(
        @PathVariable
        id: Long,
    ) {
        try {
            booksService.deleteBooksByAuthorId(id)
            authorService.deleteAuthor(id)
        } catch (e: AuthorNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }
}
