package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.TipoAtendimento
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TipoAtendimentoRepository: JpaRepository<TipoAtendimento, Long> {
	@Query("SELECT t FROM TipoAtendimento t WHERE LOWER(t.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
	fun findByNomeContainingIgnoreCase(@Param("nome") nome: String): List<TipoAtendimento>
	fun findByAtivoTrue(): List<TipoAtendimento>
}