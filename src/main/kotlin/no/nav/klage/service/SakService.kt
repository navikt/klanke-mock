package no.nav.klage.service

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
import no.nav.klage.domain.UpdateSakInput
import no.nav.klage.domain.Utfall
import no.nav.klage.getLogger
import no.nav.klage.repository.SakRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class SakService(
    private val sakRepository: SakRepository,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    fun searchSaker(klankeSearchInput: KlankeSearchInput): List<KlankeSearchHit> {
        val findAll = sakRepository.findAll()
        logger.debug("searchSaker.input: {}", klankeSearchInput)
        logger.debug("searchSaker: findAll: {}", findAll)
        return findAll
            .filter {
                it.fnr == klankeSearchInput.fnr &&
                    it.status in listOf(SakStatus.ST, SakStatus.IP) &&
                    it.sakstype == klankeSearchInput.sakstype
            }.sortedByDescending { it.vedtaksdatoAsString }
            .map {
                KlankeSearchHit(
                    sakId = it.id,
                    fagsakId = it.fagsakId,
                    tema = it.tema,
                    utfall = it.utfall,
                    enhetsnummer = it.enhetsnummer,
                    vedtaksdatoAsString = it.vedtaksdatoAsString,
                    fnr = it.fnr,
                    sakstype = it.sakstype,
                    typeResultat = it.typeResultat,
                    nivaa = it.nivaa,
                )
            }.also {
                logger.debug("searchSaker: {}", it)
            }
    }

    fun setHandledInKabal(
        sakId: String,
        handledInKabalInput: HandledInKabalInput,
    ) {
        sakRepository.getReferenceById(sakId).apply {
            status = SakStatus.IP
            saksbehandlerIdent = "KABAL"
            svardatoAsString = handledInKabalInput.svardatoAsString
        }
    }

    fun setAssignedInKabal(
        sakId: String,
        assignedInKabalInput: AssignedInKabalInput,
    ) {
        sakRepository.getReferenceById(sakId).apply {
            status = SakStatus.UB
            saksbehandlerIdent = assignedInKabalInput.saksbehandlerIdent
            if (assignedInKabalInput.enhetsnummer != null) {
                enhetsnummer = assignedInKabalInput.enhetsnummer
            }
        }
    }

    fun setSakFinished(
        sakId: String,
        sakFinishedInput: SakFinishedInput,
    ) {
        sakRepository.getReferenceById(sakId).apply {
            status = SakStatus.FINISHED
            utfall = sakFinishedInput.utfall
            saksbehandlerIdent = sakFinishedInput.saksbehandlerIdent
        }
    }

    fun setSakFeilregistrert(
        sakId: String,
        feilregistrertInKabalInput: FeilregistrertInKabalInput,
    ) {
        sakRepository.getReferenceById(sakId).apply {
            status = SakStatus.ST
            saksbehandlerIdent = feilregistrertInKabalInput.saksbehandlerIdent
        }
    }

    fun getSakAppAccess(
        sakId: String,
        input: GetSakWithSaksbehandlerIdent,
    ): KlankeSearchHit =
        sakRepository.findById(sakId).get().let {
            KlankeSearchHit(
                sakId = it.id,
                fagsakId = it.fagsakId,
                tema = it.tema,
                utfall = it.utfall,
                enhetsnummer = it.enhetsnummer,
                vedtaksdatoAsString = it.vedtaksdatoAsString,
                fnr = it.fnr,
                sakstype = it.sakstype,
                typeResultat = it.typeResultat,
                nivaa = it.nivaa,
            )
        }

    fun getAllSaker(): List<Sak> = sakRepository.findAll().sortedBy { it.id }

    fun createSak(sak: Sak): Sak = sakRepository.save(sak)

    fun updateSak(
        sakId: String,
        input: UpdateSakInput,
    ): Sak {
        if (input.id != null && input.id != sakId) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Sak id ${input.id} in body does not match sakId $sakId in path. The id of a sak cannot be changed",
            )
        }
        if (!sakRepository.existsById(sakId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Sak with id $sakId not found")
        }
        return sakRepository.save(
            Sak(
                id = sakId,
                fagsakId = input.fagsakId,
                tema = input.tema,
                utfall = input.utfall,
                enhetsnummer = input.enhetsnummer,
                vedtaksdatoAsString = input.vedtaksdatoAsString,
                svardatoAsString = input.svardatoAsString,
                fnr = input.fnr,
                sakstype = input.sakstype,
                status = input.status,
                saksbehandlerIdent = input.saksbehandlerIdent,
                typeResultat = input.typeResultat,
                nivaa = input.nivaa,
            ),
        )
    }

    fun deleteSak(sakId: String) {
        if (!sakRepository.existsById(sakId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Sak with id $sakId not found")
        }
        sakRepository.deleteById(sakId)
    }
}
