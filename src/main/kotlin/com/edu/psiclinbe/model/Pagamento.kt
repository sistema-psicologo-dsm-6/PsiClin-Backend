package com.edu.psiclinbe.model

import com.edu.psiclinbe.model.enums.PagamentoStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDateTime

@Entity
@Table(name = "pagamentos")
open class Pagamento(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_id")
    @field:NotNull(message = "A agenda e obrigatoria")
    var agenda: Agenda,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    @field:NotNull(message = "O paciente e obrigatorio")
    var paciente: Paciente,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convenio_id")
    @field:NotNull(message = "O convenio e obrigatorio")
    var convenio: Convenio? = null,

    @field:PositiveOrZero(message = "O valor total nao pode ser negativo")
    var valorTotal: Double,
    @field:PositiveOrZero(message = "O valor do convenio nao pode ser negativo")
    var valorConvenio: Double,
    @field:PositiveOrZero(message = "O valor do paciente nao pode ser negativo")
    var valorPaciente: Double,
    @Column(length = 50)
    @field:NotBlank(message = "A forma de pagamento nao pode estar vazia")
    var formaPagamento: String,
    @field:NotNull(message = "A data de pagamento e obrigatoria")
    var dataPagamento: LocalDateTime,

    @Enumerated(EnumType.STRING)
    @field:NotNull(message = "O status do pagamento e obrigatorio")
    var status: PagamentoStatus,
)