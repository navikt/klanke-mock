package no.nav.klage.service

import no.nav.klage.domain.*
import no.nav.klage.repository.SakRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SakService(
    private val sakRepository: SakRepository,
) {
    fun searchSaker(klankeSearchInput: KlankeSearchInput): List<KlankeSearchHit> {
        return sakRepository.findAll().filter {
            it.status in listOf(SakStatus.ST, SakStatus.IP)
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
                )
            }
    }

    fun setHandledInKabal(sakId: String, klankeSearchInput: KlankeSearchInput) {
        sakRepository.getReferenceById(sakId).apply {
            status = SakStatus.IP
            saksbehandlerIdent = "KABAL"
        }
    }

    fun setAssignedInKabal(sakId: String, assignedInKabalInput: AssignedInKabalInput) {
        sakRepository.getReferenceById(sakId).apply {
            status = SakStatus.UB
            saksbehandlerIdent = assignedInKabalInput.saksbehandlerIdent
            if (assignedInKabalInput.enhetsnummer != null) {
                enhetsnummer = assignedInKabalInput.enhetsnummer
            }
        }
    }

    fun setSakFinished(sakId: String, sakFinishedInput: SakFinishedInput) {
        sakRepository.getReferenceById(sakId).apply {
            status = SakStatus.FINISHED
            utfall = sakFinishedInput.utfall
            saksbehandlerIdent = sakFinishedInput.saksbehandlerIdent
        }
    }

    fun setSakFeilregistrert(sakId: String, feilregistrertInKabalInput: FeilregistrertInKabalInput) {
        sakRepository.getReferenceById(sakId).apply {
            status = SakStatus.ST
            saksbehandlerIdent = feilregistrertInKabalInput.saksbehandlerIdent
        }
    }

    fun getSakAppAccess(sakId: String, input: GetSakWithSaksbehandlerIdent): KlankeSearchHit {
        return sakRepository.findById(sakId).get().let {
            KlankeSearchHit(
                sakId = it.id,
                fagsakId = it.fagsakId,
                tema = it.tema,
                utfall = it.utfall,
                enhetsnummer = it.enhetsnummer,
                vedtaksdatoAsString = it.vedtaksdatoAsString,
                fnr = it.fnr,
                sakstype = it.sakstype,
            )
        }
    }

    fun createSak(sak: Sak): Sak {
        return sakRepository.save(sak)
    }

}