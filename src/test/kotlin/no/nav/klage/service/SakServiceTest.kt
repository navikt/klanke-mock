package no.nav.klage.service

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import no.nav.klage.domain.CreateSakInput
import no.nav.klage.domain.Nivaa
import no.nav.klage.domain.Sak
import no.nav.klage.domain.SakDefaults
import no.nav.klage.domain.SakStatus
import no.nav.klage.domain.Sakstype
import no.nav.klage.domain.TypeResultat
import no.nav.klage.domain.UpdateSakInput
import no.nav.klage.domain.Utfall
import no.nav.klage.repository.SakRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class SakServiceTest {
    private val sakRepository: SakRepository = mockk()
    private val sakService = SakService(sakRepository = sakRepository)

    @Test
    fun `getAllSaker returns all saker sorted by id`() {
        val sakB = createSak(id = "b")
        val sakA = createSak(id = "a")
        val sakC = createSak(id = "c")
        every { sakRepository.findAll() } returns listOf(sakB, sakC, sakA)

        val result = sakService.getAllSaker()

        assertThat(result.map { it.id }).containsExactly("a", "b", "c")
    }

    @Test
    fun `getAllSaker returns empty list when there are no saker`() {
        every { sakRepository.findAll() } returns emptyList()

        assertThat(sakService.getAllSaker()).isEmpty()
    }

    @Test
    fun `getEnums returns all enum values keyed by Sak field name`() {
        val result = sakService.getEnums()

        assertThat(result).containsOnlyKeys("utfall", "sakstype", "status", "typeResultat", "nivaa")
        assertThat(result["utfall"]).isEqualTo(Utfall.entries.map { it.name })
        assertThat(result["sakstype"]).isEqualTo(Sakstype.entries.map { it.name })
        assertThat(result["status"]).isEqualTo(SakStatus.entries.map { it.name })
        assertThat(result["typeResultat"]).isEqualTo(TypeResultat.entries.map { it.name })
        assertThat(result["nivaa"]).isEqualTo(Nivaa.entries.map { it.name })
    }

    @Test
    fun `createSak saves sak with generated id and fields from input`() {
        val saved = slot<Sak>()
        every { sakRepository.existsById(any()) } returns false
        every { sakRepository.save(capture(saved)) } answers { firstArg() }

        val result = sakService.createSak(createSakInput())

        assertThat(result).isSameAs(saved.captured)
        assertThat(result.id).matches("^[a-z0-9]{10}$")
        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(createSak(id = result.id))
        verify(exactly = 1) { sakRepository.existsById(result.id) }
    }

    @Test
    fun `createSak applies defaults for fields not set in input`() {
        every { sakRepository.existsById(any()) } returns false
        every { sakRepository.save(any()) } answers { firstArg() }

        val result = sakService.createSak(CreateSakInput(fagsakId = "fagsak1", fnr = "12345678910"))

        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(
                Sak(
                    id = result.id,
                    fagsakId = "fagsak1",
                    tema = "SYK",
                    utfall = Utfall.AVSLAG,
                    enhetsnummer = "4291",
                    vedtaksdatoAsString = "",
                    svardatoAsString = "",
                    fnr = "12345678910",
                    sakstype = Sakstype.KLAGE,
                    status = SakStatus.ST,
                    saksbehandlerIdent = "SYSTEMBRUKER",
                    typeResultat = TypeResultat.INNSTILLING_1,
                    nivaa = Nivaa.TK,
                ),
            )
    }

    @Test
    fun `getDefaults returns defaults keyed by Sak field name`() {
        assertThat(sakService.getDefaults()).containsExactlyEntriesOf(SakDefaults.asMap())
        assertThat(sakService.getDefaults().keys).allSatisfy { field ->
            assertThat(Sak::class.java.getDeclaredField(field)).isNotNull()
        }
    }

    @Test
    fun `createSak generates different ids`() {
        every { sakRepository.existsById(any()) } returns false
        every { sakRepository.save(any()) } answers { firstArg() }

        val ids = (1..20).map { sakService.createSak(createSakInput()).id }

        assertThat(ids).allMatch { it.matches(Regex("^[a-z0-9]{10}$")) }
        assertThat(ids).doesNotHaveDuplicates()
    }

    @Test
    fun `createSak retries when generated id already exists`() {
        val checkedIds = mutableListOf<String>()
        every { sakRepository.existsById(capture(checkedIds)) } returnsMany listOf(true, false)
        every { sakRepository.save(any()) } answers { firstArg() }

        val result = sakService.createSak(createSakInput())

        assertThat(checkedIds).hasSize(2)
        assertThat(checkedIds[0]).isNotEqualTo(checkedIds[1])
        assertThat(result.id).isEqualTo(checkedIds[1])
        verify(exactly = 1) { sakRepository.save(any()) }
    }

    @Test
    fun `createSak gives up after max attempts when every generated id exists`() {
        every { sakRepository.existsById(any()) } returns true

        assertThrows<IllegalStateException> { sakService.createSak(createSakInput()) }

        verify(exactly = SakService.SAK_ID_MAX_ATTEMPTS) { sakRepository.existsById(any()) }
        verify(exactly = 0) { sakRepository.save(any()) }
    }

    @Test
    fun `deleteSak deletes existing sak`() {
        every { sakRepository.existsById("sak1") } returns true
        every { sakRepository.deleteById("sak1") } just runs

        sakService.deleteSak(sakId = "sak1")

        verify(exactly = 1) { sakRepository.deleteById("sak1") }
    }

    @Test
    fun `deleteSak throws not found when sak does not exist`() {
        every { sakRepository.existsById("missing") } returns false

        val exception = assertThrows<ResponseStatusException> { sakService.deleteSak(sakId = "missing") }

        assertThat(exception.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        verify(exactly = 0) { sakRepository.deleteById(any()) }
    }

    @Test
    fun `updateSak saves sak with id from path and fields from input`() {
        val saved = slot<Sak>()
        every { sakRepository.existsById("sak1") } returns true
        every { sakRepository.save(capture(saved)) } answers { firstArg() }

        val result = sakService.updateSak(sakId = "sak1", input = updateSakInput(id = null))

        assertThat(result).isSameAs(saved.captured)
        assertThat(result.id).isEqualTo("sak1")
        assertThat(result).usingRecursiveComparison().isEqualTo(createSak(id = "sak1"))
    }

    @Test
    fun `updateSak accepts id in body matching path`() {
        every { sakRepository.existsById("sak1") } returns true
        every { sakRepository.save(any()) } answers { firstArg() }

        val result = sakService.updateSak(sakId = "sak1", input = updateSakInput(id = "sak1"))

        assertThat(result.id).isEqualTo("sak1")
    }

    @Test
    fun `updateSak throws not found when sak does not exist`() {
        every { sakRepository.existsById("missing") } returns false

        val exception =
            assertThrows<ResponseStatusException> {
                sakService.updateSak(sakId = "missing", input = updateSakInput(id = null))
            }

        assertThat(exception.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        verify(exactly = 0) { sakRepository.save(any()) }
    }

    @Test
    fun `updateSak throws bad request when id in body does not match path`() {
        val exception =
            assertThrows<ResponseStatusException> {
                sakService.updateSak(sakId = "sak1", input = updateSakInput(id = "other"))
            }

        assertThat(exception.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        verify(exactly = 0) { sakRepository.save(any()) }
    }

    private fun createSak(id: String): Sak =
        Sak(
            id = id,
            fagsakId = "fagsak1",
            tema = "AAP",
            utfall = Utfall.AVSLAG,
            enhetsnummer = "4219",
            vedtaksdatoAsString = "2026-01-01",
            svardatoAsString = "2026-01-15",
            fnr = "12345678910",
            sakstype = Sakstype.KLAGE,
            status = SakStatus.ST,
            saksbehandlerIdent = "Z123456",
            typeResultat = TypeResultat.RESULTAT,
            nivaa = Nivaa.KA,
        )

    private fun createSakInput(): CreateSakInput =
        CreateSakInput(
            fagsakId = "fagsak1",
            tema = "AAP",
            utfall = Utfall.AVSLAG,
            enhetsnummer = "4219",
            vedtaksdatoAsString = "2026-01-01",
            svardatoAsString = "2026-01-15",
            fnr = "12345678910",
            sakstype = Sakstype.KLAGE,
            status = SakStatus.ST,
            saksbehandlerIdent = "Z123456",
            typeResultat = TypeResultat.RESULTAT,
            nivaa = Nivaa.KA,
        )

    private fun updateSakInput(id: String?): UpdateSakInput =
        UpdateSakInput(
            id = id,
            fagsakId = "fagsak1",
            tema = "AAP",
            utfall = Utfall.AVSLAG,
            enhetsnummer = "4219",
            vedtaksdatoAsString = "2026-01-01",
            svardatoAsString = "2026-01-15",
            fnr = "12345678910",
            sakstype = Sakstype.KLAGE,
            status = SakStatus.ST,
            saksbehandlerIdent = "Z123456",
            typeResultat = TypeResultat.RESULTAT,
            nivaa = Nivaa.KA,
        )
}
