package com.edu.psiclinbe.mapper

import com.edu.psiclinbe.dto.request.PacienteDtoRequest
import com.edu.psiclinbe.dto.response.PacienteDtoResponse
import com.edu.psiclinbe.model.Paciente
import com.edu.psiclinbe.model.Pagamento
import com.edu.psiclinbe.model.Prontuario
import org.springframework.stereotype.Component

@Component
class PacienteMapper {

    fun toModel(
        dto: PacienteDtoRequest,
        prontuarios: MutableList<Prontuario> = mutableListOf(),
        pagamentos: MutableSet<Pagamento> = mutableSetOf()
    ): Paciente {

        return Paciente(
            nome = dto.nome,
            cpf = dto.cpf,
            dataNascimento = dto.dataNascimento,
            email = dto.email,
            telefone = dto.telefone,
            endereco = dto.endereco,
            observacoes = dto.observacoes,
            ativo = dto.ativo,
            prontuarios = prontuarios,
            pagamentos = pagamentos
        )
    }

    fun toResponse(paciente: Paciente): PacienteDtoResponse {

        return PacienteDtoResponse(
            id = paciente.id,
            nome = paciente.nome,
            cpf = paciente.cpf,
            dataNascimento = paciente.dataNascimento,
            email = paciente.email,
            telefone = paciente.telefone,
            endereco = paciente.endereco,
            observacoes = paciente.observacoes,
            ativo = paciente.ativo
        )
    }
}