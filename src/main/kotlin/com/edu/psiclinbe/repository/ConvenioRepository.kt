package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.Convenio
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ConvenioRepository : JpaRepository<Convenio, Long> {
    @Query("SELECT c FROM Convenio c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :termo, '%'))")
    fun buscarPorNomeParcial(@Param("termo") termo: String): List<Convenio>

    fun findByNomeContainingIgnoreCase(nome: String): List<Convenio>
    fun findByCnpj(cnpj: String): Convenio?
    fun findByTelefoneContainingIgnoreCase(telefone: String): List<Convenio>
    fun findByAtivoTrue(): List<Convenio>
}