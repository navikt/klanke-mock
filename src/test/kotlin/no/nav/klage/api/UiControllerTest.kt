package no.nav.klage.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource

class UiControllerTest {
    @Test
    fun `index html exists on classpath`() {
        assertThat(ClassPathResource(UiController.INDEX_HTML_PATH).exists()).isTrue()
    }

    @Test
    fun `index returns the ui index html`() {
        val resource = UiController().index()

        assertThat(resource.exists()).isTrue()
        assertThat(resource.getContentAsString(Charsets.UTF_8)).contains("<title>klanke-mock</title>")
    }
}
