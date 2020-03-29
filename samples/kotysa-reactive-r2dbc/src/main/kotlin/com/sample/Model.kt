package com.sample

import java.time.LocalDateTime
import java.util.*

data class Role(
        val label: String,
        val id: UUID = UUID.randomUUID()
)

data class User(
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val roleId: UUID,
        val alias: String? = null,
        val creationTime: LocalDateTime = LocalDateTime.now(),
        val id: Int? = null
)