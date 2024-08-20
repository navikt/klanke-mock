package no.nav.klage.repository

import no.nav.klage.domain.Sak
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SakRepository : JpaRepository<Sak, UUID> {
}