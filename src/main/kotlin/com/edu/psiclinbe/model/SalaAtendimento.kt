package com.edu.psiclinbe.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "salas_atendimento")
open class SalaAtendimento(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @Column(length = 100)
    @field:NotBlank(message = "O nome da sala nao pode estar vazio")
    @field:Size(max = 100)
    var nome: String,
    @Column(length = 500)
    @field:NotBlank(message = "A descricao da sala nao pode estar vazia")
    @field:Size(max = 500)
    var descricao: String?,
    @field:Positive(message = "A capacidade deve ser maior que zero")
    var capacidade: Int?,
    var ativo: Boolean = true,
    @Column(nullable = false)
    var dataCadastro: LocalDateTime = LocalDateTime.now()
)