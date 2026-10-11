package com.edu.psiclinbe.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "recepcionistas")
class Recepcionista(

    @field:NotBlank(message = "O setor nao pode estar vazio")
    var setor: String = ""

) : Funcionario()