package com.edu.psiclinbe.dto.formData

import com.edu.psiclinbe.dto.response.BloqueioAgendaDtoResponse
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse

data class BloqueioAgendaFormDataResponse(
    val psicologos: List<PsicologoDtoResponse>,
    val bloqueio: BloqueioAgendaDtoResponse?,
)