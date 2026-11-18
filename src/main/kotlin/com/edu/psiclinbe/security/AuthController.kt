package com.edu.psiclinbe.security

import com.edu.psiclinbe.dto.request.SigninRequest
import com.edu.psiclinbe.dto.response.AuthResponse
import com.edu.psiclinbe.model.enums.Role
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil
) {

    @PostMapping("/signin")
    fun signin(
        @RequestBody request: SigninRequest
    ): ResponseEntity<AuthResponse> {

        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.senha
            )
        )

        val roleAuthority = authentication.authorities
            .firstOrNull { it.authority?.startsWith("ROLE_") == true }
            ?.authority
            ?: throw IllegalStateException("Perfil não encontrado na autenticação")
        val role = Role.valueOf(roleAuthority.removePrefix("ROLE_"))

        val token =
            jwtUtil.generateToken(
                request.email
            )

        return ResponseEntity.ok(
            AuthResponse(token, role)
        )
    }

    @PostMapping("/signout")
    fun signout(): ResponseEntity<Void> =
        ResponseEntity.noContent().build()
}