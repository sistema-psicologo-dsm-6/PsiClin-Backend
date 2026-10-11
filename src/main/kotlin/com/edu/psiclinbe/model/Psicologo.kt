package com.edu.psiclinbe.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "psicologos")
class Psicologo(

    @field:NotBlank(message = "O CRP nao pode estar vazio")
    var crp: String = "",

    @field:NotBlank(message = "A especialidade nao pode estar vazia")
    var especialidade: String = ""

) : Funcionario()