package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.PagamentoDtoRequest
import com.edu.psiclinbe.dto.response.PagamentoDtoResponse
import com.edu.psiclinbe.mapper.PagamentoMapper
import com.edu.psiclinbe.model.Agenda
import com.edu.psiclinbe.model.Convenio
import com.edu.psiclinbe.model.Paciente
import com.edu.psiclinbe.model.Pagamento
import com.edu.psiclinbe.model.enums.PagamentoStatus
import com.edu.psiclinbe.repository.AgendaRepository
import com.edu.psiclinbe.repository.ConvenioRepository
import com.edu.psiclinbe.repository.PacienteRepository
import com.edu.psiclinbe.repository.PagamentoRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class PagamentoServiceTest {
    private val repository = Mockito.mock(PagamentoRepository::class.java)
    private val agendaRepository = Mockito.mock(AgendaRepository::class.java)
    private val patientRepository = Mockito.mock(PacienteRepository::class.java)
    private val convenioRepository = Mockito.mock(ConvenioRepository::class.java)
    private val mapper = Mockito.mock(PagamentoMapper::class.java)
    private val service = PagamentoService(
        repository, agendaRepository, patientRepository, convenioRepository, mapper
    )
    private val agenda = Mockito.mock(Agenda::class.java)
    private val patient = Mockito.mock(Paciente::class.java)
    private val convenio = Mockito.mock(Convenio::class.java)
    private val pagamento = Mockito.mock(Pagamento::class.java)
    private val response = Mockito.mock(PagamentoDtoResponse::class.java)
    private val request = PagamentoDtoRequest(
        agendaId = 1,
        pacienteId = 2,
        convenioId = null,
        valorTotal = 100.0,
        valorConvenio = 0.0,
        valorPaciente = 100.0,
        formaPagamento = "Pix",
        dataPagamento = LocalDateTime.of(2026, 11, 3, 10, 0),
        status = PagamentoStatus.PENDENTE
    )

    @Test
    fun `lists and searches payments by all supported filters`() {
        Mockito.`when`(repository.findAll()).thenReturn(listOf(pagamento))
        Mockito.`when`(repository.findByPacienteId(2L)).thenReturn(listOf(pagamento))
        Mockito.`when`(repository.findByConvenioId(3L)).thenReturn(listOf(pagamento))
        Mockito.`when`(repository.findByStatus(PagamentoStatus.PENDENTE))
            .thenReturn(listOf(pagamento))
        Mockito.`when`(repository.buscarPorFormaPagamento("Pix"))
            .thenReturn(listOf(pagamento))
        Mockito.`when`(mapper.toResponse(pagamento)).thenReturn(response)

        assertEquals(listOf(response), service.lerTodos())
        assertEquals(listOf(response), service.buscarPorPaciente(2L))
        assertEquals(listOf(response), service.buscarPorConvenio(3L))
        assertEquals(listOf(response), service.buscarPorStatus(PagamentoStatus.PENDENTE))
        assertEquals(listOf(response), service.buscarPorFormaPagamento("Pix"))
    }

    @Test
    fun `finds payment by id and agenda`() {
        Mockito.`when`(repository.findById(10L)).thenReturn(Optional.of(pagamento))
        Mockito.`when`(repository.findByAgendaId(1L)).thenReturn(pagamento)
        Mockito.`when`(mapper.toResponse(pagamento)).thenReturn(response)

        assertSame(response, service.buscarPorId(10L))
        assertSame(response, service.buscarPorAgenda(1L))
    }

    @Test
    fun `rejects payment lookup when missing`() {
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())
        Mockito.`when`(repository.findByAgendaId(404L)).thenReturn(null)

        assertFailsWith<NoSuchElementException> { service.buscarPorId(404L) }
        assertFailsWith<NoSuchElementException> { service.buscarPorAgenda(404L) }
    }

    @Test
    fun `registers payment with required agenda and patient`() {
        Mockito.`when`(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda))
        Mockito.`when`(patientRepository.findById(2L)).thenReturn(Optional.of(patient))
        Mockito.`when`(mapper.toModel(request.copy(id = null), agenda, patient, null))
            .thenReturn(pagamento)
        Mockito.`when`(repository.save(pagamento)).thenReturn(pagamento)
        Mockito.`when`(mapper.toResponse(pagamento)).thenReturn(response)

        assertSame(response, service.registrarPagamento(request))
    }

    @Test
    fun `updates payment and resolves optional agreement`() {
        val requestWithAgreement = request.copy(id = 10L, convenioId = 3L)
        Mockito.`when`(repository.findById(10L)).thenReturn(Optional.of(pagamento))
        Mockito.`when`(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda))
        Mockito.`when`(patientRepository.findById(2L)).thenReturn(Optional.of(patient))
        Mockito.`when`(convenioRepository.findById(3L)).thenReturn(Optional.of(convenio))
        Mockito.`when`(
            mapper.toModel(requestWithAgreement, agenda, patient, convenio)
        ).thenReturn(pagamento)
        Mockito.`when`(repository.save(pagamento)).thenReturn(pagamento)
        Mockito.`when`(mapper.toResponse(pagamento)).thenReturn(response)

        assertSame(response, service.atualizar(10L, requestWithAgreement))
    }

    @Test
    fun `rejects payment when agenda is absent`() {
        Mockito.`when`(agendaRepository.findById(1L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.registrarPagamento(request) }
        Mockito.verify(repository, Mockito.never()).save(Mockito.any())
    }

    @Test
    fun `rejects payment when patient or selected agreement is absent`() {
        Mockito.`when`(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda))
        Mockito.`when`(patientRepository.findById(2L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { service.registrarPagamento(request) }

        val withAgreement = request.copy(convenioId = 3L)
        Mockito.`when`(patientRepository.findById(2L)).thenReturn(Optional.of(patient))
        Mockito.`when`(convenioRepository.findById(3L)).thenReturn(Optional.empty())
        assertFailsWith<NoSuchElementException> { service.registrarPagamento(withAgreement) }
    }

    @Test
    fun `rejects update when payment does not exist`() {
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())

        assertFailsWith<NoSuchElementException> { service.atualizar(404L, request) }
    }

    @Test
    fun `deletes existing payment and rejects missing payment`() {
        Mockito.`when`(repository.findById(10L)).thenReturn(Optional.of(pagamento))
        Mockito.`when`(repository.findById(404L)).thenReturn(Optional.empty())

        service.deletarPagamento(10L)
        Mockito.verify(repository).delete(pagamento)
        assertFailsWith<NoSuchElementException> { service.deletarPagamento(404L) }
    }
}