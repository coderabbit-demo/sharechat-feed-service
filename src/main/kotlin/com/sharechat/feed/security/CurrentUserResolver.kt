package com.sharechat.feed.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Component

/**
 * Works out who is making the request. The API gateway validates the session token and forwards
 * the user's ID and role as headers.
 */
@Component
class CurrentUserResolver {

    fun userId(request: HttpServletRequest): String =
        request.getHeader(USER_ID_HEADER)
            // Older app versions call us directly with user_id, like /v1/feed does
            ?: request.getParameter("user_id")
            ?: throw UnauthenticatedException()

    fun isModerator(request: HttpServletRequest): Boolean =
        request.getHeader(ROLE_HEADER).equals("moderator", ignoreCase = true)

    companion object {
        const val USER_ID_HEADER = "X-User-Id"
        const val ROLE_HEADER = "X-User-Role"
    }
}

class UnauthenticatedException : RuntimeException("missing user identity")

class ForbiddenException(message: String) : RuntimeException(message)
