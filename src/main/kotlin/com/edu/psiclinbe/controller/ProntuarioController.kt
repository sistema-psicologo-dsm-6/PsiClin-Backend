package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.request.ProntuarioDtoRequest
import com.edu.psiclinbe.dto.response.ProntuarioDtoResponse
import com.edu.psiclinbe.model.enums.ProntuarioStatus
import com.edu.psiclinbe.service.ProntuarioService
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
@RequestMapping("/prontuarios")
class ProntuarioController(
    private val prontuarioService: ProntuarioService,
) {
    @GetMapping
    fun index(): ResponseEntity<List<ProntuarioDtoResponse>> =
        ResponseEntity.ok(prontuarioService.listarProntuarios())

    @GetMapping("/{id}")
    fun show(@PathVariable id: Long): ResponseEntity<ProntuarioDtoResponse> =
        ResponseEntity.ok(prontuarioService.buscarProntuario(id))

    @GetMapping("/por-queixa-principal")
    fun indexByChiefComplaint(@RequestParam termo: String): ResponseEntity<List<ProntuarioDtoResponse>> =
        ResponseEntity.ok(prontuarioService.buscarPorQueixaPrincipal(termo))

    @GetMapping("/por-paciente/{pacienteId}")
    fun indexByPatient(@PathVariable pacienteId: Long): ResponseEntity<List<ProntuarioDtoResponse>> =
        ResponseEntity.ok(prontuarioService.buscarPorPaciente(pacienteId))

    @GetMapping("/por-paciente/{pacienteId}/status/{status}")
    fun indexByPatientAndStatus(
        @PathVariable pacienteId: Long,
        @PathVariable status: ProntuarioStatus,
    ): ResponseEntity<List<ProntuarioDtoResponse>> =
        ResponseEntity.ok(prontuarioService.buscarPorPacienteEStatus(pacienteId, status))

    @PostMapping
    fun store(
        @Valid @RequestBody dto: ProntuarioDtoRequest,
    ): ResponseEntity<ProntuarioDtoResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(prontuarioService.cadastrarProntuario(dto))

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: ProntuarioDtoRequest,
    ): ResponseEntity<ProntuarioDtoResponse> =
        ResponseEntity.ok(prontuarioService.atualizarProntuario(id, dto))

    @DeleteMapping("/{id}")
    fun destroy(@PathVariable id: Long): ResponseEntity<Void> {
        prontuarioService.excluirProntuario(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/destroy/{id}")
    fun destroyPermanently(@PathVariable id: Long): ResponseEntity<Void> {
        prontuarioService.excluirPermanentemente(id)
        return ResponseEntity.noContent().build()
    }
}
