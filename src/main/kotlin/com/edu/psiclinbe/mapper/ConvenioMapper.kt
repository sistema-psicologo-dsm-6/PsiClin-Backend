package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.request.ConvenioDtoRequest
import com.edu.psiclinbe.dto.response.ConvenioDtoResponse
import com.edu.psiclinbe.model.Convenio
import org.springframework.stereotype.Component

@Component

class ConvenioMapper {

    fun toModel(dto: ConvenioDtoRequest): Convenio {
        return Convenio(
            nome = dto.nome,
            cnpj = dto.cnpj,
            telefone = dto.telefone,
            percentualCobertura = dto.percentualCobertura,
            ativo = dto.ativo,
        )
    }

    fun toResponse(convenio: Convenio): ConvenioDtoResponse {
        return ConvenioDtoResponse(
            id = convenio.id,
            nome = convenio.nome,
            cnpj = convenio.cnpj,
            telefone = convenio.telefone,
            percentualCobertura = convenio.percentualCobertura,
            ativo = convenio.ativo,
            pagamentos = convenio.pagamentos.map { it.id!! }.toSet(),
        )
    }
}