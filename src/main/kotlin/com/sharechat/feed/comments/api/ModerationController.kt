package com.sharechat.feed.comments.api

import com.sharechat.feed.comments.model.CommentResponse
import com.sharechat.feed.comments.model.HideCommentRequest
import com.sharechat.feed.comments.model.toResponse
import com.sharechat.feed.comments.service.CommentService
import com.sharechat.feed.security.CurrentUserResolver
import com.sharechat.feed.security.ForbiddenException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/** Tools for the trust & safety team to take down comments after they were published. */
@RestController
class ModerationController(
    private val comments: CommentService,
    private val currentUser: CurrentUserResolver,
) {

    @PostMapping("/v1/moderation/comments/{commentId}/hide")
    fun hide(
        @PathVariable commentId: String,
        @RequestBody body: HideCommentRequest,
        request: HttpServletRequest,
    ): CommentResponse {
        if (!currentUser.isModerator(request)) throw ForbiddenException("moderators only")
        return comments.hide(commentId, currentUser.userId(request), body.reason).toResponse()
    }
}
