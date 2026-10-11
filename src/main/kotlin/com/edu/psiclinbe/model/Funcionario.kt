package com.edu.psiclinbe.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
@Table(name = "funcionarios")
@Inheritance(strategy = InheritanceType.JOINED)
open class Funcionario(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    open var id: Long? = null,

    @Column(length = 150)
    @field:NotBlank(message = "O nome nao pode estar vazio")
    open var nome: String = "",

    @Column(length = 14)
    @field:NotBlank(message = "O CPF nao pode estar vazio")
    open var cpf: String = "",

    @Column(length = 254)
    @field:NotBlank(message = "O e-mail nao pode estar vazio")
    @field:Email(message = "O e-mail deve ser valido")
    open var email: String = "",

    @Column(length = 20)
    @field:NotBlank(message = "O telefone nao pode estar vazio")
    open var telefone: String = "",

    @Column(length = 255)
    @field:NotBlank(message = "A senha nao pode estar vazia")
    open var senhaHash: String = "",

    open var ativo: Boolean = true,

    open var dataCadastro: LocalDateTime = LocalDateTime.now()
)