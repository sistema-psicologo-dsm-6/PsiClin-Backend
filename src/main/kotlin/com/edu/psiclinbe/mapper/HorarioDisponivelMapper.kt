package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.formData.HorarioDisponivelFormDataResponse
import com.edu.psiclinbe.dto.request.HorarioDisponivelDtoRequest
import com.edu.psiclinbe.dto.response.HorarioDisponivelDtoResponse
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse
import com.edu.psiclinbe.model.HorarioDisponivel
import com.edu.psiclinbe.model.Psicologo
import com.edu.psiclinbe.model.enums.DiaSemana
import org.springframework.stereotype.Component

@Component
class HorarioDisponivelMapper {
    fun toModel(
        dto: HorarioDisponivelDtoRequest,
        psicologo: Psicologo
    ): HorarioDisponivel {
        return HorarioDisponivel(
            psicologo = psicologo,
            diaSemana = DiaSemana.valueOf(dto.diaSemana),
            horaInicio = dto.horaInicio,
            horaFim = dto.horaFim,
            descricao = dto.descricao,
        )
    }

    fun toResponse(horarioDisponivel: HorarioDisponivel): HorarioDisponivelDtoResponse {
        return HorarioDisponivelDtoResponse(
            id = horarioDisponivel.id,
            psicologoId = horarioDisponivel.psicologo.id,
            diaSemana = horarioDisponivel.diaSemana.name,
            horaInicio = horarioDisponivel.horaInicio,
            horaFim = horarioDisponivel.horaFim,
            descricao = horarioDisponivel.descricao,
            ativo = horarioDisponivel.ativo,
        )
    }

    fun toFormData(
        psicologos: List<PsicologoDtoResponse>,
        horarioDisponivel: HorarioDisponivelDtoResponse?,
    ): HorarioDisponivelFormDataResponse = HorarioDisponivelFormDataResponse(
        psicologos = psicologos,
        diasSemana = DiaSemana.entries.map { it.name },
        horarioDisponivel = horarioDisponivel,
    )
}
