package com.ipp.bookEmit.domain.service.employee

import com.ipp.bookEmit.domain.service.employee.dto.LibraryDto
import com.ipp.bookEmit.domain.service.employee.entity.LibraryBookEntity
import com.ipp.bookEmit.domain.service.employee.entity.LibraryEntity
import com.ipp.bookEmit.domain.service.employee.exceptions.library.LibraryNotFoundException
import org.springframework.stereotype.Service

@Service
class LibraryService {
    private val libraries: MutableList<LibraryEntity> = mutableListOf(
        LibraryEntity(
            id = 1,
            title = "Библеотека 1"
        ),
        LibraryEntity(
            id = 2,
            title = "Библеотека 2"
        ),
    )

    private val libraryBooks: MutableList<LibraryBookEntity> = mutableListOf(
        LibraryBookEntity(
            libraryId = 2,
            bookId = 1,
        ),
    )

    fun getAllLibraries(): List<LibraryDto> {
        return libraries.map { it.toLibraryDto() }
    }

    fun getLibraryById(id: Long): LibraryDto {
        val library = libraries.find { it.id == id } ?: throw LibraryNotFoundException()
        return library.toLibraryDto()
    }

    fun createLibrary(title: String): LibraryDto {
        val id = getNextId()
        libraries.add(
            LibraryEntity(
                id,
                title
            )
        )
        return getLibraryById(id)
    }

    fun updateLibrary(
        libraryId: Long, title: String?
    ): LibraryDto {
        val library = libraries.find { it.id == libraryId } ?: throw LibraryNotFoundException()
        val updatedLibrary = library.copy(title = title ?: library.title)
        libraries.remove(library)
        libraries.add(updatedLibrary)
        return updatedLibrary.toLibraryDto()
    }

    fun deleteLibrary(
        libraryId: Long,
    ) {
        val library = libraries.find { it.id == libraryId } ?: throw LibraryNotFoundException()
        libraryBooks.removeAll { it.libraryId == libraryId }
        libraries.remove(library)
    }

    fun getByBookIds(bookIds: List<Long>): Map<Long, List<LibraryDto>> {
        val foundedLibraryBooks = libraryBooks
            .filter { bookIds.contains(it.bookId) }
            .groupBy { it.bookId }
        return foundedLibraryBooks.entries.associate { (bookId, librariesEntities) ->
            val libraries = libraries
                .filter {
                    librariesEntities
                        .map { library -> library.libraryId }.contains(it.id)
                }
                .map { libraryEntity -> libraryEntity.toLibraryDto() }
            bookId to libraries
        }
    }

    fun getByIds(libraryIds: List<Long>): List<LibraryDto> {
        val foundedLibraries = libraries.filter { libraryIds.contains(it.id) }
        if (!foundedLibraries.map { it.id }.containsAll(libraryIds)) throw LibraryNotFoundException()
        return foundedLibraries.map { it.toLibraryDto() }
    }

    fun createLibraryBook(bookId: Long, libraryIds: List<Long>) {
        val libraries = getByIds(libraryIds)
        if (!libraries.map { it.id }.containsAll(libraryIds)) throw LibraryNotFoundException()
        libraryBooks.addAll(libraryIds.map {
            LibraryBookEntity(
                bookId, it
            )
        })
    }

    fun removeLibraryBook(bookId: Long, libraryIds: List<Long>){
        val libraries = getByIds(libraryIds)
        if (!libraries.map { it.id }.containsAll(libraryIds)) throw LibraryNotFoundException()
        libraryBooks.removeAll(
            libraryIds.map {
                LibraryBookEntity(
                    bookId, it
                )
            }
        )
    }


    /**
     * Имитация автоинкримента
     */
    private fun getNextId(): Long {
        return (libraries.maxOfOrNull { it.id } ?: 0) + 1
    }

    private fun LibraryEntity.toLibraryDto(): LibraryDto = LibraryDto(
        id = id,
        title = title
    )
}