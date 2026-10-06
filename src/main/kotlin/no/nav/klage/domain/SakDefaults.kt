package no.nav.klage.domain

// Values used for fields left out (or sent as null) when creating a sak. Also served by GET /api/defaults.
object SakDefaults {
    const val TEMA = "SYK"
    val UTFALL = Utfall.AVSLAG
    const val ENHETSNUMMER = "4291"
    const val VEDTAKSDATO_AS_STRING = ""
    const val SVARDATO_AS_STRING = ""
    val SAKSTYPE = Sakstype.KLAGE
    val STATUS = SakStatus.ST
    const val SAKSBEHANDLER_IDENT = "SYSTEMBRUKER"
    val TYPE_RESULTAT = TypeResultat.INNSTILLING_1
    val NIVAA = Nivaa.TK

    // Keyed by Sak field name.
    fun asMap(): Map<String, String> =
        linkedMapOf(
            "tema" to TEMA,
            "utfall" to UTFALL.name,
            "enhetsnummer" to ENHETSNUMMER,
            "vedtaksdatoAsString" to VEDTAKSDATO_AS_STRING,
            "svardatoAsString" to SVARDATO_AS_STRING,
            "sakstype" to SAKSTYPE.name,
            "status" to STATUS.name,
            "saksbehandlerIdent" to SAKSBEHANDLER_IDENT,
            "typeResultat" to TYPE_RESULTAT.name,
            "nivaa" to NIVAA.name,
        )
}
