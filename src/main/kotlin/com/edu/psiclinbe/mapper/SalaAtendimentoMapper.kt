package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.request.SalaAtendimentoDtoRequest
import com.edu.psiclinbe.dto.response.SalaAtendimentoDtoResponse
import com.edu.psiclinbe.model.SalaAtendimento
import org.springframework.stereotype.Component

@Component
class SalaAtendimentoMapper {
    fun toModel(dto: SalaAtendimentoDtoRequest): SalaAtendimento =
        SalaAtendimento(
            id = dto.id,
            nome = dto.nome,
            descricao = dto.descricao,
            capacidade = requireNotNull(dto.capacidade),
            ativo = dto.ativo,
        )

    fun toResponse(sala: SalaAtendimento): SalaAtendimentoDtoResponse =
        SalaAtendimentoDtoResponse(
            id = sala.id,
            nome = sala.nome,
            descricao = sala.descricao,
            capacidade = sala.capacidade,
            ativo = sala.ativo,
        )
}