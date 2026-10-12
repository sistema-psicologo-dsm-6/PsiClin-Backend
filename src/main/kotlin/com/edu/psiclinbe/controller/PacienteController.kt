package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.request.PacienteDtoRequest
import com.edu.psiclinbe.dto.response.PacienteDtoResponse
import com.edu.psiclinbe.mapper.PacienteMapper
import com.edu.psiclinbe.service.PacienteService
import org.springframework.http.ResponseEntity
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/pacientes")
class PacienteController(
    private val mapper: PacienteMapper,
    private val pacienteService: PacienteService,

    ) {
    @GetMapping
    fun index(): ResponseEntity<List<PacienteDtoResponse>> =
        ResponseEntity.ok(
            pacienteService.listarTodos()
        )

    @GetMapping("/ativos")
    fun indexActive(): ResponseEntity<List<PacienteDtoResponse>> =
        ResponseEntity.ok(
            pacienteService.listarAtivos()
        )

    @GetMapping("/por-nome")
    fun indexByName(
        @RequestParam nome: String,
    ): ResponseEntity<List<PacienteDtoResponse>> =
        ResponseEntity.ok(
            pacienteService.buscarPorNome(nome)
        )

    @GetMapping("/por-cpf")
    fun showByCpf(
        @RequestParam cpf: String,
    ): ResponseEntity<PacienteDtoResponse> =
        ResponseEntity.ok(
            pacienteService.buscarPorCpf(cpf)
        )

    @GetMapping("/por-email")
    fun showByEmail(
        @RequestParam email: String,
    ): ResponseEntity<PacienteDtoResponse> =
        ResponseEntity.ok(
            pacienteService.buscarPorEmail(email)
        )

    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long,
    ): ResponseEntity<PacienteDtoResponse> =
        ResponseEntity.ok(
            pacienteService.buscarPorId(id)
        )

    @PostMapping
    fun store(
        @Valid @RequestBody dto: PacienteDtoRequest
    ): ResponseEntity<PacienteDtoResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(
                pacienteService.salvar(dto)
            )

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: PacienteDtoRequest
    ): ResponseEntity<PacienteDtoResponse> =
        ResponseEntity.ok(
            pacienteService.atualizar(id, dto)
        )

    @PatchMapping("/{id}/inativar")
    fun deactivate(
        @PathVariable id: Long,
    ): ResponseEntity<PacienteDtoResponse> =
        ResponseEntity.ok(
            pacienteService.inativar(id),
        )

    @DeleteMapping("/{id}", "/destroy/{id}")
    fun destroy(
        @PathVariable id: Long,
    ): ResponseEntity<Void>{
        pacienteService.deletar(id)
        return ResponseEntity.noContent().build()
    }

}