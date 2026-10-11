package com.edu.psiclinbe.dto.formData

import com.edu.psiclinbe.dto.response.AgendaDtoResponse
import com.edu.psiclinbe.dto.response.ConvenioDtoResponse
import com.edu.psiclinbe.dto.response.PacienteDtoResponse
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse
import com.edu.psiclinbe.dto.response.SalaAtendimentoDtoResponse
import com.edu.psiclinbe.dto.response.TipoAtendimentoDtoResponse
import com.edu.psiclinbe.model.enums.AgendaStatus

data class AgendaFormDataResponse(
    val pacientes: List<PacienteDtoResponse>,
    val psicologos: List<PsicologoDtoResponse>,
    val salas: List<SalaAtendimentoDtoResponse>,
    val tiposAtendimento: List<TipoAtendimentoDtoResponse>,
    val convenios: List<ConvenioDtoResponse>,
    val agenda: AgendaDtoResponse?,
    val statusDisponiveis: List<AgendaStatus> = AgendaStatus.entries
)