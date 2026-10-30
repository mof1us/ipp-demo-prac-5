package com.ipp.bookEmit.domain.service.employee.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "contacts")
open class ContactEntity(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long = 0,
    @field:Column(nullable = false)
    open var name: String,
    @field:Column(nullable = false)
    open var phone: String,
    @field:Column(nullable = false)
    open var email: String,
)
