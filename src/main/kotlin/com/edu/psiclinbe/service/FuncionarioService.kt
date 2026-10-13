package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.FuncionarioDtoRequest
import com.edu.psiclinbe.dto.response.FuncionarioDtoResponse
import com.edu.psiclinbe.mapper.FuncionarioMapper
import com.edu.psiclinbe.model.Funcionario
import com.edu.psiclinbe.model.enums.Role
import com.edu.psiclinbe.repository.FuncionarioRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class FuncionarioService(
    private val funcionarioRepository: FuncionarioRepository,
    private val funcionarioMapper: FuncionarioMapper,
    private val passwordEncoder: PasswordEncoder
) {

    fun listarTodos(): List<FuncionarioDtoResponse> =
        funcionarioRepository.findAll().map(funcionarioMapper::toResponse)

    fun listarAtivos(): List<FuncionarioDtoResponse> =
        funcionarioRepository.findByAtivoTrue().map(funcionarioMapper::toResponse)

    fun buscarPorId(id: Long): FuncionarioDtoResponse {
        val funcionario = funcionarioRepository.findById(id)
            .orElseThrow { NoSuchElementException("Funcionário não encontrado com id: $id") }
        return funcionarioMapper.toResponse(funcionario)
    }

    fun buscarPorNome(nome: String): List<FuncionarioDtoResponse> =
        funcionarioRepository.buscarPorNomeParcial(nome).map(funcionarioMapper::toResponse)

    fun buscarPorEmail(email: String): FuncionarioDtoResponse {
        val funcionario = funcionarioRepository.findByEmail(email)
            ?: throw NoSuchElementException("Funcionário não encontrado com e-mail: $email")
        return funcionarioMapper.toResponse(funcionario)
    }

    fun buscarPorCpf(cpf: String): FuncionarioDtoResponse {
        val funcionario = funcionarioRepository.findByCpf(cpf)
            ?: throw NoSuchElementException("Funcionário não encontrado com CPF: $cpf")
        return funcionarioMapper.toResponse(funcionario)
    }

    fun salvar(dto: FuncionarioDtoRequest): FuncionarioDtoResponse {
        funcionarioRepository.findByEmailAndCpf(dto.email, dto.cpf)?.let {
            throw IllegalArgumentException("Já existe um funcionário com este e-mail e CPF")
        }

        val funcionario = Funcionario(
            nome = dto.nome,
            cpf = dto.cpf,
            email = dto.email,
            telefone = dto.telefone,
            senha = passwordEncoder.encode(dto.senha)!!,
            ativo = dto.ativo,
            role = dto.role
        )

        return funcionarioMapper.toResponse(funcionarioRepository.save(funcionario))
    }

    @Transactional
    fun criarAdminInicial(dto: FuncionarioDtoRequest): FuncionarioDtoResponse {
        if (funcionarioRepository.count() > 0) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "O cadastro público está disponível somente para criar o primeiro usuário"
            )
        }

        return salvar(dto.copy(role = Role.ADMIN))
    }

    fun atualizar(id: Long, dto: FuncionarioDtoRequest): FuncionarioDtoResponse {
        val funcionario = funcionarioRepository.findById(id)
            .orElseThrow { NoSuchElementException("Funcionário não encontrado com id: $id") }

        funcionario.nome = dto.nome
        funcionario.cpf = dto.cpf
        funcionario.email = dto.email
        funcionario.telefone = dto.telefone
        funcionario.ativo = dto.ativo

        if (dto.senha.isNotBlank()) {
            funcionario.senha =
                passwordEncoder.encode(dto.senha)!!
        }

        return funcionarioMapper.toResponse(funcionarioRepository.save(funcionario))
    }

    fun inativar(id: Long): FuncionarioDtoResponse {
        val funcionario = funcionarioRepository.findById(id)
            .orElseThrow { NoSuchElementException("Funcionário não encontrado com id: $id") }

        funcionario.ativo = false
        return funcionarioMapper.toResponse(funcionarioRepository.save(funcionario))
    }

    fun deletar(id: Long) {
        if (!funcionarioRepository.existsById(id)) {
            throw NoSuchElementException("Funcionário não encontrado com id: $id")
        }
        funcionarioRepository.deleteById(id)
    }
}
