package com.edu.psiclinbe.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.edu.psiclinbe.dto.request.PacienteDtoRequest;
import com.edu.psiclinbe.dto.response.PacienteDtoResponse;
import com.edu.psiclinbe.mapper.PacienteMapper;
import com.edu.psiclinbe.model.Paciente;
import com.edu.psiclinbe.repository.PacienteRepository;

class PacienteServiceJavaTest {
    private final PacienteRepository repository = mock(PacienteRepository.class);
    private final PacienteMapper mapper = mock(PacienteMapper.class);
    private final PacienteService service = new PacienteService(repository, mapper);
    private final Paciente paciente = mock(Paciente.class);
    private final PacienteDtoResponse response = mock(PacienteDtoResponse.class);

    @Test
    void listsAndSearchesPatients() {
        when(repository.findAll()).thenReturn(List.of(paciente));
        when(repository.findByAtivoTrue()).thenReturn(List.of(paciente));
        when(repository.buscarPorNomeParcial("Ana")).thenReturn(List.of(paciente));
        when(mapper.toResponse(paciente)).thenReturn(response);

        assertEquals(List.of(response), service.listarTodos());
        assertEquals(List.of(response), service.listarAtivos());
        assertEquals(List.of(response), service.buscarPorNome("Ana"));
    }

    @Test
    void findsPatientByIdCpfEmailAndReportsMissingRecords() {
        when(repository.findById(1L)).thenReturn(Optional.of(paciente));
        when(repository.findById(404L)).thenReturn(Optional.empty());
        when(repository.findByCpf("12345678900")).thenReturn(paciente);
        when(repository.findByEmail("ana@example.com")).thenReturn(paciente);
        when(repository.findByCpf("missing")).thenReturn(null);
        when(repository.findByEmail("missing@example.com")).thenReturn(null);
        when(mapper.toResponse(paciente)).thenReturn(response);

        assertSame(response, service.buscarPorId(1L));
        assertSame(response, service.buscarPorCpf("12345678900"));
        assertSame(response, service.buscarPorEmail("ana@example.com"));
        assertThrows(RuntimeException.class, () -> service.buscarPorId(404L));
        assertThrows(RuntimeException.class, () -> service.buscarPorCpf("missing"));
        assertThrows(RuntimeException.class, () -> service.buscarPorEmail("missing@example.com"));
    }

    @Test
    void rejectsDuplicateCpfAndEmail() {
        PacienteDtoRequest request = new PacienteDtoRequest(
                null, "Ana", "12345678900", LocalDate.of(1990, 1, 1),
                "ana@example.com", "11999990000", "Rua Central", "", true);
        when(repository.findByCpf(request.getCpf())).thenReturn(paciente);

        assertThrows(IllegalArgumentException.class, () -> service.salvar(request));
        when(repository.findByCpf(request.getCpf())).thenReturn(null);
        when(repository.findByEmail(request.getEmail())).thenReturn(paciente);
        assertThrows(IllegalArgumentException.class, () -> service.salvar(request));
    }
}