package no.nav.klage.api

import no.nav.klage.domain.Access
import no.nav.klage.domain.AssignedInKabalInput
import no.nav.klage.domain.CreateSakInput
import no.nav.klage.domain.FeilregistrertInKabalInput
import no.nav.klage.domain.GetSakWithSaksbehandlerIdent
import no.nav.klage.domain.HandledInKabalInput
import no.nav.klage.domain.KlankeSearchHit
import no.nav.klage.domain.KlankeSearchInput
import no.nav.klage.domain.Mottaker
import no.nav.klage.domain.Nivaa
import no.nav.klage.domain.Sak
import no.nav.klage.domain.SakFinishedInput
import no.nav.klage.domain.SakStatus
import no.nav.klage.domain.Sakstype
import no.nav.klage.domain.Status
import no.nav.klage.domain.TypeResultat
import no.nav.klage.domain.UpdateSakInput
import no.nav.klage.domain.Utfall
import no.nav.klage.getLogger
import no.nav.klage.service.SakService
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import tools.jackson.core.JacksonException

@RestController
@RequestMapping("/api")
class SakController(
    private val sakService: SakService,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    // Utility. Not in the original api we are mocking. Could be useful in tests.
    @GetMapping("/saker")
    fun getAllSaker(): List<Sak> {
        logger.debug("getAllSaker")

        return sakService.getAllSaker()
    }

    // Utility. Not in the original api we are mocking. Could be useful in tests.
    @GetMapping("/defaults")
    fun getDefaults(): Map<String, String> {
        logger.debug("getDefaults")

        return sakService.getDefaults()
    }

    // Utility. Not in the original api we are mocking. Could be useful in tests.
    @PostMapping("/saker")
    fun createSak(
        @RequestBody input: CreateSakInput,
    ): Sak {
        logger.debug("createSak")

        return sakService.createSak(input)
    }

    // Utility. Not in the original api we are mocking. Could be useful in tests.
    @PutMapping("/saker/{sakId}")
    fun updateSak(
        @PathVariable("sakId") sakId: String,
        @RequestBody input: UpdateSakInput,
    ): Sak {
        logger.debug("updateSak")

        return sakService.updateSak(sakId = sakId, input = input)
    }

    // Utility. Not in the original api we are mocking. Could be useful in tests.
    @DeleteMapping("/saker/{sakId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSak(
        @PathVariable("sakId") sakId: String,
    ) {
        logger.debug("deleteSak")

        sakService.deleteSak(sakId)
    }

    @PostMapping("/saker.rest")
    fun searchSaker(
        @RequestBody klankeSearchInput: KlankeSearchInput,
    ): List<KlankeSearchHit> {
        logger.debug("searchSaker")

        // sleep to simulate slow Infotrygd
        Thread.sleep(1000)

        return sakService.searchSaker(klankeSearchInput)
    }

    @PostMapping("/saker/{sakId}/handledinkabal.rest")
    fun setHandledInKabal(
        @PathVariable("sakId") sakId: String,
        @RequestBody handledInKabalInput: HandledInKabalInput,
    ) {
        logger.debug("setHandledInKabal")

        sakService.setHandledInKabal(sakId, handledInKabalInput)
    }

    @PostMapping("/saker/{sakId}/assignedinkabal.rest")
    fun setAssignedInKabal(
        @PathVariable("sakId") sakId: String,
        @RequestBody assignedInKabalInput: AssignedInKabalInput,
    ) {
        logger.debug("setAssignedInKabal")

        sakService.setAssignedInKabal(sakId, assignedInKabalInput)
    }

    @PostMapping("/saker/{sakId}/finished.rest")
    fun setSakFinished(
        @PathVariable("sakId") sakId: String,
        @RequestBody sakFinishedInput: SakFinishedInput,
    ) {
        logger.debug("setSakFinished")

        sakService.setSakFinished(sakId, sakFinishedInput)
    }

    @PostMapping("/saker/{sakId}/feilregistrert.rest")
    fun setSakFeilregistrert(
        @PathVariable("sakId") sakId: String,
        @RequestBody feilregistrertInKabalInput: FeilregistrertInKabalInput,
    ) {
        logger.debug("setSakFeilregistrert")

        sakService.setSakFeilregistrert(sakId, feilregistrertInKabalInput)
    }

    @PostMapping("/saker/{sakId}/detailsappaccess.rest")
    fun setDetailsAppAccess(
        @PathVariable("sakId") sakId: String,
        @RequestBody input: GetSakWithSaksbehandlerIdent,
    ): KlankeSearchHit {
        logger.debug("setDetailsAppAccess")

        return sakService.getSakAppAccess(sakId, input)
    }

    @GetMapping("/access.rest")
    fun setAccess(): Access {
        logger.debug("setAccess")

        return Access(access = true)
    }

    // A missing required field (e.g. fnr when creating a sak), an invalid enum value or malformed JSON
    // gives 400 with Jackson's description of the problem, instead of a 400 with no explanation.
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableBody(e: HttpMessageNotReadableException): ProblemDetail {
        val detail = (e.cause as? JacksonException)?.originalMessage ?: e.message ?: "Unreadable request body"
        logger.debug("Unreadable request body: {}", detail)

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail)
    }

    // Used by the utility endpoints (e.g. id mismatch on update, unknown sak on update/delete). Returns the
    // reason as a problem detail, instead of Tomcat's error page without it.
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(e: ResponseStatusException): ResponseEntity<ProblemDetail> {
        logger.debug("Responding with {}: {}", e.statusCode, e.reason)

        return ResponseEntity.status(e.statusCode).body(e.body)
    }
}
