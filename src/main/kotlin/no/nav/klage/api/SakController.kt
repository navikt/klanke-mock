package no.nav.klage.api

import no.nav.klage.domain.Access
import no.nav.klage.domain.AssignedInKabalInput
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
import no.nav.klage.domain.Utfall
import no.nav.klage.getLogger
import no.nav.klage.service.SakService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    @PostMapping("/saker")
    fun createSak(
        @RequestBody sak: Sak,
    ): Sak {
        logger.debug("createSak")

        return sakService.createSak(sak)
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
}
