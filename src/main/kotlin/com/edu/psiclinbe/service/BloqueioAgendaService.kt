package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.BloqueioAgendaDtoRequest
import com.edu.psiclinbe.dto.response.BloqueioAgendaDtoResponse
import com.edu.psiclinbe.mapper.BloqueioAgendaMapper
import com.edu.psiclinbe.model.BloqueioAgenda
import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.repository.BloqueioAgendaRepository
import com.edu.psiclinbe.repository.PsicologoRepository
import com.edu.psiclinbe.validation.ValidarDataInicioFim
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BloqueioAgendaService(

    private val bloqueioAgendaRepository: BloqueioAgendaRepository,
    private val psicologoRepository: PsicologoRepository,
    private val bloqueioAgendaMapper: BloqueioAgendaMapper,
    private val validarDataInicioFim: ValidarDataInicioFim

) {

    fun listarTodos(): List<BloqueioAgendaDtoResponse> =
        bloqueioAgendaRepository.findAllByAtivoTrue()
            .map(bloqueioAgendaMapper::toResponse)

    fun buscarPorId(id: Long): BloqueioAgendaDtoResponse =
        bloqueioAgendaMapper.toResponse(buscarEntidadeAtiva(id))

    fun buscarPorMotivo(termo: String): List<BloqueioAgendaDtoResponse> =
        bloqueioAgendaRepository.buscarPorMotivo(termo)
            .map(bloqueioAgendaMapper::toResponse)

    fun buscarPorPsicologo(
        psicologoId: Long
    ): List<BloqueioAgendaDtoResponse> =
        bloqueioAgendaRepository.findByPsicologoIdAndAtivoTrue(psicologoId)
            .map(bloqueioAgendaMapper::toResponse)

    fun buscarPorPeriodo(
        inicio: LocalDateTime,
        fim: LocalDateTime
    ): List<BloqueioAgendaDtoResponse> =
        bloqueioAgendaRepository
            .findByDataHoraInicioBetween(inicio, fim)
            .map(bloqueioAgendaMapper::toResponse)

    fun salvar(
        dto: BloqueioAgendaDtoRequest
    ): BloqueioAgendaDtoResponse {

        validarDataInicioFim.validarHorario(
            dto.dataHoraInicio.toLocalTime(),
            dto.dataHoraFim.toLocalTime()
        )

        val psicologo = psicologoRepository.findById(
            dto.psicologoId
        )
            .orElseThrow {
                NoSuchElementException("Psicólogo não encontrado com o id: ${dto.psicologoId}")
            }

        verificarConflitoDeHorario(
            psicologoId = psicologo.id!!,
            inicio = dto.dataHoraInicio,
            fim = dto.dataHoraFim,
            bloqueioIdIgnorar = null
        )

        val bloqueio = bloqueioAgendaMapper.toModel(
            dto,
            psicologo
        )

        return bloqueioAgendaMapper.toResponse(
            bloqueioAgendaRepository.save(bloqueio)
        )
    }

    fun atualizar(
        id: Long,
        dto: BloqueioAgendaDtoRequest
    ): BloqueioAgendaDtoResponse {

        val bloqueio = buscarEntidadeAtiva(id)

        validarDataInicioFim.validarHorario(
            dto.dataHoraInicio.toLocalTime(),
            dto.dataHoraFim.toLocalTime()
        )

        val psicologo = psicologoRepository.findById(
            dto.psicologoId
        )
            .orElseThrow {
                NoSuchElementException("Psicólogo não encontrado com o id: ${dto.psicologoId}")
            }

        verificarConflitoDeHorario(
            psicologoId = psicologo.id!!,
            inicio = dto.dataHoraInicio,
            fim = dto.dataHoraFim,
            bloqueioIdIgnorar = id
        )

        bloqueio.apply {
            this.psicologo = psicologo
            this.dataHoraInicio = dto.dataHoraInicio
            this.dataHoraFim = dto.dataHoraFim
            this.motivo = dto.motivo
        }

        return bloqueioAgendaMapper.toResponse(
            bloqueioAgendaRepository.save(bloqueio)
        )
    }

    fun deletar(id: Long) {
        val bloqueio = buscarEntidadeAtiva(id)
        bloqueio.ativo = false
        bloqueioAgendaRepository.save(bloqueio)
    }

    fun excluirPermanentemente(id: Long) {
        if (!bloqueioAgendaRepository.existsById(id)) {
            throw NoSuchElementException("Bloqueio de agenda não encontrado com id: $id")
        }
        bloqueioAgendaRepository.deleteById(id)
    }

    private fun verificarConflitoDeHorario(
        psicologoId: Long,
        inicio: LocalDateTime,
        fim: LocalDateTime,
        bloqueioIdIgnorar: Long?
    ) {

        val conflito =
            bloqueioAgendaRepository
                .findByPsicologoIdAndAtivoTrue(psicologoId)
                .filter {
                    it.id != bloqueioIdIgnorar
                }
                .any { existente ->
                    existente.dataHoraInicio.isBefore(fim) &&
                            inicio.isBefore(existente.dataHoraFim)
                }

        if (conflito) {
            throw IllegalArgumentException(
                "Já existe um bloqueio para este psicólogo neste horário"
            )
        }
    }

    private fun buscarEntidadeAtiva(id: Long): BloqueioAgenda =
        bloqueioAgendaRepository.findByIdAndAtivoTrue(id)
            ?: throw NoSuchElementException("Bloqueio não encontrado com id: $id")
}