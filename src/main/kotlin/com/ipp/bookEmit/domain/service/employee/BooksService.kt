package com.ipp.bookEmit.domain.service.employee

import com.ipp.bookEmit.domain.service.employee.dto.AuthorDto
import com.ipp.bookEmit.domain.service.employee.dto.BookDto
import com.ipp.bookEmit.domain.service.employee.dto.LibraryDto
import com.ipp.bookEmit.domain.service.employee.entity.BookEntity
import com.ipp.bookEmit.domain.service.employee.exceptions.author.AuthorNotFoundException
import com.ipp.bookEmit.domain.service.employee.exceptions.book.BookNotFoundException
import org.springframework.stereotype.Service
import kotlin.collections.listOf

@Service
class BooksService(
    private val authorService: AuthorService,
    private val libraryService: LibraryService,
) {
    private val books = mutableListOf<BookEntity>(
        BookEntity(
            id = 1,
            title = "Книга 1",
            authorId = 1,
        )
    )


    fun getAllBooks(): List<BookDto> {
        val booksIds = books.map { it.id }
        val authorsIds = books.map { it.authorId }
        val authors = authorService.getAuthorByIds(authorsIds)
        val libraries = libraryService.getByBookIds(booksIds)
        return books.map { book ->
            book.toBookDto(
                author = authors.find { it.id == book.authorId } ?: throw AuthorNotFoundException(),
                libraries = libraries[book.id] ?: emptyList()
            )
        }
    }

    fun getBookById(id: Long): BookDto {
        val bookEntity = books.find { it.id == id } ?: throw BookNotFoundException()
        val author = authorService.getAuthorByIds(listOf(bookEntity.authorId)).getOrNull(0) ?: throw AuthorNotFoundException()
        val libraries = libraryService.getByBookIds(listOf(bookEntity.id))[id] ?: emptyList()
        return bookEntity.toBookDto(
            author,
            libraries
        )
    }

    fun createBook(title: String, authorId: Long, libraries: List<Long>): BookDto {
        /**
         * Проверка, что автор сущ. Метод выкинет ошибку
         */
        authorService.getAuthorByIds(listOf(authorId))
        libraryService.getByIds(libraries)
        val id = getNextId()
        books.add(
            BookEntity(
                id,
                title,
                authorId
            )
        )
        libraryService.createLibraryBook(id, libraries)
        return getBookById(id)
    }

    fun updateBook(
        bookId: Long, title: String?, authorId: Long?, librariesIds: List<Long>?
    ): BookDto {
        val book = books.find { it.id == bookId } ?: throw BookNotFoundException()
        val newAuthor = authorId?.let {
            authorService.getAuthorByIds(listOf(it)).firstOrNull() ?: throw AuthorNotFoundException()
        } ?: authorService.getAuthorByIds(listOf(book.authorId)).firstOrNull() ?: throw AuthorNotFoundException()
        val libraries = libraryService.getByBookIds(listOf(bookId))[bookId] ?: emptyList()

        librariesIds?.let { libraryService.getByIds(it) }
        val updatedBook = book.copy(title = title ?: book.title, authorId = authorId ?: book.authorId)
        /**
         * Сделаем через удаление
         */
        books.remove(book)
        books.add(updatedBook)
        librariesIds?.let {
            libraryService.removeLibraryBook(bookId, libraries.map { it.id })
            libraryService.createLibraryBook(bookId,  librariesIds)
        }

        return updatedBook.toBookDto(
            newAuthor,
            libraryService.getByBookIds(listOf(bookId))[bookId] ?: emptyList(),
        )
    }

    fun deleteBook(
        bookId: Long,
    ){
        val book = books.find { it.id == bookId } ?: throw BookNotFoundException()
        val libraries = libraryService.getByBookIds(listOf(bookId))[bookId] ?: emptyList()
        libraryService.removeLibraryBook(bookId, libraries.map { it.id })
        books.remove(book)
    }

    fun deleteBooksByAuthorId(authorId: Long) {
        authorService.getAuthorById(authorId)
        val booksIds = books.filter { it.authorId == authorId }.map { it.id }
        booksIds.forEach { deleteBook(it) }
    }

    /**
     * Имитация автоинкримента
     */
    private fun getNextId(): Long {
        return (books.maxOfOrNull { it.id } ?: 0) + 1
    }

    private fun BookEntity.toBookDto(author: AuthorDto, libraries: List<LibraryDto>): BookDto = BookDto(
        id = id,
        title = title,
        author = author,
        libraries = libraries,
    )
}