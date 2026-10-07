package com.sharechat.feed.api

import com.google.gson.Gson
import com.sharechat.feed.model.FeedResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FeedControllerTest(
    @Autowired private val rest: TestRestTemplate,
    @Autowired private val gson: Gson,
) {

    @Test
    fun `returns a page of feed items`() {
        val body = rest.getForObject("/v1/feed?user_id=u_1&limit=2", String::class.java)
        val feed = gson.fromJson(body, FeedResponse::class.java)

        assertEquals(2, feed.items.size)
        assertEquals("p_1001", feed.items[0].postId)
        assertEquals(1_240_000L, feed.items[0].likeCount)
        assertNotNull(feed.nextCursor)
    }

    @Test
    fun `filters by language`() {
        val body = rest.getForObject("/v1/feed?user_id=u_1&lang=ta", String::class.java)
        val feed = gson.fromJson(body, FeedResponse::class.java)

        assertEquals(listOf("ta"), feed.items.map { it.language })
    }
}
