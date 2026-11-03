package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.ProntuarioDtoRequest
import com.edu.psiclinbe.dto.response.ProntuarioDtoResponse
import com.edu.psiclinbe.mapper.ProntuarioMapper
import com.edu.psiclinbe.model.Paciente
import com.edu.psiclinbe.model.Prontuario
import com.edu.psiclinbe.model.enums.ProntuarioStatus
import com.edu.psiclinbe.repository.PacienteRepository
import com.edu.psiclinbe.repository.ProntuarioRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class ProntuarioServiceTest {
    private val repository = Mockito.mock(ProntuarioRepository::class.java)
    private val patientRepository = Mockito.mock(PacienteRepository::class.java)
    private val mapper = Mockito.mock(ProntuarioMapper::class.java)
    private val service = ProntuarioService(repository, patientRepository, mapper)
    private val patient = Mockito.mock(Paciente::class.java)
    private val prontuario = Mockito.mock(Prontuario::class.java)
    private val response = Mockito.mock(ProntuarioDtoResponse::class.java)
    private val request = ProntuarioDtoRequest(
        pacienteId = 1,
        dataAbertura = LocalDateTime.of(2026, 11, 3, 9, 0),
        queixaPrincipal = "Ansiedade",
        status = ProntuarioStatus.ATIVO
    )

    @Test
    fun `lists records by patient status complaint and all`() {
        Mockito.`when`(repository.findByPacienteId(1L)).thenReturn(listOf(prontuario))
        Mockito.`when`(repository.findByPacienteIdAndStatus(1L, ProntuarioStatus.ATIVO))
            .thenReturn(listOf(prontuario))
        Mockito.`when`(repository.buscarPorQueixaPrincipal("Ansiedade"))
            .thenReturn(listOf(prontuario))
        Mockito.`when`(repository.findAll()).thenReturn(listOf(prontuario))
        Mockito.`when`(mapper.toResponse(prontuario)).thenReturn(response)

        assertEquals(listOf(response), service.buscarPorPaciente(1L))
        assertEquals(listOf(response), service.buscarPorPacienteEStatus(1L, ProntuarioStatus.ATIVO))
        assertEquals(listOf(response), service.buscarPorQueixaPrincipal("Ansiedade"))
        assertEquals(listOf(response), service.listarProntuarios())
    }

    @Test
    fun `finds record and rejects missing record`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(prontuario))
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        Mockito.`when`(mapper.toResponse(prontuario)).thenReturn(response)

        assertSame(response, service.buscarProntuario(1L))
        assertFailsWith<NoSuchElementException> { service.buscarProntuario(404L) }
    }

    @Test
    fun `creates and updates record using patient relationship`() {
        Mockito.`when`(patientRepository.findById(1L)).thenReturn(Optional.of(patient))
        Mockito.`when`(mapper.toModel(request.copy(id = null), patient)).thenReturn(prontuario)
        Mockito.`when`(mapper.toModel(request.copy(id = 10L), patient)).thenReturn(prontuario)
        Mockito.`when`(repository.save(prontuario)).thenReturn(prontuario)
        Mockito.`when`(repository.findById(10L)).thenReturn(Optional.of(prontuario))
        Mockito.`when`(mapper.toResponse(prontuario)).thenReturn(response)

        assertSame(response, service.cadastrarProntuario(request))
        assertSame(response, service.atualizarProntuario(10L, request))
    }

    @Test
    fun `rejects record creation when patient does not exist`() {
        Mockito.`when`(patientRepository.findById(1L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.cadastrarProntuario(request) }
        Mockito.verify(repository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `rejects update when record or patient is missing`() {
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { service.atualizarProntuario(404L, request) }

        Mockito.`when`(repository.findById(10L)).thenReturn(Optional.of(prontuario))
        Mockito.`when`(patientRepository.findById(1L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { service.atualizarProntuario(10L, request) }
    }

    @Test
    fun `soft deletes and permanently deletes record`() {
        Mockito.`when`(repository.findById(10L)).thenReturn(Optional.of(prontuario))
        Mockito.`when`(repository.save(prontuario)).thenReturn(prontuario)
        Mockito.`when`(repository.excluirPermanentementePorId(10L)).thenReturn(1)
        Mockito.`when`(repository.excluirPermanentementePorId(404L)).thenReturn(0)

        service.excluirProntuario(10L)
        Mockito.verify(prontuario).ativo = false
        service.excluirPermanentemente(10L)
        assertFailsWith<NoSuchElementException> { service.excluirPermanentemente(404L) }
    }
}