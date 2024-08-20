package no.nav.klage.service

import no.nav.klage.api.*
import no.nav.klage.repository.SakRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SakService(
    sakRepository: SakRepository,
) {
    fun searchSaker(klankeSearchInput: KlankeSearchInput): List<KlankeSearchHit> {
        TODO("Not yet implemented")
    }

    fun setHandledInKabal(sakId: String, klankeSearchInput: KlankeSearchInput) {
        TODO("Not yet implemented")
    }

    fun setAssignedInKabal(sakId: String, assignedInKabalInput: AssignedInKabalInput) {
        TODO("Not yet implemented")
    }

    fun setSakFinished(sakId: String, sakFinishedInput: SakFinishedInput) {
        TODO("Not yet implemented")
    }

    fun setSakFeilregistrert(sakId: String, feilregistrertInKabalInput: FeilregistrertInKabalInput) {
        TODO("Not yet implemented")
    }

    fun getSakAppAccess(sakId: String, input: GetSakWithSaksbehandlerIdent): KlankeSearchHit {
        TODO("Not yet implemented")
    }


}