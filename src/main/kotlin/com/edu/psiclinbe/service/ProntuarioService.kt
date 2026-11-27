package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.ProntuarioDtoRequest
import com.edu.psiclinbe.dto.response.ProntuarioDtoResponse
import com.edu.psiclinbe.mapper.ProntuarioMapper
import com.edu.psiclinbe.model.Prontuario
import com.edu.psiclinbe.model.enums.ProntuarioStatus
import com.edu.psiclinbe.repository.PacienteRepository
import com.edu.psiclinbe.repository.ProntuarioRepository
import org.springframework.stereotype.Service

@Service
class ProntuarioService(
    private val prontuarioRepository: ProntuarioRepository,
    private val pacienteRepository: PacienteRepository,
    private val prontuarioMapper: ProntuarioMapper,
) {
    fun buscarPorPaciente(id: Long): List<ProntuarioDtoResponse> =
        ActiveRecordVisibility.filter(prontuarioRepository.findByPacienteId(id)) { it.ativo }
            .map { prontuarioMapper.toResponse(it) }

    fun buscarPorPacienteEStatus(paciente: Long, status: ProntuarioStatus): List<ProntuarioDtoResponse> =
        ActiveRecordVisibility.filter(prontuarioRepository.findByPacienteIdAndStatus(paciente, status)) { it.ativo }
            .map { prontuarioMapper.toResponse(it) }

    fun buscarPorQueixaPrincipal(termo: String): List<ProntuarioDtoResponse> =
        ActiveRecordVisibility.filter(prontuarioRepository.buscarPorQueixaPrincipal(termo)) { it.ativo }
            .map(prontuarioMapper::toResponse)

    fun cadastrarProntuario(dto: ProntuarioDtoRequest): ProntuarioDtoResponse {
        val prontuario = prontuarioRepository.save(montar(dto.copy(id = null)))
        return prontuarioMapper.toResponse(prontuario)
    }

    fun listarProntuarios(): List<ProntuarioDtoResponse> =
        ActiveRecordVisibility.filter(prontuarioRepository.findAll()) { it.ativo }
            .map { prontuarioMapper.toResponse(it) }

    fun buscarProntuario(id: Long): ProntuarioDtoResponse =
        prontuarioMapper.toResponse(buscarEntidade(id))

    fun atualizarProntuario(id: Long, dto: ProntuarioDtoRequest): ProntuarioDtoResponse {
        buscarEntidade(id)
        val prontuario = prontuarioRepository.save(montar(dto.copy(id = id)))
        return prontuarioMapper.toResponse(prontuario)
    }

    fun excluirProntuario(id: Long) {
        val prontuario = buscarEntidade(id)
        prontuario.ativo = false
        prontuarioRepository.save(prontuario)
    }

    fun excluirPermanentemente(id: Long) {
        if (prontuarioRepository.excluirPermanentementePorId(id) == 0) {
            throw NoSuchElementException("Prontuário não encontrado com o id: $id.")
        }
    }

    private fun buscarEntidade(id: Long): Prontuario {
        val prontuario = prontuarioRepository.findById(id)
            .orElseThrow { NoSuchElementException("Prontuário não encontrado com o id: $id.") }
        ActiveRecordVisibility.requireVisible(prontuario.ativo, "Prontuário não encontrado com o id: $id.")
        return prontuario
    }

    private fun montar(dto: ProntuarioDtoRequest): Prontuario {
        val pacienteId = requireNotNull(dto.pacienteId)
        val paciente = pacienteRepository.findById(pacienteId)
            .orElseThrow { NoSuchElementException("Paciente não encontrado com o id: $pacienteId.") }
        ActiveRecordVisibility.requireVisible(paciente.ativo, "Paciente não encontrado com o id: $pacienteId.")
        return prontuarioMapper.toModel(dto, paciente)
    }
}
