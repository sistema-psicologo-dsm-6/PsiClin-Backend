package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.Funcionario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FuncionarioRepository: JpaRepository<Funcionario, Long> {
    @Query("SELECT f FROM Funcionario f WHERE LOWER(f.nome) LIKE LOWER(CONCAT('%', :termo, '%'))")
    fun buscarPorNomeParcial(@Param("termo") termo: String): List<Funcionario>

    fun findByNomeContainingIgnoreCase(nome: String): List<Funcionario>
    fun findByEmail(email: String): Funcionario?
    fun existsByEmail(email: String): Boolean
    fun findByCpf(cpf: String): Funcionario?
    fun findByEmailAndCpf(email: String, cpf: String): Funcionario?
    fun findByAtivoTrue(): List<Funcionario>
}