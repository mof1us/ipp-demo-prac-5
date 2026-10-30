package com.ipp.bookEmit.server

import com.ipp.bookEmit.domain.service.employee.BooksService
import com.ipp.bookEmit.domain.service.employee.dto.BookDto
import com.ipp.bookEmit.domain.service.employee.exceptions.author.AuthorNotFoundException
import com.ipp.bookEmit.domain.service.employee.exceptions.book.BookNotFoundException
import com.ipp.bookEmit.domain.service.employee.exceptions.library.LibraryNotFoundException
import com.ipp.bookEmit.server.request.CreateBookRequest
import com.ipp.bookEmit.server.request.PartitionUpdateBookRequest
import com.ipp.bookEmit.server.request.UpdateBookRequest
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
class BooksController(
    private val booksService: BooksService,
) {
    @GetMapping("/books")
    fun getBooks(): List<BookDto> {
        return booksService.getAllBooks()
    }

    @GetMapping("/books/{id}")
    fun getBooks(
        @PathVariable
        id: Long
    ): BookDto {
        val book = try {
            booksService.getBookById(id)
        } catch (e: BookNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        return book
    }

    @PostMapping("/books")
    fun createBook(
        @RequestBody
        request: CreateBookRequest
    ): BookDto {
        return try {
            booksService
                .createBook(request.title, request.author, request.libraries)
        } catch (e: AuthorNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        } catch (e: LibraryNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/books/{id}")
    fun updateBook(
        @PathVariable
        id: Long,
        @RequestBody
        request: UpdateBookRequest
    ): BookDto {
        return try {
            booksService.updateBook(id, request.title, request.author, request.libraries)
        }
        catch (e: BookNotFoundException){
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        catch (e: AuthorNotFoundException){
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        catch (e: LibraryNotFoundException){
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

        @PatchMapping("/books/{id}")
        fun partitionUpdateBook(
            @PathVariable
            id: Long,
            @RequestBody
            request: PartitionUpdateBookRequest,
        ): BookDto {
            return try{
                booksService.updateBook(id, request.title, request.authorId, request.libraries)
            }
            catch (e: BookNotFoundException){
                throw ResponseStatusException(HttpStatus.NOT_FOUND)
            }
            catch (e: AuthorNotFoundException){
                throw ResponseStatusException(HttpStatus.NOT_FOUND)
            }
            catch (e: LibraryNotFoundException){
                throw ResponseStatusException(HttpStatus.NOT_FOUND)
            }
        }

        @DeleteMapping("/books/{id}")
        fun deleteBook(
            @PathVariable
            id: Long,
        ) {
            try{
                booksService.deleteBook(id)
            }
            catch (e: BookNotFoundException){
                throw ResponseStatusException(HttpStatus.NOT_FOUND)
            }
        }
    }