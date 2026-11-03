package com.edu.psiclinbe.service;

import com.edu.psiclinbe.dto.request.FuncionarioDtoRequest;
import com.edu.psiclinbe.dto.response.FuncionarioDtoResponse;
import com.edu.psiclinbe.mapper.FuncionarioMapper;
import com.edu.psiclinbe.model.Funcionario;
import com.edu.psiclinbe.model.enums.Role;
import com.edu.psiclinbe.repository.FuncionarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FuncionarioServiceJavaTest {
    private final FuncionarioRepository repository = mock(FuncionarioRepository.class);
    private final FuncionarioMapper mapper = mock(FuncionarioMapper.class);
    private final PasswordEncoder encoder = mock(PasswordEncoder.class);
    private final FuncionarioService service = new FuncionarioService(repository, mapper, encoder);
    private final Funcionario funcionario = mock(Funcionario.class);
    private final FuncionarioDtoResponse response = mock(FuncionarioDtoResponse.class);

    @Test
    void listsEmployeesAndRejectsDuplicateEmailCpf() {
        when(repository.findAll()).thenReturn(List.of(funcionario));
        when(repository.findByAtivoTrue()).thenReturn(List.of(funcionario));
        when(repository.buscarPorNomeParcial("Ana")).thenReturn(List.of(funcionario));
        when(mapper.toResponse(funcionario)).thenReturn(response);
        assertEquals(List.of(response), service.listarTodos());
        assertEquals(List.of(response), service.listarAtivos());
        assertEquals(List.of(response), service.buscarPorNome("Ana"));

        FuncionarioDtoRequest request = request(Role.RECEPCIONISTA);
        when(repository.findByEmailAndCpf(request.getEmail(), request.getCpf()))
                .thenReturn(funcionario);
        assertThrows(IllegalArgumentException.class, () -> service.salvar(request));
    }

    @Test
    void createsInitialAdminOnlyWhenRepositoryIsEmpty() {
        FuncionarioDtoRequest request = request(Role.PSICOLOGO);
        when(repository.count()).thenReturn(0L);
        when(repository.findByEmailAndCpf(request.getEmail(), request.getCpf())).thenReturn(null);
        when(encoder.encode(request.getSenha())).thenReturn("encoded-password");
        when(repository.save(any(Funcionario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(Funcionario.class))).thenReturn(response);

        assertSame(response, service.criarAdminInicial(request));

        when(repository.count()).thenReturn(1L);
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.criarAdminInicial(request));
        assertEquals(HttpStatus.CONFLICT, error.getStatusCode());
    }

    private FuncionarioDtoRequest request(Role role) {
        return new FuncionarioDtoRequest(
                null, "Ana", "12345678900", "ana@example.com", "11999990000",
                "password", true, role);
    }
}