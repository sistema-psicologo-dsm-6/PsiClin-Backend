package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.FuncionarioDtoRequest
import com.edu.psiclinbe.dto.response.FuncionarioDtoResponse
import com.edu.psiclinbe.model.Funcionario
import com.edu.psiclinbe.repository.FuncionarioRepository
import org.springframework.stereotype.Service

@Service
class FuncionarioService(
    private val funcionarioRepository: FuncionarioRepository
) {

    fun listarTodos(): List<FuncionarioDtoResponse> =
        funcionarioRepository.findAll().map { it.toDto() }

    fun listarAtivos(): List<FuncionarioDtoResponse> =
        funcionarioRepository.findByAtivoTrue().map { it.toDto() }

    fun buscarPorId(id: Long): FuncionarioDtoResponse {
        val funcionario = funcionarioRepository.findById(id)
            .orElseThrow { NoSuchElementException("Funcionário não encontrado com id: $id") }
        return funcionario.toDto()
    }

    fun buscarPorNome(nome: String): List<FuncionarioDtoResponse> =
        funcionarioRepository.buscarPorNomeParcial(nome).map { it.toDto() }

    fun buscarPorEmail(email: String): FuncionarioDtoResponse {
        val funcionario = funcionarioRepository.findByEmail(email)
            ?: throw NoSuchElementException("Funcionário não encontrado com e-mail: $email")
        return funcionario.toDto()
    }

    fun buscarPorCpf(cpf: String): FuncionarioDtoResponse {
        val funcionario = funcionarioRepository.findByCpf(cpf)
            ?: throw NoSuchElementException("Funcionário não encontrado com CPF: $cpf")
        return funcionario.toDto()
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
            senha = dto.senha,
            ativo = dto.ativo
        )

        return funcionarioRepository.save(funcionario).toDto()
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
            funcionario.senha = dto.senha
        }

        return funcionarioRepository.save(funcionario).toDto()
    }

    fun inativar(id: Long): FuncionarioDtoResponse {
        val funcionario = funcionarioRepository.findById(id)
            .orElseThrow { NoSuchElementException("Funcionário não encontrado com id: $id") }

        funcionario.ativo = false
        return funcionarioRepository.save(funcionario).toDto()
    }

    fun deletar(id: Long) {
        if (!funcionarioRepository.existsById(id)) {
            throw NoSuchElementException("Funcionário não encontrado com id: $id")
        }
        funcionarioRepository.deleteById(id)
    }

    private fun Funcionario.toDto(): FuncionarioDtoResponse = FuncionarioDtoResponse(
        id = this.id,
        nome = this.nome,
        cpf = this.cpf,
        email = this.email,
        telefone = this.telefone,
        senha = null,
        ativo = this.ativo
    )
}