package com.sharechat.feed.comments

import com.google.gson.Gson
import com.sharechat.feed.comments.model.CommentStatus
import com.sharechat.feed.comments.model.CreateCommentRequest
import com.sharechat.feed.comments.moderation.ModerationClient
import com.sharechat.feed.comments.repository.InMemoryCommentRepository
import com.sharechat.feed.comments.service.CommentService
import com.sharechat.feed.security.ForbiddenException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CommentServiceTest {

    private val repository = InMemoryCommentRepository()
    private val service = CommentService(repository, ModerationClient("http://localhost:1", Gson()))

    @Test
    fun `creates an approved comment`() {
        val comment = service.create("p_1001", "u_1", CreateCommentRequest("Beautiful garba!", null, null), "hi")

        assertEquals(CommentStatus.APPROVED, comment.status)
        assertEquals("u_1", comment.authorId)
    }

    @Test
    fun `lists comments for a post`() {
        service.create("p_1001", "u_1", CreateCommentRequest("first", null, null), "hi")
        service.create("p_1001", "u_2", CreateCommentRequest("second", null, null), "hi")

        val (page, next) = service.list("p_1001", null, 20)

        assertEquals(listOf("first", "second"), page.map { it.text })
        assertNull(next)
    }

    @Test
    fun `only the author can delete a comment`() {
        val comment = service.create("p_1001", "u_1", CreateCommentRequest("mine", null, null), "hi")

        assertThrows<ForbiddenException> { service.delete(comment.id, "u_2") }
        service.delete(comment.id, "u_1")
        assertNull(repository.findById(comment.id))
    }

    @Test
    fun `counts comments per post`() {
        service.create("p_1001", "u_1", CreateCommentRequest("a", null, null), "hi")
        service.create("p_1001", "u_2", CreateCommentRequest("b", null, null), "hi")
        service.create("p_1002", "u_1", CreateCommentRequest("c", null, null), "ta")

        assertEquals(mapOf("p_1001" to 2, "p_1002" to 1), service.countsFor(listOf("p_1001", "p_1002")))
    }
}
