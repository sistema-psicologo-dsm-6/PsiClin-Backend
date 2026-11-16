package com.edu.psiclinbe

import com.edu.psiclinbe.dto.request.AlterarSenhaDtoRequest
import com.edu.psiclinbe.dto.request.FuncionarioDtoUpdateRequest
import com.edu.psiclinbe.dto.request.PsicologoDtoUpdateRequest
import com.edu.psiclinbe.dto.request.RecepcionistaDtoUpdateRequest
import com.edu.psiclinbe.mapper.FuncionarioMapper
import com.edu.psiclinbe.mapper.PsicologoMapper
import com.edu.psiclinbe.mapper.RecepcionistaMapper
import com.edu.psiclinbe.model.Funcionario
import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.model.Recepcionista
import com.edu.psiclinbe.model.enums.Role
import com.edu.psiclinbe.repository.FuncionarioRepository
import com.edu.psiclinbe.repository.PsicologoRepository
import com.edu.psiclinbe.repository.RecepcionistaRepository
import com.edu.psiclinbe.service.FuncionarioService
import com.edu.psiclinbe.service.PsicologoService
import com.edu.psiclinbe.service.RecepcionistaService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.util.Optional

class PasswordManagementTests {

    @Test
    fun funcionarioUpdateLeavesPasswordUnchangedAndPasswordChangeEncodesIt() {
        val repository = Mockito.mock(FuncionarioRepository::class.java)
        val encoder = passwordEncoder()
        val funcionario = Funcionario(
            id = 1,
            nome = "Nome",
            cpf = "123",
            email = "funcionario@example.com",
            telefone = "11999999999",
            senha = "old-hash",
            role = Role.ADMIN,
        )
        Mockito.`when`(repository.findById(1)).thenReturn(Optional.of(funcionario))
        Mockito.`when`(repository.save(Mockito.any(Funcionario::class.java)))
            .thenAnswer { it.getArgument<Funcionario>(0) }

        val service = FuncionarioService(repository, FuncionarioMapper(), encoder)
        service.atualizar(
            1,
            FuncionarioDtoUpdateRequest(
                nome = funcionario.nome,
                cpf = funcionario.cpf,
                email = funcionario.email,
                telefone = funcionario.telefone,
            ),
        )
        assertEquals("old-hash", funcionario.senha)

        service.alterarSenha(1, AlterarSenhaDtoRequest("nova-senha"))
        assertEquals("encoded:nova-senha", funcionario.senha)
    }

    @Test
    fun psicologoUpdateLeavesPasswordUnchangedAndPasswordChangeEncodesIt() {
        val repository = Mockito.mock(PsicologoRepository::class.java)
        val funcionarioRepository = Mockito.mock(FuncionarioRepository::class.java)
        val encoder = passwordEncoder()
        val psicologo = Psicologo(crp = "CRP-123", especialidade = "TCC").apply {
            id = 1
            nome = "Nome"
            cpf = "123"
            email = "psicologo@example.com"
            telefone = "11999999999"
            senha = "old-hash"
        }
        Mockito.`when`(repository.findById(1)).thenReturn(Optional.of(psicologo))
        Mockito.`when`(repository.save(Mockito.any(Psicologo::class.java)))
            .thenAnswer { it.getArgument<Psicologo>(0) }

        val service = PsicologoService(
            repository,
            funcionarioRepository,
            PsicologoMapper(),
            encoder,
        )
        service.atualizar(
            1,
            PsicologoDtoUpdateRequest(
                nome = psicologo.nome,
                cpf = psicologo.cpf,
                email = psicologo.email,
                telefone = psicologo.telefone,
                crp = psicologo.crp,
                especialidade = psicologo.especialidade,
            ),
        )
        assertEquals("old-hash", psicologo.senha)

        service.alterarSenha(1, AlterarSenhaDtoRequest("nova-senha"))
        assertEquals("encoded:nova-senha", psicologo.senha)
    }

    @Test
    fun recepcionistaUpdateLeavesPasswordUnchangedAndPasswordChangeEncodesIt() {
        val repository = Mockito.mock(RecepcionistaRepository::class.java)
        val encoder = passwordEncoder()
        val recepcionista = Recepcionista(setor = "Recepção").apply {
            id = 1
            nome = "Nome"
            cpf = "123"
            email = "recepcionista@example.com"
            telefone = "11999999999"
            senha = "old-hash"
        }
        Mockito.`when`(repository.findById(1)).thenReturn(Optional.of(recepcionista))
        Mockito.`when`(repository.save(Mockito.any(Recepcionista::class.java)))
            .thenAnswer { it.getArgument<Recepcionista>(0) }

        val service = RecepcionistaService(repository, RecepcionistaMapper(), encoder)
        service.atualizarRecepcionista(
            1,
            RecepcionistaDtoUpdateRequest(
                nome = recepcionista.nome,
                cpf = recepcionista.cpf,
                email = recepcionista.email,
                telefone = recepcionista.telefone,
                setor = recepcionista.setor,
            ),
        )
        assertEquals("old-hash", recepcionista.senha)

        service.alterarSenha(1, AlterarSenhaDtoRequest("nova-senha"))
        assertEquals("encoded:nova-senha", recepcionista.senha)
    }

    @Test
    fun responseMappersIncludeRegistrationDate() {
        val dataCadastro = LocalDateTime.of(2026, 11, 15, 10, 30)
        val funcionario = Funcionario(
            id = 1,
            nome = "Nome",
            cpf = "123",
            email = "funcionario@example.com",
            telefone = "11999999999",
            senha = "hash",
            dataCadastro = dataCadastro,
            role = Role.ADMIN,
        )
        val psicologo = Psicologo(crp = "CRP-123", especialidade = "TCC").apply {
            id = 2
            this.dataCadastro = dataCadastro
        }
        val recepcionista = Recepcionista(setor = "Recepção").apply {
            id = 3
            this.dataCadastro = dataCadastro
        }

        assertEquals(dataCadastro, FuncionarioMapper().toResponse(funcionario).dataCadastro)
        assertEquals(dataCadastro, PsicologoMapper().toResponse(psicologo).dataCadastro)
        assertEquals(dataCadastro, RecepcionistaMapper().toResponse(recepcionista).dataCadastro)
    }

    private fun passwordEncoder(): PasswordEncoder =
        Mockito.mock(PasswordEncoder::class.java).also {
            Mockito.`when`(it.encode("nova-senha")).thenReturn("encoded:nova-senha")
        }
}
