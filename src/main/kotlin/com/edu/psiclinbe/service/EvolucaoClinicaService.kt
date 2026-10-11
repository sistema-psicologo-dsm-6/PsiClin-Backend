package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.EvolucaoClinicaDtoRequest
import com.edu.psiclinbe.dto.response.EvolucaoClinicaDtoResponse
import com.edu.psiclinbe.mapper.EvolucaoClinicaMapper
import com.edu.psiclinbe.model.Agenda
import com.edu.psiclinbe.model.EvolucaoClinica
import com.edu.psiclinbe.model.Prontuario
import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.repository.AgendaRepository
import com.edu.psiclinbe.repository.EvolucaoClinicaRepository
import com.edu.psiclinbe.repository.ProntuarioRepository
import com.edu.psiclinbe.repository.PsicologoRepository
import org.springframework.stereotype.Service

@Service
class EvolucaoClinicaService(
    private val evolucaoClinicaRepository: EvolucaoClinicaRepository,
    private val prontuarioRepository: ProntuarioRepository,
    private val psicologoRepository: PsicologoRepository,
    private val agendaRepository: AgendaRepository,
    private val evolucaoClinicaMapper: EvolucaoClinicaMapper
) {

    fun listarTodos(): List<EvolucaoClinicaDtoResponse> =
        evolucaoClinicaRepository.findAll().map(evolucaoClinicaMapper::toResponse)

    fun buscarPorId(id: Long): EvolucaoClinicaDtoResponse =
        evolucaoClinicaMapper.toResponse(buscarEvolucao(id))

    fun buscarPorDescricao(termo: String): List<EvolucaoClinicaDtoResponse> =
        evolucaoClinicaRepository.buscarPorDescricao(termo)
            .map(evolucaoClinicaMapper::toResponse)

    fun buscarPorProntuario(prontuarioId: Long): List<EvolucaoClinicaDtoResponse> =
        evolucaoClinicaRepository.findByProntuarioId(prontuarioId)
            .map(evolucaoClinicaMapper::toResponse)

    fun buscarPorPsicologo(psicologoId: Long): List<EvolucaoClinicaDtoResponse> =
        evolucaoClinicaRepository.findByPsicologoId(psicologoId)
            .map(evolucaoClinicaMapper::toResponse)

    fun buscarPorAgenda(
        agendaId: Long
    ): List<EvolucaoClinicaDtoResponse> =
        evolucaoClinicaRepository
            .findByAgendaId(agendaId)
            .map(evolucaoClinicaMapper::toResponse)

    fun salvar(dto: EvolucaoClinicaDtoRequest): EvolucaoClinicaDtoResponse {
        val prontuario = buscarProntuario(dto.prontuarioId)
        val psicologo = buscarPsicologo(dto.psicologoId)
        val agenda = buscarAgenda(dto.agendaId)

        val evolucao = evolucaoClinicaMapper.toModel(dto, prontuario, psicologo, agenda)

        return evolucaoClinicaMapper.toResponse(evolucaoClinicaRepository.save(evolucao))
    }

    fun atualizar(id: Long, dto: EvolucaoClinicaDtoRequest): EvolucaoClinicaDtoResponse {
        val evolucao = buscarEvolucao(id)
        val prontuario = buscarProntuario(dto.prontuarioId)
        val psicologo = buscarPsicologo(dto.psicologoId)
        val agenda = buscarAgenda(dto.agendaId)

        evolucao.prontuario = prontuario
        evolucao.psicologo = psicologo
        evolucao.agenda = agenda
        evolucao.dataRegistro = dto.dataRegistro
        evolucao.descricao = dto.descricao

        return evolucaoClinicaMapper.toResponse(evolucaoClinicaRepository.save(evolucao))
    }

    fun deletar(id: Long) {
        val evolucao = buscarEvolucao(id)
        evolucao.ativo = false
        evolucaoClinicaRepository.save(evolucao)
    }

    fun excluirPermanentemente(id: Long) {
        if (evolucaoClinicaRepository.excluirPermanentementePorId(id) == 0) {
            throw NoSuchElementException("Evolução clínica não encontrada com id: $id")
        }
    }

    private fun buscarEvolucao(id: Long): EvolucaoClinica =
        evolucaoClinicaRepository.findById(id)
            .orElseThrow { NoSuchElementException("Evolução clínica não encontrada com id: $id") }

    private fun buscarProntuario(id: Long): Prontuario =
        prontuarioRepository.findById(id)
            .orElseThrow { NoSuchElementException("Prontuário não encontrado com id: $id") }

    private fun buscarPsicologo(id: Long): Psicologo =
        psicologoRepository.findById(id)
            .orElseThrow { NoSuchElementException("Psicólogo não encontrado com id: $id") }

    private fun buscarAgenda(id: Long): Agenda =
        agendaRepository.findById(id)
            .orElseThrow { NoSuchElementException("Agenda não encontrada com id: $id") }
}