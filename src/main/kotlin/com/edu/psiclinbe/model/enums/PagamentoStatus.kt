package com.edu.psiclinbe.model.enums

enum class PagamentoStatus(
    val descricao: String
) {
    PENDENTE("Pendente") ,
    PAGO("Pago"),
    CANCELADO("Cancelado"),
}