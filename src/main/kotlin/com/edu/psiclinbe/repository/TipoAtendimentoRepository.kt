package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.TipoAtendimento
import org.springframework.data.jpa.repository.JpaRepository

interface TipoAtendimentoRepository: JpaRepository<TipoAtendimento, Long> {
	fun findByNomeContainingIgnoreCase(nome: String): List<TipoAtendimento>
	fun findByAtivoTrue(): List<TipoAtendimento>
}