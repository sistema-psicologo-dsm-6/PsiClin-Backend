package com.edu.psiclinbe.dto.response

import com.edu.psiclinbe.model.enums.Role

data class AuthResponse(
    val token: String,
    val role: Role,
)