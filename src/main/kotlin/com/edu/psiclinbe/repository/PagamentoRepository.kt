package com.edu.psiclinbe.repository

import com.edu.psiclinbe.model.Pagamento
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PagamentoRepository: JpaRepository<Pagamento, Long> {
    @Query("SELECT p FROM Pagamento p WHERE LOWER(p.formaPagamento) LIKE LOWER(CONCAT('%', :termo, '%'))")
    fun buscarPorFormaPagamento(@Param("termo") termo: String): List<Pagamento>

	fun findByAgendaId(agendaId: Long): Pagamento?
	fun findByPacienteId(pacienteId: Long): List<Pagamento>
	fun findByConvenioId(convenioId: Long): List<Pagamento>
	fun findByStatus(status: com.edu.psiclinbe.model.enums.PagamentoStatus): List<Pagamento>
}