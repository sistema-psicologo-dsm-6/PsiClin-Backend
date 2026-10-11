package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.request.BloqueioAgendaDtoRequest
import com.edu.psiclinbe.dto.formData.BloqueioAgendaFormDataResponse
import com.edu.psiclinbe.dto.response.BloqueioAgendaDtoResponse
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse
import com.edu.psiclinbe.model.BloqueioAgenda
import com.edu.psiclinbe.model.Psicologo
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class BloqueioAgendaMapper {

    fun toModel(
        dto: BloqueioAgendaDtoRequest,
        psicologo: Psicologo
    ): BloqueioAgenda {
        return BloqueioAgenda(
            psicologo = psicologo,
            dataHoraInicio = dto.dataHoraInicio,
            dataHoraFim = dto.dataHoraFim,
            motivo = dto.motivo,
        )
    }

    fun toResponse(bloqueio: BloqueioAgenda): BloqueioAgendaDtoResponse {
        return BloqueioAgendaDtoResponse(
            id = bloqueio.id,
            psicologoId = bloqueio.psicologo.id,
            dataHoraInicio = bloqueio.dataHoraInicio,
            dataHoraFim = bloqueio.dataHoraFim,
            motivo = bloqueio.motivo,
            ativo = bloqueio.ativo,
        )
    }

    fun toFormData(
        psicologos: List<PsicologoDtoResponse>,
        bloqueio: BloqueioAgendaDtoResponse?,
    ): BloqueioAgendaFormDataResponse = BloqueioAgendaFormDataResponse(
        psicologos = psicologos,
        bloqueio = bloqueio,
    )
}