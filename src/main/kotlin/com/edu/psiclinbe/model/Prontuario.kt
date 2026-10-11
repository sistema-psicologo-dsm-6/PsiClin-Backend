package com.edu.psiclinbe.model

import com.edu.psiclinbe.model.enums.ProntuarioStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "prontuarios")
@SQLRestriction("ativo = true")
open class Prontuario(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false )
    @JoinColumn(name = "paciente_id")
    @field:NotNull(message = "O paciente e obrigatorio")
    var paciente: Paciente,
    @field:NotNull(message = "A data de abertura e obrigatoria")
    var dataAbertura: LocalDateTime? = LocalDateTime.now(),
    @Column(length = 2000)
    @field:NotBlank(message = "A queixa principal nao pode estar vazia")
    @field:Size(max = 2000, message = "A queixa principal deve ter no maximo 2000 caracteres")
    var queixaPrincipal: String,

    @Enumerated(EnumType.STRING)
    @field:NotNull(message = "O status do prontuario e obrigatorio")
    var status: ProntuarioStatus,

    @Column(nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE")
    var ativo: Boolean = true,
)
