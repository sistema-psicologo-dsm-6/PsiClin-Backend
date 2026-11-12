package com.edu.psiclinbe.service

import com.edu.psiclinbe.dto.request.AgendaDtoRequest
import com.edu.psiclinbe.dto.response.AgendaDtoResponse
import com.edu.psiclinbe.mapper.AgendaMapper
import com.edu.psiclinbe.model.Convenio
import com.edu.psiclinbe.model.enums.DiaSemana
import com.edu.psiclinbe.model.Paciente
import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.model.SalaAtendimento
import com.edu.psiclinbe.model.TipoAtendimento
import com.edu.psiclinbe.model.enums.AgendaStatus
import com.edu.psiclinbe.repository.AgendaRepository
import com.edu.psiclinbe.repository.BloqueioAgendaRepository
import com.edu.psiclinbe.repository.ConvenioRepository
import com.edu.psiclinbe.repository.HorarioDisponivelRepository
import com.edu.psiclinbe.repository.PacienteRepository
import com.edu.psiclinbe.repository.PsicologoRepository
import com.edu.psiclinbe.repository.SalaAtendimentoRepository
import com.edu.psiclinbe.repository.TipoAtendimentoRepository
import com.edu.psiclinbe.validation.ValidarDataInicioFim
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class AgendaService(
    private val agendaRepository: AgendaRepository,
    private val horarioDisponivelRepository: HorarioDisponivelRepository,
    private val bloqueioAgendaRepository: BloqueioAgendaRepository,
    private val pacienteRepository: PacienteRepository,
    private val psicologoRepository: PsicologoRepository,
    private val salaRepository: SalaAtendimentoRepository,
    private val tipoAtendimentoRepository: TipoAtendimentoRepository,
    private val convenioRepository: ConvenioRepository,
    private val agendaMapper: AgendaMapper,
    private val validarDataInicioFim: ValidarDataInicioFim
) {

    private val statusQueReservamHorario = setOf(
        AgendaStatus.AGENDADO,
        AgendaStatus.CONFIRMADO
    )

    private data class AgendaRelacionamentos(
        val paciente: Paciente,
        val psicologo: Psicologo,
        val sala: SalaAtendimento?,
        val tipoAtendimento: TipoAtendimento,
        val convenio: Convenio?
    )

    fun listarTodos(): List<AgendaDtoResponse> =
        agendaRepository.findAll()
            .map(agendaMapper::toResponse)

    fun buscarPorId(id: Long): AgendaDtoResponse =
        agendaMapper.toResponse(
            agendaRepository.findById(id)
                .orElseThrow {
                    NoSuchElementException(
                        "Agendamento não encontrado com id: $id"
                    )
                }
        )

    fun buscarPorObservacoes(termo: String): List<AgendaDtoResponse> =
        agendaRepository.buscarPorObservacoes(termo)
            .map(agendaMapper::toResponse)

    fun listarHorariosDisponiveis(
        psicologoId: Long,
        data: LocalDate,
        duracaoMinutos: Long
    ): List<LocalTime> {
        require(duracaoMinutos > 0) {
            "A duração do atendimento deve ser maior que zero"
        }

        psicologoRepository.findById(psicologoId)
            .orElseThrow {
                NoSuchElementException("Psicólogo não encontrado com id: $psicologoId")
            }

        val inicioDia = data.atStartOfDay()
        val fimDia = data.plusDays(1).atStartOfDay()
        val horarios = horarioDisponivelRepository
            .findByPsicologoIdAndDiaSemana(psicologoId, diaSemana(data.dayOfWeek))
        val agendas = agendaRepository.findByPsicologoId(psicologoId)
            .filter { it.status in statusQueReservamHorario }
        val bloqueios = bloqueioAgendaRepository
            .findByPsicologoIdAndAtivoTrue(psicologoId)
            .filter { sobrepoe(inicioDia, fimDia, it.dataHoraInicio, it.dataHoraFim) }
        val disponiveis = mutableSetOf<LocalTime>()

        horarios.forEach { horario ->
            var inicioSlot = data.atTime(horario.horaInicio)
            val fimHorario = data.atTime(horario.horaFim)

            while (!inicioSlot.plusMinutes(duracaoMinutos).isAfter(fimHorario)) {
                val fimSlot = inicioSlot.plusMinutes(duracaoMinutos)
                val conflitoAgenda = agendas.any {
                    sobrepoe(inicioSlot, fimSlot, it.dataHoraInicio, it.dataHoraFim)
                }
                val conflitoBloqueio = bloqueios.any {
                    sobrepoe(inicioSlot, fimSlot, it.dataHoraInicio, it.dataHoraFim)
                }

                if (!conflitoAgenda && !conflitoBloqueio) {
                    disponiveis.add(inicioSlot.toLocalTime())
                }

                inicioSlot = fimSlot
            }
        }

        return disponiveis.sorted()
    }

    fun salvar(dto: AgendaDtoRequest): AgendaDtoResponse {

        validarDataInicioFim.validarDataHora(
            dto.dataHoraInicio,
            dto.dataHoraFim
        )

        val rel = buscarRelacionamentos(dto)

        verificarDisponibilidadePsicologo(
            psicologoId = rel.psicologo.id!!,
            inicio = dto.dataHoraInicio,
            fim = dto.dataHoraFim
        )

        verificarConflitoDeHorario(
            psicologoId = rel.psicologo.id!!,
            salaId = rel.sala?.id,
            inicio = dto.dataHoraInicio,
            fim = dto.dataHoraFim,
            agendaIdIgnorar = null
        )

        val agenda = agendaMapper.toModel(
            dto,
            rel.paciente,
            rel.psicologo,
            rel.sala,
            rel.tipoAtendimento,
            rel.convenio
        )

        return agendaMapper.toResponse(
            agendaRepository.save(agenda)
        )
    }

    fun atualizar(
        id: Long,
        dto: AgendaDtoRequest
    ): AgendaDtoResponse {

        val agenda = agendaRepository.findById(id)
            .orElseThrow {
                NoSuchElementException(
                    "Agendamento não encontrado com id: $id"
                )
            }

        validarDataInicioFim.validarDataHora(
            dto.dataHoraInicio,
            dto.dataHoraFim
        )

        val rel = buscarRelacionamentos(dto)

        verificarDisponibilidadePsicologo(
            psicologoId = rel.psicologo.id!!,
            inicio = dto.dataHoraInicio,
            fim = dto.dataHoraFim
        )

        verificarConflitoDeHorario(
            psicologoId = rel.psicologo.id!!,
            salaId = rel.sala?.id,
            inicio = dto.dataHoraInicio,
            fim = dto.dataHoraFim,
            agendaIdIgnorar = id
        )

        agenda.apply {
            paciente = rel.paciente
            psicologo = rel.psicologo
            sala = rel.sala!!
            tipoAtendimento = rel.tipoAtendimento
            convenio = rel.convenio!!
            dataHoraInicio = dto.dataHoraInicio
            dataHoraFim = dto.dataHoraFim
            status = dto.status
            observacoes = dto.observacoes
        }

        return agendaMapper.toResponse(
            agendaRepository.save(agenda)
        )
    }

    fun atualizarStatus(
        id: Long,
        status: AgendaStatus
    ): AgendaDtoResponse {

        val agenda = agendaRepository.findById(id)
            .orElseThrow {
                NoSuchElementException(
                    "Agendamento não encontrado com id: $id"
                )
            }

        agenda.status = status

        return agendaMapper.toResponse(
            agendaRepository.save(agenda)
        )
    }

    fun deletar(id: Long) {
        val agenda = agendaRepository.findById(id)
            .orElseThrow {
                NoSuchElementException("Agendamento não encontrado com id: $id")
            }
        agenda.ativo = false
        agendaRepository.save(agenda)
    }

    fun excluirPermanentemente(id: Long) {
        if (agendaRepository.excluirPermanentementePorId(id) == 0) {
            throw NoSuchElementException("Agendamento não encontrado com id: $id")
        }
    }

    private fun buscarRelacionamentos(
        dto: AgendaDtoRequest
    ): AgendaRelacionamentos {

        val paciente = pacienteRepository.findById(dto.pacienteId)
            .orElseThrow {
                NoSuchElementException(
                    "Paciente não encontrado com id: ${dto.pacienteId}"
                )
            }

        val psicologo = psicologoRepository.findById(dto.psicologoId)
            .orElseThrow {
                NoSuchElementException(
                    "Psicólogo não encontrado com id: ${dto.psicologoId}"
                )
            }

        val tipoAtendimento =
            tipoAtendimentoRepository.findById(dto.tipoAtendimentoId)
                .orElseThrow {
                    NoSuchElementException(
                        "Tipo de atendimento não encontrado com id: ${dto.tipoAtendimentoId}"
                    )
                }

        val sala = dto.salaId.let {
            salaRepository.findById(it)
                .orElseThrow {
                    NoSuchElementException(
                        "Sala não encontrada com id: $it"
                    )
                }
        }

        val convenio = dto.convenioId.let {
            convenioRepository.findById(it)
                .orElseThrow {
                    NoSuchElementException(
                        "Convênio não encontrado com id: $it"
                    )
                }
        }

        return AgendaRelacionamentos(
            paciente,
            psicologo,
            sala,
            tipoAtendimento,
            convenio
        )
    }

    private fun verificarConflitoDeHorario(
        psicologoId: Long,
        salaId: Long?,
        inicio: LocalDateTime,
        fim: LocalDateTime,
        agendaIdIgnorar: Long?
    ) {

        val conflitoPsicologo =
            agendaRepository.findByPsicologoId(psicologoId)
                .filter { it.id != agendaIdIgnorar }
                .any {
                    it.status in statusQueReservamHorario &&
                    sobrepoe(inicio, fim, it.dataHoraInicio, it.dataHoraFim)
                }

        if (conflitoPsicologo) {
            throw IllegalArgumentException(
                "O psicólogo já possui um agendamento neste horário"
            )
        }

        if (salaId != null) {

            val conflitoSala =
                agendaRepository.findBySalaId(salaId)
                    .filter { it.id != agendaIdIgnorar }
                    .any {
                        it.status in statusQueReservamHorario &&
                        sobrepoe(inicio, fim, it.dataHoraInicio, it.dataHoraFim)
                    }

            if (conflitoSala) {
                throw IllegalArgumentException(
                    "A sala já está reservada neste horário"
                )
            }
        }
    }

    private fun verificarDisponibilidadePsicologo(
        psicologoId: Long,
        inicio: LocalDateTime,
        fim: LocalDateTime
    ) {
        if (inicio.toLocalDate() != fim.toLocalDate()) {
            throw IllegalArgumentException(
                "O agendamento deve começar e terminar no mesmo dia"
            )
        }

        val diaSemana = diaSemana(inicio.dayOfWeek)
        val dentroDaGrade = horarioDisponivelRepository
            .findByPsicologoIdAndDiaSemana(psicologoId, diaSemana)
            .any { horario ->
                !inicio.toLocalTime().isBefore(horario.horaInicio) &&
                    !fim.toLocalTime().isAfter(horario.horaFim)
            }

        if (!dentroDaGrade) {
            throw IllegalArgumentException(
                "O psicólogo não possui disponibilidade para este dia e horário"
            )
        }

        val conflitoComBloqueio = bloqueioAgendaRepository
            .findByPsicologoIdAndAtivoTrue(psicologoId)
            .any { bloqueio ->
                    sobrepoe(inicio, fim, bloqueio.dataHoraInicio, bloqueio.dataHoraFim)
            }

        if (conflitoComBloqueio) {
            throw IllegalArgumentException(
                "O horário do psicólogo está bloqueado"
            )
        }
    }

    private fun diaSemana(dia: DayOfWeek): DiaSemana =
        when (dia) {
            DayOfWeek.MONDAY -> DiaSemana.SEGUNDA
            DayOfWeek.TUESDAY -> DiaSemana.TERCA
            DayOfWeek.WEDNESDAY -> DiaSemana.QUARTA
            DayOfWeek.THURSDAY -> DiaSemana.QUINTA
            DayOfWeek.FRIDAY -> DiaSemana.SEXTA
            DayOfWeek.SATURDAY -> DiaSemana.SABADO
            DayOfWeek.SUNDAY -> DiaSemana.DOMINGO
        }

    private fun sobrepoe(
        inicio: LocalDateTime,
        fim: LocalDateTime,
        outroInicio: LocalDateTime,
        outroFim: LocalDateTime
    ): Boolean = outroInicio.isBefore(fim) && inicio.isBefore(outroFim)
}