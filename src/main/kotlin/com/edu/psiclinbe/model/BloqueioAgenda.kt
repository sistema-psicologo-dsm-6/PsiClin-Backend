package com.edu.psiclinbe.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "bloqueios_agenda")
open class BloqueioAgenda(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psicologo_id")
    @field:NotNull(message = "O psicologo e obrigatorio")
    var psicologo: Psicologo,
    @field:NotNull(message = "A data e hora de inicio sao obrigatorias")
    var dataHoraInicio: LocalDateTime,
    @field:NotNull(message = "A data e hora de fim sao obrigatorias")
    var dataHoraFim: LocalDateTime,

    @Column(length = 500)
    @field:NotBlank(message = "O motivo do bloqueio nao pode estar vazio")
    @field:Size(max = 500, message = "O motivo do bloqueio deve ter no maximo 500 caracteres")
    var motivo: String,

    @Column(nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE")
    var ativo: Boolean = true
)
