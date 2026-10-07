# feed-service engineering guidelines

`/v1/feed` is the most-called endpoint in the app. These rules keep it fast and safe.

## Request path
- Keep the request thread for building the response. Work the response doesn't need (analytics,
  notifications, cache warming) must run asynchronously, for example through the event publisher,
  and must never delay or fail the request.
- Every outbound HTTP call sets a connect timeout and a request timeout. On the request path the
  total budget is 200 ms.

## Configuration and secrets
- Endpoints, credentials and tunables come from configuration (`@ConfigurationProperties` backed by
  `application.properties` or environment variables). Never hard-code them in source.
- Credentials are injected from the secret manager at runtime and are never committed.

## Logging
- Use SLF4J placeholders (`log.info("served {} items", n)`), not string concatenation.
- Never log personal data: raw user IDs, device IDs, IP addresses, phone numbers, emails or location.
  Log a pseudonymous ID if you need to correlate requests.
- One INFO line per request at most. Per-item detail goes to DEBUG.

## Error handling
- Never swallow exceptions. Log them with context and record a metric, or rethrow.
- Client errors return `ErrorResponse` with a stable `code`.

## Dependencies
- Construct collaborators through constructor injection, not by creating them inside classes, so
  they can be configured and replaced in tests.
