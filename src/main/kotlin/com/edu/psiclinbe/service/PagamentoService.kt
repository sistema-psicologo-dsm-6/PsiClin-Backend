package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.PagamentoDtoRequest
import com.edu.psiclinbe.dto.response.PagamentoDtoResponse
import com.edu.psiclinbe.mapper.PagamentoMapper
import com.edu.psiclinbe.model.Pagamento
import com.edu.psiclinbe.model.enums.PagamentoStatus
import com.edu.psiclinbe.repository.AgendaRepository
import com.edu.psiclinbe.repository.ConvenioRepository
import com.edu.psiclinbe.repository.PacienteRepository
import com.edu.psiclinbe.repository.PagamentoRepository
import org.springframework.stereotype.Service

@Service
class PagamentoService(
    private val pagamentoRepository: PagamentoRepository,
    private val agendaRepository: AgendaRepository,
    private val pacienteRepository: PacienteRepository,
    private val convenioRepository: ConvenioRepository,
    private val pagamentoMapper: PagamentoMapper,
) {

    fun lerTodos(): List<PagamentoDtoResponse> =
        pagamentoRepository.findAll().map { pagamentoMapper.toResponse(it) }

    fun buscarPorId(id: Long): PagamentoDtoResponse =
        pagamentoMapper.toResponse(buscarEntidade(id))

    fun buscarPorAgenda(agendaId: Long): PagamentoDtoResponse {
        val pagamento = pagamentoRepository.findByAgendaId(agendaId)
            ?: throw NoSuchElementException("Pagamento não encontrado para a agenda: $agendaId.")
        return pagamentoMapper.toResponse(pagamento)
    }

    fun buscarPorPaciente(id: Long): List<PagamentoDtoResponse> =
        pagamentoRepository.findByPacienteId(id).map { pagamentoMapper.toResponse(it) }

    fun buscarPorConvenio(id: Long): List<PagamentoDtoResponse> =
        pagamentoRepository.findByConvenioId(id).map { pagamentoMapper.toResponse(it) }

    fun buscarPorStatus(status: PagamentoStatus): List<PagamentoDtoResponse> =
        pagamentoRepository.findByStatus(status).map { pagamentoMapper.toResponse(it) }

    fun buscarPorFormaPagamento(termo: String): List<PagamentoDtoResponse> =
        pagamentoRepository.buscarPorFormaPagamento(termo)
            .map(pagamentoMapper::toResponse)

    fun registrarPagamento(dto: PagamentoDtoRequest): PagamentoDtoResponse {
        val pagamento = pagamentoRepository.save(montar(dto.copy(id = null)))
        return pagamentoMapper.toResponse(pagamento)
    }

    fun atualizar(id: Long, dto: PagamentoDtoRequest): PagamentoDtoResponse {
        buscarEntidade(id)
        val pagamento = pagamentoRepository.save(montar(dto.copy(id = id)))
        return pagamentoMapper.toResponse(pagamento)
    }

    fun deletarPagamento(id: Long) {
        pagamentoRepository.delete(buscarEntidade(id))
    }

    private fun buscarEntidade(id: Long): Pagamento =
        pagamentoRepository.findById(id)
            .orElseThrow { NoSuchElementException("Pagamento não encontrado com o id: $id.") }

    private fun montar(dto: PagamentoDtoRequest): Pagamento {
        val agendaId = requireNotNull(dto.agendaId)
        val pacienteId = requireNotNull(dto.pacienteId)
        val agenda = agendaRepository.findById(agendaId)
            .orElseThrow { NoSuchElementException("Agenda não encontrada com o id: $agendaId.") }
        val paciente = pacienteRepository.findById(pacienteId)
            .orElseThrow { NoSuchElementException("Paciente não encontrado com o id: $pacienteId.") }
        val convenio = dto.convenioId?.let {
            convenioRepository.findById(it)
                .orElseThrow { NoSuchElementException("Convênio não encontrado com o id: $it.") }
        }
        return pagamentoMapper.toModel(dto, agenda, paciente, convenio)
    }
}