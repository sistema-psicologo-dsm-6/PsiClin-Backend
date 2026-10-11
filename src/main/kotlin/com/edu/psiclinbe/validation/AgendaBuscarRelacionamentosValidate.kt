package com.edu.psiclinbe.validation

import com.edu.psiclinbe.dto.request.AgendaDtoRequest
import com.edu.psiclinbe.model.Convenio
import com.edu.psiclinbe.model.Paciente
import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.model.SalaAtendimento
import com.edu.psiclinbe.model.TipoAtendimento
import com.edu.psiclinbe.repository.AgendaRepository
import com.edu.psiclinbe.repository.ConvenioRepository
import com.edu.psiclinbe.repository.PacienteRepository
import com.edu.psiclinbe.repository.PsicologoRepository
import com.edu.psiclinbe.repository.SalaAtendimentoRepository
import com.edu.psiclinbe.repository.TipoAtendimentoRepository
data class AgendaRelacionamentos(
    val paciente: Paciente,
    val psicologo: Psicologo,
    val sala: SalaAtendimento,
    val tipoAtendimento: TipoAtendimento,
    val convenio: Convenio
)

class AgendaBuscarRelacionamentosValidate(
    private val pacienteRepository: PacienteRepository,
    private val psicologoRepository: PsicologoRepository,
    private val tipoAtendimentoRepository: TipoAtendimentoRepository,
    private val salaRepository: SalaAtendimentoRepository,
    private val convenioRepository: ConvenioRepository,

    ) {
    fun buscarRelacionamentos(
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
                    NoSuchElementException("Sala não encontrada com id: $it")
                }
        }

        val convenio = dto.convenioId.let {
            convenioRepository.findById(it)
                .orElseThrow {
                    NoSuchElementException("Convênio não encontrado com id: $it")
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

}