package com.edu.psiclinbe.service;

import com.edu.psiclinbe.dto.request.PagamentoDtoRequest;
import com.edu.psiclinbe.dto.response.PagamentoDtoResponse;
import com.edu.psiclinbe.mapper.PagamentoMapper;
import com.edu.psiclinbe.model.Agenda;
import com.edu.psiclinbe.model.Convenio;
import com.edu.psiclinbe.model.Paciente;
import com.edu.psiclinbe.model.Pagamento;
import com.edu.psiclinbe.model.enums.PagamentoStatus;
import com.edu.psiclinbe.repository.AgendaRepository;
import com.edu.psiclinbe.repository.ConvenioRepository;
import com.edu.psiclinbe.repository.PacienteRepository;
import com.edu.psiclinbe.repository.PagamentoRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class PagamentoServiceJavaTest {
    private final PagamentoRepository repository = mock(PagamentoRepository.class);
    private final AgendaRepository agendaRepository = mock(AgendaRepository.class);
    private final PacienteRepository patientRepository = mock(PacienteRepository.class);
    private final ConvenioRepository convenioRepository = mock(ConvenioRepository.class);
    private final PagamentoMapper mapper = mock(PagamentoMapper.class);
    private final PagamentoService service = new PagamentoService(
            repository, agendaRepository, patientRepository, convenioRepository, mapper);
    private final Pagamento payment = mock(Pagamento.class);
    private final Agenda agenda = mock(Agenda.class);
    private final Paciente patient = mock(Paciente.class);
    private final Convenio convenio = mock(Convenio.class);
    private final PagamentoDtoResponse response = mock(PagamentoDtoResponse.class);
    private final PagamentoDtoRequest request = new PagamentoDtoRequest(
            null, 1L, 2L, null, 100.0, 0.0, 100.0, "Pix",
            LocalDateTime.of(2026, 11, 3, 10, 0), PagamentoStatus.PENDENTE);

    @Test
    void listsAndSearchesPayments() {
        when(repository.findAll()).thenReturn(List.of(payment));
        when(repository.findByPacienteId(2L)).thenReturn(List.of(payment));
        when(repository.findByConvenioId(3L)).thenReturn(List.of(payment));
        when(repository.findByStatus(PagamentoStatus.PENDENTE)).thenReturn(List.of(payment));
        when(repository.buscarPorFormaPagamento("Pix")).thenReturn(List.of(payment));
        when(mapper.toResponse(payment)).thenReturn(response);

        assertEquals(List.of(response), service.lerTodos());
        assertEquals(List.of(response), service.buscarPorPaciente(2L));
        assertEquals(List.of(response), service.buscarPorConvenio(3L));
        assertEquals(List.of(response), service.buscarPorStatus(PagamentoStatus.PENDENTE));
        assertEquals(List.of(response), service.buscarPorFormaPagamento("Pix"));
    }

    @Test
    void registersPaymentWithAgendaAndPatient() {
        when(agendaRepository.findById(1L)).thenReturn(Optional.of(agenda));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(mapper.toModel(request, agenda, patient, null)).thenReturn(payment);
        when(repository.save(payment)).thenReturn(payment);
        when(mapper.toResponse(payment)).thenReturn(response);

        assertSame(response, service.registrarPagamento(request));
    }

    @Test
    void reportsMissingAgendaOrPayment() {
        when(agendaRepository.findById(1L)).thenReturn(Optional.empty());
        when(repository.findById(404L)).thenReturn(Optional.empty());
        when(repository.findByAgendaId(404L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> service.registrarPagamento(request));
        assertThrows(RuntimeException.class, () -> service.buscarPorId(404L));
        assertThrows(RuntimeException.class, () -> service.buscarPorAgenda(404L));
    }

    @Test
    void deletesExistingPayment() {
        when(repository.findById(1L)).thenReturn(Optional.of(payment));

        service.deletarPagamento(1L);

        verify(repository).delete(payment);
    }
}