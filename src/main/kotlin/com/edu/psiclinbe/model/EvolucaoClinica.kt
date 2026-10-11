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
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "evolucoes_clinicas")
@SQLRestriction("ativo = true")
open class EvolucaoClinica(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prontuario_id")
    @field:NotNull(message = "O prontuario e obrigatorio")
    var prontuario: Prontuario,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psicologo_id")
    @field:NotNull(message = "O psicologo e obrigatorio")
    var psicologo: Psicologo,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_id")
    @field:NotNull(message = "A agenda e obrigatoria")
    var agenda: Agenda,

    @field:NotNull(message = "A data de registro e obrigatoria")
    var dataRegistro: LocalDateTime,

    @Column(length = 5000)
    @field:Size(max = 5000, message = "A descricao deve ter no maximo 5000 caracteres")
    var descricao: String?,

    @Column(nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE")
    var ativo: Boolean = true
)