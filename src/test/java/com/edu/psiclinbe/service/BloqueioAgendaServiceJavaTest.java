package com.edu.psiclinbe.service;

import com.edu.psiclinbe.dto.request.BloqueioAgendaDtoRequest;
import com.edu.psiclinbe.dto.response.BloqueioAgendaDtoResponse;
import com.edu.psiclinbe.mapper.BloqueioAgendaMapper;
import com.edu.psiclinbe.model.BloqueioAgenda;
import com.edu.psiclinbe.model.Psicologo;
import com.edu.psiclinbe.repository.BloqueioAgendaRepository;
import com.edu.psiclinbe.repository.PsicologoRepository;
import com.edu.psiclinbe.validation.ValidarDataInicioFim;
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

class BloqueioAgendaServiceJavaTest {
    private final BloqueioAgendaRepository repository = mock(BloqueioAgendaRepository.class);
    private final PsicologoRepository psychologistRepository = mock(PsicologoRepository.class);
    private final BloqueioAgendaMapper mapper = mock(BloqueioAgendaMapper.class);
    private final BloqueioAgendaService service = new BloqueioAgendaService(
            repository, psychologistRepository, mapper, new ValidarDataInicioFim());
    private final Psicologo psychologist = mock(Psicologo.class);
    private final BloqueioAgenda block = mock(BloqueioAgenda.class);
    private final BloqueioAgendaDtoResponse response = mock(BloqueioAgendaDtoResponse.class);
    private final LocalDateTime start = LocalDateTime.of(2026, 11, 3, 10, 0);
    private final LocalDateTime end = LocalDateTime.of(2026, 11, 3, 11, 0);
    private final BloqueioAgendaDtoRequest request = new BloqueioAgendaDtoRequest(
            0L, 2L, start, end, "Indisponibilidade");

    @Test
    void listsAndSearchesBlocks() {
        when(repository.findAllByAtivoTrue()).thenReturn(List.of(block));
        when(repository.buscarPorMotivo("Indisponibilidade")).thenReturn(List.of(block));
        when(repository.findByPsicologoIdAndAtivoTrue(2L)).thenReturn(List.of(block));
        when(repository.findByDataHoraInicioBetween(start, end)).thenReturn(List.of(block));
        when(mapper.toResponse(block)).thenReturn(response);

        assertEquals(List.of(response), service.listarTodos());
        assertEquals(List.of(response), service.buscarPorMotivo("Indisponibilidade"));
        assertEquals(List.of(response), service.buscarPorPsicologo(2L));
        assertEquals(List.of(response), service.buscarPorPeriodo(start, end));
    }

    @Test
    void createsBlockAndRejectsInvalidOrConflictingIntervals() {
        when(psychologist.getId()).thenReturn(2L);
        when(psychologistRepository.findById(2L)).thenReturn(Optional.of(psychologist));
        when(repository.findByPsicologoIdAndAtivoTrue(2L)).thenReturn(List.of());
        when(mapper.toModel(request, psychologist)).thenReturn(block);
        when(repository.save(block)).thenReturn(block);
        when(mapper.toResponse(block)).thenReturn(response);

        assertSame(response, service.salvar(request));

        BloqueioAgendaDtoRequest invalid = new BloqueioAgendaDtoRequest(
                0L, 2L, end, start, "Invalido");
        assertThrows(IllegalArgumentException.class, () -> service.salvar(invalid));

        BloqueioAgenda existing = new BloqueioAgenda(
                5L, psychologist, start, end, "Outro bloqueio", true);
        when(repository.findByPsicologoIdAndAtivoTrue(2L)).thenReturn(List.of(existing));
        assertThrows(IllegalArgumentException.class, () -> service.salvar(request));
    }

    @Test
    void updatesDeactivatesAndDeletesBlocks() {
        when(block.getId()).thenReturn(1L);
        when(repository.findByIdAndAtivoTrue(1L)).thenReturn(block);
        when(psychologist.getId()).thenReturn(2L);
        when(psychologistRepository.findById(2L)).thenReturn(Optional.of(psychologist));
        when(repository.findByPsicologoIdAndAtivoTrue(2L)).thenReturn(List.of(block));
        when(repository.save(block)).thenReturn(block);
        when(mapper.toResponse(block)).thenReturn(response);

        assertSame(response, service.atualizar(1L, request));
        service.deletar(1L);
        verify(block).setAtivo(false);

        when(repository.existsById(1L)).thenReturn(true);
        when(repository.existsById(404L)).thenReturn(false);
        service.excluirPermanentemente(1L);
        verify(repository).deleteById(1L);
        assertThrows(RuntimeException.class, () -> service.excluirPermanentemente(404L));
    }
}