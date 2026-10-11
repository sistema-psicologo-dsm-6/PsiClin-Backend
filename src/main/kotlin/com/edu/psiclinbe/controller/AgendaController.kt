package com.edu.psiclinbe.controller

import com.edu.psiclinbe.dto.formData.AgendaFormDataResponse
import com.edu.psiclinbe.dto.request.AgendaDtoRequest
import com.edu.psiclinbe.dto.response.AgendaDtoResponse
import com.edu.psiclinbe.mapper.AgendaMapper
import com.edu.psiclinbe.model.enums.AgendaStatus
import com.edu.psiclinbe.service.AgendaService
import com.edu.psiclinbe.service.ConvenioService
import com.edu.psiclinbe.service.PacienteService
import com.edu.psiclinbe.service.PsicologoService
import com.edu.psiclinbe.service.SalaAtendimentoService
import com.edu.psiclinbe.service.TIpoAtendimentoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/agendas")
class AgendaController(
    private val agendaService: AgendaService,
    private val pacienteService: PacienteService,
    private val psicologoService: PsicologoService,
    private val salaAtendimentoService: SalaAtendimentoService,
    private val tipoAtendimentoService: TIpoAtendimentoService,
    private val convenioService: ConvenioService,
    private val mapper: AgendaMapper
) {

    @GetMapping
    fun index(): ResponseEntity<List<AgendaDtoResponse>> =
        ResponseEntity.ok(
            agendaService.listarTodos()
        )

    @GetMapping("/{id}")
    fun show(
        @PathVariable id: Long
    ): ResponseEntity<AgendaDtoResponse> =
        ResponseEntity.ok(
            agendaService.buscarPorId(id)
        )

    @GetMapping("/por-observacoes")
    fun indexByObservations(
        @RequestParam termo: String
    ): ResponseEntity<List<AgendaDtoResponse>> =
        ResponseEntity.ok(agendaService.buscarPorObservacoes(termo))

    @PostMapping
    fun store(
        @Valid @RequestBody dto: AgendaDtoRequest
    ): ResponseEntity<AgendaDtoResponse> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(
                agendaService.salvar(dto)
            )

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody dto: AgendaDtoRequest
    ): ResponseEntity<AgendaDtoResponse> =
        ResponseEntity.ok(
            agendaService.atualizar(id, dto)
        )

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestParam status: AgendaStatus
    ): ResponseEntity<AgendaDtoResponse> =
        ResponseEntity.ok(
            agendaService.atualizarStatus(id, status)
        )

    @DeleteMapping("/{id}")
    fun destroy(
        @PathVariable id: Long
    ): ResponseEntity<Void> {

        agendaService.deletar(id)

        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/destroy/{id}")
    fun destroyPermanently(
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        agendaService.excluirPermanentemente(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/form-data")
    fun formData(
        @RequestParam(required = false) id: Long?,
    ): ResponseEntity<AgendaFormDataResponse> {
        val pacientes = pacienteService.listarTodos()
        val psicologos = psicologoService.listarTodos()
        val salas = salaAtendimentoService.listarTodasAsSalas()
        val tipoAtendimento = tipoAtendimentoService.listarTodosTiposAtendimento()
        val conveios = convenioService.listarTodos()

        val agenda = id?.let { agendaService.buscarPorId(it) }

        return ResponseEntity.ok(
            mapper.toFormData(
                pacientes,
                psicologos,
                salas,
                tipoAtendimento,
                conveios,
                agenda

            )
        )

    }
}