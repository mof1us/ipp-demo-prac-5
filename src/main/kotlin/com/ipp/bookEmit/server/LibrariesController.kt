package com.ipp.bookEmit.server

import com.ipp.bookEmit.domain.service.employee.LibraryService
import com.ipp.bookEmit.domain.service.employee.dto.LibraryDto
import com.ipp.bookEmit.domain.service.employee.exceptions.library.LibraryNotFoundException
import com.ipp.bookEmit.server.request.CreateLibraryRequest
import com.ipp.bookEmit.server.request.PartitionUpdateLibraryRequest
import com.ipp.bookEmit.server.request.UpdateLibraryRequest
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
class LibrariesController(
    private val libraryService: LibraryService,
) {
    @GetMapping("/libraries")
    fun getLibraries(): List<LibraryDto> {
        return libraryService.getAllLibraries()
    }

    @GetMapping("/libraries/{id}")
    fun getLibraries(
        @PathVariable
        id: Long
    ): LibraryDto {
        val library = try {
            libraryService.getLibraryById(id)
        } catch (e: LibraryNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        return library
    }

    @PostMapping("/libraries")
    fun createLibrary(
        @RequestBody
        request: CreateLibraryRequest
    ): LibraryDto {
        return libraryService.createLibrary(request.title)
    }

    @PutMapping("/libraries/{id}")
    fun updateLibrary(
        @PathVariable
        id: Long,
        @RequestBody
        request: UpdateLibraryRequest
    ): LibraryDto {
        return try {
            libraryService.updateLibrary(id, request.title)
        } catch (e: LibraryNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    @PatchMapping("/libraries/{id}")
    fun partitionUpdateLibrary(
        @PathVariable
        id: Long,
        @RequestBody
        request: PartitionUpdateLibraryRequest,
    ): LibraryDto {
        return try {
            libraryService.updateLibrary(id, request.title)
        } catch (e: LibraryNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("/libraries/{id}")
    fun deleteLibrary(
        @PathVariable
        id: Long,
    ) {
        try {
            libraryService.deleteLibrary(id)
        } catch (e: LibraryNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }
}
