package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.Psicologo
import org.springframework.data.jpa.repository.JpaRepository

interface PsicologoRepository: JpaRepository<Psicologo, Long> {
	fun findByCrp(crp: String): Psicologo?
	fun findByEspecialidadeContainingIgnoreCase(especialidade: String): List<Psicologo>
	fun findByAtivoTrue(): List<Psicologo>
}