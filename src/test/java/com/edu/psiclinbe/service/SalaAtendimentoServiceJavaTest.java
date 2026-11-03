package com.edu.psiclinbe.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.edu.psiclinbe.dto.request.SalaAtendimentoDtoRequest;
import com.edu.psiclinbe.dto.response.SalaAtendimentoDtoResponse;
import com.edu.psiclinbe.mapper.SalaAtendimentoMapper;
import com.edu.psiclinbe.model.SalaAtendimento;
import com.edu.psiclinbe.repository.SalaAtendimentoRepository;

class SalaAtendimentoServiceJavaTest {
    private final SalaAtendimentoRepository repository = mock(SalaAtendimentoRepository.class);
    private final SalaAtendimentoMapper mapper = mock(SalaAtendimentoMapper.class);
    private final SalaAtendimentoService service = new SalaAtendimentoService(repository, mapper);
    private final SalaAtendimento room = mock(SalaAtendimento.class);
    private final SalaAtendimentoDtoResponse response = mock(SalaAtendimentoDtoResponse.class);
    private final SalaAtendimentoDtoRequest request = new SalaAtendimentoDtoRequest(
            null, "Sala 1", "Consultório", 1, true);

    @Test
    void listsAndSearchesRooms() {
        when(repository.findAll()).thenReturn(List.of(room));
        when(repository.buscarPorNomeParcial("Sala")).thenReturn(List.of(room));
        when(repository.findByAtivoTrue()).thenReturn(List.of(room));
        when(mapper.toResponse(room)).thenReturn(response);

        assertEquals(List.of(response), service.listarTodasAsSalas());
        assertEquals(List.of(response), service.buscarSalaPorNome("Sala"));
        assertEquals(List.of(response), service.buscarSalasAtivas());
    }

    @Test
    void createsRoomAndUpdatesItsFields() {
        when(mapper.toModel(request)).thenReturn(room);
        when(repository.save(room)).thenReturn(room);
        when(repository.findById(1L)).thenReturn(Optional.of(room));
        when(mapper.toResponse(room)).thenReturn(response);

        assertSame(response, service.cadastrarSala(request));
        assertSame(response, service.atualizarSala(1L, request));
        verify(room).setNome(request.getNome());
        verify(room).setCapacidade(request.getCapacidade());
    }

    @Test
    void findsDeactivatesDeletesAndRejectsMissingRoom() {
        when(repository.findById(1L)).thenReturn(Optional.of(room));
        when(repository.findById(404L)).thenReturn(Optional.empty());
        when(repository.save(room)).thenReturn(room);
        when(mapper.toResponse(room)).thenReturn(response);

        assertSame(response, service.buscarSala(1L));
        assertSame(response, service.desativarSala(1L));
        service.excluirSala(1L);
        assertThrows(RuntimeException.class, () -> service.buscarSala(404L));
        verify(repository).delete(room);
        verify(room).setAtivo(false);
    }
}