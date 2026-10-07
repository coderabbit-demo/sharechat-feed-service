package com.sharechat.feed.comments.api

import com.sharechat.feed.comments.model.CommentPage
import com.sharechat.feed.comments.model.CommentResponse
import com.sharechat.feed.comments.model.CreateCommentRequest
import com.sharechat.feed.comments.model.UpdateCommentRequest
import com.sharechat.feed.comments.model.toResponse
import com.sharechat.feed.comments.service.CommentService
import com.sharechat.feed.security.CurrentUserResolver
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class CommentController(
    private val comments: CommentService,
    private val currentUser: CurrentUserResolver,
) {

    @GetMapping("/v1/posts/{postId}/comments")
    fun list(
        @PathVariable postId: String,
        @RequestParam("cursor", required = false) cursor: String?,
        @RequestParam("limit", defaultValue = "20") limit: Int,
    ): CommentPage {
        require(limit in 1..50) { "limit must be between 1 and 50" }
        val (page, next) = comments.list(postId, cursor, limit)
        return CommentPage(items = page.map { it.toResponse() }, nextCursor = next)
    }

    @PostMapping("/v1/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable postId: String,
        @RequestParam("lang", required = false) lang: String?,
        @RequestBody body: CreateCommentRequest,
        request: HttpServletRequest,
    ): CommentResponse =
        comments.create(postId, currentUser.userId(request), body, lang).toResponse()

    @PatchMapping("/v1/comments/{commentId}")
    fun edit(
        @PathVariable commentId: String,
        @RequestBody body: UpdateCommentRequest,
        request: HttpServletRequest,
    ): CommentResponse =
        comments.edit(commentId, currentUser.userId(request), body).toResponse()

    @DeleteMapping("/v1/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable commentId: String, request: HttpServletRequest) {
        comments.delete(commentId, currentUser.userId(request))
    }
}
