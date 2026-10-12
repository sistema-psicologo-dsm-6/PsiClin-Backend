package com.edu.psiclinbe.service
import com.edu.psiclinbe.model.enums.DiaSemana

import com.edu.psiclinbe.dto.request.HorarioDisponivelDtoRequest
import com.edu.psiclinbe.dto.response.HorarioDisponivelDtoResponse
import com.edu.psiclinbe.mapper.HorarioDisponivelMapper
import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.repository.HorarioDisponivelRepository
import com.edu.psiclinbe.repository.PsicologoRepository
import com.edu.psiclinbe.validation.ValidarDataInicioFim
import org.springframework.stereotype.Service
import java.time.LocalTime

@Service
class HorarioDisponivelService(
    private val horarioDisponivelRepository: HorarioDisponivelRepository,
    private val psicologoRepository: PsicologoRepository,
    private val horarioDisponivelMapper: HorarioDisponivelMapper,
    private val validarDataInicioFim: ValidarDataInicioFim
) {

    fun listarTodos(): List<HorarioDisponivelDtoResponse> =
        horarioDisponivelRepository.findAll()
            .map(horarioDisponivelMapper::toResponse)

    fun buscarPorId(id: Long): HorarioDisponivelDtoResponse =
        horarioDisponivelMapper.toResponse(
            horarioDisponivelRepository.findById(id)
                .orElseThrow {
                    NoSuchElementException(
                        "Horário disponível não encontrado com id: $id"
                    )
                }
        )

    fun buscarPorDescricao(termo: String): List<HorarioDisponivelDtoResponse> =
        horarioDisponivelRepository.buscarPorDescricao(termo)
            .map(horarioDisponivelMapper::toResponse)

    fun buscarPorPsicologo(psicologoId: Long): List<HorarioDisponivelDtoResponse> =
        horarioDisponivelRepository.findByPsicologoId(psicologoId)
            .map(horarioDisponivelMapper::toResponse)

    fun buscarPorPsicologoEDia(
        psicologoId: Long,
        diaSemana: String
    ): List<HorarioDisponivelDtoResponse> =
        horarioDisponivelRepository.findByPsicologoIdAndDiaSemana(
            psicologoId,
            DiaSemana.valueOf(diaSemana)
        ).map(horarioDisponivelMapper::toResponse)

    fun salvar(dto: HorarioDisponivelDtoRequest): HorarioDisponivelDtoResponse {

        validarDataInicioFim.validarHorario(
            dto.horaInicio,
            dto.horaFim
        )

        val psicologo = buscarPsicologo(dto.psicologoId!!)

        verificarConflitoDeHorario(
            psicologoId = psicologo.id!!,
            diaSemana = dto.diaSemana,
            inicio = dto.horaInicio,
            fim = dto.horaFim,
            horarioIdIgnorar = null
        )

        val horario = horarioDisponivelMapper.toModel(
            dto,
            psicologo
        )

        return horarioDisponivelMapper.toResponse(
            horarioDisponivelRepository.save(horario)
        )
    }

    fun atualizar(
        id: Long,
        dto: HorarioDisponivelDtoRequest
    ): HorarioDisponivelDtoResponse {

        val horario = horarioDisponivelRepository.findById(id)
            .orElseThrow {
                NoSuchElementException(
                    "Horário disponível não encontrado com id: $id"
                )
            }

        validarDataInicioFim.validarHorario(
            dto.horaInicio,
            dto.horaFim
        )

        val psicologo = buscarPsicologo(dto.psicologoId!!)

        verificarConflitoDeHorario(
            psicologoId = psicologo.id!!,
            diaSemana = dto.diaSemana,
            inicio = dto.horaInicio,
            fim = dto.horaFim,
            horarioIdIgnorar = id
        )

        horario.apply {
            this.psicologo = psicologo
            diaSemana = DiaSemana.valueOf(dto.diaSemana)
            horaInicio = dto.horaInicio
            horaFim = dto.horaFim
            descricao = dto.descricao
        }

        return horarioDisponivelMapper.toResponse(
            horarioDisponivelRepository.save(horario)
        )
    }

    fun deletar(id: Long) {
        val horario = horarioDisponivelRepository.findById(id)
            .orElseThrow {
                NoSuchElementException("Horário disponível não encontrado com id: $id")
            }
        horario.ativo = false
        horarioDisponivelRepository.save(horario)
    }

    fun excluirPermanentemente(id: Long) {
        if (horarioDisponivelRepository.excluirPermanentementePorId(id) == 0) {
            throw NoSuchElementException("Horário disponível não encontrado com id: $id")
        }
    }

    private fun buscarPsicologo(id: Long): Psicologo =
        psicologoRepository.findById(id)
            .orElseThrow {
                NoSuchElementException(
                    "Psicólogo não encontrado com id: $id"
                )
            }

    private fun verificarConflitoDeHorario(
        psicologoId: Long,
        diaSemana: String,
        inicio: LocalTime,
        fim: LocalTime,
        horarioIdIgnorar: Long?
    ) {

        val conflito =
            horarioDisponivelRepository.findByPsicologoIdAndDiaSemana(
                psicologoId,
                DiaSemana.valueOf(diaSemana)
            )
                .filter { it.id != horarioIdIgnorar }
                .any {
                    it.horaInicio.isBefore(fim) &&
                            inicio.isBefore(it.horaFim)
                }

        if (conflito) {
            throw IllegalArgumentException(
                "Já existe um horário disponível cadastrado para este psicólogo neste dia e período"
            )
        }
    }
}