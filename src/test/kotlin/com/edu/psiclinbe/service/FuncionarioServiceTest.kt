package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.FuncionarioDtoRequest
import com.edu.psiclinbe.dto.response.FuncionarioDtoResponse
import com.edu.psiclinbe.mapper.FuncionarioMapper
import com.edu.psiclinbe.model.Funcionario
import com.edu.psiclinbe.model.enums.Role
import com.edu.psiclinbe.repository.FuncionarioRepository
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class FuncionarioServiceTest {
    private val repository = Mockito.mock(FuncionarioRepository::class.java)
    private val mapper = Mockito.mock(FuncionarioMapper::class.java)
    private val encoder = Mockito.mock(PasswordEncoder::class.java)
    private val service = FuncionarioService(repository, mapper, encoder)
    private val funcionario = Mockito.mock(Funcionario::class.java)
    private val response = Mockito.mock(FuncionarioDtoResponse::class.java)
    private val request = FuncionarioDtoRequest(
        nome = "Admin",
        cpf = "12345678900",
        email = "admin@example.com",
        telefone = "11999990000",
        senha = "senha-segura"
    )

    @Test
    fun `lists and searches employees`() {
        val recepcionista = Mockito.mock(Funcionario::class.java)
        Mockito.`when`(funcionario.role).thenReturn(Role.ADMIN)
        Mockito.`when`(recepcionista.role).thenReturn(Role.RECEPCIONISTA)
        Mockito.`when`(repository.findAll()).thenReturn(listOf(funcionario, recepcionista))
        Mockito.`when`(repository.findByAtivoTrue()).thenReturn(listOf(funcionario, recepcionista))
        Mockito.`when`(repository.buscarPorNomeParcial("Admin"))
            .thenReturn(listOf(funcionario, recepcionista))
        Mockito.`when`(mapper.toResponse(funcionario)).thenReturn(response)

        assertEquals(listOf(response), service.listarTodos())
        assertEquals(listOf(response), service.listarAtivos())
        assertEquals(listOf(response), service.buscarPorNome("Admin"))
        Mockito.verify(mapper, Mockito.never()).toResponse(recepcionista)
    }

    @Test
    fun `finds employee by id email and cpf`() {
        Mockito.`when`(funcionario.role).thenReturn(Role.ADMIN)
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(funcionario))
        Mockito.`when`(repository.findByEmail(request.email)).thenReturn(funcionario)
        Mockito.`when`(repository.findByCpf(request.cpf)).thenReturn(funcionario)
        Mockito.`when`(mapper.toResponse(funcionario)).thenReturn(response)

        assertSame(response, service.buscarPorId(1L))
        assertSame(response, service.buscarPorEmail(request.email))
        assertSame(response, service.buscarPorCpf(request.cpf))
    }

    @Test
    fun `hides non-admin employees from direct employee lookups`() {
        val recepcionista = Mockito.mock(Funcionario::class.java)
        Mockito.`when`(recepcionista.role).thenReturn(Role.RECEPCIONISTA)
        Mockito.`when`(repository.findById(2L)).thenReturn(Optional.of(recepcionista))
        Mockito.`when`(repository.findByEmail(request.email)).thenReturn(recepcionista)
        Mockito.`when`(repository.findByCpf(request.cpf)).thenReturn(recepcionista)

        assertFailsWith<NoSuchElementException> { service.buscarPorId(2L) }
        assertFailsWith<NoSuchElementException> { service.buscarPorEmail(request.email) }
        assertFailsWith<NoSuchElementException> { service.buscarPorCpf(request.cpf) }
    }

    @Test
    fun `rejects missing employee lookups`() {
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        Mockito.`when`(repository.findByEmail("missing@example.com")).thenReturn(null)
        Mockito.`when`(repository.findByCpf("missing")).thenReturn(null)

        assertFailsWith<NoSuchElementException> { service.buscarPorId(404L) }
        assertFailsWith<NoSuchElementException> { service.buscarPorEmail("missing@example.com") }
        assertFailsWith<NoSuchElementException> { service.buscarPorCpf("missing") }
    }

    @Test
    fun `creates employee with encoded password`() {
        Mockito.`when`(repository.findByEmailAndCpf(request.email, request.cpf))
            .thenReturn(null)
        Mockito.`when`(encoder.encode(request.senha)).thenReturn("hash")
        Mockito.`when`(repository.save(Mockito.any(Funcionario::class.java) ?: funcionario))
            .thenAnswer { it.getArgument(0) }
        Mockito.`when`(mapper.toResponse(Mockito.any(Funcionario::class.java) ?: funcionario))
            .thenReturn(response)

        assertSame(response, service.salvar(request))
        val captor = ArgumentCaptor.forClass(Funcionario::class.java)
        Mockito.verify(repository).save(captor.capture())
        assertEquals("hash", captor.value.senha)
        assertEquals(Role.RECEPCIONISTA, captor.value.role)
    }

    @Test
    fun `rejects duplicate email and cpf pair`() {
        Mockito.`when`(repository.findByEmailAndCpf(request.email, request.cpf))
            .thenReturn(funcionario)

        assertFailsWith<IllegalArgumentException> { service.salvar(request) }
        Mockito.verify(repository, Mockito.never())
            .save(Mockito.any(Funcionario::class.java) ?: funcionario)
    }

    @Test
    fun `updates employee without changing role and only rehashes nonblank password`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(funcionario))
        Mockito.`when`(repository.save(funcionario)).thenReturn(funcionario)
        Mockito.`when`(mapper.toResponse(funcionario)).thenReturn(response)
        Mockito.`when`(encoder.encode("nova-senha")).thenReturn("hash-novo")

        assertSame(response, service.atualizar(1L, request.copy(senha = "", role = Role.ADMIN)))
        Mockito.verify(encoder, Mockito.never()).encode("")
        Mockito.verify(funcionario, Mockito.never()).role = Role.ADMIN
        assertSame(response, service.atualizar(1L, request.copy(senha = "nova-senha")))
        Mockito.verify(encoder).encode("nova-senha")
    }

    @Test
    fun `rejects update for missing employee`() {
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.atualizar(404L, request) }
    }

    @Test
    fun `inactivates and deletes employee`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(funcionario))
        Mockito.`when`(repository.save(funcionario)).thenReturn(funcionario)
        Mockito.`when`(repository.existsById(1L)).thenReturn(true)
        Mockito.`when`(mapper.toResponse(funcionario)).thenReturn(response)

        assertSame(response, service.inativar(1L))
        service.deletar(1L)
        Mockito.verify(funcionario).ativo = false
        Mockito.verify(repository).deleteById(1L)
    }

    @Test
    fun `rejects deleting missing employee`() {
        Mockito.`when`(repository.existsById(404L)).thenReturn(false)

        assertFailsWith<NoSuchElementException> { service.deletar(404L) }
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { service.inativar(404L) }
    }

    @Test
    fun `creates initial administrator only on empty database`() {
        Mockito.`when`(repository.count()).thenReturn(0)
        Mockito.`when`(repository.findByEmailAndCpf(request.email, request.cpf)).thenReturn(null)
        Mockito.`when`(encoder.encode(request.senha)).thenReturn("hash")
        Mockito.`when`(repository.save(Mockito.any(Funcionario::class.java) ?: funcionario))
            .thenAnswer { it.getArgument(0) }
        Mockito.`when`(mapper.toResponse(Mockito.any(Funcionario::class.java) ?: funcionario)).thenReturn(response)

        assertSame(response, service.criarAdminInicial(request.copy(role = Role.RECEPCIONISTA)))

        val captor = ArgumentCaptor.forClass(Funcionario::class.java)
        Mockito.verify(repository).save(captor.capture())
        assertEquals(Role.ADMIN, captor.value.role)
        assertEquals(true, captor.value.ativo)
    }

    @Test
    fun `rejects initial administrator when a employee already exists`() {
        Mockito.`when`(repository.count()).thenReturn(1)

        val error = assertFailsWith<org.springframework.web.server.ResponseStatusException> {
            service.criarAdminInicial(request)
        }

        assertEquals(org.springframework.http.HttpStatus.CONFLICT, error.statusCode)
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Funcionario::class.java) ?: funcionario)
    }
}