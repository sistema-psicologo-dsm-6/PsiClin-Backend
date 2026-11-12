package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.EvolucaoClinicaDtoRequest
import com.edu.psiclinbe.dto.response.EvolucaoClinicaDtoResponse
import com.edu.psiclinbe.mapper.EvolucaoClinicaMapper
import com.edu.psiclinbe.model.Agenda
import com.edu.psiclinbe.model.EvolucaoClinica
import com.edu.psiclinbe.model.Prontuario
import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.repository.AgendaRepository
import com.edu.psiclinbe.repository.EvolucaoClinicaRepository
import com.edu.psiclinbe.repository.ProntuarioRepository
import com.edu.psiclinbe.repository.PsicologoRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class EvolucaoClinicaServiceTest {
    private val repository = Mockito.mock(EvolucaoClinicaRepository::class.java)
    private val prontuarioRepository = Mockito.mock(ProntuarioRepository::class.java)
    private val psychologistRepository = Mockito.mock(PsicologoRepository::class.java)
    private val agendaRepository = Mockito.mock(AgendaRepository::class.java)
    private val mapper = Mockito.mock(EvolucaoClinicaMapper::class.java)
    private val service = EvolucaoClinicaService(
        repository, prontuarioRepository, psychologistRepository, agendaRepository, mapper
    )
    private val prontuario = Mockito.mock(Prontuario::class.java)
    private val psychologist = Mockito.mock(Psicologo::class.java)
    private val agenda = Mockito.mock(Agenda::class.java)
    private val evolucao = Mockito.mock(EvolucaoClinica::class.java)
    private val response = Mockito.mock(EvolucaoClinicaDtoResponse::class.java)
    private val request = EvolucaoClinicaDtoRequest(
        prontuarioId = 1,
        psicologoId = 2,
        agendaId = 3,
        dataRegistro = LocalDateTime.of(2026, 11, 3, 10, 0),
        descricao = "Evolução clínica"
    )

    private fun stubRelationships() {
        Mockito.`when`(prontuarioRepository.findById(1L)).thenReturn(Optional.of(prontuario))
        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.of(psychologist))
        Mockito.`when`(agendaRepository.findById(3L)).thenReturn(Optional.of(agenda))
    }

    @Test
    fun `lists evolutions by all description record psychologist and agenda`() {
        Mockito.`when`(repository.findAll()).thenReturn(listOf(evolucao))
        Mockito.`when`(repository.buscarPorDescricao("clínica")).thenReturn(listOf(evolucao))
        Mockito.`when`(repository.findByProntuarioId(1L)).thenReturn(listOf(evolucao))
        Mockito.`when`(repository.findByPsicologoId(2L)).thenReturn(listOf(evolucao))
        Mockito.`when`(repository.findByAgendaId(3L)).thenReturn(listOf(evolucao))
        Mockito.`when`(mapper.toResponse(evolucao)).thenReturn(response)

        assertEquals(listOf(response), service.listarTodos())
        assertEquals(listOf(response), service.buscarPorDescricao("clínica"))
        assertEquals(listOf(response), service.buscarPorProntuario(1L))
        assertEquals(listOf(response), service.buscarPorPsicologo(2L))
        assertEquals(listOf(response), service.buscarPorAgenda(3L))
    }

    @Test
    fun `finds evolution by id and rejects missing id`() {
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(evolucao))
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        Mockito.`when`(mapper.toResponse(evolucao)).thenReturn(response)

        assertSame(response, service.buscarPorId(1L))
        assertFailsWith<NoSuchElementException> { service.buscarPorId(404L) }
    }

    @Test
    fun `creates evolution with all required relationships`() {
        stubRelationships()
        Mockito.`when`(mapper.toModel(request, prontuario, psychologist, agenda))
            .thenReturn(evolucao)
        Mockito.`when`(repository.save(evolucao)).thenReturn(evolucao)
        Mockito.`when`(mapper.toResponse(evolucao)).thenReturn(response)

        assertSame(response, service.salvar(request))
    }

    @Test
    fun `rejects evolution when related record is missing`() {
        Mockito.`when`(prontuarioRepository.findById(1L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.salvar(request) }
        Mockito.verify(repository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `rejects evolution when psychologist or agenda is missing`() {
        Mockito.`when`(prontuarioRepository.findById(1L)).thenReturn(Optional.of(prontuario))
        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { service.salvar(request) }

        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.of(psychologist))
        Mockito.`when`(agendaRepository.findById(3L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { service.salvar(request) }
    }

    @Test
    fun `updates evolution fields`() {
        stubRelationships()
        Mockito.`when`(repository.findById(10L)).thenReturn(Optional.of(evolucao))
        Mockito.`when`(repository.save(evolucao)).thenReturn(evolucao)
        Mockito.`when`(mapper.toResponse(evolucao)).thenReturn(response)

        assertSame(response, service.atualizar(10L, request))
        Mockito.verify(evolucao).descricao = request.descricao
        Mockito.verify(evolucao).dataRegistro = request.dataRegistro
    }

    @Test
    fun `soft deletes evolution and supports permanent deletion`() {
        Mockito.`when`(repository.findById(10L)).thenReturn(Optional.of(evolucao))
        Mockito.`when`(repository.save(evolucao)).thenReturn(evolucao)
        Mockito.`when`(repository.excluirPermanentementePorId(10L)).thenReturn(1)
        Mockito.`when`(repository.excluirPermanentementePorId(404L)).thenReturn(0)

        service.deletar(10L)
        Mockito.verify(evolucao).ativo = false
        service.excluirPermanentemente(10L)
        assertFailsWith<NoSuchElementException> { service.excluirPermanentemente(404L) }
    }

    @Test
    fun `rejects update and delete when evolution does not exist`() {
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.atualizar(404L, request) }
        assertFailsWith<NoSuchElementException> { service.deletar(404L) }
    }
}