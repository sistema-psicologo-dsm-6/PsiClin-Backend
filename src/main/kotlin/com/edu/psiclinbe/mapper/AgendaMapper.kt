package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.formData.AgendaFormDataResponse
import com.edu.psiclinbe.dto.request.AgendaDtoRequest
import com.edu.psiclinbe.dto.response.AgendaDtoResponse
import com.edu.psiclinbe.dto.response.ConvenioDtoResponse
import com.edu.psiclinbe.dto.response.PacienteDtoResponse
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse
import com.edu.psiclinbe.dto.response.SalaAtendimentoDtoResponse
import com.edu.psiclinbe.dto.response.TipoAtendimentoDtoResponse
import com.edu.psiclinbe.model.Agenda
import com.edu.psiclinbe.model.Convenio
import com.edu.psiclinbe.model.Paciente
import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.model.SalaAtendimento
import com.edu.psiclinbe.model.TipoAtendimento
import org.springframework.stereotype.Component

@Component
class AgendaMapper {

    fun toModel(
        dto: AgendaDtoRequest,
        paciente: Paciente,
        psicologo: Psicologo,
        sala: SalaAtendimento?,
        tipoAtendimento: TipoAtendimento,
        convenio: Convenio?
    ): Agenda {

        return Agenda(
            paciente = paciente,
            psicologo = psicologo,
            sala = sala!!,
            tipoAtendimento = tipoAtendimento,
            convenio = convenio!!,
            dataHoraInicio = dto.dataHoraInicio,
            dataHoraFim = dto.dataHoraFim,
            status = dto.status,
            observacoes = dto.observacoes
        )
    }

    fun toResponse(agenda: Agenda) =
        AgendaDtoResponse(
            id = agenda.id!!,
            pacienteId = agenda.paciente.id!!,
            psicologoId = agenda.psicologo.id!!,
            salaId = agenda.sala.id,
            tipoAtendimentoId = agenda.tipoAtendimento.id!!,
            convenioId = agenda.convenio.id,
            dataHoraInicio = agenda.dataHoraInicio,
            dataHoraFim = agenda.dataHoraFim,
            status = agenda.status,
            observacoes = agenda.observacoes,
            ativo = agenda.ativo,
        )

    fun toFormData(
        pacientes: List<PacienteDtoResponse>,
        psicologos: List<PsicologoDtoResponse>,
        salas: List<SalaAtendimentoDtoResponse>,
        tiposAtendimento: List<TipoAtendimentoDtoResponse>,
        convenios: List<ConvenioDtoResponse>,
        agenda: AgendaDtoResponse?
        ): AgendaFormDataResponse{
        return AgendaFormDataResponse(
            pacientes = pacientes,
            psicologos = psicologos,
            salas = salas,
            tiposAtendimento = tiposAtendimento,
            convenios = convenios,
            agenda = agenda,
            statusDisponiveis = com.edu.psiclinbe.model.enums.AgendaStatus.entries
        )
    }
}