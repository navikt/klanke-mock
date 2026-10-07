package no.nav.klage.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

// Served at /v3/api-docs, with Swagger UI at /swagger-ui.html.
@Configuration
class OpenApiConfig {
    @Bean
    fun openApi(): OpenAPI =
        OpenAPI().info(
            Info()
                .title("klanke-mock")
                .description(
                    "Mock of the Klanke API, for testing. Endpoints marked as utility in the code " +
                        "(GET/POST /api/saker, PUT/DELETE /api/saker/{sakId}, GET /api/enums, GET /api/defaults) " +
                        "are not in the real Klanke API.",
                ),
        )
}
