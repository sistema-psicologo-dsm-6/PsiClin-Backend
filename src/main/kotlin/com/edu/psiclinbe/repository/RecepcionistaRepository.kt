package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.Recepcionista
import org.springframework.data.jpa.repository.JpaRepository

interface RecepcionistaRepository: JpaRepository<Recepcionista, Long> {
	fun findBySetorContainingIgnoreCase(setor: String): List<Recepcionista>
	fun findByAtivoTrue(): List<Recepcionista>
}