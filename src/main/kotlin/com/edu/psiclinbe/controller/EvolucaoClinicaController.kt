package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.request.EvolucaoClinicaDtoRequest
import com.edu.psiclinbe.dto.response.EvolucaoClinicaDtoResponse
import com.edu.psiclinbe.service.EvolucaoClinicaService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
@RestController
@RequestMapping("/evolucoes-clinicas")
class EvolucaoClinicaController(
    private val service: EvolucaoClinicaService
) {

    @GetMapping
    fun index(): ResponseEntity<List<EvolucaoClinicaDtoResponse>> =
        ResponseEntity.ok(
            service.listarTodos()
        )

    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long
    ): ResponseEntity<EvolucaoClinicaDtoResponse> =
        ResponseEntity.ok(
            service.buscarPorId(id)
        )

    @GetMapping("/por-descricao")
    fun indexByDescription(
        @RequestParam termo: String
    ): ResponseEntity<List<EvolucaoClinicaDtoResponse>> =
        ResponseEntity.ok(service.buscarPorDescricao(termo))

    @GetMapping("/por-prontuario/{prontuarioId}")
    fun indexByRecord(
        @PathVariable prontuarioId: Long
    ): ResponseEntity<List<EvolucaoClinicaDtoResponse>> =
        ResponseEntity.ok(
            service.buscarPorProntuario(prontuarioId)
        )

    @GetMapping("/por-psicologo/{psicologoId}")
    fun indexByPsychologist(
        @PathVariable psicologoId: Long
    ): ResponseEntity<List<EvolucaoClinicaDtoResponse>> =
        ResponseEntity.ok(
            service.buscarPorPsicologo(psicologoId)
        )

    @GetMapping("/por-agenda/{agendaId}")
    fun indexByAppointment(
        @PathVariable agendaId: Long
    ): ResponseEntity<List<EvolucaoClinicaDtoResponse>> =
        ResponseEntity.ok(
            service.buscarPorAgenda(agendaId)
        )

    @PostMapping
    fun store(
        @Valid @RequestBody dto: EvolucaoClinicaDtoRequest
    ): ResponseEntity<EvolucaoClinicaDtoResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(
                service.salvar(dto)
            )

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: EvolucaoClinicaDtoRequest
    ): ResponseEntity<EvolucaoClinicaDtoResponse> =
        ResponseEntity.ok(
            service.atualizar(id, dto)
        )

    @DeleteMapping("/{id}")
    fun destroy(
        @PathVariable id: Long
    ): ResponseEntity<Void> {

        service.deletar(id)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/destroy/{id}")
    fun destroyPermanently(
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        service.excluirPermanentemente(id)
        return ResponseEntity.noContent().build()
    }
}