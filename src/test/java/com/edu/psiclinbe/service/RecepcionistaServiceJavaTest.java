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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.edu.psiclinbe.dto.request.RecepcionistaDtoRequest;
import com.edu.psiclinbe.dto.response.RecepcionistaDtoResponse;
import com.edu.psiclinbe.mapper.RecepcionistaMapper;
import com.edu.psiclinbe.model.Recepcionista;
import com.edu.psiclinbe.model.enums.Role;
import com.edu.psiclinbe.repository.RecepcionistaRepository;

class RecepcionistaServiceJavaTest {
    private final RecepcionistaRepository repository = mock(RecepcionistaRepository.class);
    private final RecepcionistaMapper mapper = mock(RecepcionistaMapper.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final RecepcionistaService service = new RecepcionistaService(repository, mapper, encoder);
    private final Recepcionista receptionist = mock(Recepcionista.class);
    private final RecepcionistaDtoResponse response = mock(RecepcionistaDtoResponse.class);
    private final RecepcionistaDtoRequest request = new RecepcionistaDtoRequest(
            null, "Joana", "12345678900", "joana@example.com", "11999990000",
            "password", "Recepção", true);

    @Test
    void listsAndSearchesReceptionists() {
        when(repository.findAll()).thenReturn(List.of(receptionist));
        when(repository.findBySetorContainingIgnoreCase("Recepção"))
                .thenReturn(List.of(receptionist));
        when(repository.findByAtivoTrue()).thenReturn(List.of(receptionist));
        when(mapper.toResponse(receptionist)).thenReturn(response);

        assertEquals(List.of(response), service.listarTodos());
        assertEquals(List.of(response), service.buscarPorSetor("Recepção"));
        assertEquals(List.of(response), service.buscarRecepcionistasAtivos());
    }

    @Test
    void createsReceptionistWithEncodedPasswordAndRole() {
        when(mapper.toModel(request)).thenReturn(receptionist);
        when(encoder.encode(request.getSenha())).thenReturn("hash");
        when(repository.save(receptionist)).thenReturn(receptionist);
        when(mapper.toResponse(receptionist)).thenReturn(response);

        assertSame(response, service.cadastrarRecepcionista(request));
        verify(receptionist).setSenha("hash");
        verify(receptionist).setRole(Role.RECEPCIONISTA);
    }

    @Test
    void findsUpdatesDeactivatesAndDeletesReceptionist() {
        when(repository.findById(1L)).thenReturn(Optional.of(receptionist));
        when(repository.findById(404L)).thenReturn(Optional.empty());
        when(repository.save(receptionist)).thenReturn(receptionist);
        when(mapper.toResponse(receptionist)).thenReturn(response);

        assertSame(response, service.buscarRecepcionista(1L));
        assertSame(response, service.atualizarRecepcionista(1L, request));
        assertSame(response, service.desativarRecepcionista(1L));
        service.excluitRecepcionista(1L);
        assertThrows(RuntimeException.class, () -> service.buscarRecepcionista(404L));
        verify(receptionist).setAtivo(false);
        verify(repository).delete(receptionist);
    }
}