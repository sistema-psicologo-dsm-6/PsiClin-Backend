package com.edu.psiclinbe.model

import jakarta.persistence.Entity
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "pacientes")
open class Paciente(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    open var id: Long? = null,

    @Column(length = 150)
    @field:NotBlank(message = "O nome nao pode estar vazio")
    open var nome: String = "",

    @Column(length = 14)
    @field:NotBlank(message = "O CPF nao pode estar vazio")
    open var cpf: String = "",

    @field:NotNull(message = "A data de nascimento e obrigatoria")
    open var dataNascimento: LocalDate = LocalDate.now(),

    @Column(length = 254)
    @field:NotBlank(message = "O e-mail nao pode estar vazio")
    @field:Email(message = "O e-mail deve ser valido")
    open var email: String = "",

    @Column(length = 20)
    @field:NotBlank(message = "O telefone nao pode estar vazio")
    open var telefone: String = "",

    @Column(length = 500)
    @field:NotBlank(message = "O endereco nao pode estar vazio")
    open var endereco: String = "",

    @Column(length = 5000)
    open var observacoes: String = "",

    open var ativo: Boolean = true,

    open var dataCadastro: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "paciente")
    var prontuarios: MutableList<Prontuario> = mutableListOf(),

    @OneToMany(mappedBy = "paciente")
    var pagamentos: MutableSet<Pagamento> = mutableSetOf(),
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Paciente) return false

        if (id == null || other.id == null) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}