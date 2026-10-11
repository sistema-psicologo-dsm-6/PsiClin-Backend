package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.EvolucaoClinica
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface EvolucaoClinicaRepository : JpaRepository<EvolucaoClinica, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM evolucoes_clinicas WHERE id = :id", nativeQuery = true)
    fun excluirPermanentementePorId(@Param("id") id: Long): Int

    @Query("SELECT e FROM EvolucaoClinica e WHERE LOWER(COALESCE(e.descricao, '')) LIKE LOWER(CONCAT('%', :termo, '%'))")
    fun buscarPorDescricao(@Param("termo") termo: String): List<EvolucaoClinica>

    fun findByProntuarioId(prontuarioId: Long): List<EvolucaoClinica>
    fun findByPsicologoId(psicologoId: Long): List<EvolucaoClinica>
    fun findByAgendaId(agendaId: Long): List<EvolucaoClinica>
}