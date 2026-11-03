package com.edu.psiclinbe.service;

import com.edu.psiclinbe.dto.request.HorarioDisponivelDtoRequest;
import com.edu.psiclinbe.dto.response.HorarioDisponivelDtoResponse;
import com.edu.psiclinbe.mapper.HorarioDisponivelMapper;
import com.edu.psiclinbe.model.HorarioDisponivel;
import com.edu.psiclinbe.model.Psicologo;
import com.edu.psiclinbe.model.enums.DiaSemana;
import com.edu.psiclinbe.repository.HorarioDisponivelRepository;
import com.edu.psiclinbe.repository.PsicologoRepository;
import com.edu.psiclinbe.validation.ValidarDataInicioFim;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HorarioDisponivelServiceJavaTest {
    private final HorarioDisponivelRepository repository = mock(HorarioDisponivelRepository.class);
    private final PsicologoRepository psychologistRepository = mock(PsicologoRepository.class);
    private final HorarioDisponivelMapper mapper = mock(HorarioDisponivelMapper.class);
    private final HorarioDisponivelService service = new HorarioDisponivelService(
            repository, psychologistRepository, mapper, new ValidarDataInicioFim());
    private final Psicologo psychologist = mock(Psicologo.class);
    private final HorarioDisponivel schedule = mock(HorarioDisponivel.class);
    private final HorarioDisponivelDtoResponse response = mock(HorarioDisponivelDtoResponse.class);
    private final HorarioDisponivelDtoRequest request = new HorarioDisponivelDtoRequest(
            null, 2L, "TERCA", LocalTime.of(9, 0), LocalTime.of(12, 0), "Manhã");

    @Test
    void listsAndSearchesSchedules() {
        when(repository.findAll()).thenReturn(List.of(schedule));
        when(repository.buscarPorDescricao("Manhã")).thenReturn(List.of(schedule));
        when(repository.findByPsicologoId(2L)).thenReturn(List.of(schedule));
        when(repository.findByPsicologoIdAndDiaSemana(2L, DiaSemana.TERCA))
                .thenReturn(List.of(schedule));
        when(repository.findById(1L)).thenReturn(Optional.of(schedule));
        when(mapper.toResponse(schedule)).thenReturn(response);

        assertEquals(List.of(response), service.listarTodos());
        assertEquals(List.of(response), service.buscarPorDescricao("Manhã"));
        assertEquals(List.of(response), service.buscarPorPsicologo(2L));
        assertEquals(List.of(response), service.buscarPorPsicologoEDia(2L, "TERCA"));
        assertSame(response, service.buscarPorId(1L));
        assertThrows(IllegalArgumentException.class,
                () -> service.buscarPorPsicologoEDia(2L, "INVALIDO"));
    }

    @Test
    void createsScheduleAndRejectsInvalidIntervalOrOverlap() {
        when(psychologist.getId()).thenReturn(2L);
        when(psychologistRepository.findById(2L)).thenReturn(Optional.of(psychologist));
        when(repository.findByPsicologoIdAndDiaSemana(2L, DiaSemana.TERCA)).thenReturn(List.of());
        when(mapper.toModel(request, psychologist)).thenReturn(schedule);
        when(repository.save(schedule)).thenReturn(schedule);
        when(mapper.toResponse(schedule)).thenReturn(response);

        assertSame(response, service.salvar(request));

        HorarioDisponivelDtoRequest invalid = new HorarioDisponivelDtoRequest(
                null, 2L, "TERCA", LocalTime.of(12, 0), LocalTime.of(9, 0), null);
        assertThrows(IllegalArgumentException.class, () -> service.salvar(invalid));

        HorarioDisponivel existing = new HorarioDisponivel(
                7L, psychologist, DiaSemana.TERCA,
                LocalTime.of(10, 0), LocalTime.of(11, 0), "Conflito", true);
        when(repository.findByPsicologoIdAndDiaSemana(2L, DiaSemana.TERCA))
                .thenReturn(List.of(existing));
        assertThrows(IllegalArgumentException.class, () -> service.salvar(request));
    }

    @Test
    void updatesAndDeactivatesSchedule() {
        when(schedule.getId()).thenReturn(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(schedule));
        when(psychologist.getId()).thenReturn(2L);
        when(psychologistRepository.findById(2L)).thenReturn(Optional.of(psychologist));
        when(repository.findByPsicologoIdAndDiaSemana(2L, DiaSemana.TERCA))
                .thenReturn(List.of(schedule));
        when(repository.save(schedule)).thenReturn(schedule);
        when(mapper.toResponse(schedule)).thenReturn(response);

        assertSame(response, service.atualizar(1L, request));
        service.deletar(1L);
        verify(schedule).setAtivo(false);
        assertThrows(RuntimeException.class, () -> service.buscarPorId(404L));

        when(repository.excluirPermanentementePorId(1L)).thenReturn(1);
        when(repository.excluirPermanentementePorId(404L)).thenReturn(0);
        service.excluirPermanentemente(1L);
        assertThrows(RuntimeException.class, () -> service.excluirPermanentemente(404L));
    }
}