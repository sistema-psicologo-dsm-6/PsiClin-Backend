package com.edu.psiclinbe.security

import com.edu.psiclinbe.dto.request.FuncionarioDtoRequest
import com.edu.psiclinbe.dto.request.SigninRequest
import com.edu.psiclinbe.dto.response.AuthResponse
import com.edu.psiclinbe.dto.response.FuncionarioDtoResponse
import com.edu.psiclinbe.model.enums.Role
import com.edu.psiclinbe.service.FuncionarioService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val funcionarioService: FuncionarioService,
    private val jwtUtil: JwtUtil
) {

    @PostMapping("/signup")
    fun signup(
        @Valid
        @RequestBody dto: FuncionarioDtoRequest
    ): ResponseEntity<FuncionarioDtoResponse> {

        val funcionario = funcionarioService.criarAdminInicial(dto)

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(funcionario)
    }

    @PostMapping("/signin")
    fun signin(
        @RequestBody request: SigninRequest
    ): ResponseEntity<AuthResponse> {

        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.email,
                request.senha
            )
        )

        val token =
            jwtUtil.generateToken(
                request.email
            )

        return ResponseEntity.ok(
            AuthResponse(token)
        )
    }
}