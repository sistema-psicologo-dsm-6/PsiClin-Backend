package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.Recepcionista
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RecepcionistaRepository: JpaRepository<Recepcionista, Long> {
	@Query("SELECT r FROM Recepcionista r WHERE LOWER(r.setor) LIKE LOWER(CONCAT('%', :setor, '%'))")
	fun findBySetorContainingIgnoreCase(@Param("setor") setor: String): List<Recepcionista>
	fun findByAtivoTrue(): List<Recepcionista>
}