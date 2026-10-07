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

## Consumers

| Repo | Reads |
|---|---|
| `sharechat-android` | `FeedItem.kt` (Gson, snake_case keys) |
| `sharechat-ios` | `FeedItem.swift` (Codable, snake_case keys, ISO-8601 dates) |

Changes must stay backward-compatible with installed app versions. Follow the
`mobile-api-compatibility` skill from `sharechat-ai-context`.

## Run

```
./gradlew bootRun
curl "localhost:8080/v1/feed?user_id=u_1&limit=2"
```
