package com.edu.psiclinbe.model

import com.edu.psiclinbe.model.enums.AgendaStatus
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(name = "agendas")
@SQLRestriction("ativo = true")
open class Agenda(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id")
    var paciente: Paciente,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "psicologo_id")
    @field:NotNull(message = "O psicologo e obrigatorio")
    var psicologo: Psicologo,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id")
    var sala: SalaAtendimento,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_atendimento_id")
    @field:NotNull(message = "O tipo de atendimento e obrigatorio")
    var tipoAtendimento: TipoAtendimento,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convenio_id")
    var convenio: Convenio,

    @field:NotNull(message = "A data e hora de inicio sao obrigatorias")
    var dataHoraInicio: LocalDateTime,

    @field:NotNull(message = "A data e hora de fim sao obrigatorias")
    var dataHoraFim: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @field:NotNull(message = "O status da agenda e obrigatorio")
    var status: AgendaStatus,

    @Column(length = 2000)
    @field:Size(max = 2000, message = "As observacoes devem ter no maximo 2000 caracteres")
    var observacoes: String? = null,

    @Column(nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE")
    var ativo: Boolean = true
)