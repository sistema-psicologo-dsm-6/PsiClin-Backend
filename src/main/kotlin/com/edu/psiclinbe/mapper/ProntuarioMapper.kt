package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.formData.ProntuarioFormDataResponse
import com.edu.psiclinbe.dto.request.ProntuarioDtoRequest
import com.edu.psiclinbe.dto.response.PacienteDtoResponse
import com.edu.psiclinbe.dto.response.ProntuarioDtoResponse
import com.edu.psiclinbe.model.Paciente
import com.edu.psiclinbe.model.Prontuario
import com.edu.psiclinbe.model.enums.ProntuarioStatus
import org.springframework.stereotype.Component

@Component
class ProntuarioMapper {
    fun toModel(dto: ProntuarioDtoRequest, paciente: Paciente): Prontuario =
        Prontuario(
            id = dto.id,
            paciente = paciente,
            dataAbertura = requireNotNull(dto.dataAbertura),
            queixaPrincipal = dto.queixaPrincipal,
            status = requireNotNull(dto.status),
        )

    fun toResponse(prontuario: Prontuario): ProntuarioDtoResponse =
        ProntuarioDtoResponse(
            id = prontuario.id,
            pacienteId = prontuario.paciente.id,
            dataAbertura = prontuario.dataAbertura,
            queixaPrincipal = prontuario.queixaPrincipal,
            status = prontuario.status,
            ativo = prontuario.ativo,
        )

    fun toFormData(
        pacientes: List<PacienteDtoResponse>,
        prontuario: ProntuarioDtoResponse?,
    ): ProntuarioFormDataResponse = ProntuarioFormDataResponse(
        pacientes = pacientes,
        statusDisponiveis = ProntuarioStatus.entries,
        prontuario = prontuario,
    )
}