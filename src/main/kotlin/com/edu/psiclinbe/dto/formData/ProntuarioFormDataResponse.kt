package com.edu.psiclinbe.dto.formData

import com.edu.psiclinbe.dto.response.PacienteDtoResponse
import com.edu.psiclinbe.dto.response.ProntuarioDtoResponse
import com.edu.psiclinbe.model.enums.ProntuarioStatus

data class ProntuarioFormDataResponse(
    val pacientes: List<PacienteDtoResponse>,
    val statusDisponiveis: List<ProntuarioStatus>,
    //teste
    val prontuario: ProntuarioDtoResponse?,
)