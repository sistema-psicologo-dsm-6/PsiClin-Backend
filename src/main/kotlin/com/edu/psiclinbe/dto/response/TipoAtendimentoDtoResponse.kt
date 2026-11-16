package com.edu.psiclinbe.dto.response

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class TipoAtendimentoDtoResponse(
    val id: Long? = 0,
    val nome: String = "",
    val descricao: String = "",
    val duracao: String = "",
    val ativo: Boolean = true,
    val dataCadastro: LocalDateTime,
)