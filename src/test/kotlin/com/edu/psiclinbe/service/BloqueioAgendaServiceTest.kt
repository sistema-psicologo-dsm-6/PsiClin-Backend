package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.BloqueioAgendaDtoRequest
import com.edu.psiclinbe.dto.response.BloqueioAgendaDtoResponse
import com.edu.psiclinbe.mapper.BloqueioAgendaMapper
import com.edu.psiclinbe.model.BloqueioAgenda
import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.repository.BloqueioAgendaRepository
import com.edu.psiclinbe.repository.PsicologoRepository
import com.edu.psiclinbe.validation.ValidarDataInicioFim
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class BloqueioAgendaServiceTest {
    private val repository = Mockito.mock(BloqueioAgendaRepository::class.java)
    private val psychologistRepository = Mockito.mock(PsicologoRepository::class.java)
    private val mapper = Mockito.mock(BloqueioAgendaMapper::class.java)
    private val service = BloqueioAgendaService(
        repository, psychologistRepository, mapper, ValidarDataInicioFim()
    )
    private val psychologist = Mockito.mock(Psicologo::class.java)
    private val bloqueio = Mockito.mock(BloqueioAgenda::class.java)
    private val response = Mockito.mock(BloqueioAgendaDtoResponse::class.java)
    private val inicio = LocalDateTime.of(2026, 11, 3, 10, 0)
    private val fim = LocalDateTime.of(2026, 11, 3, 11, 0)
    private val request = BloqueioAgendaDtoRequest(0, 2, inicio, fim, "Indisponível")

    @Test
    fun `lists blocks by active state motive psychologist and period`() {
        Mockito.`when`(repository.findAllByAtivoTrue()).thenReturn(listOf(bloqueio))
        Mockito.`when`(repository.buscarPorMotivo("Indisponível"))
            .thenReturn(listOf(bloqueio))
        Mockito.`when`(repository.findByPsicologoIdAndAtivoTrue(2L))
            .thenReturn(listOf(bloqueio))
        Mockito.`when`(repository.findByDataHoraInicioBetween(inicio, fim))
            .thenReturn(listOf(bloqueio))
        Mockito.`when`(mapper.toResponse(bloqueio)).thenReturn(response)

        assertEquals(listOf(response), service.listarTodos())
        assertEquals(listOf(response), service.buscarPorMotivo("Indisponível"))
        assertEquals(listOf(response), service.buscarPorPsicologo(2L))
        assertEquals(listOf(response), service.buscarPorPeriodo(inicio, fim))
    }

    @Test
    fun `finds active block and rejects missing block`() {
        Mockito.`when`(repository.findByIdAndAtivoTrue(1L)).thenReturn(bloqueio)
        Mockito.`when`(repository.findByIdAndAtivoTrue(404L)).thenReturn(null)
        Mockito.`when`(mapper.toResponse(bloqueio)).thenReturn(response)

        assertSame(response, service.buscarPorId(1L))
        assertFailsWith<NoSuchElementException> { service.buscarPorId(404L) }
    }

    @Test
    fun `creates block for existing psychologist`() {
        Mockito.`when`(psychologist.id).thenReturn(2L)
        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.of(psychologist))
        Mockito.`when`(repository.findByPsicologoIdAndAtivoTrue(2L)).thenReturn(emptyList())
        Mockito.`when`(mapper.toModel(request, psychologist)).thenReturn(bloqueio)
        Mockito.`when`(repository.save(bloqueio)).thenReturn(bloqueio)
        Mockito.`when`(mapper.toResponse(bloqueio)).thenReturn(response)

        assertSame(response, service.salvar(request))
    }

    @Test
    fun `rejects invalid interval unknown psychologist and overlapping block`() {
        val invalidRequest = request.copy(
            dataHoraInicio = fim,
            dataHoraFim = inicio
        )
        assertFailsWith<IllegalArgumentException> { service.salvar(invalidRequest) }

        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { service.salvar(request) }

        val existing = BloqueioAgenda(
            id = 5,
            psicologo = psychologist,
            dataHoraInicio = inicio,
            dataHoraFim = fim,
            motivo = "Outro bloqueio"
        )
        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.of(psychologist))
        Mockito.`when`(psychologist.id).thenReturn(2L)
        Mockito.`when`(repository.findByPsicologoIdAndAtivoTrue(2L)).thenReturn(listOf(existing))

        assertFailsWith<IllegalArgumentException> { service.salvar(request) }
        Mockito.verify(repository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `updates ignores its own interval and deactivates block`() {
        Mockito.`when`(bloqueio.id).thenReturn(1L)
        Mockito.`when`(repository.findByIdAndAtivoTrue(1L)).thenReturn(bloqueio)
        Mockito.`when`(psychologist.id).thenReturn(2L)
        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.of(psychologist))
        Mockito.`when`(repository.findByPsicologoIdAndAtivoTrue(2L)).thenReturn(listOf(bloqueio))
        Mockito.`when`(repository.save(bloqueio)).thenReturn(bloqueio)
        Mockito.`when`(mapper.toResponse(bloqueio)).thenReturn(response)

        assertSame(response, service.atualizar(1L, request))
        service.deletar(1L)
        Mockito.verify(bloqueio).ativo = false
        Mockito.verify(repository, Mockito.times(2)).save(bloqueio)
    }

    @Test
    fun `rejects update when block is missing or overlaps another block`() {
        Mockito.`when`(repository.findByIdAndAtivoTrue(404L)).thenReturn(null)
        assertFailsWith<NoSuchElementException> { service.atualizar(404L, request) }

        val existing = BloqueioAgenda(
            id = 5,
            psicologo = psychologist,
            dataHoraInicio = inicio,
            dataHoraFim = fim,
            motivo = "Outro bloqueio"
        )
        Mockito.`when`(repository.findByIdAndAtivoTrue(1L)).thenReturn(bloqueio)
        Mockito.`when`(psychologist.id).thenReturn(2L)
        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.of(psychologist))
        Mockito.`when`(repository.findByPsicologoIdAndAtivoTrue(2L)).thenReturn(listOf(existing))

        assertFailsWith<IllegalArgumentException> { service.atualizar(1L, request) }
    }

    @Test
    fun `permanently deletes existing block and rejects missing block`() {
        Mockito.`when`(repository.existsById(1L)).thenReturn(true)
        Mockito.`when`(repository.existsById(404L)).thenReturn(false)

        service.excluirPermanentemente(1L)
        Mockito.verify(repository).deleteById(1L)
        assertFailsWith<NoSuchElementException> { service.excluirPermanentemente(404L) }
    }
}