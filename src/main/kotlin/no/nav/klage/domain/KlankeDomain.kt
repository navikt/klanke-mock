package no.nav.klage.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class KlankeSearchInput(
    val fnr: String,
    val sakstype: Sakstype,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KlankeSearchHit(
    val sakId: String,
    val fagsakId: String,
    val tema: String,
    val utfall: Utfall,
    val enhetsnummer: String,
    val vedtaksdatoAsString: String,
    val fnr: String,
    val sakstype: Sakstype,
    val typeResultat: TypeResultat,
    val nivaa: Nivaa,
)

enum class Sakstype {
    ANKE,
    KLAGE,
    KLAGE_AVREGNING,
    KLAGE_CONDICTIO_INDEBITI,
    KLAGE_ETTERGIVELSE,
    KLAGE_TILBAKEBETALING,
    KLAGE_TILBAKEBETALING_SU,
    SOEKNAD,
    SOEKNAD_ETTERGIVELSE,
    STRAFFERETTSLIG_VURDERING,
    SOEKNAD_GODKJENNING_Y_SKADE_SYKDOM;
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class HandledInKabalInput(
    //aka frist
    val svardatoAsString: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AssignedInKabalInput(
    val saksbehandlerIdent: String,
    val enhetsnummer: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FeilregistrertInKabalInput(
    val saksbehandlerIdent: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SakFinishedInput(
    val status: Status,
    val nivaa: Nivaa,
    val typeResultat: TypeResultat,
    val utfall: Utfall,
    val mottaker: Mottaker,
    val saksbehandlerIdent: String,
)

enum class Status {
    RETURNERT_TK,
    VIDERESENDT_TR,
}

enum class Nivaa {
    TK, KA, TR
}

enum class TypeResultat {
    RESULTAT,
    INNSTILLING_1,
    INNSTILLING_2,
}

enum class Utfall {
    AVSLAG,
    AVSLAG_GODKJENNING,
    AVVIST_KLAGE,
    ADVARSEL,
    DELVIS_GODKJENNING,
    DELVIS_INNVILGET,
    DELVIS_TILBAKEBETALING,
    GODKJENNING,
    HENLAGT,
    HENLAGT_BORTFALT,
    INNVILGET,
    IKKE_BEHANDLET,
    IKKE_STRAFFBART,
    IKKE_TILBAKEBETALING,
    HJEMVIST_FOR_NY_BEHANDLING,
    POLITIANMELDELSE,
    TILBAKEBETALING,
    TVANGSGEBYR_FASTHOLDES,
    IKKE_BRUK
}

enum class Mottaker {
    TRYGDEKONTOR, TRYGDERETTEN
}

data class Access(
    val access: Boolean
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GetSakWithSaksbehandlerIdent(
    val saksbehandlerIdent: String,
)