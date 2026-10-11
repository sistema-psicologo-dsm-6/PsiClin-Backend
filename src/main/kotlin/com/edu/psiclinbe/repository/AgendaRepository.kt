package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.Agenda
import com.edu.psiclinbe.model.enums.AgendaStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface AgendaRepository : JpaRepository<Agenda, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM agendas WHERE id = :id", nativeQuery = true)
    fun excluirPermanentementePorId(@Param("id") id: Long): Int

    @Query("SELECT a FROM Agenda a WHERE LOWER(COALESCE(a.observacoes, '')) LIKE LOWER(CONCAT('%', :termo, '%'))")
    fun buscarPorObservacoes(@Param("termo") termo: String): List<Agenda>

    fun findByPacienteId(pacienteId: Long): List<Agenda>
    fun findByPsicologoId(psicologoId: Long): List<Agenda>
    fun findBySalaId(salaId: Long): List<Agenda>
    fun findByTipoAtendimentoId(tipoAtendimentoId: Long): List<Agenda>
    fun findByConvenioId(convenioId: Long): List<Agenda>
    fun findByStatus(status: AgendaStatus): List<Agenda>
    fun findByDataHoraInicioBetween(inicio: java.time.LocalDateTime, fim: java.time.LocalDateTime): List<Agenda>
}