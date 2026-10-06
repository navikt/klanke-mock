package no.nav.klage.api

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.nav.klage.domain.Sak
import no.nav.klage.domain.SakDefaults
import no.nav.klage.repository.SakRepository
import no.nav.klage.service.SakService
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.http.MediaType
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper

class SakControllerTest {
    private val sakRepository: SakRepository = mockk()

    // Standalone MockMvc with the real SakService and a mocked repository (no DB needed), using the
    // JsonMapper Spring Boot configures for the app.
    private val mockMvc: MockMvc =
        MockMvcBuilders
            .standaloneSetup(SakController(sakService = SakService(sakRepository = sakRepository)))
            .setMessageConverters(JacksonJsonHttpMessageConverter(bootJsonMapper()))
            .build()

    init {
        every { sakRepository.existsById(any()) } returns false
        every { sakRepository.save(any()) } answers { firstArg() }
    }

    @Test
    fun `POST saker with all fields creates sak with generated id`() {
        postSak(FULL_BODY)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(matchesIdPattern()))
            .andExpect(jsonPath("$.fagsakId").value("fagsak1"))
            .andExpect(jsonPath("$.fnr").value("12345678910"))
            .andExpect(jsonPath("$.tema").value("AAP"))
            .andExpect(jsonPath("$.utfall").value("INNVILGET"))
            .andExpect(jsonPath("$.enhetsnummer").value("4219"))
            .andExpect(jsonPath("$.vedtaksdatoAsString").value("20240101"))
            .andExpect(jsonPath("$.svardatoAsString").value("20240201"))
            .andExpect(jsonPath("$.sakstype").value("ANKE"))
            .andExpect(jsonPath("$.status").value("IP"))
            .andExpect(jsonPath("$.saksbehandlerIdent").value("Z123456"))
            .andExpect(jsonPath("$.typeResultat").value("RESULTAT"))
            .andExpect(jsonPath("$.nivaa").value("KA"))
    }

    @Test
    fun `POST saker ignores id sent by old clients`() {
        postSak(FULL_BODY.withId("client-chosen-id"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(matchesIdPattern()))

        verify(exactly = 0) { sakRepository.existsById("client-chosen-id") }
    }

    @Test
    fun `POST saker with only fnr and fagsakId applies all defaults`() {
        postSak("""{"fnr": "12345678910", "fagsakId": "fagsak1"}""")
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(matchesIdPattern()))
            .andExpect(jsonPath("$.fnr").value("12345678910"))
            .andExpect(jsonPath("$.fagsakId").value("fagsak1"))
            .andExpectDefaults(SakDefaults.asMap())
    }

    @Test
    fun `POST saker uses defaults for fields sent as null`() {
        postSak("""{"fnr": "12345678910", "fagsakId": "fagsak1", "tema": null, "nivaa": null, "utfall": "INNVILGET"}""")
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.tema").value(SakDefaults.TEMA))
            .andExpect(jsonPath("$.nivaa").value(SakDefaults.NIVAA.name))
            .andExpect(jsonPath("$.utfall").value("INNVILGET"))
    }

    @Test
    fun `POST saker without fnr returns 400 naming the missing field`() {
        postSak("""{"fagsakId": "fagsak1"}""")
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.detail").value(containsString("'fnr'")))

        verify(exactly = 0) { sakRepository.save(any()) }
    }

    @Test
    fun `POST saker with null fnr returns 400 naming the field`() {
        postSak("""{"fagsakId": "fagsak1", "fnr": null}""")
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.detail").value(containsString("fnr")))

        verify(exactly = 0) { sakRepository.save(any()) }
    }

    @Test
    fun `POST saker without fagsakId returns 400 naming the missing field`() {
        postSak("""{"fnr": "12345678910"}""")
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.detail").value(containsString("'fagsakId'")))
    }

    @Test
    fun `PUT saker without id in body updates sak with id from path`() {
        val saved = slot<Sak>()
        every { sakRepository.existsById("sak1") } returns true
        every { sakRepository.save(capture(saved)) } answers { firstArg() }

        putSak(sakId = "sak1", body = FULL_BODY)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("sak1"))
            .andExpect(jsonPath("$.tema").value("AAP"))
            .andExpect(jsonPath("$.nivaa").value("KA"))

        assertThat(saved.captured.id).isEqualTo("sak1")
        assertThat(saved.captured.fagsakId).isEqualTo("fagsak1")
    }

    @Test
    fun `PUT saker with id in body matching path updates sak`() {
        every { sakRepository.existsById("sak1") } returns true

        putSak(sakId = "sak1", body = FULL_BODY.withId("sak1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("sak1"))
    }

    @Test
    fun `PUT saker with different id in body returns 400 and does not change the id`() {
        every { sakRepository.existsById(any()) } returns true

        putSak(sakId = "sak1", body = FULL_BODY.withId("other"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.detail").value(containsString("cannot be changed")))

        verify(exactly = 0) { sakRepository.save(any()) }
    }

    @Test
    fun `PUT saker for unknown sak returns 404 with reason`() {
        putSak(sakId = "missing", body = FULL_BODY)
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.detail").value("Sak with id missing not found"))
    }

    @Test
    fun `PUT saker without a required field returns 400 naming the field`() {
        every { sakRepository.existsById("sak1") } returns true

        putSak(sakId = "sak1", body = """{"fagsakId": "fagsak1", "fnr": "12345678910"}""")
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.detail").value(containsString("Missing required creator property")))

        verify(exactly = 0) { sakRepository.save(any()) }
    }

    @Test
    fun `GET defaults returns defaults keyed by field name`() {
        mockMvc
            .perform(get("/api/defaults"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(SakDefaults.asMap().size))
            .andExpectDefaults(
                mapOf(
                    "tema" to "SYK",
                    "utfall" to "AVSLAG",
                    "enhetsnummer" to "4291",
                    "vedtaksdatoAsString" to "",
                    "svardatoAsString" to "",
                    "sakstype" to "KLAGE",
                    "status" to "ST",
                    "saksbehandlerIdent" to "SYSTEMBRUKER",
                    "typeResultat" to "INNSTILLING_1",
                    "nivaa" to "TK",
                ),
            )
    }

    @Test
    fun `spring boot json mapper ignores unknown properties`() {
        val config = bootJsonMapper().deserializationConfig()

        assertThat(config.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)).isFalse()
    }

    private fun postSak(body: String): ResultActions =
        mockMvc.perform(post("/api/saker").contentType(MediaType.APPLICATION_JSON).content(body))

    private fun putSak(
        sakId: String,
        body: String,
    ): ResultActions = mockMvc.perform(put("/api/saker/$sakId").contentType(MediaType.APPLICATION_JSON).content(body))

    private fun String.withId(id: String): String = replaceFirst(oldValue = "{", newValue = """{"id": "$id",""")

    private fun ResultActions.andExpectDefaults(defaults: Map<String, String>): ResultActions =
        defaults.entries.fold(this) { actions, (field, value) -> actions.andExpect(jsonPath("$.$field").value(value)) }

    private fun matchesIdPattern() = org.hamcrest.Matchers.matchesPattern("^[a-z0-9]{10}$")

    private fun bootJsonMapper(): JsonMapper {
        lateinit var jsonMapper: JsonMapper
        ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration::class.java))
            .run { context -> jsonMapper = context.getBean(JsonMapper::class.java) }
        return jsonMapper
    }

    private companion object {
        val FULL_BODY =
            """
            {
              "fagsakId": "fagsak1",
              "tema": "AAP",
              "utfall": "INNVILGET",
              "enhetsnummer": "4219",
              "vedtaksdatoAsString": "20240101",
              "svardatoAsString": "20240201",
              "fnr": "12345678910",
              "sakstype": "ANKE",
              "status": "IP",
              "saksbehandlerIdent": "Z123456",
              "typeResultat": "RESULTAT",
              "nivaa": "KA"
            }
            """.trimIndent()
    }
}
