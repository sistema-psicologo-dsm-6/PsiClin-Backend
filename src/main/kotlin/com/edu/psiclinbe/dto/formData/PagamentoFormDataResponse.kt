package com.edu.psiclinbe.dto.formData

import com.edu.psiclinbe.dto.response.AgendaDtoResponse
import com.edu.psiclinbe.dto.response.ConvenioDtoResponse
import com.edu.psiclinbe.dto.response.PacienteDtoResponse
import com.edu.psiclinbe.dto.response.PagamentoDtoResponse
import com.edu.psiclinbe.model.enums.PagamentoStatus

data class PagamentoFormDataResponse(
    val agendas: List<AgendaDtoResponse>,
    val pacientes: List<PacienteDtoResponse>,
    val convenios: List<ConvenioDtoResponse>,
    val statusDisponiveis: List<PagamentoStatus>,
    val pagamento: PagamentoDtoResponse?,
)