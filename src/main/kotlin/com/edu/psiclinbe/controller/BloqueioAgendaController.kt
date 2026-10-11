package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.request.BloqueioAgendaDtoRequest
import com.edu.psiclinbe.dto.response.BloqueioAgendaDtoResponse
import com.edu.psiclinbe.service.BloqueioAgendaService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
@RestController
@RequestMapping("/bloqueios-agenda")
class BloqueioAgendaController(
    private val service: BloqueioAgendaService
) {

    @GetMapping
    fun index(): ResponseEntity<List<BloqueioAgendaDtoResponse>> =
        ResponseEntity.ok(
            service.listarTodos()
        )

    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long
    ): ResponseEntity<BloqueioAgendaDtoResponse> =
        ResponseEntity.ok(
            service.buscarPorId(id)
        )

    @GetMapping("/por-motivo")
    fun indexByReason(
        @RequestParam termo: String
    ): ResponseEntity<List<BloqueioAgendaDtoResponse>> =
        ResponseEntity.ok(service.buscarPorMotivo(termo))

    @GetMapping("/por-psicologo/{psicologoId}")
    fun indexByPsychologist(
        @PathVariable psicologoId: Long
    ): ResponseEntity<List<BloqueioAgendaDtoResponse>> =
        ResponseEntity.ok(
            service.buscarPorPsicologo(psicologoId)
        )

    @GetMapping("/por-periodo")
    fun indexByPeriod(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        inicio: LocalDateTime,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        fim: LocalDateTime
    ): ResponseEntity<List<BloqueioAgendaDtoResponse>> =
        ResponseEntity.ok(
            service.buscarPorPeriodo(
                inicio,
                fim
            )
        )

    @PostMapping
    fun store(
        @Valid @RequestBody dto: BloqueioAgendaDtoRequest
    ): ResponseEntity<BloqueioAgendaDtoResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(
                service.salvar(dto)
            )

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: BloqueioAgendaDtoRequest
    ): ResponseEntity<BloqueioAgendaDtoResponse> =
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