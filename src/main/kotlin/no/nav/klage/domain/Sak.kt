package no.nav.klage.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import java.util.*

@Entity
@Table(name = "sak", schema = "klage")
@DynamicUpdate
class Sak(
    @Id
    val id: UUID = UUID.randomUUID(),
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
}