package no.nav.klage.domain

import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate

@Entity
@Table(name = "sak", schema = "klage")
@DynamicUpdate
class Sak(
    @Id
    val id: String,
    val fagsakId: String,
    val tema: String,
    @Enumerated(EnumType.STRING)
    var utfall: Utfall,
    var enhetsnummer: String,
    val vedtaksdatoAsString: String,
    var svardatoAsString: String,
    val fnr: String,
    @Enumerated(EnumType.STRING)
    val sakstype: Sakstype,
    @Enumerated(EnumType.STRING)
    var status: SakStatus,
    var saksbehandlerIdent: String,
    @Enumerated(EnumType.STRING)
    var typeResultat: TypeResultat,
    @Enumerated(EnumType.STRING)
    var nivaa: Nivaa,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sak

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Sak(id='$id', fagsakId='$fagsakId', tema='$tema', utfall=$utfall, enhetsnummer='$enhetsnummer', vedtaksdatoAsString='$vedtaksdatoAsString', svardatoAsString='$svardatoAsString', fnr='$fnr', sakstype=$sakstype, status=$status, saksbehandlerIdent='$saksbehandlerIdent', typeResultat=$typeResultat, nivaa=$nivaa)"
    }

}

enum class SakStatus {
    FINISHED,
    ST,
    IP,
    UB,
}