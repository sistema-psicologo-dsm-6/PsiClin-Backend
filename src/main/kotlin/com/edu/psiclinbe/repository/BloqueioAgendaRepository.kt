package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.BloqueioAgenda
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface BloqueioAgendaRepository : JpaRepository<BloqueioAgenda, Long> {
    @Query("SELECT b FROM BloqueioAgenda b WHERE b.ativo = true AND LOWER(b.motivo) LIKE LOWER(CONCAT('%', :termo, '%'))")
    fun buscarPorMotivo(@Param("termo") termo: String): List<BloqueioAgenda>

    fun findAllByAtivoTrue(): List<BloqueioAgenda>
    fun findByIdAndAtivoTrue(id: Long): BloqueioAgenda?
    fun findByPsicologoIdAndAtivoTrue(psicologoId: Long): List<BloqueioAgenda>

    @Query(
        """
        SELECT b
        FROM BloqueioAgenda b
        WHERE b.ativo = true
        AND b.dataHoraInicio < :fim
        AND b.dataHoraFim > :inicio
        """
    )
    fun findByDataHoraInicioBetween(inicio: LocalDateTime, fim: LocalDateTime): List<BloqueioAgenda>
}