package com.ipp.bookEmit.data.repository

import com.ipp.bookEmit.domain.service.employee.entity.ContactEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ContactRepository: JpaRepository<ContactEntity, Long>
