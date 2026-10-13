package com.edu.psiclinbe.security

import com.edu.psiclinbe.repository.FuncionarioRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val funcionarioRepository: FuncionarioRepository
) : UserDetailsService {

    override fun loadUserByUsername(
        username: String
    ): UserDetails {

        val funcionario =
            funcionarioRepository.findByEmail(username)
                ?: throw UsernameNotFoundException(
                    "Usuário não encontrado"
                )

        return User.builder()
            .username(funcionario.email)
            .password(funcionario.senha)
            .authorities(
                listOf(
                    SimpleGrantedAuthority(
                        "ROLE_${funcionario.role}"
                    )
                )
            )
            .disabled(!funcionario.ativo)
            .build()
    }
}