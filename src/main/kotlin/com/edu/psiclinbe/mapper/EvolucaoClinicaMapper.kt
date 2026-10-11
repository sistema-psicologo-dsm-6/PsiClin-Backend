package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.formData.EvolucaoClinicaFormDataResponse
import com.edu.psiclinbe.dto.request.EvolucaoClinicaDtoRequest
import com.edu.psiclinbe.dto.response.AgendaDtoResponse
import com.edu.psiclinbe.dto.response.EvolucaoClinicaDtoResponse
import com.edu.psiclinbe.dto.response.PacienteDtoResponse
import com.edu.psiclinbe.dto.response.ProntuarioDtoResponse
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse
import com.edu.psiclinbe.model.Agenda
import com.edu.psiclinbe.model.EvolucaoClinica
import com.edu.psiclinbe.model.Prontuario
import com.edu.psiclinbe.model.Psicologo
import org.springframework.stereotype.Component

@Component

class EvolucaoClinicaMapper {
    fun toModel(
        dto: EvolucaoClinicaDtoRequest,
        prontuario: Prontuario,
        psicologo: Psicologo,
        agenda: Agenda
    ): EvolucaoClinica {
        return EvolucaoClinica(
            prontuario = prontuario,
            psicologo = psicologo,
            agenda = agenda,
            dataRegistro = dto.dataRegistro,
            descricao = dto.descricao,
        )
    }

    fun toResponse(
        evo: EvolucaoClinica
    ): EvolucaoClinicaDtoResponse {

        return EvolucaoClinicaDtoResponse(
            evo.id!!,
            evo.prontuario.id!!,
            evo.psicologo.id!!,
            evo.agenda.id!!,
            evo.dataRegistro,
            evo.descricao,
            evo.ativo,
        )
    }

    fun toFormData(
        prontuarios: List<ProntuarioDtoResponse>,
        psicologos: List<PsicologoDtoResponse>,
        agendas: List<AgendaDtoResponse>,
        evolucaoClinica: EvolucaoClinicaDtoResponse?,
    ): EvolucaoClinicaFormDataResponse = EvolucaoClinicaFormDataResponse(
        prontuarios = prontuarios,
        psicologos = psicologos,
        agendas = agendas,
        evolucaoClinica = evolucaoClinica,
    )
}