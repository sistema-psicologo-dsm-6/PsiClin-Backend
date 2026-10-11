package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.request.TipoAtendimentoDtoRequest
import com.edu.psiclinbe.dto.response.TipoAtendimentoDtoResponse
import com.edu.psiclinbe.model.TipoAtendimento
import org.springframework.stereotype.Component

@Component
class TipoAtendimentoMapper {
    fun toModel(dto: TipoAtendimentoDtoRequest): TipoAtendimento =
        TipoAtendimento(
            id = dto.id,
            nome = dto.nome,
            descricao = dto.descricao,
            duracao = dto.duracao,
            ativo = dto.ativo,
        )

    fun toResponse(tipoAtendimento: TipoAtendimento): TipoAtendimentoDtoResponse =
        TipoAtendimentoDtoResponse(
            id = tipoAtendimento.id,
            nome = tipoAtendimento.nome,
            descricao = tipoAtendimento.descricao,
            duracao = tipoAtendimento.duracao,
            ativo = tipoAtendimento.ativo,
        )
}