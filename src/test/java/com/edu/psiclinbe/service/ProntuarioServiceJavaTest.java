package com.edu.psiclinbe.service;

import com.edu.psiclinbe.dto.request.ProntuarioDtoRequest;
import com.edu.psiclinbe.dto.response.ProntuarioDtoResponse;
import com.edu.psiclinbe.mapper.ProntuarioMapper;
import com.edu.psiclinbe.model.Paciente;
import com.edu.psiclinbe.model.Prontuario;
import com.edu.psiclinbe.model.enums.ProntuarioStatus;
import com.edu.psiclinbe.repository.PacienteRepository;
import com.edu.psiclinbe.repository.ProntuarioRepository;
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

class ProntuarioServiceJavaTest {
    private final ProntuarioRepository repository = mock(ProntuarioRepository.class);
    private final PacienteRepository patientRepository = mock(PacienteRepository.class);
    private final ProntuarioMapper mapper = mock(ProntuarioMapper.class);
    private final ProntuarioService service = new ProntuarioService(repository, patientRepository, mapper);
    private final Prontuario record = mock(Prontuario.class);
    private final Paciente patient = mock(Paciente.class);
    private final ProntuarioDtoResponse response = mock(ProntuarioDtoResponse.class);
    private final ProntuarioDtoRequest request = new ProntuarioDtoRequest(
            null, 1L, LocalDateTime.of(2026, 11, 3, 9, 0),
            "Ansiedade", ProntuarioStatus.ATIVO);

    @Test
    void listsRecordsByPatientStatusComplaintAndAll() {
        when(repository.findByPacienteId(1L)).thenReturn(List.of(record));
        when(repository.findByPacienteIdAndStatus(1L, ProntuarioStatus.ATIVO))
                .thenReturn(List.of(record));
        when(repository.buscarPorQueixaPrincipal("Ansiedade")).thenReturn(List.of(record));
        when(repository.findAll()).thenReturn(List.of(record));
        when(mapper.toResponse(record)).thenReturn(response);

        assertEquals(List.of(response), service.buscarPorPaciente(1L));
        assertEquals(List.of(response), service.buscarPorPacienteEStatus(1L, ProntuarioStatus.ATIVO));
        assertEquals(List.of(response), service.buscarPorQueixaPrincipal("Ansiedade"));
        assertEquals(List.of(response), service.listarProntuarios());
    }

    @Test
    void createsRecordWithPatientRelationship() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(mapper.toModel(request, patient)).thenReturn(record);
        when(repository.save(record)).thenReturn(record);
        when(mapper.toResponse(record)).thenReturn(response);

        assertSame(response, service.cadastrarProntuario(request));
    }

    @Test
    void rejectsCreationWhenPatientIsMissing() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.cadastrarProntuario(request));
    }

    @Test
    void softDeletesRecordAndRejectsMissingRecord() {
        when(repository.findById(1L)).thenReturn(Optional.of(record));
        when(repository.findById(404L)).thenReturn(Optional.empty());
        when(repository.save(record)).thenReturn(record);
        when(mapper.toResponse(record)).thenReturn(response);

        assertSame(response, service.buscarProntuario(1L));
        service.excluirProntuario(1L);
        verify(record).setAtivo(false);
        assertThrows(RuntimeException.class, () -> service.buscarProntuario(404L));
    }
}