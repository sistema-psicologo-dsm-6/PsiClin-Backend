package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.FuncionarioDtoRequest
import com.edu.psiclinbe.dto.response.FuncionarioDtoResponse
import com.edu.psiclinbe.mapper.FuncionarioMapper
import com.edu.psiclinbe.model.Funcionario
import com.edu.psiclinbe.model.enums.Role
import com.edu.psiclinbe.repository.FuncionarioRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FuncionarioService(
    private val funcionarioRepository: FuncionarioRepository,
    private val funcionarioMapper: FuncionarioMapper,
    private val passwordEncoder: PasswordEncoder
) {

    fun listarTodos(): List<FuncionarioDtoResponse> =
        funcionarioRepository.findAll()
            .filter { it.role == Role.ADMIN }
            .map(funcionarioMapper::toResponse)

    fun listarAtivos(): List<FuncionarioDtoResponse> =
        funcionarioRepository.findByAtivoTrue()
            .filter { it.role == Role.ADMIN }
            .map(funcionarioMapper::toResponse)

    fun buscarPorId(id: Long): FuncionarioDtoResponse {
        val funcionario = funcionarioRepository.findById(id)
            .orElseThrow { NoSuchElementException("Funcionário não encontrado com id: $id") }
        return toAdminResponse(funcionario)
    }

    fun buscarPorNome(nome: String): List<FuncionarioDtoResponse> =
        funcionarioRepository.buscarPorNomeParcial(nome)
            .filter { it.role == Role.ADMIN }
            .map(funcionarioMapper::toResponse)

    fun buscarPorEmail(email: String): FuncionarioDtoResponse {
        val funcionario = funcionarioRepository.findByEmail(email)
            ?: throw NoSuchElementException("Funcionário não encontrado com e-mail: $email")
        return toAdminResponse(funcionario)
    }

    fun buscarPorCpf(cpf: String): FuncionarioDtoResponse {
        val funcionario = funcionarioRepository.findByCpf(cpf)
            ?: throw NoSuchElementException("Funcionário não encontrado com CPF: $cpf")
        return toAdminResponse(funcionario)
    }

    fun salvar(dto: FuncionarioDtoRequest): FuncionarioDtoResponse {
        val senhaNova = dto.senha?.takeIf { it.isNotBlank() }
            ?: throw IllegalArgumentException("A senha não pode estar vazia")

        funcionarioRepository.findByEmailAndCpf(dto.email, dto.cpf)?.let {
            throw IllegalArgumentException("Já existe um funcionário com este e-mail e CPF")
        }

        val funcionario = Funcionario(
            nome = dto.nome,
            cpf = dto.cpf,
            email = dto.email,
            telefone = dto.telefone,
            senha = passwordEncoder.encode(senhaNova)!!,
            ativo = dto.ativo,
            role = dto.role
        )

        return funcionarioMapper.toResponse(funcionarioRepository.save(funcionario))
    }

    fun atualizar(id: Long, dto: FuncionarioDtoRequest): FuncionarioDtoResponse {
        val funcionario = funcionarioRepository.findById(id)
            .orElseThrow { NoSuchElementException("Funcionário não encontrado com id: $id") }

        funcionario.nome = dto.nome
        funcionario.cpf = dto.cpf
        funcionario.email = dto.email
        funcionario.telefone = dto.telefone
        funcionario.ativo = dto.ativo

        if (!dto.senha.isNullOrBlank()) {
            funcionario.senha =
                passwordEncoder.encode(dto.senha!!)!!
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

    private fun toAdminResponse(funcionario: Funcionario): FuncionarioDtoResponse {
        if (funcionario.role != Role.ADMIN) {
            throw NoSuchElementException("Funcionário não encontrado")
        }
        return funcionarioMapper.toResponse(funcionario)
    }

    @Transactional
    fun criarAdminInicial(dto: FuncionarioDtoRequest): FuncionarioDtoResponse {
        if (funcionarioRepository.count() > 0) {
            throw org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.CONFLICT,
                "Já existe um funcionário inicial cadastrado",
            )
        }
        return salvar(dto.copy(role = Role.ADMIN, ativo = true))
    }
}
