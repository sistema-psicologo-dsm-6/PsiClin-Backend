package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.request.RecepcionistaDtoRequest
import com.edu.psiclinbe.dto.response.RecepcionistaDtoResponse
import com.edu.psiclinbe.model.Recepcionista
import com.edu.psiclinbe.model.enums.Role
import org.springframework.stereotype.Component

@Component
class RecepcionistaMapper {
    fun toModel(dto: RecepcionistaDtoRequest): Recepcionista =
        Recepcionista(setor = dto.setor).apply {
            id = dto.id
            nome = dto.nome
            cpf = dto.cpf
            email = dto.email
            telefone = dto.telefone
            senha = dto.senha
            ativo = dto.ativo
            role = Role.RECEPCIONISTA
        }

    fun toResponse(recepcionista: Recepcionista): RecepcionistaDtoResponse =
        RecepcionistaDtoResponse(
            id = recepcionista.id,
            nome = recepcionista.nome,
            cpf = recepcionista.cpf,
            email = recepcionista.email,
            telefone = recepcionista.telefone,
            senha = "",
            setor = recepcionista.setor,
            ativo = recepcionista.ativo,
        )
}