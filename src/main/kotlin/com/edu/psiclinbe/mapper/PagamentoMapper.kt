package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.formData.PagamentoFormDataResponse
import com.edu.psiclinbe.dto.request.PagamentoDtoRequest
import com.edu.psiclinbe.dto.response.AgendaDtoResponse
import com.edu.psiclinbe.dto.response.ConvenioDtoResponse
import com.edu.psiclinbe.dto.response.PacienteDtoResponse
import com.edu.psiclinbe.dto.response.PagamentoDtoResponse
import com.edu.psiclinbe.model.Agenda
import com.edu.psiclinbe.model.Convenio
import com.edu.psiclinbe.model.Paciente
import com.edu.psiclinbe.model.Pagamento
import com.edu.psiclinbe.model.enums.PagamentoStatus
import org.springframework.stereotype.Component

@Component
class PagamentoMapper {
    fun toModel(
        dto: PagamentoDtoRequest,
        agenda: Agenda,
        paciente: Paciente,
        convenio: Convenio?,
    ): Pagamento = Pagamento(
        id = dto.id,
        agenda = agenda,
        paciente = paciente,
        convenio = convenio,
        valorTotal = requireNotNull(dto.valorTotal),
        valorConvenio = requireNotNull(dto.valorConvenio),
        valorPaciente = requireNotNull(dto.valorPaciente),
        formaPagamento = dto.formaPagamento,
        dataPagamento = requireNotNull(dto.dataPagamento),
        status = requireNotNull(dto.status),
    )

    fun toResponse(pagamento: Pagamento): PagamentoDtoResponse =
        PagamentoDtoResponse(
            id = pagamento.id,
            agendaId = pagamento.agenda.id,
            pacienteId = pagamento.paciente.id,
            convenioId = pagamento.convenio?.id,
            valorTotal = pagamento.valorTotal,
            valorConvenio = pagamento.valorConvenio,
            valorPaciente = pagamento.valorPaciente,
            formaPagamento = pagamento.formaPagamento,
            dataPagamento = pagamento.dataPagamento,
            status = pagamento.status,
        )

    fun toFormData(
        agendas: List<AgendaDtoResponse>,
        pacientes: List<PacienteDtoResponse>,
        convenios: List<ConvenioDtoResponse>,
        pagamento: PagamentoDtoResponse?,
    ): PagamentoFormDataResponse = PagamentoFormDataResponse(
        agendas = agendas,
        pacientes = pacientes,
        convenios = convenios,
        statusDisponiveis = PagamentoStatus.entries,
        pagamento = pagamento,
    )
}