package no.nav.klage.api

import io.mockk.mockk
import no.nav.klage.repository.SakRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

// Starts the web layer without a database, so it runs without Docker.
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "spring.autoconfigure.exclude=" +
            "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration," +
            "org.springframework.boot.flyway.autoconfigure.FlywayAutoConfiguration," +
            "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration," +
            "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration",
    ],
)
class OpenApiDocsTest(
    @Value("\${local.server.port}") private val port: Int,
) {
    @TestConfiguration
    class MockRepositoryConfig {
        @Bean
        fun sakRepository(): SakRepository = mockk()
    }

    private val client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()

    private fun get(path: String): HttpResponse<String> =
        client.send(
            HttpRequest.newBuilder(URI.create("http://localhost:$port$path")).build(),
            HttpResponse.BodyHandlers.ofString(),
        )

    @Test
    fun `api docs are served as json and include the saker endpoints`() {
        val response = get("/v3/api-docs")

        assertThat(response.statusCode()).isEqualTo(200)
        assertThat(response.headers().firstValue("Content-Type")).hasValueSatisfying {
            assertThat(it).startsWith("application/json")
        }
        assertThat(response.body()).contains("\"/api/saker\"", "\"/api/saker/{sakId}\"", "\"title\":\"klanke-mock\"")
        assertThat(response.body()).doesNotContain("\"/\":")
    }

    @Test
    fun `swagger ui and its assets are served`() {
        val ui = get("/swagger-ui.html")
        assertThat(ui.statusCode()).isEqualTo(200)
        assertThat(ui.uri().path).isEqualTo("/swagger-ui/index.html")

        assertThat(get("/swagger-ui/swagger-ui.css").statusCode()).isEqualTo(200)
    }
}
