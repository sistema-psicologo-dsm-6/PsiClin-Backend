package com.edu.psiclinbe.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.edu.psiclinbe.dto.formData.AgendaFormDataResponse;
import com.edu.psiclinbe.dto.request.AgendaDtoRequest;
import com.edu.psiclinbe.dto.response.AgendaDtoResponse;
import com.edu.psiclinbe.dto.response.ConvenioDtoResponse;
import com.edu.psiclinbe.dto.response.PacienteDtoResponse;
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse;
import com.edu.psiclinbe.dto.response.SalaAtendimentoDtoResponse;
import com.edu.psiclinbe.dto.response.TipoAtendimentoDtoResponse;
import com.edu.psiclinbe.mapper.AgendaMapper;
import com.edu.psiclinbe.model.enums.AgendaStatus;
import com.edu.psiclinbe.service.AgendaService;
import com.edu.psiclinbe.service.ConvenioService;
import com.edu.psiclinbe.service.PacienteService;
import com.edu.psiclinbe.service.PsicologoService;
import com.edu.psiclinbe.service.SalaAtendimentoService;
import com.edu.psiclinbe.service.TIpoAtendimentoService;

class AgendaControllerTest {

    private final AgendaService agendaService = mock(AgendaService.class);
    private final PacienteService pacienteService = mock(PacienteService.class);
    private final PsicologoService psicologoService = mock(PsicologoService.class);
    private final SalaAtendimentoService salaService = mock(SalaAtendimentoService.class);
    private final TIpoAtendimentoService tipoService = mock(TIpoAtendimentoService.class);
    private final ConvenioService convenioService = mock(ConvenioService.class);
    private final AgendaMapper mapper = mock(AgendaMapper.class);
    private final AgendaController controller = new AgendaController(
            agendaService,
            pacienteService,
            psicologoService,
            salaService,
            tipoService,
            convenioService,
            mapper);

    private final AgendaDtoResponse agendaResponse = mock(AgendaDtoResponse.class);
    private final AgendaDtoRequest agendaRequest = mock(AgendaDtoRequest.class);

    @Test
    void indexReturnsAgendas() {
        List<AgendaDtoResponse> agendas = List.of(agendaResponse);
        when(agendaService.listarTodos()).thenReturn(agendas);

        ResponseEntity<List<AgendaDtoResponse>> response = controller.index();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(agendas, response.getBody());
    }

    @Test
    void showReturnsAgendaById() {
        when(agendaService.buscarPorId(10L)).thenReturn(agendaResponse);

        ResponseEntity<AgendaDtoResponse> response = controller.show(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(agendaResponse, response.getBody());
        verify(agendaService).buscarPorId(10L);
    }

    @Test
    void indexByObservationsReturnsMatchingAgendas() {
        List<AgendaDtoResponse> agendas = List.of(agendaResponse);
        when(agendaService.buscarPorObservacoes("retorno")).thenReturn(agendas);

        ResponseEntity<List<AgendaDtoResponse>> response =
                controller.indexByObservations("retorno");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(agendas, response.getBody());
    }

    @Test
    void availableTimesReturnsServiceAvailability() {
        List<LocalTime> times = List.of(LocalTime.of(9, 0), LocalTime.of(10, 0));
        LocalDate date = LocalDate.of(2026, 11, 3);
        when(agendaService.listarHorariosDisponiveis(2L, date, 60L)).thenReturn(times);

        ResponseEntity<List<LocalTime>> response =
                controller.availableTimes(2L, date, 60L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(times, response.getBody());
    }

    @Test
    void storeCreatesAgenda() {
        when(agendaService.salvar(agendaRequest)).thenReturn(agendaResponse);

        ResponseEntity<AgendaDtoResponse> response = controller.store(agendaRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(agendaResponse, response.getBody());
        verify(agendaService).salvar(agendaRequest);
    }

    @Test
    void updateReplacesAgenda() {
        when(agendaService.atualizar(10L, agendaRequest)).thenReturn(agendaResponse);

        ResponseEntity<AgendaDtoResponse> response = controller.update(10L, agendaRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(agendaResponse, response.getBody());
    }

    @Test
    void updateStatusChangesAgendaStatus() {
        when(agendaService.atualizarStatus(10L, AgendaStatus.CONFIRMADO))
                .thenReturn(agendaResponse);

        ResponseEntity<AgendaDtoResponse> response =
                controller.updateStatus(10L, AgendaStatus.CONFIRMADO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(agendaResponse, response.getBody());
    }

    @Test
    void destroySoftDeletesAgenda() {
        ResponseEntity<Void> response = controller.destroy(10L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(agendaService).deletar(10L);
    }

    @Test
    void destroyPermanentlyDeletesAgenda() {
        ResponseEntity<Void> response = controller.destroyPermanently(10L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(agendaService).excluirPermanentemente(10L);
    }

    @Test
    void formDataLoadsOptionsWithoutAnExistingAgenda() {
        List<PacienteDtoResponse> patients = List.of();
        List<PsicologoDtoResponse> psychologists = List.of();
        List<SalaAtendimentoDtoResponse> rooms = List.of();
        List<TipoAtendimentoDtoResponse> types = List.of();
        List<ConvenioDtoResponse> plans = List.of();
        AgendaFormDataResponse formData = mock(AgendaFormDataResponse.class);

        when(pacienteService.listarTodos()).thenReturn(patients);
        when(psicologoService.listarTodos()).thenReturn(psychologists);
        when(salaService.listarTodasAsSalas()).thenReturn(rooms);
        when(tipoService.listarTodosTiposAtendimento()).thenReturn(types);
        when(convenioService.listarTodos()).thenReturn(plans);
        when(mapper.toFormData(patients, psychologists, rooms, types, plans, null))
                .thenReturn(formData);

        ResponseEntity<AgendaFormDataResponse> response = controller.formData(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(formData, response.getBody());
    }

    @Test
    void formDataLoadsExistingAgendaForEditing() {
        List<PacienteDtoResponse> patients = List.of();
        List<PsicologoDtoResponse> psychologists = List.of();
        List<SalaAtendimentoDtoResponse> rooms = List.of();
        List<TipoAtendimentoDtoResponse> types = List.of();
        List<ConvenioDtoResponse> plans = List.of();
        AgendaFormDataResponse formData = mock(AgendaFormDataResponse.class);

        when(pacienteService.listarTodos()).thenReturn(patients);
        when(psicologoService.listarTodos()).thenReturn(psychologists);
        when(salaService.listarTodasAsSalas()).thenReturn(rooms);
        when(tipoService.listarTodosTiposAtendimento()).thenReturn(types);
        when(convenioService.listarTodos()).thenReturn(plans);
        when(agendaService.buscarPorId(10L)).thenReturn(agendaResponse);
        when(mapper.toFormData(patients, psychologists, rooms, types, plans, agendaResponse))
                .thenReturn(formData);

        ResponseEntity<AgendaFormDataResponse> response = controller.formData(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(formData, response.getBody());
        verify(agendaService).buscarPorId(10L);
    }
}