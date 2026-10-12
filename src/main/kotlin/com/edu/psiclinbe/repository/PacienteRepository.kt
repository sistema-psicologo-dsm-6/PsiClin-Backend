package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.Paciente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PacienteRepository : JpaRepository<Paciente, Long> {
    @Query("SELECT p FROM Paciente p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%'))")
    fun buscarPorNomeParcial(@Param("termo") termo: String): List<Paciente>

	fun findByNomeContainingIgnoreCase(nome: String): List<Paciente>
	fun findByCpf(cpf: String): Paciente?
	fun findByEmail(email: String): Paciente?
	fun findByAtivoTrue(): List<Paciente>
}