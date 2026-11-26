package com.edu.psiclinbe.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val authHeader = request.getHeader("Authorization")?.trim()

        if (
            authHeader == null ||
            !authHeader.regionMatches(0, "Bearer ", 0, 7, true)
        ) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7).trim()

        val email = try {
            jwtUtil.extractUsername(token)
        } catch (e: Exception) {
            null
        }

        if (
            email != null &&
            !token.isBlank() &&
            SecurityContextHolder.getContext().authentication == null
        ) {
            val userDetails = runCatching {
                userDetailsService.loadUserByUsername(email)
            }.getOrNull()

            val principal = userDetails
            val authorities = userDetails?.authorities
                ?: jwtUtil.extractRole(token)
                    ?.takeIf { it in listOf("ADMIN", "RECEPCIONISTA", "PSICOLOGO") }
                    ?.let { listOf(SimpleGrantedAuthority("ROLE_$it")) }

            if (
                authorities != null &&
                jwtUtil.validateToken(token, email)
            ) {
                val authentication = UsernamePasswordAuthenticationToken(
                    principal ?: email,
                    null,
                    authorities
                )

                authentication.details =
                    WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }
}