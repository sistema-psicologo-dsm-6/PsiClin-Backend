package com.edu.psiclinbe.model

import com.edu.psiclinbe.model.enums.DiaSemana
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
import jakarta.persistence.Column
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.SQLRestriction
import java.time.LocalTime

@Entity
@Table(name = "horarios_disponiveis")
@SQLRestriction("ativo = true")
open class HorarioDisponivel(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "psicologo_id")
    @field:NotNull(message = "O psicologo e obrigatorio")
    var psicologo: Psicologo,

    @Enumerated(EnumType.STRING)
    @field:NotNull(message = "O dia da semana é obrigatório")
    var diaSemana: DiaSemana,

    @field:NotNull(message = "A hora de inicio e obrigatoria")
    var horaInicio: LocalTime,

    @field:NotNull(message = "A hora de fim e obrigatoria")
    var horaFim: LocalTime,

    @Column(length = 500)
    @field:Size(max = 500, message = "A descricao deve ter no maximo 500 caracteres")
    var descricao: String? = null,

    @Column(nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE")
    var ativo: Boolean = true,
)