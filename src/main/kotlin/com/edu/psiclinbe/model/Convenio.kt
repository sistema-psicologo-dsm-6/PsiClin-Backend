package com.edu.psiclinbe.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

@Entity
@Table(name = "convenios")

open class Convenio(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,

    @Column(name = "nome", length = 150, nullable = false)
    @field:NotBlank(message = "O nome do convenio nao pode estar vazio")
    @field:Size(min = 2, max = 150, message = "O nome do convenio deve ter entre 2 e 150 caracteres")
    var nome: String,
    @Column(name = "cnpj", length = 18, nullable = false)
    @field:NotBlank(message = "O CNPJ do convenio nao pode estar vazio")
    var cnpj: String,
    @Column(name = "telefone", length = 20, nullable = false)
    @field:NotBlank(message = "O telefone do convenio nao pode estar vazio")
    var telefone: String,
    @field:NotNull(message = "O percentual de cobertura e obrigatorio")
    @field:PositiveOrZero(message = "O percentual de cobertura nao pode ser negativo")
    var percentualCobertura: Double?,
    var ativo: Boolean,

    @OneToMany(mappedBy = "convenio", fetch = FetchType.LAZY)
    var pagamentos: MutableSet<Pagamento> = mutableSetOf(),
)