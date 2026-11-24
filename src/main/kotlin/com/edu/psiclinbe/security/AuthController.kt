package com.edu.psiclinbe.security

import com.edu.psiclinbe.dto.request.SigninRequest
import com.edu.psiclinbe.dto.response.AuthResponse
import com.edu.psiclinbe.model.enums.Role
import com.edu.psiclinbe.dto.request.FuncionarioDtoRequest
import com.edu.psiclinbe.dto.response.FuncionarioDtoResponse
import com.edu.psiclinbe.service.FuncionarioService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login, bootstrap do administrador inicial e logout")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil,
    private val funcionarioService: FuncionarioService,
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

    @PostMapping("/signup")
    fun signup(
        @Valid @RequestBody dto: FuncionarioDtoRequest,
    ): ResponseEntity<FuncionarioDtoResponse> {
        val funcionario = funcionarioService.criarAdminInicial(dto)
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionario)
    }
}