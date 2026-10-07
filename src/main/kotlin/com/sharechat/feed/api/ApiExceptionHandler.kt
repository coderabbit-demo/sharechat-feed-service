package com.sharechat.feed.api

import com.sharechat.feed.comments.service.CommentNotFoundException
import com.sharechat.feed.model.ErrorResponse
import com.sharechat.feed.security.ForbiddenException
import com.sharechat.feed.security.UnauthenticatedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(e: IllegalArgumentException) = error(HttpStatus.BAD_REQUEST, "invalid_request", e.message)

    @ExceptionHandler(UnauthenticatedException::class)
    fun unauthenticated(e: UnauthenticatedException) = error(HttpStatus.UNAUTHORIZED, "unauthenticated", e.message)

    @ExceptionHandler(ForbiddenException::class)
    fun forbidden(e: ForbiddenException) = error(HttpStatus.FORBIDDEN, "forbidden", e.message)

    @ExceptionHandler(CommentNotFoundException::class)
    fun notFound(e: CommentNotFoundException) = error(HttpStatus.NOT_FOUND, "not_found", e.message)

    private fun error(status: HttpStatus, code: String, message: String?): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(status).body(ErrorResponse(code, message ?: code))
}
