package com.edu.psiclinbe.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "tipos_atendimento")
open class TipoAtendimento(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(name = "nome", nullable = false, length = 100)
    @field:NotBlank(message = "O nome do tipo de atendimento nao pode estar vazio")
    @field:Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    var nome: String,
    @Column(name = "descricao", length = 500)
    @field:NotBlank(message = "A descricao nao pode estar vazia")
    @field:Size(max = 500)
    var descricao: String,
    @Column(name = "duracao", length = 20)
    @field:NotBlank(message = "A duracao nao pode estar vazia")
    @field:Size(max = 20)
    var duracao: String,
    @Column(name = "ativo")
    var ativo: Boolean = true,
    @Column(nullable = false)
    var dataCadastro: LocalDateTime = LocalDateTime.now()
)
