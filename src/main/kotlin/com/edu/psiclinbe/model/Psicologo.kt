package com.edu.psiclinbe.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
@Table(name = "psicologos")
class Psicologo(

    @Column(length = 20)
    @field:NotBlank(message = "O CRP nao pode estar vazio")
    @field:Size(max = 20)
    var crp: String = "",

    @Column(length = 150)
    @field:NotBlank(message = "A especialidade nao pode estar vazia")
    @field:Size(max = 150)
    var especialidade: String = ""

) : Funcionario()