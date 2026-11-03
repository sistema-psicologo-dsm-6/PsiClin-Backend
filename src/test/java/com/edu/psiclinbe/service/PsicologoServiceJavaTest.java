package com.edu.psiclinbe.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.edu.psiclinbe.dto.request.PsicologoDtoRequest;
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse;
import com.edu.psiclinbe.mapper.PsicologoMapper;
import com.edu.psiclinbe.model.Psicologo;
import com.edu.psiclinbe.repository.FuncionarioRepository;
import com.edu.psiclinbe.repository.PsicologoRepository;

class PsicologoServiceJavaTest {
    private final PsicologoRepository repository = mock(PsicologoRepository.class);
    private final FuncionarioRepository employeeRepository = mock(FuncionarioRepository.class);
    private final PsicologoMapper mapper = mock(PsicologoMapper.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final PsicologoService service = new PsicologoService(
            repository, employeeRepository, mapper, encoder);
    private final Psicologo psychologist = mock(Psicologo.class);
    private final PsicologoDtoResponse response = mock(PsicologoDtoResponse.class);

    @Test
    void listsPsychologistsAndRejectsDuplicateCrp() {
        when(repository.findAll()).thenReturn(List.of(psychologist));
        when(repository.findByAtivoTrue()).thenReturn(List.of(psychologist));
        when(repository.findByEspecialidadeContainingIgnoreCase("Clínica"))
                .thenReturn(List.of(psychologist));
        when(mapper.toResponse(psychologist)).thenReturn(response);
        assertEquals(List.of(response), service.listarTodos());
        assertEquals(List.of(response), service.listarAtivos());
        assertEquals(List.of(response), service.buscarPorEspecialidade("Clínica"));

        PsicologoDtoRequest request = request();
        when(employeeRepository.findByEmail(request.getEmail())).thenReturn(null);
        when(employeeRepository.findByCpf(request.getCpf())).thenReturn(null);
        when(repository.findByCrp(request.getCrp())).thenReturn(psychologist);
        assertThrows(IllegalArgumentException.class, () -> service.salvar(request));
    }

    @Test
    void findsPsychologistAndRejectsUnknownIdOrCrp() {
        when(repository.findById(2L)).thenReturn(Optional.of(psychologist));
        when(repository.findById(404L)).thenReturn(Optional.empty());
        when(repository.findByCrp("CRP-123")).thenReturn(psychologist);
        when(repository.findByCrp("missing")).thenReturn(null);
        when(mapper.toResponse(psychologist)).thenReturn(response);

        assertEquals(response, service.buscarPorId(2L));
        assertEquals(response, service.buscarPorCrp("CRP-123"));
        assertThrows(RuntimeException.class, () -> service.buscarPorId(404L));
        assertThrows(RuntimeException.class, () -> service.buscarPorCrp("missing"));
    }

    private PsicologoDtoRequest request() {
        return new PsicologoDtoRequest(
                null, "Ana", "12345678900", "ana@example.com", "11999990000",
                "password", "CRP-123", "Clínica", true);
    }
}