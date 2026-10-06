package no.nav.klage.domain

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Body for replacing a sak with `PUT /api/saker/{sakId}`. The id is immutable and always taken from the path.
 *
 * Every other field is required; no create defaults are applied, since a full replace that silently reset
 * missing fields to defaults would be surprising. [id] may be left out. If it is sent, it must match the
 * path, so a client trying to change the id gets a 400 instead of having it silently ignored.
 */
data class UpdateSakInput(
    val id: String? = null,
    @JsonProperty(required = true)
    val fagsakId: String,
    @JsonProperty(required = true)
    val tema: String,
    @JsonProperty(required = true)
    val utfall: Utfall,
    @JsonProperty(required = true)
    val enhetsnummer: String,
    @JsonProperty(required = true)
    val vedtaksdatoAsString: String,
    @JsonProperty(required = true)
    val svardatoAsString: String,
    @JsonProperty(required = true)
    val fnr: String,
    @JsonProperty(required = true)
    val sakstype: Sakstype,
    @JsonProperty(required = true)
    val status: SakStatus,
    @JsonProperty(required = true)
    val saksbehandlerIdent: String,
    @JsonProperty(required = true)
    val typeResultat: TypeResultat,
    @JsonProperty(required = true)
    val nivaa: Nivaa,
)
