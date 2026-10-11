package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.SalaAtendimento
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SalaAtendimentoRepository: JpaRepository<SalaAtendimento, Long> {
    @Query("SELECT s FROM SalaAtendimento s WHERE LOWER(s.nome) LIKE LOWER(CONCAT('%', :termo, '%'))")
    fun buscarPorNomeParcial(@Param("termo") termo: String): List<SalaAtendimento>

	fun findByNomeContainingIgnoreCase(nome: String): List<SalaAtendimento>
	fun findByAtivoTrue(): List<SalaAtendimento>
    fun findById(id: SalaAtendimento?)
}