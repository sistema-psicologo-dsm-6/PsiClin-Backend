package com.edu.psiclinbe.dto.formData

import com.edu.psiclinbe.dto.response.HorarioDisponivelDtoResponse
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse

data class HorarioDisponivelFormDataResponse(
    val psicologos: List<PsicologoDtoResponse>,
    val diasSemana: List<String>,
    val horarioDisponivel: HorarioDisponivelDtoResponse?,
)