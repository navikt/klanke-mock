package no.nav.klage.api

import io.swagger.v3.oas.annotations.Hidden
import no.nav.klage.getLogger
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

// The HTML UI, not an API endpoint, so it is left out of the OpenAPI docs.
@Hidden
@RestController
class UiController {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)

        const val INDEX_HTML_PATH = "ui/index.html"
    }

    // Utility. Not in the original api we are mocking. Could be useful in tests.
    @GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
    fun index(): Resource {
        logger.debug("index")

        return ClassPathResource(INDEX_HTML_PATH)
    }
}
