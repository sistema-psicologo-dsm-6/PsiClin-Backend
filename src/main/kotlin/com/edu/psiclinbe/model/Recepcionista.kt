package com.edu.psiclinbe.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
@Table(name = "recepcionistas")
class Recepcionista(

    @Column(length = 100)
    @field:NotBlank(message = "O setor nao pode estar vazio")
    @field:Size(max = 100)
    var setor: String = ""

) : Funcionario()