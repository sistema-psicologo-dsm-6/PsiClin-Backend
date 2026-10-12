package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.PacienteDtoRequest
import com.edu.psiclinbe.dto.response.PacienteDtoResponse
import com.edu.psiclinbe.mapper.PacienteMapper
import com.edu.psiclinbe.repository.PacienteRepository
import org.springframework.stereotype.Service

@Service
class PacienteService(
    private val pacienteRepository: PacienteRepository,
    private val pacienteMapper: PacienteMapper
) {

    fun listarTodos(): List<PacienteDtoResponse> =
        pacienteRepository.findAll()
            .map(pacienteMapper::toResponse)

    fun listarAtivos(): List<PacienteDtoResponse> =
        pacienteRepository.findByAtivoTrue()
            .map(pacienteMapper::toResponse)

    fun buscarPorId(id: Long): PacienteDtoResponse =
        pacienteMapper.toResponse(
            pacienteRepository.findById(id)
                .orElseThrow {
                    NoSuchElementException(
                        "Paciente não encontrado com id: $id"
                    )
                }
        )

    fun buscarPorNome(nome: String): List<PacienteDtoResponse> =
        pacienteRepository.buscarPorNomeParcial(nome)
            .map(pacienteMapper::toResponse)

    fun buscarPorCpf(cpf: String): PacienteDtoResponse {

        val paciente = pacienteRepository.findByCpf(cpf)
            ?: throw NoSuchElementException(
                "Paciente não encontrado com CPF: $cpf"
            )

        return pacienteMapper.toResponse(paciente)
    }

    fun buscarPorEmail(email: String): PacienteDtoResponse {

        val paciente = pacienteRepository.findByEmail(email)
            ?: throw NoSuchElementException(
                "Paciente não encontrado com e-mail: $email"
            )

        return pacienteMapper.toResponse(paciente)
    }

    fun salvar(dto: PacienteDtoRequest): PacienteDtoResponse {

        pacienteRepository.findByCpf(dto.cpf)?.let {
            throw IllegalArgumentException(
                "Já existe um paciente cadastrado com este CPF"
            )
        }

        pacienteRepository.findByEmail(dto.email)?.let {
            throw IllegalArgumentException(
                "Já existe um paciente cadastrado com este e-mail"
            )
        }

        val paciente = pacienteMapper.toModel(dto)

        return pacienteMapper.toResponse(
            pacienteRepository.save(paciente)
        )
    }

    fun atualizar(
        id: Long,
        dto: PacienteDtoRequest
    ): PacienteDtoResponse {

        val paciente = pacienteRepository.findById(id)
            .orElseThrow {
                NoSuchElementException(
                    "Paciente não encontrado com id: $id"
                )
            }

        pacienteRepository.findByCpf(dto.cpf)?.let {
            if (it.id != id) {
                throw IllegalArgumentException(
                    "Já existe outro paciente cadastrado com este CPF"
                )
            }
        }

        pacienteRepository.findByEmail(dto.email)?.let {
            if (it.id != id) {
                throw IllegalArgumentException(
                    "Já existe outro paciente cadastrado com este e-mail"
                )
            }
        }

        paciente.apply {
            nome = dto.nome
            cpf = dto.cpf
            dataNascimento = dto.dataNascimento
            email = dto.email
            telefone = dto.telefone
            endereco = dto.endereco
            observacoes = dto.observacoes
            ativo = dto.ativo
        }

        return pacienteMapper.toResponse(
            pacienteRepository.save(paciente)
        )
    }

    fun inativar(id: Long): PacienteDtoResponse {

        val paciente = pacienteRepository.findById(id)
            .orElseThrow {
                NoSuchElementException(
                    "Paciente não encontrado com id: $id"
                )
            }

        paciente.ativo = false

        return pacienteMapper.toResponse(
            pacienteRepository.save(paciente)
        )
    }

    fun deletar(id: Long) {

        if (!pacienteRepository.existsById(id)) {
            throw NoSuchElementException(
                "Paciente não encontrado com id: $id"
            )
        }

        pacienteRepository.deleteById(id)
    }
}