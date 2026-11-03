package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.PsicologoDtoRequest
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse
import com.edu.psiclinbe.mapper.PsicologoMapper
import com.edu.psiclinbe.model.Funcionario
import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.model.enums.Role
import com.edu.psiclinbe.repository.FuncionarioRepository
import com.edu.psiclinbe.repository.PsicologoRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class PsicologoServiceTest {
    private val repository = Mockito.mock(PsicologoRepository::class.java)
    private val funcionarioRepository = Mockito.mock(FuncionarioRepository::class.java)
    private val mapper = Mockito.mock(PsicologoMapper::class.java)
    private val encoder = Mockito.mock(PasswordEncoder::class.java)
    private val service = PsicologoService(repository, funcionarioRepository, mapper, encoder)
    private val psicologo = Mockito.mock(Psicologo::class.java)
    private val funcionario = Mockito.mock(Funcionario::class.java)
    private val response = Mockito.mock(PsicologoDtoResponse::class.java)
    private val request = PsicologoDtoRequest(
        nome = "Dra. Ana",
        cpf = "12345678900",
        email = "ana@example.com",
        telefone = "11999990000",
        senha = "senha-segura",
        crp = "CRP123",
        especialidade = "Clínica"
    )

    @Test
    fun `lists and searches psychologists`() {
        Mockito.`when`(repository.findAll()).thenReturn(listOf(psicologo))
        Mockito.`when`(repository.findByAtivoTrue()).thenReturn(listOf(psicologo))
        Mockito.`when`(repository.findByEspecialidadeContainingIgnoreCase("Clínica"))
            .thenReturn(listOf(psicologo))
        Mockito.`when`(mapper.toResponse(psicologo)).thenReturn(response)

        assertEquals(listOf(response), service.listarTodos())
        assertEquals(listOf(response), service.listarAtivos())
        assertEquals(listOf(response), service.buscarPorEspecialidade("Clínica"))
    }

    @Test
    fun `finds psychologist by id and crp`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(psicologo))
        Mockito.`when`(repository.findByCrp(request.crp)).thenReturn(psicologo)
        Mockito.`when`(mapper.toResponse(psicologo)).thenReturn(response)

        assertSame(response, service.buscarPorId(1L))
        assertSame(response, service.buscarPorCrp(request.crp))
    }

    @Test
    fun `rejects missing psychologist lookup`() {
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        Mockito.`when`(repository.findByCrp("missing")).thenReturn(null)

        assertFailsWith<NoSuchElementException> { service.buscarPorId(404L) }
        assertFailsWith<NoSuchElementException> { service.buscarPorCrp("missing") }
    }

    @Test
    fun `creates psychologist with encoded password and role`() {
        Mockito.`when`(funcionarioRepository.findByEmail(request.email)).thenReturn(null)
        Mockito.`when`(funcionarioRepository.findByCpf(request.cpf)).thenReturn(null)
        Mockito.`when`(repository.findByCrp(request.crp)).thenReturn(null)
        Mockito.`when`(mapper.toModel(request)).thenReturn(psicologo)
        Mockito.`when`(encoder.encode(request.senha)).thenReturn("hash")
        Mockito.`when`(repository.save(psicologo)).thenReturn(psicologo)
        Mockito.`when`(mapper.toResponse(psicologo)).thenReturn(response)

        assertSame(response, service.salvar(request))
        Mockito.verify(psicologo).senha = "hash"
        Mockito.verify(psicologo).role = Role.PSICOLOGO
    }

    @Test
    fun `rejects duplicate psychologist email cpf or crp`() {
        Mockito.`when`(funcionarioRepository.findByEmail(request.email)).thenReturn(funcionario)
        assertFailsWith<IllegalArgumentException> { service.salvar(request) }

        Mockito.`when`(funcionarioRepository.findByEmail(request.email)).thenReturn(null)
        Mockito.`when`(funcionarioRepository.findByCpf(request.cpf)).thenReturn(funcionario)
        assertFailsWith<IllegalArgumentException> { service.salvar(request) }

        Mockito.`when`(funcionarioRepository.findByCpf(request.cpf)).thenReturn(null)
        Mockito.`when`(repository.findByCrp(request.crp)).thenReturn(psicologo)
        assertFailsWith<IllegalArgumentException> { service.salvar(request) }
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Psicologo::class.java))
    }

    @Test
    fun `updates psychologist while ignoring own identifiers`() {
        Mockito.`when`(psicologo.id).thenReturn(1L)
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(psicologo))
        Mockito.`when`(funcionarioRepository.findByEmail(request.email)).thenReturn(funcionario)
        Mockito.`when`(funcionario.id).thenReturn(1L)
        Mockito.`when`(funcionarioRepository.findByCpf(request.cpf)).thenReturn(funcionario)
        Mockito.`when`(repository.findByCrp(request.crp)).thenReturn(psicologo)
        Mockito.`when`(repository.save(psicologo)).thenReturn(psicologo)
        Mockito.`when`(mapper.toResponse(psicologo)).thenReturn(response)

        assertSame(response, service.atualizar(1L, request))
        Mockito.verify(psicologo).especialidade = request.especialidade
    }

    @Test
    fun `rejects update for missing psychologist`() {
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.atualizar(404L, request) }
    }

    @Test
    fun `inactivates and deletes psychologist`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(psicologo))
        Mockito.`when`(repository.save(psicologo)).thenReturn(psicologo)
        Mockito.`when`(repository.existsById(1L)).thenReturn(true)
        Mockito.`when`(mapper.toResponse(psicologo)).thenReturn(response)

        assertSame(response, service.inativar(1L))
        service.deletar(1L)
        Mockito.verify(psicologo).ativo = false
        Mockito.verify(repository).deleteById(1L)
    }

    @Test
    fun `rejects inactivation for missing psychologist`() {
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.inativar(404L) }
    }

    @Test
    fun `rejects delete for missing psychologist`() {
        Mockito.`when`(repository.existsById(404L)).thenReturn(false)

        assertFailsWith<NoSuchElementException> { service.deletar(404L) }
    }
}