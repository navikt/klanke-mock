package no.nav.klage.api

import no.nav.klage.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import tools.jackson.core.JacksonException

// Error handling shared by the API controllers. Scoped to them, so other controllers (UI, springdoc) are unaffected.
@RestControllerAdvice(assignableTypes = [KlankeApiController::class, MockDataController::class])
class ApiExceptionHandler {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    // A missing required field (e.g. fnr when creating a sak), an invalid enum value or malformed JSON
    // gives 400 with Jackson's description of the problem, instead of a 400 with no explanation.
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableBody(e: HttpMessageNotReadableException): ProblemDetail {
        val detail = (e.cause as? JacksonException)?.originalMessage ?: e.message ?: "Unreadable request body"
        logger.debug("Unreadable request body: {}", detail)

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail)
    }

    // Used by the utility endpoints (e.g. id mismatch on update, unknown sak on update/delete). Returns the
    // reason as a problem detail, instead of Tomcat's error page without it.
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(e: ResponseStatusException): ResponseEntity<ProblemDetail> {
        logger.debug("Responding with {}: {}", e.statusCode, e.reason)

        return ResponseEntity.status(e.statusCode).body(e.body)
    }
}
