package com.edu.psiclinbe.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.edu.psiclinbe.dto.request.ConvenioDtoRequest;
import com.edu.psiclinbe.dto.response.ConvenioDtoResponse;
import com.edu.psiclinbe.mapper.ConvenioMapper;
import com.edu.psiclinbe.model.Convenio;
import com.edu.psiclinbe.repository.ConvenioRepository;

class ConvenioServiceJavaTest {
    private final ConvenioRepository repository = mock(ConvenioRepository.class);
    private final ConvenioMapper mapper = mock(ConvenioMapper.class);
    private final ConvenioService service = new ConvenioService(repository, mapper);
    private final Convenio convenio = mock(Convenio.class);
    private final ConvenioDtoResponse response = mock(ConvenioDtoResponse.class);

    @Test
    void mapsAgreementListsAndSearches() {
        when(repository.findAll()).thenReturn(List.of(convenio));
        when(repository.findByAtivoTrue()).thenReturn(List.of(convenio));
        when(repository.buscarPorNomeParcial("Plano")).thenReturn(List.of(convenio));
        when(repository.findByTelefoneContainingIgnoreCase("9999"))
                .thenReturn(List.of(convenio));
        when(mapper.toResponse(convenio)).thenReturn(response);

        assertEquals(List.of(response), service.listarTodos());
        assertEquals(List.of(response), service.listarAtivos());
        assertEquals(List.of(response), service.buscarPorNome("Plano"));
        assertEquals(List.of(response), service.buscarPorTelefone("9999"));
    }

    @Test
    void findsAgreementAndReportsMissingIdentifiers() {
        when(repository.findById(5L)).thenReturn(Optional.of(convenio));
        when(repository.findByCnpj("12345678000199")).thenReturn(convenio);
        when(repository.findById(404L)).thenReturn(Optional.empty());
        when(repository.findByCnpj("missing")).thenReturn(null);
        when(mapper.toResponse(convenio)).thenReturn(response);

        assertSame(response, service.buscarPorId(5L));
        assertSame(response, service.buscarPorCnpj("12345678000199"));
        assertThrows(NoSuchElementException.class, () -> service.buscarPorId(404L));
        assertThrows(NoSuchElementException.class, () -> service.buscarPorCnpj("missing"));
    }

    @Test
    void rejectsDuplicateTaxNumberBeforeSaving() {
        ConvenioDtoRequest request = new ConvenioDtoRequest(
                null, "Plano", "12345678000199", "11999990000", 80.0, true);
        when(repository.findByCnpj(request.getCnpj())).thenReturn(convenio);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(request));
        verify(repository, org.mockito.Mockito.never()).save(convenio);
    }
}