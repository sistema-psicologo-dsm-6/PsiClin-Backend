package com.edu.psiclinbe.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtUtil(
    @Value("\${jwt.secret}") private val secretKey: String
) {

    fun generateToken(
        username: String,
        role: String
    ): String {

        return Jwts.builder()
            .setSubject(username)
            .claim("role", role)
            .setIssuedAt(Date())
            .setExpiration(
                Date(
                    System.currentTimeMillis() +
                            1000 * 60 * 60 * 24
                )
            )
            .signWith(
                Keys.hmacShaKeyFor(secretKey.toByteArray()),
                SignatureAlgorithm.HS256
            )
            .compact()
    }

    fun generateToken(username: String): String =
        Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()), SignatureAlgorithm.HS256)
            .compact()

    fun extractUsername(
        token: String
    ): String {

        return extractAllClaims(token)
            .subject
    }

    fun extractRole(token: String): String? =
        runCatching {
            extractAllClaims(token)["role"] as? String
        }.getOrNull()

    fun validateToken(
        token: String,
        username: String
    ): Boolean {

        return try {

            extractUsername(token) == username

        } catch (e: Exception) {

            false
        }
    }
    private fun extractAllClaims(
        token: String
    ): Claims {

        return Jwts.parserBuilder()
            .setSigningKey(
                Keys.hmacShaKeyFor(
                    secretKey.toByteArray()
                )
            )
            .build()
            .parseClaimsJws(token)
            .body
    }

}