package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.Prontuario
import com.edu.psiclinbe.model.enums.ProntuarioStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface ProntuarioRepository: JpaRepository<Prontuario, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM prontuarios WHERE id = :id", nativeQuery = true)
    fun excluirPermanentementePorId(@Param("id") id: Long): Int

    @Query("SELECT p FROM Prontuario p WHERE LOWER(p.queixaPrincipal) LIKE LOWER(CONCAT('%', :termo, '%'))")
    fun buscarPorQueixaPrincipal(@Param("termo") termo: String): List<Prontuario>

	fun findByPacienteId(pacienteId: Long): List<Prontuario>
	fun findByPacienteIdAndStatus(pacienteId: Long, status: ProntuarioStatus): List<Prontuario>
}
