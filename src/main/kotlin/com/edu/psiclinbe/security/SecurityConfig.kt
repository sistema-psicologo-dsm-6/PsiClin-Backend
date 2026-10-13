package com.edu.psiclinbe.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableMethodSecurity
@Configuration
class SecurityConfig(
    private val userDetailsService: CustomUserDetailsService,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {

    private fun writeError(
        response: jakarta.servlet.http.HttpServletResponse,
        status: Int,
        message: String
    ) {
        response.status = status
        response.contentType = "application/json;charset=UTF-8"
        response.writer.write("""{"status":$status,"message":"$message"}""")
    }

    @Bean
    fun authenticationManager(
        configuration: AuthenticationConfiguration
    ): AuthenticationManager {
        return configuration.authenticationManager
    }

    @Bean
    fun authenticationProvider(
        passwordEncoder: PasswordEncoder
    ): DaoAuthenticationProvider {

        val provider = DaoAuthenticationProvider(userDetailsService)

        provider.setPasswordEncoder(passwordEncoder)

        return provider
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity
    ): SecurityFilterChain {

        return http
            .csrf { it.disable() }

            .sessionManagement {
                it.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }

            .authorizeHttpRequests {

                val admin = "ADMIN"
                val recep = "RECEPCIONISTA"
                val psi = "PSICOLOGO"

                it.requestMatchers("/auth/signin", "/auth/signup").permitAll()

                // Somente ADMIN gerencia usuários
                it.requestMatchers("/funcionarios/**").hasRole(admin)

                // Equipe: leitura para todos, escrita só ADMIN
                it.requestMatchers(HttpMethod.GET, "/psicologos/**")
                    .hasAnyRole(admin, recep, psi)
                it.requestMatchers("/psicologos/**").hasRole(admin)
                it.requestMatchers(HttpMethod.GET, "/recepcionistas/**")
                    .hasAnyRole(admin, recep)
                it.requestMatchers("/recepcionistas/**").hasRole(admin)

                // Área clínica: ADMIN e PSICOLOGO
                it.requestMatchers("/prontuarios/**", "/evolucoes-clinicas/**")
                    .hasAnyRole(admin, psi)

                // Disponibilidade: PSICOLOGO edita, RECEPCIONISTA consulta
                it.requestMatchers(
                    HttpMethod.GET,
                    "/horarios-disponiveis/**",
                    "/bloqueios-agenda/**"
                ).hasAnyRole(admin, recep, psi)
                it.requestMatchers(
                    "/horarios-disponiveis/**",
                    "/bloqueios-agenda/**"
                ).hasAnyRole(admin, psi)

                // Financeiro: ADMIN e RECEPCIONISTA
                it.requestMatchers("/pagamentos/**")
                    .hasAnyRole(admin, recep)

                // Operação: PSICOLOGO consulta, RECEPCIONISTA gerencia
                it.requestMatchers(
                    HttpMethod.GET,
                    "/agendas/**",
                    "/pacientes/**",
                    "/convenios/**",
                    "/salas-atendimento/**",
                    "/tipos-atendimento/**"
                ).hasAnyRole(admin, recep, psi)
                it.requestMatchers(
                    "/agendas/**",
                    "/pacientes/**",
                    "/convenios/**",
                    "/salas-atendimento/**",
                    "/tipos-atendimento/**"
                ).hasAnyRole(admin, recep)

                it.anyRequest().denyAll()
            }

            .exceptionHandling {
                it.authenticationEntryPoint { _, response, _ ->
                    writeError(response, 401, "Não autenticado")
                }
                it.accessDeniedHandler { _, response, _ ->
                    writeError(response, 403, "Acesso negado para este perfil")
                }
            }

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter::class.java
            )
            .build()
    }
}