package no.nav.klage.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.tags.Tag
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

// Served at /v3/api-docs, with Swagger UI at /swagger-ui.html. Endpoints are grouped by tag, one per controller.
@Configuration
class OpenApiConfig {
    companion object {
        const val KLANKE_API_TAG = "Klanke API"
        const val MOCK_DATA_TAG = "Mock data"
    }

    @Bean
    fun openApi(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("klanke-mock")
                    .description(
                        "Mock of the Klanke API, for testing. Endpoints tagged '$KLANKE_API_TAG' mirror the real " +
                            "Klanke API. Endpoints tagged '$MOCK_DATA_TAG' are utilities for managing the mock data, " +
                            "and are not in the real Klanke API.",
                    ),
            ).tags(
                listOf(
                    Tag().name(KLANKE_API_TAG).description("Mirrors the real Klanke API."),
                    Tag()
                        .name(MOCK_DATA_TAG)
                        .description("Utilities for managing the mock data. Not in the real Klanke API."),
                ),
            )
}
