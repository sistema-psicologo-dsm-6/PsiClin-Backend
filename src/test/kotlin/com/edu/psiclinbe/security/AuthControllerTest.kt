package com.edu.psiclinbe.security

import com.edu.psiclinbe.dto.request.SigninRequest
import com.edu.psiclinbe.model.enums.Role
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import kotlin.test.assertEquals

class AuthControllerTest {
    private val authenticationManager = Mockito.mock(AuthenticationManager::class.java)
    private val jwtUtil = Mockito.mock(JwtUtil::class.java)
    private val controller = AuthController(authenticationManager, jwtUtil)

    @Test
    fun `signin returns token and authenticated role`() {
        val authentication = Mockito.mock(Authentication::class.java)
        val fallbackToken = UsernamePasswordAuthenticationToken("", "")
        Mockito.`when`(
            authenticationManager.authenticate(
                Mockito.any(UsernamePasswordAuthenticationToken::class.java) ?: fallbackToken
            )
        ).thenReturn(authentication)
        Mockito.`when`(authentication.authorities)
            .thenReturn(listOf(SimpleGrantedAuthority("ROLE_ADMIN")))
        Mockito.`when`(jwtUtil.generateToken("admin@example.com")).thenReturn("jwt-token")

        val response = controller.signin(
            SigninRequest(email = "admin@example.com", senha = "senha")
        )

        assertEquals("jwt-token", response.body?.token)
        assertEquals(Role.ADMIN, response.body?.role)
    }
}