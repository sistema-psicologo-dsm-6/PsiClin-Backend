package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.HorarioDisponivel
import com.edu.psiclinbe.model.enums.DiaSemana
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime

interface HorarioDisponivelRepository
    : JpaRepository<HorarioDisponivel, Long> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM horarios_disponiveis WHERE id = :id", nativeQuery = true)
    fun excluirPermanentementePorId(@Param("id") id: Long): Int

    @Query("SELECT h FROM HorarioDisponivel h WHERE LOWER(COALESCE(h.descricao, '')) LIKE LOWER(CONCAT('%', :termo, '%'))")
    fun buscarPorDescricao(@Param("termo") termo: String): List<HorarioDisponivel>

    fun findByPsicologoId(
        psicologoId: Long
    ): List<HorarioDisponivel>

    fun findByPsicologoIdAndDiaSemana(
        psicologoId: Long,
        diaSemana: DiaSemana
    ): List<HorarioDisponivel>

    fun existsByPsicologoIdAndDiaSemanaAndHoraInicioAndHoraFim(
        psicologoId: Long,
        diaSemana: DiaSemana,
        horaInicio: LocalTime,
        horaFim: LocalTime
    ): Boolean

    @Query("""
        SELECT h
        FROM HorarioDisponivel h
        WHERE h.psicologo.id = :psicologoId
          AND h.diaSemana = :diaSemana
          AND h.horaInicio < :horaFim
          AND h.horaFim > :horaInicio
    """)
    fun buscarConflitos(
        psicologoId: Long,
        diaSemana: DiaSemana,
        horaInicio: LocalTime,
        horaFim: LocalTime
    ): List<HorarioDisponivel>
}