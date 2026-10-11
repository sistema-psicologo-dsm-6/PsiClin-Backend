package com.edu.psiclinbe.dto.formData

import com.edu.psiclinbe.dto.response.AgendaDtoResponse
import com.edu.psiclinbe.dto.response.EvolucaoClinicaDtoResponse
import com.edu.psiclinbe.dto.response.ProntuarioDtoResponse
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse

data class EvolucaoClinicaFormDataResponse(
    val prontuarios: List<ProntuarioDtoResponse>,
    val psicologos: List<PsicologoDtoResponse>,
    val agendas: List<AgendaDtoResponse>,
    val evolucaoClinica: EvolucaoClinicaDtoResponse?,
)