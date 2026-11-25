package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.PacienteDtoRequest
import com.edu.psiclinbe.dto.response.PacienteDtoResponse
import com.edu.psiclinbe.mapper.PacienteMapper
import com.edu.psiclinbe.model.Paciente
import com.edu.psiclinbe.repository.PacienteRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDate
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class PacienteServiceTest {
    private val repository = Mockito.mock(PacienteRepository::class.java)
    private val mapper = Mockito.mock(PacienteMapper::class.java)
    private val service = PacienteService(repository, mapper)
    private val paciente = Mockito.mock(Paciente::class.java)
    private val response = Mockito.mock(PacienteDtoResponse::class.java)
    private val request = PacienteDtoRequest(
        nome = "Ana Silva",
        cpf = "12345678900",
        dataNascimento = LocalDate.of(1990, 1, 1),
        email = "ana@example.com",
        telefone = "11999990000",
        endereco = "Rua Central, 10",
        observacoes = ""
    )

    @BeforeEach
    fun markPatientActive() {
        Mockito.`when`(paciente.ativo).thenReturn(true)
    }

    @Test
    fun `lists patients by all active name`() {
        Mockito.`when`(repository.findAll()).thenReturn(listOf(paciente))
        Mockito.`when`(repository.findByAtivoTrue()).thenReturn(listOf(paciente))
        Mockito.`when`(repository.buscarPorNomeParcial("Ana"))
            .thenReturn(listOf(paciente))
        Mockito.`when`(mapper.toResponse(paciente)).thenReturn(response)

        assertEquals(listOf(response), service.listarTodos())
        assertEquals(listOf(response), service.listarAtivos())
        assertEquals(listOf(response), service.buscarPorNome("Ana"))
    }

    @Test
    fun `hides inactive patients from receptionists and psychologists`() {
        val activePatient = Mockito.mock(Paciente::class.java)
        val inactivePatient = Mockito.mock(Paciente::class.java)
        val inactiveResponse = Mockito.mock(PacienteDtoResponse::class.java)
        Mockito.`when`(activePatient.ativo).thenReturn(true)
        Mockito.`when`(inactivePatient.ativo).thenReturn(false)
        Mockito.`when`(repository.findAll()).thenReturn(listOf(activePatient, inactivePatient))
        Mockito.`when`(repository.findById(2L)).thenReturn(Optional.of(inactivePatient))
        Mockito.`when`(mapper.toResponse(activePatient)).thenReturn(response)
        Mockito.`when`(mapper.toResponse(inactivePatient)).thenReturn(inactiveResponse)

        withRole("ROLE_RECEPCIONISTA") {
            assertEquals(listOf(response), service.listarTodos())
            assertFailsWith<NoSuchElementException> { service.buscarPorId(2L) }
        }
        withRole("ROLE_PSICOLOGO") {
            assertEquals(listOf(response), service.listarTodos())
            assertFailsWith<NoSuchElementException> { service.buscarPorId(2L) }
        }
        withRole("ROLE_ADMIN") {
            assertEquals(listOf(response, inactiveResponse), service.listarTodos())
            assertSame(inactiveResponse, service.buscarPorId(2L))
        }
    }

    @Test
    fun `finds patient by id cpf and email`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(paciente))
        Mockito.`when`(repository.findByCpf(request.cpf)).thenReturn(paciente)
        Mockito.`when`(repository.findByEmail(request.email)).thenReturn(paciente)
        Mockito.`when`(mapper.toResponse(paciente)).thenReturn(response)

        assertSame(response, service.buscarPorId(1L))
        assertSame(response, service.buscarPorCpf(request.cpf))
        assertSame(response, service.buscarPorEmail(request.email))
    }

    @Test
    fun `throws when patient lookup misses`() {
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        Mockito.`when`(repository.findByCpf("missing")).thenReturn(null)
        Mockito.`when`(repository.findByEmail("missing@example.com")).thenReturn(null)

        assertFailsWith<NoSuchElementException> { service.buscarPorId(404L) }
        assertFailsWith<NoSuchElementException> { service.buscarPorCpf("missing") }
        assertFailsWith<NoSuchElementException> {
            service.buscarPorEmail("missing@example.com")
        }
    }

    @Test
    fun `saves patient with unique identifiers`() {
        Mockito.`when`(repository.findByCpf(request.cpf)).thenReturn(null)
        Mockito.`when`(repository.findByEmail(request.email)).thenReturn(null)
        Mockito.`when`(mapper.toModel(request)).thenReturn(paciente)
        Mockito.`when`(repository.save(paciente)).thenReturn(paciente)
        Mockito.`when`(mapper.toResponse(paciente)).thenReturn(response)

        assertSame(response, service.salvar(request))
    }

    @Test
    fun `rejects duplicate patient cpf or email`() {
        Mockito.`when`(repository.findByCpf(request.cpf)).thenReturn(paciente)
        assertFailsWith<IllegalArgumentException> { service.salvar(request) }

        Mockito.`when`(repository.findByCpf(request.cpf)).thenReturn(null)
        Mockito.`when`(repository.findByEmail(request.email)).thenReturn(paciente)
        assertFailsWith<IllegalArgumentException> { service.salvar(request) }
        Mockito.verify(repository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `updates patient while allowing its own cpf and email`() {
        Mockito.`when`(paciente.id).thenReturn(1L)
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(paciente))
        Mockito.`when`(repository.findByCpf(request.cpf)).thenReturn(paciente)
        Mockito.`when`(repository.findByEmail(request.email)).thenReturn(paciente)
        Mockito.`when`(repository.save(paciente)).thenReturn(paciente)
        Mockito.`when`(mapper.toResponse(paciente)).thenReturn(response)

        assertSame(response, service.atualizar(1L, request))
        Mockito.verify(paciente).nome = request.nome
    }

    @Test
    fun `rejects patient update when cpf or email belongs to another patient`() {
        Mockito.`when`(paciente.id).thenReturn(2L)
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(paciente))
        Mockito.`when`(repository.findByCpf(request.cpf)).thenReturn(paciente)

        assertFailsWith<IllegalArgumentException> { service.atualizar(1L, request) }

        Mockito.`when`(repository.findByCpf(request.cpf)).thenReturn(null)
        Mockito.`when`(repository.findByEmail(request.email)).thenReturn(paciente)
        assertFailsWith<IllegalArgumentException> { service.atualizar(1L, request) }
    }

    @Test
    fun `rejects update for missing patient`() {
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.atualizar(404L, request) }
    }

    @Test
    fun `inactivates and deletes patient`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(paciente))
        Mockito.`when`(repository.save(paciente)).thenReturn(paciente)
        Mockito.`when`(repository.existsById(1L)).thenReturn(true)
        Mockito.`when`(mapper.toResponse(paciente)).thenReturn(response)

        assertSame(response, service.inativar(1L))
        service.deletar(1L)
        Mockito.verify(paciente).ativo = false
        Mockito.verify(repository).deleteById(1L)
    }

    @Test
    fun `rejects deletion of missing patient`() {
        Mockito.`when`(repository.existsById(404L)).thenReturn(false)

        assertFailsWith<NoSuchElementException> { service.deletar(404L) }
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { service.inativar(404L) }
    }

    private fun withRole(role: String, action: () -> Unit) {
        val previousContext = SecurityContextHolder.getContext()
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = UsernamePasswordAuthenticationToken(
            "test-user",
            "",
            listOf(SimpleGrantedAuthority(role))
        )
        SecurityContextHolder.setContext(context)
        try {
            action()
        } finally {
            SecurityContextHolder.setContext(previousContext)
        }
    }
}