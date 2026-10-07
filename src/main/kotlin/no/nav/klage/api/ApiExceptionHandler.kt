package no.nav.klage.api

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import tools.jackson.core.JacksonException

// Returns Spring MVC exceptions as problem details, including ResponseStatusException (e.g. id mismatch on update,
// unknown sak on update/delete), which would otherwise give Tomcat's error page without the reason.
@RestControllerAdvice
class ApiExceptionHandler : ResponseEntityExceptionHandler() {
    // A missing required field (e.g. fnr when creating a sak), an invalid enum value or malformed JSON
    // gives 400 with Jackson's description of the problem, instead of the generic "Failed to read request".
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        val detail = (ex.cause as? JacksonException)?.originalMessage ?: ex.message ?: "Unreadable request body"

        return handleExceptionInternal(
            ex = ex,
            body = ProblemDetail.forStatusAndDetail(status, detail),
            headers = headers,
            statusCode = status,
            request = request,
        )
    }

    override fun handleExceptionInternal(
        ex: Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        if (logger.isDebugEnabled) {
            logger.debug("Responding with $statusCode: ${(body as? ProblemDetail)?.detail ?: ex.message}")
        }

        return super.handleExceptionInternal(ex, body, headers, statusCode, request)
    }
}
