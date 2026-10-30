package com.ipp.bookEmit.domain.service.employee

import com.ipp.bookEmit.domain.service.employee.dto.ContactDto
import com.ipp.bookEmit.domain.service.employee.entity.ContactEntity
import com.ipp.bookEmit.domain.service.employee.exceptions.contact.ContactNotFoundException
import com.ipp.bookEmit.data.repository.ContactRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ContactService(
    private val contactRepository: ContactRepository,
) {
    @Transactional(readOnly = true)
    fun getAllContacts(): List<ContactDto> {
        return contactRepository.findAll().map { it.toContactDto() }
    }

    @Transactional(readOnly = true)
    fun getContactById(id: Long): ContactDto {
        val contact = contactRepository.findById(id).orElseThrow { ContactNotFoundException() }
        return contact.toContactDto()
    }

    @Transactional
    fun createContact(name: String, phone: String, email: String): ContactDto {
        val contact = contactRepository.save(
            ContactEntity(
                name = name,
                phone = phone,
                email = email,
            )
        )
        return contact.toContactDto()
    }

    @Transactional
    fun updateContact(
        contactId: Long, name: String, phone: String, email: String
    ): ContactDto {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException() }
        contact.name = name
        contact.phone = phone
        contact.email = email
        return contactRepository.save(contact).toContactDto()
    }

    @Transactional
    fun deleteContact(
        contactId: Long,
    ) {
        val contact = contactRepository.findById(contactId).orElseThrow { ContactNotFoundException() }
        contactRepository.delete(contact)
    }

    @Transactional
    fun deleteAllContacts() {
        contactRepository.deleteAllInBatch()
    }

    private fun ContactEntity.toContactDto(): ContactDto = ContactDto(
        id = id,
        name = name,
        phone = phone,
        email = email,
    )
}
