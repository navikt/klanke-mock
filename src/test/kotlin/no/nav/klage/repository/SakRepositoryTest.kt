package no.nav.klage.repository

import no.nav.klage.db.TestPostgresqlContainer
import no.nav.klage.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@ActiveProfiles("local")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SakRepositoryTest {

    companion object {
        @Container
        @JvmField
        val postgreSQLContainer: TestPostgresqlContainer = TestPostgresqlContainer.instance
    }

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var sakRepository: SakRepository

    @Test
    fun `store all values in sak works`() {

        val sak = testEntityManager.persistAndFlush(
            Sak(
                id = "nunc",
                fagsakId = "aliquip",
                tema = "regione",
                utfall = Utfall.AVSLAG,
                enhetsnummer = "fusce",
                vedtaksdatoAsString = "arcu",
                svardatoAsString = "efficitur",
                fnr = "efficitur",
                sakstype = Sakstype.KLAGE,
                status = SakStatus.FINISHED,
                saksbehandlerIdent = "falli",
                typeResultat = TypeResultat.INNSTILLING_1,
                nivaa = Nivaa.TK,
            )
        )

        testEntityManager.clear()

        val sakFromDB = sakRepository.findById(sak.id).get()

        // Check that all values are stored correctly
        assertThat(sakFromDB.id).isEqualTo(sak.id)
        assertThat(sakFromDB.fagsakId).isEqualTo(sak.fagsakId)
        assertThat(sakFromDB.tema).isEqualTo(sak.tema)
        assertThat(sakFromDB.utfall).isEqualTo(sak.utfall)
        assertThat(sakFromDB.enhetsnummer).isEqualTo(sak.enhetsnummer)
        assertThat(sakFromDB.vedtaksdatoAsString).isEqualTo(sak.vedtaksdatoAsString)
        assertThat(sakFromDB.fnr).isEqualTo(sak.fnr)
        assertThat(sakFromDB.sakstype).isEqualTo(sak.sakstype)
        assertThat(sakFromDB.status).isEqualTo(sak.status)
        assertThat(sakFromDB.saksbehandlerIdent).isEqualTo(sak.saksbehandlerIdent)

    }

}