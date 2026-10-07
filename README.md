# sharechat-feed-service

Serves the home feed to the ShareChat Android and iOS apps.

## API

`GET /v1/feed?user_id=&lang=&cursor=&limit=`. The contract is [`api/openapi.yaml`](api/openapi.yaml).
Update it in the same PR as any change to the response.

```json
{
  "items": [
    {
      "post_id": "p_1001",
      "author": { "id": "u_501", "handle": "priya_sings", "avatar_url": "https://..." },
      "media": { "type": "video", "url": "https://...", "duration_ms": 31000 },
      "caption": "Navratri garba 💃",
      "language": "hi",
      "like_count": 1240000,
      "created_at": "2026-10-05T18:30:00Z"
    }
  ],
  "next_cursor": "1"
}
```

## Comments

| Method | Path | Who |
|---|---|---|
| `GET` | `/v1/posts/{postId}/comments` | Anyone |
| `POST` | `/v1/posts/{postId}/comments` | Signed-in users |
| `PATCH` / `DELETE` | `/v1/comments/{commentId}` | The comment's author |
| `POST` | `/v1/moderation/comments/{commentId}/hide` | Trust & safety moderators |

New comments are checked by the moderation service before they're published. The API gateway forwards
the caller's identity in `X-User-Id` and their role in `X-User-Role`. Feed items include `comment_count`.

## Run

```
./gradlew bootRun
curl "localhost:8080/v1/feed?user_id=u_1&limit=2"
```
