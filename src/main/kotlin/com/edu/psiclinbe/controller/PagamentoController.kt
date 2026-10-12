package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.request.PagamentoDtoRequest
import com.edu.psiclinbe.dto.response.PagamentoDtoResponse
import com.edu.psiclinbe.model.enums.PagamentoStatus
import com.edu.psiclinbe.service.PagamentoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pagamentos")
class PagamentoController(
    private val pagamentoService: PagamentoService,
) {
    @GetMapping
    fun index(): ResponseEntity<List<PagamentoDtoResponse>> =
        ResponseEntity.ok(pagamentoService.lerTodos())

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long): ResponseEntity<PagamentoDtoResponse> =
        ResponseEntity.ok(pagamentoService.buscarPorId(id))

    @GetMapping("/por-agenda/{agendaId}")
    fun showByAgenda(@PathVariable agendaId: Long): ResponseEntity<PagamentoDtoResponse> =
        ResponseEntity.ok(pagamentoService.buscarPorAgenda(agendaId))

    @GetMapping("/por-paciente/{pacienteId}")
    fun indexByPatient(@PathVariable pacienteId: Long): ResponseEntity<List<PagamentoDtoResponse>> =
        ResponseEntity.ok(pagamentoService.buscarPorPaciente(pacienteId))

    @GetMapping("/por-convenio/{convenioId}")
    fun indexByInsurance(@PathVariable convenioId: Long): ResponseEntity<List<PagamentoDtoResponse>> =
        ResponseEntity.ok(pagamentoService.buscarPorConvenio(convenioId))

    @GetMapping("/por-status/{status}")
    fun indexByStatus(@PathVariable status: PagamentoStatus): ResponseEntity<List<PagamentoDtoResponse>> =
        ResponseEntity.ok(pagamentoService.buscarPorStatus(status))

    @GetMapping("/por-forma-pagamento")
    fun indexByPaymentMethod(@RequestParam termo: String): ResponseEntity<List<PagamentoDtoResponse>> =
        ResponseEntity.ok(pagamentoService.buscarPorFormaPagamento(termo))

    @PostMapping
    fun store(
        @Valid @RequestBody dto: PagamentoDtoRequest,
    ): ResponseEntity<PagamentoDtoResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(pagamentoService.registrarPagamento(dto))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: PagamentoDtoRequest,
    ): ResponseEntity<PagamentoDtoResponse> =
        ResponseEntity.ok(pagamentoService.atualizar(id, dto))

    @DeleteMapping("/{id}", "/destroy/{id}")
    fun destroy(@PathVariable id: Long): ResponseEntity<Void> {
        pagamentoService.deletarPagamento(id)
        return ResponseEntity.noContent().build()
    }
}