package com.edu.psiclinbe.service;

import com.edu.psiclinbe.dto.request.TipoAtendimentoDtoRequest;
import com.edu.psiclinbe.dto.response.TipoAtendimentoDtoResponse;
import com.edu.psiclinbe.mapper.TipoAtendimentoMapper;
import com.edu.psiclinbe.model.TipoAtendimento;
import com.edu.psiclinbe.repository.TipoAtendimentoRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TipoAtendimentoServiceJavaTest {
    private final TipoAtendimentoRepository repository = mock(TipoAtendimentoRepository.class);
    private final TipoAtendimentoMapper mapper = mock(TipoAtendimentoMapper.class);
    private final TIpoAtendimentoService service = new TIpoAtendimentoService(repository, mapper);
    private final TipoAtendimento type = mock(TipoAtendimento.class);
    private final TipoAtendimentoDtoResponse response = mock(TipoAtendimentoDtoResponse.class);
    private final TipoAtendimentoDtoRequest request = new TipoAtendimentoDtoRequest(
            null, "Psicoterapia", "Atendimento clínico", "50 minutos", true);

    @Test
    void listsAndSearchesTypes() {
        when(repository.findAll()).thenReturn(List.of(type));
        when(repository.findByNomeContainingIgnoreCase("Psico")).thenReturn(List.of(type));
        when(repository.findByAtivoTrue()).thenReturn(List.of(type));
        when(mapper.toResponse(type)).thenReturn(response);

        assertEquals(List.of(response), service.listarTodosTiposAtendimento());
        assertEquals(List.of(response), service.buscarTipoAtendimentoPeloNome("Psico"));
        assertEquals(List.of(response), service.buscarTiposAtendimetnoAtivos());
    }

    @Test
    void createsUpdatesAndDeactivatesType() {
        when(mapper.toModel(request)).thenReturn(type);
        when(repository.save(type)).thenReturn(type);
        when(repository.findById(1L)).thenReturn(Optional.of(type));
        when(mapper.toResponse(type)).thenReturn(response);

        assertSame(response, service.cadastrarTipoAtendimento(request));
        assertSame(response, service.atualizarTipoAtendimento(1L, request));
        verify(type).setDuracao(request.getDuracao());
        assertSame(response, service.desativarTipoAtendimento(1L));
        verify(type).setAtivo(false);
    }

    @Test
    void findsDeletesAndRejectsMissingType() {
        when(repository.findById(1L)).thenReturn(Optional.of(type));
        when(repository.findById(404L)).thenReturn(Optional.empty());
        when(mapper.toResponse(type)).thenReturn(response);

        assertSame(response, service.buscarTipoAtendimentoPorId(1L));
        service.excluirTipoAtendimetno(1L);
        assertThrows(RuntimeException.class, () -> service.excluirTipoAtendimetno(404L));
        verify(repository).delete(type);
    }
}