package com.ipp.bookEmit.server

import com.ipp.bookEmit.domain.service.employee.ContactService
import com.ipp.bookEmit.domain.service.employee.dto.ContactDto
import com.ipp.bookEmit.domain.service.employee.exceptions.contact.ContactNotFoundException
import com.ipp.bookEmit.server.request.CreateContactRequest
import com.ipp.bookEmit.server.request.UpdateContactRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
class ContactController(
    private val contactService: ContactService,
) {
    @GetMapping("/v1/contact")
    fun getContacts(): List<ContactDto> {
        return contactService.getAllContacts()
    }

    @GetMapping("/v1/contact/{uid}")
    fun getContacts(
        @PathVariable
        uid: Long
    ): ContactDto {
        val contact = try {
            contactService.getContactById(uid)
        } catch (e: ContactNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        return contact
    }

    @PostMapping("/v1/contact")
    fun createContact(
        @RequestBody
        request: CreateContactRequest
    ): ContactDto {
        return contactService.createContact(request.name, request.phone, request.email)
    }

    @PutMapping("/v1/contact/{uid}")
    fun updateContact(
        @PathVariable
        uid: Long,
        @RequestBody
        request: UpdateContactRequest
    ): ContactDto {
        return try {
            contactService.updateContact(uid, request.name, request.phone, request.email)
        } catch (e: ContactNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("/v1/contact/{uid}")
    fun deleteContact(
        @PathVariable
        uid: Long,
    ) {
        try {
            contactService.deleteContact(uid)
        } catch (e: ContactNotFoundException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("/v1/contact")
    fun deleteAllContacts() {
        contactService.deleteAllContacts()
    }
}
