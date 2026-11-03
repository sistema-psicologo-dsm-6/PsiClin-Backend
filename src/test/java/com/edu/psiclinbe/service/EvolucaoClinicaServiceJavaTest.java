package com.edu.psiclinbe.service;

import com.edu.psiclinbe.dto.request.EvolucaoClinicaDtoRequest;
import com.edu.psiclinbe.dto.response.EvolucaoClinicaDtoResponse;
import com.edu.psiclinbe.mapper.EvolucaoClinicaMapper;
import com.edu.psiclinbe.model.Agenda;
import com.edu.psiclinbe.model.EvolucaoClinica;
import com.edu.psiclinbe.model.Prontuario;
import com.edu.psiclinbe.model.Psicologo;
import com.edu.psiclinbe.repository.AgendaRepository;
import com.edu.psiclinbe.repository.EvolucaoClinicaRepository;
import com.edu.psiclinbe.repository.ProntuarioRepository;
import com.edu.psiclinbe.repository.PsicologoRepository;
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

class EvolucaoClinicaServiceJavaTest {
    private final EvolucaoClinicaRepository repository = mock(EvolucaoClinicaRepository.class);
    private final ProntuarioRepository prontuarioRepository = mock(ProntuarioRepository.class);
    private final PsicologoRepository psychologistRepository = mock(PsicologoRepository.class);
    private final AgendaRepository agendaRepository = mock(AgendaRepository.class);
    private final EvolucaoClinicaMapper mapper = mock(EvolucaoClinicaMapper.class);
    private final EvolucaoClinicaService service = new EvolucaoClinicaService(
            repository, prontuarioRepository, psychologistRepository, agendaRepository, mapper);
    private final EvolucaoClinica evolution = mock(EvolucaoClinica.class);
    private final Prontuario record = mock(Prontuario.class);
    private final Psicologo psychologist = mock(Psicologo.class);
    private final Agenda agenda = mock(Agenda.class);
    private final EvolucaoClinicaDtoResponse response = mock(EvolucaoClinicaDtoResponse.class);
    private final EvolucaoClinicaDtoRequest request = new EvolucaoClinicaDtoRequest(
            null, 1L, 2L, 3L, LocalDateTime.of(2026, 11, 3, 10, 0), "Evolução");

    @Test
    void listsAndSearchesClinicalEvolutions() {
        when(repository.findAll()).thenReturn(List.of(evolution));
        when(repository.buscarPorDescricao("Evolução")).thenReturn(List.of(evolution));
        when(repository.findByProntuarioId(1L)).thenReturn(List.of(evolution));
        when(repository.findByPsicologoId(2L)).thenReturn(List.of(evolution));
        when(repository.findByAgendaId(3L)).thenReturn(List.of(evolution));
        when(mapper.toResponse(evolution)).thenReturn(response);

        assertEquals(List.of(response), service.listarTodos());
        assertEquals(List.of(response), service.buscarPorDescricao("Evolução"));
        assertEquals(List.of(response), service.buscarPorProntuario(1L));
        assertEquals(List.of(response), service.buscarPorPsicologo(2L));
        assertEquals(List.of(response), service.buscarPorAgenda(3L));
    }

    @Test
    void createsEvolutionAfterResolvingAllRelationships() {
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.of(record));
        when(psychologistRepository.findById(2L)).thenReturn(Optional.of(psychologist));
        when(agendaRepository.findById(3L)).thenReturn(Optional.of(agenda));
        when(mapper.toModel(request, record, psychologist, agenda)).thenReturn(evolution);
        when(repository.save(evolution)).thenReturn(evolution);
        when(mapper.toResponse(evolution)).thenReturn(response);

        assertSame(response, service.salvar(request));
    }

    @Test
    void rejectsEvolutionWhenRequiredRecordIsMissing() {
        when(prontuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.salvar(request));
    }

    @Test
    void softDeletesEvolutionAndRejectsMissingId() {
        when(repository.findById(1L)).thenReturn(Optional.of(evolution));
        when(repository.findById(404L)).thenReturn(Optional.empty());
        when(repository.save(evolution)).thenReturn(evolution);
        service.deletar(1L);
        verify(evolution).setAtivo(false);
        assertThrows(RuntimeException.class, () -> service.buscarPorId(404L));
    }
}