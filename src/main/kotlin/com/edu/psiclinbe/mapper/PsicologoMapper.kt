package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.request.PsicologoDtoRequest
import com.edu.psiclinbe.dto.response.PsicologoDtoResponse
import com.edu.psiclinbe.model.Psicologo
import org.springframework.stereotype.Component

@Component
class PsicologoMapper {

    fun toModel(dto: PsicologoDtoRequest): Psicologo {

        return Psicologo().apply {
            nome = dto.nome
            cpf = dto.cpf
            email = dto.email
            telefone = dto.telefone
            senhaHash = dto.senhaHash
            ativo = dto.ativo
            crp = dto.crp
            especialidade = dto.especialidade
        }
    }

    fun toResponse(
        psicologo: Psicologo
    ): PsicologoDtoResponse {

        return PsicologoDtoResponse(
            id = psicologo.id!!,
            nome = psicologo.nome,
            cpf = psicologo.cpf,
            email = psicologo.email,
            telefone = psicologo.telefone,
            ativo = psicologo.ativo,
            dataCadastro = psicologo.dataCadastro,
            crp = psicologo.crp,
            especialidade = psicologo.especialidade
        )
    }
}