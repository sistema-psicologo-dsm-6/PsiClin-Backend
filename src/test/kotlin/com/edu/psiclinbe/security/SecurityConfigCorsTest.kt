package com.edu.psiclinbe.security

import org.junit.jupiter.api.Test
import org.springframework.http.HttpMethod
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfigurationSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SecurityConfigCorsTest {
    private val config = SecurityConfig(
        org.mockito.Mockito.mock(CustomUserDetailsService::class.java),
        org.mockito.Mockito.mock(JwtAuthenticationFilter::class.java)
    )

    @Test
    fun `allows browser preflight for authenticated delete`() {
        val origin = "http://localhost:5173"
        val source: CorsConfigurationSource = config.corsConfigurationSource(origin)
        val request = MockHttpServletRequest("OPTIONS", "/psicologos/123")
        request.addHeader("Origin", origin)
        request.addHeader("Access-Control-Request-Method", "DELETE")
        request.addHeader("Access-Control-Request-Headers", "authorization")

        val cors = source.getCorsConfiguration(request)

        assertNotNull(cors)
        assertEquals(origin, cors.checkOrigin(origin))
        assertNotNull(cors.checkHttpMethod(HttpMethod.DELETE))
        assertNotNull(cors.checkHeaders(listOf("authorization")))
    }
}