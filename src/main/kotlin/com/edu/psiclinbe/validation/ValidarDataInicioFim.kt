package com.edu.psiclinbe.validation

import org.springframework.stereotype.Component
import java.time.LocalTime

@Component
class ValidarDataInicioFim {

    fun validarHorario(
        inicio: LocalTime,
        fim: LocalTime
    ) {
        if (!fim.isAfter(inicio)) {
            throw IllegalArgumentException(
                "A data/hora de fim deve ser posterior à data/hora de início"
            )
        }
    }
}