package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.request.FuncionarioDtoRequest
import com.edu.psiclinbe.dto.response.FuncionarioDtoResponse
import com.edu.psiclinbe.model.Funcionario
import org.springframework.stereotype.Component

@Component
class FuncionarioMapper {
    fun toModel(dto: FuncionarioDtoRequest): Funcionario = Funcionario(
        id = dto.id,
        nome = dto.nome,
        cpf = dto.cpf,
        email = dto.email,
        telefone = dto.telefone,
        senhaHash = dto.senhaHash,
        ativo = dto.ativo,
    )

    fun toResponse(funcionario: Funcionario): FuncionarioDtoResponse =
        FuncionarioDtoResponse(
            id = funcionario.id,
            nome = funcionario.nome,
            cpf = funcionario.cpf,
            email = funcionario.email,
            telefone = funcionario.telefone,
            senhaHash = null,
            ativo = funcionario.ativo,
        )
}