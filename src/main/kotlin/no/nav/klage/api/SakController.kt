package no.nav.klage.api

import no.nav.klage.domain.*
import no.nav.klage.getLogger
import no.nav.klage.service.SakService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class SakController(
    private val sakService: SakService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }
    //Utility. Not in the original api we are mocking. Could be useful in tests.
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

        return sakService.searchSaker(klankeSearchInput)
    }

    @PostMapping("/saker/{sakId}/handledinkabal.rest")
    fun setHandledInKabal(
        @PathVariable("sakId") sakId: String,
        @RequestBody klankeSearchInput: KlankeSearchInput,
    ) {
        logger.debug("setHandledInKabal")

        sakService.setHandledInKabal(sakId, klankeSearchInput)
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