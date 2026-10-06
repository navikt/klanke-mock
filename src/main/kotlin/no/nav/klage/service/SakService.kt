package no.nav.klage.service

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
import no.nav.klage.domain.SakDefaults
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
import java.security.SecureRandom

@Service
@Transactional
class SakService(
    private val sakRepository: SakRepository,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)

        const val SAK_ID_LENGTH = 10
        const val SAK_ID_MAX_ATTEMPTS = 5
        private const val SAK_ID_ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789"
        private val secureRandom = SecureRandom()
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

    fun getEnums(): Map<String, List<String>> =
        mapOf(
            "utfall" to Utfall.entries.map { it.name },
            "sakstype" to Sakstype.entries.map { it.name },
            "status" to SakStatus.entries.map { it.name },
            "typeResultat" to TypeResultat.entries.map { it.name },
            "nivaa" to Nivaa.entries.map { it.name },
        )

    fun createSak(input: CreateSakInput): Sak =
        sakRepository.save(
            Sak(
                id = generateSakId(),
                fagsakId = input.fagsakId,
                tema = input.tema ?: SakDefaults.TEMA,
                utfall = input.utfall ?: SakDefaults.UTFALL,
                enhetsnummer = input.enhetsnummer ?: SakDefaults.ENHETSNUMMER,
                vedtaksdatoAsString = input.vedtaksdatoAsString ?: SakDefaults.VEDTAKSDATO_AS_STRING,
                svardatoAsString = input.svardatoAsString ?: SakDefaults.SVARDATO_AS_STRING,
                fnr = input.fnr,
                sakstype = input.sakstype ?: SakDefaults.SAKSTYPE,
                status = input.status ?: SakDefaults.STATUS,
                saksbehandlerIdent = input.saksbehandlerIdent ?: SakDefaults.SAKSBEHANDLER_IDENT,
                typeResultat = input.typeResultat ?: SakDefaults.TYPE_RESULTAT,
                nivaa = input.nivaa ?: SakDefaults.NIVAA,
            ),
        )

    fun getDefaults(): Map<String, String> = SakDefaults.asMap()

    /**
     * Generates a new sak id: [SAK_ID_LENGTH] random characters from `[a-z0-9]`.
     *
     * Shorter than a UUID so it is easy to read and type in tests and the UI, while 36^10
     * (about 3.7e15) possible values make collisions very unlikely. Random rather than sequential,
     * so it does not depend on the format of existing (hand picked) ids. JPA `save` would overwrite
     * an existing row with the same id, so a taken id is retried, but only a few times.
     */
    private fun generateSakId(): String {
        repeat(SAK_ID_MAX_ATTEMPTS) {
            val id =
                (1..SAK_ID_LENGTH)
                    .map { SAK_ID_ALPHABET[secureRandom.nextInt(SAK_ID_ALPHABET.length)] }
                    .joinToString("")
            if (!sakRepository.existsById(id)) {
                return id
            }
            logger.warn("Generated sak id {} already exists, retrying", id)
        }
        error("Could not generate a unique sak id after $SAK_ID_MAX_ATTEMPTS attempts")
    }

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
