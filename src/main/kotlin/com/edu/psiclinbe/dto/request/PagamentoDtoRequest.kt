package com.edu.psiclinbe.dto.request

import com.edu.psiclinbe.model.enums.PagamentoStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDateTime

data class PagamentoDtoRequest(
    val id: Long? = null,
    @field:NotNull(message = "A agenda e obrigatoria")
    val agendaId: Long? = null,
    @field:NotNull(message = "O paciente e obrigatorio")
    val pacienteId: Long? = null,
    @field:NotNull(message = "O convenio e obrigatorio")
    val convenioId: Long? = null,
    @field:NotNull(message = "O valor total e obrigatorio")
    @field:PositiveOrZero(message = "O valor total nao pode ser negativo")
    val valorTotal: Double,
    @field:NotNull(message = "O valor do convenio e obrigatorio")
    @field:PositiveOrZero(message = "O valor do convenio nao pode ser negativo")
    val valorConvenio: Double,
    @field:NotNull(message = "O valor do paciente e obrigatorio")
    @field:PositiveOrZero(message = "O valor do paciente nao pode ser negativo")
    val valorPaciente: Double,
    @field:NotBlank(message = "A forma de pagamento nao pode estar vazia")
    val formaPagamento: String,
    @field:NotNull(message = "A data de pagamento e obrigatoria")
    val dataPagamento: LocalDateTime,
    @field:NotNull(message = "O status do pagamento e obrigatorio")
    val status: PagamentoStatus,
)