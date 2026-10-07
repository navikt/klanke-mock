package no.nav.klage.api

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.config.OpenApiConfig
import no.nav.klage.domain.CreateSakInput
import no.nav.klage.domain.Sak
import no.nav.klage.domain.UpdateSakInput
import no.nav.klage.getLogger
import no.nav.klage.service.SakService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

// Utility endpoints for managing the mock data, used by the UI. Not in the original api we are mocking.
// Could be useful in tests.
@Tag(name = OpenApiConfig.MOCK_DATA_TAG)
@RestController
@RequestMapping("/mock-data")
class MockDataController(
    private val sakService: SakService,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/saker")
    fun getAllSaker(): List<Sak> {
        logger.debug("getAllSaker")

        return sakService.getAllSaker()
    }

    @GetMapping("/enums")
    fun getEnums(): Map<String, List<String>> {
        logger.debug("getEnums")

        return sakService.getEnums()
    }

    @GetMapping("/defaults")
    fun getDefaults(): Map<String, String> {
        logger.debug("getDefaults")

        return sakService.getDefaults()
    }

    @PostMapping("/saker")
    fun createSak(
        @RequestBody input: CreateSakInput,
    ): Sak {
        logger.debug("createSak")

        return sakService.createSak(input)
    }

    @PutMapping("/saker/{sakId}")
    fun updateSak(
        @PathVariable("sakId") sakId: String,
        @RequestBody input: UpdateSakInput,
    ): Sak {
        logger.debug("updateSak")

        return sakService.updateSak(sakId = sakId, input = input)
    }

    @DeleteMapping("/saker/{sakId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSak(
        @PathVariable("sakId") sakId: String,
    ) {
        logger.debug("deleteSak")

        sakService.deleteSak(sakId)
    }
}
