package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.Psicologo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PsicologoRepository: JpaRepository<Psicologo, Long> {
	@Query("SELECT p FROM Psicologo p WHERE p.crp = :crp")
	fun findByCrp(@Param("crp") crp: String): Psicologo?

	@Query("SELECT p FROM Psicologo p WHERE LOWER(p.especialidade) LIKE LOWER(CONCAT('%', :especialidade, '%'))")
	fun findByEspecialidadeContainingIgnoreCase(@Param("especialidade") especialidade: String): List<Psicologo>
	fun findByAtivoTrue(): List<Psicologo>
}