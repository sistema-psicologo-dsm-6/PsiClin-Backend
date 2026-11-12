package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.HorarioDisponivelDtoRequest
import com.edu.psiclinbe.dto.response.HorarioDisponivelDtoResponse
import com.edu.psiclinbe.mapper.HorarioDisponivelMapper
import com.edu.psiclinbe.model.HorarioDisponivel
import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.model.enums.DiaSemana
import com.edu.psiclinbe.repository.HorarioDisponivelRepository
import com.edu.psiclinbe.repository.PsicologoRepository
import com.edu.psiclinbe.validation.ValidarDataInicioFim
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalTime
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class HorarioDisponivelServiceTest {
    private val repository = Mockito.mock(HorarioDisponivelRepository::class.java)
    private val psychologistRepository = Mockito.mock(PsicologoRepository::class.java)
    private val mapper = Mockito.mock(HorarioDisponivelMapper::class.java)
    private val service = HorarioDisponivelService(
        repository, psychologistRepository, mapper, ValidarDataInicioFim()
    )
    private val psychologist = Mockito.mock(Psicologo::class.java)
    private val horario = Mockito.mock(HorarioDisponivel::class.java)
    private val response = Mockito.mock(HorarioDisponivelDtoResponse::class.java)
    private val request = HorarioDisponivelDtoRequest(
        psicologoId = 2L,
        diaSemana = "TERCA",
        horaInicio = LocalTime.of(9, 0),
        horaFim = LocalTime.of(12, 0),
        descricao = "Manhã"
    )

    @Test
    fun `lists schedules by all description psychologist and weekday`() {
        Mockito.`when`(repository.findAll()).thenReturn(listOf(horario))
        Mockito.`when`(repository.buscarPorDescricao("Manhã")).thenReturn(listOf(horario))
        Mockito.`when`(repository.findByPsicologoId(2L)).thenReturn(listOf(horario))
        Mockito.`when`(repository.findByPsicologoIdAndDiaSemana(2L, DiaSemana.TERCA))
            .thenReturn(listOf(horario))
        Mockito.`when`(mapper.toResponse(horario)).thenReturn(response)

        assertEquals(listOf(response), service.listarTodos())
        assertEquals(listOf(response), service.buscarPorDescricao("Manhã"))
        assertEquals(listOf(response), service.buscarPorPsicologo(2L))
        assertEquals(listOf(response), service.buscarPorPsicologoEDia(2L, "TERCA"))
    }

    @Test
    fun `rejects invalid weekday and missing schedule`() {
        assertFailsWith<IllegalArgumentException> {
            service.buscarPorPsicologoEDia(2L, "INVALIDO")
        }
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(horario))
        Mockito.`when`(mapper.toResponse(horario)).thenReturn(response)
        assertSame(response, service.buscarPorId(1L))
        assertFailsWith<NoSuchElementException> { service.buscarPorId(404L) }
    }

    @Test
    fun `creates schedule for psychologist without overlapping schedule`() {
        Mockito.`when`(psychologist.id).thenReturn(2L)
        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.of(psychologist))
        Mockito.`when`(repository.findByPsicologoIdAndDiaSemana(2L, DiaSemana.TERCA))
            .thenReturn(emptyList())
        Mockito.`when`(mapper.toModel(request, psychologist)).thenReturn(horario)
        Mockito.`when`(repository.save(horario)).thenReturn(horario)
        Mockito.`when`(mapper.toResponse(horario)).thenReturn(response)

        assertSame(response, service.salvar(request))
    }

    @Test
    fun `rejects invalid interval missing psychologist and overlap`() {
        val invalidRequest = request.copy(
            horaInicio = LocalTime.of(12, 0),
            horaFim = LocalTime.of(9, 0)
        )
        assertFailsWith<IllegalArgumentException> { service.salvar(invalidRequest) }

        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { service.salvar(request) }

        val existing = Mockito.mock(HorarioDisponivel::class.java)
        Mockito.`when`(existing.id).thenReturn(5L)
        Mockito.`when`(existing.horaInicio).thenReturn(LocalTime.of(10, 0))
        Mockito.`when`(existing.horaFim).thenReturn(LocalTime.of(11, 0))
        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.of(psychologist))
        Mockito.`when`(psychologist.id).thenReturn(2L)
        Mockito.`when`(repository.findByPsicologoIdAndDiaSemana(2L, DiaSemana.TERCA))
            .thenReturn(listOf(existing))

        assertFailsWith<IllegalArgumentException> { service.salvar(request) }
        Mockito.verify(repository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `updates schedule ignores itself and deactivates it`() {
        Mockito.`when`(horario.id).thenReturn(1L)
        Mockito.`when`(repository.findById(1L)).thenReturn(Optional.of(horario))
        Mockito.`when`(psychologist.id).thenReturn(2L)
        Mockito.`when`(psychologistRepository.findById(2L)).thenReturn(Optional.of(psychologist))
        Mockito.`when`(repository.findByPsicologoIdAndDiaSemana(2L, DiaSemana.TERCA))
            .thenReturn(listOf(horario))
        Mockito.`when`(repository.save(horario)).thenReturn(horario)
        Mockito.`when`(mapper.toResponse(horario)).thenReturn(response)

        assertSame(response, service.atualizar(1L, request))
        Mockito.verify(horario).horaInicio = request.horaInicio
        service.deletar(1L)
        Mockito.verify(horario).ativo = false
    }

    @Test
    fun `rejects schedule update and soft delete when schedule is missing`() {
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.atualizar(404L, request) }
        assertFailsWith<NoSuchElementException> { service.deletar(404L) }
    }

    @Test
    fun `permanently deletes existing schedule and rejects missing id`() {
        Mockito.`when`(repository.excluirPermanentementePorId(1L)).thenReturn(1)
        Mockito.`when`(repository.excluirPermanentementePorId(404L)).thenReturn(0)

        service.excluirPermanentemente(1L)
        assertFailsWith<NoSuchElementException> { service.excluirPermanentemente(404L) }
    }
}